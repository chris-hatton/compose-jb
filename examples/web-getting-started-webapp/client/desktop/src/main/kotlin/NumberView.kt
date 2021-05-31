import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import example.NumberViewModel

@Composable
fun NumberView(viewModel: NumberViewModel) {

    val displayedNumber: String by viewModel.displayedNumberFlow.collectAsState()
    val isEnabled: Boolean by viewModel.isControlsEnabledFlow.collectAsState()

    Row(modifier = Modifier.padding(25.dp).wrapContentHeight()) {
        Button(
            enabled = isEnabled,
            onClick = viewModel::onUserDecrement
        ) {
            Text("-")
        }

        Box(
            modifier = Modifier
                .width(32.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = displayedNumber,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Button(
            enabled = isEnabled,
            onClick = viewModel::onUserIncrement
        ) {
            Text("+")
        }
    }
}
