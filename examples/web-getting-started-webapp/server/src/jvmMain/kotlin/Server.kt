
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
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
 * Its name is derived from the `client` Gradle module it is compiled from.
 */
private const val jsFileName = "client.js"

/**
 * The Kotlin/JS compiler also produces a same-named,
 * standard JavaScript Map file to assist with debugging.
 * See: https://www.html5rocks.com/en/tutorials/developertools/sourcemaps/
 */
private const val jsFileMapName = "$jsFileName.map"

private const val initialNumber = 0
private val numberFlow: MutableStateFlow<Int> = MutableStateFlow(value = initialNumber)

/**
 * This `main` function supports running the Ktor Server directly during development,
 * using an embedded Servlet Engine.  When running from a WAR distributable in a
 * full Servlet Container, this is not used.
 */
fun main(args: Array<String>): Unit = io.ktor.server.jetty.EngineMain.main(args)

/**
 * This `main` Ktor module definition is the entry-point once inside a Container engine.
 */
@OptIn(ExperimentalWebSocketExtensionApi::class)
fun Application.main() {

    install(ContentNegotiation) {
        json()
    }

    install(WebSockets)

    routing {

        /**
         * Serve a basic page defined as HTML DOM that embeds the 'Compose for Web' client App.
         */
        get("/") {
            call.respondHtml {
                head {
                    title("web-getting-deployed")
                }
                body {
                    div { id = ROOT_DIV_ID } // Required by Compose Web
                    script(src = "static/$jsFileName") {}
                }
            }
        }

        /**
         * Provide the current number to a client
         * See: https://ktor.io/docs/requests.html
         */
        get(GET_NUMBER_PATH) {
            val response = GetNumberResponse(numberFlow.value)
            call.respond(response)
        }

        /**
         * Enable clients to set the number
         * See: https://ktor.io/docs/requests.html
         */
        post(SET_NUMBER_PATH) {
            val request = call.receive<SetNumberRequest>()
            numberFlow.value = request.number
        }

        /**
         * Emissions on numberFlow will be broadcast to all connected clients
         * See: https://ktor.io/docs/websocket.html
         */
        webSocket(NUMBER_UPDATES_PATH) {
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
