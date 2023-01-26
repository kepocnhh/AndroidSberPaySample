package test.android.sberpay

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import sp.kx.functional.computation.Single
import sp.kx.functional.computation.util.coroutine.singled
import sp.kx.okhttp.execute
import sp.kx.okhttp.requireBody

private fun onSberWebRegister(login: String, password: String, onResult: (Single<SberWebResponse>) -> Unit) {
    App.scope.launch {
        val returnUrl = "foo" // todo
        val orderNumber = System.currentTimeMillis().toString() // todo
        val amount = 100 // todo
//            val deepLink = "bar" // todo
        val result = singled(Dispatchers.IO) {
            if (login.isEmpty()) error("Login is empty!")
            if (password.isEmpty()) error("Password is empty!")
            NetworkProvider.client.execute(
                url = "${NetworkProvider.URL}/register.do",
                queries = mapOf(
                    "userName" to login,
                    "password" to password,
                    "orderNumber" to orderNumber,
                    "amount" to amount.toString(),
                    "returnUrl" to "https://web_pay_return",
                    "failUrl" to "https://web_pay_fail"
                ),
                headers = emptyMap()
            ).use {
                when (it.code) {
                    200 -> {
                        val preferences = App.preferences ?: TODO()
                        val body = it.requireBody().string()
                        val jsonObject = JSONObject(body)
                        val orderId = jsonObject.optString("orderId")
                        val formUrl = jsonObject.optString("formUrl")
                        if (orderId.isNullOrEmpty() || formUrl.isNullOrEmpty()) {
                            val errorCode = jsonObject.optString("errorCode")
                            val errorMessage = jsonObject.optString("errorMessage")
                            if (errorCode.isNullOrEmpty()) {
                                error("Unknown error!")
                            } else {
                                error("Error [$errorCode] $errorMessage!")
                            }
                        }
                        preferences.edit()
                            .putString("sberweb:userName", login)
                            .putString("sberweb:password", password)
                            .apply()
                        SberWebResponse(
                            orderId = orderId,
                            formUrl = formUrl
                        )
                    }
                    else -> error("Code ${it.code}!")
                }
            }
        }
        onResult(result)
    }
}

@Composable
private fun SberWebEnter(onResult: (SberWebResponse) -> Unit) {
    val context = LocalContext.current
    val preferences = App.preferences ?: TODO()
    val loginState = rememberSaveable { mutableStateOf(preferences.getString("sberweb:userName", "") ?: "") }
    val passwordState = rememberSaveable { mutableStateOf(preferences.getString("sberweb:password", "") ?: "") }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            BasicText(
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(color = Color.Gray),
                text = "login"
            )
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                textStyle = TextStyle(color = Color.White),
                singleLine = true,
                value = loginState.value,
                onValueChange = { loginState.value = it }
            )
            BasicText(
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(color = Color.Gray),
                text = "password"
            )
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                textStyle = TextStyle(color = Color.White),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                value = passwordState.value,
                onValueChange = { passwordState.value = it }
            )
            BasicText(
                modifier = Modifier
                    .height(48.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        onSberWebRegister(login = loginState.value, password = passwordState.value) {
                            when (it) {
                                is Single.Success -> onResult(it.value)
                                is Single.Error -> {
                                    context.showToast("error: ${it.error}")
                                }
                            }
                        }
                    },
                style = TextStyle(
                    color = Color.Blue,
                    textAlign = TextAlign.Center
                ),
                text = "register"
            )
        }
    }
}

@Composable
private fun PaymentWebView(url: String) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).also { view ->
                view.loadUrl(url)
                view.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        url: String
                    ): Boolean {
                        println("url: $url")
                        // todo
                        return false
                    }
                }
                view.settings.javaScriptEnabled = true
            }
        }
    )
}

@Composable
internal fun SberWebScreen() {
    val responseState = remember { mutableStateOf<SberWebResponse?>(null) }
    val response = responseState.value
    if (response == null) {
        SberWebEnter { responseState.value = it }
    } else {
        PaymentWebView(url = response.formUrl)
    }
}
