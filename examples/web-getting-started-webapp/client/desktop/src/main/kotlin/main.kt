import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import example.NumberViewModel
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.websocket.WebSockets
import io.ktor.http.Url
import kotlinx.coroutines.GlobalScope

fun main() {
    val httpClient = HttpClient() {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        // See: https://ktor.io/docs/websocket-client.html#usage
        install(WebSockets)
    }

    val viewModel = NumberViewModel(
        baseUrl = Url("http://localhost:8080"),
        httpClient = httpClient,
        coroutineContext = GlobalScope.coroutineContext
    )

    Window {
        MaterialTheme {
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("Example") })
                }
            ) {
                NumberView(viewModel)
            }
        }
    }
}
