package test.android.sberpay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

internal class DeeplinkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        if (intent == null) {
            println("intent null")
            finish()
            return
        }
        when (intent.action) {
            "android.intent.action.VIEW" -> {
                val uri = intent.data
                if (uri == null) {
                    println("uri null")
                    finish()
                    return
                }
//                println("uri: $uri")
                println("intent: $intent")
                finish()
            }
            else -> TODO()
        }
    }
}
