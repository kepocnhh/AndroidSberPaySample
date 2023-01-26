package test.android.sberpay

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

class MainActivity : AppCompatActivity() {
    private enum class RouterState {
        SBER_PAY,
        WEB_VIEW
    }

    @Composable
    private fun Router() {
        Box(modifier = Modifier.fillMaxSize()) {
            val state = remember { mutableStateOf<RouterState?>(null) }
            when (state.value) {
                RouterState.SBER_PAY -> {
                    SberPayScreen()
                }
                RouterState.WEB_VIEW -> {
                    SberWebScreen()
                }
                null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    ) {
                        BasicText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clickable {
                                    state.value = RouterState.SBER_PAY
                                }
                                .wrapContentHeight(Alignment.CenterVertically),
                            style = TextStyle(
                                color = Color.Blue,
                                textAlign = TextAlign.Center
                            ),
                            text = "Sber Pay"
                        )
                        BasicText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clickable {
                                    state.value = RouterState.WEB_VIEW
                                }
                                .wrapContentHeight(Alignment.CenterVertically),
                            style = TextStyle(
                                color = Color.Blue,
                                textAlign = TextAlign.Center
                            ),
                            text = "WebView"
                        )
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Router()
            }
        }
    }
}
