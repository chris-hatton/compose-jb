package example

import GET_NUMBER_PATH
import GetNumberResponse
import NUMBER_UPDATES_PATH
import SET_NUMBER_PATH
import SetNumberRequest
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * The [httpClient] Ktor HTTP client must have JSON and WebSockets features installed.
 */
class NumberViewModel(
    private val httpClient: HttpClient,
    baseUrl: Url,
    override val coroutineContext: CoroutineContext
) : CoroutineScope {

    companion object {
        const val NUMBER_LOADING = "-"
    }

    private val url = baseUrl
    private val hostname = url.host
    private val port = url.port

    private val getNumberUrl = url.copy(encodedPath = GET_NUMBER_PATH)
    private val setNumberUrl = url.copy(encodedPath = SET_NUMBER_PATH)

    private suspend fun getServerNumber() = httpClient.get<GetNumberResponse>(getNumberUrl).number
    private suspend fun setServerNumber(number: Int) = httpClient.post<Unit>(setNumberUrl) {
        contentType(ContentType.Application.Json)
        body = SetNumberRequest(number)
    }

    private sealed class Operation {
        sealed class User : Operation() {
            object Increment : User()
            object Decrement : User()
        }
        sealed class Server : Operation() {
            data class Set(val number: Int) : Operation()
        }
    }

    private val operationChannel = Channel<Operation>()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val operationToResultFlow: SharedFlow<Pair<Operation?, Int>> = flow {
        val initialNumber = getServerNumber()
        val initialOperation: Operation = Operation.Server.Set(initialNumber)
        val numberFlow = operationChannel
            .receiveAsFlow()
            .scan(initialOperation to initialNumber) { (_, number), operation ->
                val result = when (operation) {
                    Operation.User.Increment -> number + 1
                    Operation.User.Decrement -> number - 1
                    is Operation.Server.Set -> operation.number
                }
                operation to result
            }
        emitAll(numberFlow)
    }.shareIn(
        scope = this,
        started = SharingStarted.Eagerly,
        replay = 1
    )

    //region View Binding

    //region Input

    fun onUserDecrement() = operationChannel.offer(Operation.User.Decrement)
    fun onUserIncrement() = operationChannel.offer(Operation.User.Increment)

    //endregion Input

    //region Output

    /**
     * Controls are enabled once the first number is known.
     */
    val isControlsEnabledFlow: StateFlow<Boolean> = operationToResultFlow
        .take(1)
        .map { true }
        .stateIn(
            scope = this,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    val displayedNumberFlow: StateFlow<String> = operationToResultFlow
        .map { (_, number) -> number.toString() }
        .stateIn(
            scope = this,
            started = SharingStarted.Eagerly,
            initialValue = NUMBER_LOADING
        )

    //endregion Output

    //endregion View Binding

    init {

        // Listen for number updates from the server
        launch {
            httpClient.webSocket(
                method = HttpMethod.Get,
                host = hostname,
                port = port,
                path = NUMBER_UPDATES_PATH
            ) {
                for (frame in incoming) {
                    val textFrame = frame as? Frame.Text ?: continue
                    val incomingNumber = textFrame.readText().toInt()
                    operationChannel.offer(Operation.Server.Set(incomingNumber))
                }
            }
        }

        // Send the result of User taps on '-' and '+' back to the server
        launch {
            // Numbers resulting from a Server operation need not be echoed back to the Server; while all others are.
            operationToResultFlow
                .filter { (operation, _) -> operation !is Operation.Server }
                .collect { (_, number) -> setServerNumber(number) }
        }
    }
}
