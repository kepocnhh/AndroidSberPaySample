package test.android.sberpay

import okhttp3.OkHttpClient

internal object NetworkProvider {
    const val URL = "https://3dsec.sberbank.ru/payment/rest"
    val client = OkHttpClient.Builder().build()
}
