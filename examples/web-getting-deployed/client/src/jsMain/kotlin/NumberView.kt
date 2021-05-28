import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.web.attributes.disabled
import androidx.compose.web.css.padding
import androidx.compose.web.css.px
import androidx.compose.web.elements.Button
import androidx.compose.web.elements.Div
import androidx.compose.web.elements.Span
import androidx.compose.web.elements.Text
import example.NumberViewModel

@Composable
fun NumberView(viewModel: NumberViewModel) {

    val displayedNumber: String by viewModel.displayedNumberFlow.collectAsState()
    val isEnabled: Boolean by viewModel.isControlsEnabledFlow.collectAsState()

    Div(style = { padding(25.px) }) {
        Button(
            attrs = {
                onClick { viewModel.onUserDecrement() }
                disabled(!isEnabled)
            }
        ) {
            Text("-")
        }

        Span(style = { padding(15.px) }) {
            Text(displayedNumber)
        }

        Button(
            attrs = {
                onClick { viewModel.onUserIncrement() }
                disabled(!isEnabled)
            }
        ) {
            Text("+")
        }
    }
}
