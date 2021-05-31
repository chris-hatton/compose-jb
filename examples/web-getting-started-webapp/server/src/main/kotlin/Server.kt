
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.html.respondHtml
import io.ktor.http.cio.websocket.ExperimentalWebSocketExtensionApi
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.script
import kotlinx.html.title

/**
 * This is the name of the browser-executable JavaScript file of the Client.
 * Its name is derived from the `browser` Gradle module it is compiled from.
 */
private const val jsFileName = "browser.js"

/**
 * The Kotlin/JS compiler also produces a same-named,
 * standard JavaScript Map file to assist with debugging.
 * See: https://www.html5rocks.com/en/tutorials/developertools/sourcemaps/
 */
private const val jsFileMapName = "$jsFileName.map"

private const val initialNumber = 0
private val numberFlow: MutableStateFlow<Int> = MutableStateFlow(value = initialNumber)

/**
 * This `main` Ktor module definition is the entry-point once inside a Container engine.
 */
@OptIn(ExperimentalWebSocketExtensionApi::class)
fun Application.main() {

    install(ContentNegotiation) {
        json()
    }

    install(WebSockets)

    // Call logging is not strictly essential, but a practical necessity for developing the server
    // May be disabled (e.g. by configuration) for production Apps
    // See: https://ktor.io/docs/logging.html#call_logging
    install(CallLogging)

    install(StatusPages)

    routing {

        /**
         * Serve a basic page defined as HTML DOM that embeds the 'Compose for Web' client App.
         */
        get("/") {
            // See: https://ktor.io/docs/logging.html#access_logger
            call.application.environment.log.info("Serving Application JS")

            try {
                call.respondHtml {
                    head {
                        title("web-getting-deployed")
                    }
                    body {
                        div { id = ROOT_DIV_ID } // Required by Compose Web
                        script(src = "static/$jsFileName") {}
                    }
                }
            } catch (e: Exception) {
                call.application.environment.log.error("Failed serving Application JS", e)
            }

            call.application.environment.log.info("Served Application JS")
        }

        /**
         * Provide the current number to a client
         * See: https://ktor.io/docs/requests.html
         */
        get(GET_NUMBER_PATH) {
            call.application.environment.log.info("Serving number to client")

            val response = GetNumberResponse(numberFlow.value)
            call.respond(response)
        }

        /**
         * Enable clients to set the number
         * See: https://ktor.io/docs/requests.html
         */
        post(SET_NUMBER_PATH) {
            call.application.environment.log.info("Accepting number from client")

            val request = call.receive<SetNumberRequest>()
            numberFlow.value = request.number
        }

        /**
         * Emissions on numberFlow will be broadcast to all connected clients
         * See: https://ktor.io/docs/websocket.html
         */
        webSocket(NUMBER_UPDATES_PATH) {
            call.application.environment.log.info("Pushing number to client")

            numberFlow.collect { number ->
                val numberFrame = Frame.Text(number.toString())
                send(numberFrame)
            }
        }

        static("/static") {
            resource(jsFileName) // Serve the Client Web-App JavaScript file
            resource(jsFileMapName) // Expose Kotlin source to the browser to assist with debugging
        }
    }
}
