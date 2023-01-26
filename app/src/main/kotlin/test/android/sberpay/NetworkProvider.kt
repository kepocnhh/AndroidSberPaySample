package test.android.sberpay

import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate

internal object NetworkProvider {
    const val URL = "https://3dsec.sberbank.ru/payment/rest"
    val client = OkHttpClient.Builder().also { builder ->
        val factory = CertificateFactory.getInstance("X.509")
//        val context = App.context ?: TODO()
//        val streams = setOf(context.assets.open("foo.pem"))
//        val certificates = streams.map { stream ->
//            factory.generateCertificate(stream) as X509Certificate
//        }
        val trustManager: X509TrustManager = object : X509TrustManager {
            override fun checkClientTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {}

            override fun checkServerTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {}

            override fun getAcceptedIssuers(): Array<X509Certificate> {
//                return certificates.toTypedArray()
                return emptyArray()
            }
        }
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
        builder.sslSocketFactory(sslContext.socketFactory, trustManager)
    }.build()
}
