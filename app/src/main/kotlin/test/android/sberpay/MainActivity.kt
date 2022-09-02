package test.android.sberpay

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import sberpay.sdk.sberpaysdk.domain.SberbankOnlineManager
import sberpay.sdk.sberpaysdk.view.SberButton
import test.android.kiosk.showToast

class MainActivity : AppCompatActivity() {
    companion object {
        private val manager = SberbankOnlineManager()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    modifier = Modifier.align(Alignment.Center),
                    factory = { context ->
                        SberButton(context).also {
                            it.setOnClickListener {
                                if (manager.isSberbankOnlineInstalled(context)) {
                                    val bankInvoiceId = TODO()
                                    manager.openSberbankOnline(context, bankInvoiceId)
                                } else {
                                    showToast("no sberpay")
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}
