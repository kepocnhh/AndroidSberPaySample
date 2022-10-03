package test.android.sberpay

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import sberpay.sdk.sberpaysdk.domain.SberbankOnlineManager
import sberpay.sdk.sberpaysdk.view.SberButton
import sp.kx.functional.computation.Single
import sp.kx.functional.computation.util.coroutine.singled
import sp.kx.okhttp.execute
import sp.kx.okhttp.requireBody
import test.android.kiosk.showToast

class MainActivity : AppCompatActivity() {
    companion object {
        private const val URL = "https://3dsec.sberbank.ru/payment/rest"
        private val scope = CoroutineScope(Dispatchers.Main)
        private val manager = SberbankOnlineManager()
        private val client = OkHttpClient.Builder().build()
        private val preferences = requireNotNull(App.context).getSharedPreferences("sberpaysample", Context.MODE_PRIVATE) ?: TODO()
    }

    private fun onRegister(login: String, password: String, onResult: (Single<SberResponse>) -> Unit) {
        scope.launch {
            val returnUrl = "foo" // todo
            val orderNumber = System.currentTimeMillis().toString() // todo
            val amount = 100 // todo
            val deepLink = "bar" // todo
            val jsonParams = "{\"app2app\":true,\"app.osType\":\"android\",\"app.deepLink\":\"$deepLink\"}"
            val result = singled(Dispatchers.IO) {
                if (login.isEmpty()) error("Login is empty!")
                if (password.isEmpty()) error("Password is empty!")
                client.execute(
                    url = "$URL/register.do",
                    queries = mapOf(
                        "userName" to login,
                        "password" to password,
                        "returnUrl" to returnUrl,
                        "orderNumber" to orderNumber,
                        "amount" to amount.toString(),
                        "jsonParams" to jsonParams,
                    ),
                    headers = emptyMap()
                ).use {
                    when (it.code) {
                        200 -> {
                            val body = it.requireBody().string()
                            val jsonObject = JSONObject(body)
                            val orderId = jsonObject.optString("orderId")
                            if (orderId.isNullOrEmpty()) {
                                val errorCode = jsonObject.optString("errorCode")
                                val errorMessage = jsonObject.optString("errorMessage")
                                if (errorCode.isNullOrEmpty()) {
                                    error("Unknown error!")
                                } else {
                                    error("Error [$errorCode] $errorMessage!")
                                }
                            }
                            preferences.edit()
                                .putString("userName", login)
                                .putString("password", password)
                                .apply()
                            SberResponse(
                                orderId = orderId,
                                sbolBankInvoiceId = jsonObject.getJSONObject("externalParams").getString("sbolBankInvoiceId")
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
    private fun Enter(onResult: (SberResponse) -> Unit) {
        val loginState = rememberSaveable { mutableStateOf(preferences.getString("userName", "") ?: "") }
        val passwordState = rememberSaveable { mutableStateOf(preferences.getString("password", "") ?: "") }
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
                            onRegister(login = loginState.value, password = passwordState.value) {
                                when (it) {
                                    is Single.Success -> onResult(it.value)
                                    is Single.Error -> showToast("error: ${it.error}")
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
    private fun Pay(response: SberResponse) {
        val context: Context = LocalContext.current
        Box(modifier = Modifier.fillMaxSize()) {
            BasicText(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .align(Alignment.Center)
                    .clickable {
                        if (manager.isSberbankOnlineInstalled(context)) {
                            manager.openSberbankOnline(context, response.sbolBankInvoiceId)
                        } else {
                            showToast("no sberpay")
                        }
                    },
                style = TextStyle(
                    color = Color.Blue,
                    textAlign = TextAlign.Center
                ),
                text = "pay"
            )
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
                val responseState = remember { mutableStateOf<SberResponse?>(null) }
                val response = responseState.value
                if (response == null) {
                    Enter { responseState.value = it }
                } else {
                    Pay(response)
                }
            }
//            Box(modifier = Modifier.fillMaxSize()) {
//                AndroidView(
//                    modifier = Modifier.align(Alignment.Center),
//                    factory = { context ->
//                        SberButton(context).also {
//                            it.setOnClickListener {
//                                if (manager.isSberbankOnlineInstalled(context)) {
//                                    // todo
//                                } else {
//                                    showToast("no sberpay")
//                                }
//                            }
//                        }
//                    }
//                )
//            }
        }
    }
}
