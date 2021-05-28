import androidx.compose.web.renderComposable
import example.NumberViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.websocket.WebSockets
import io.ktor.http.Url
import kotlinx.browser.window
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope

@OptIn(ExperimentalCoroutinesApi::class)
fun main() {

    val httpClient = HttpClient(Js) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        // See: https://ktor.io/docs/websocket-client.html#usage
        install(WebSockets)
    }

    val viewModel = NumberViewModel(
        baseUrl = Url(window.location.href),
        httpClient = httpClient,
        coroutineContext = GlobalScope.coroutineContext
    )

    renderComposable(rootElementId = ROOT_DIV_ID) {
        NumberView(viewModel)
    }
}
