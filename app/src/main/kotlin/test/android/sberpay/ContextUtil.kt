package test.android.sberpay

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

internal fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

internal fun Context.isSberbankOnlineInstalled(): Boolean {
    val info = try {
        packageManager.getPackageInfo("ru.sberbankmobile", 0)
    } catch (e: Throwable) {
        return false
    }
    return info != null
}

internal fun Context.openSberbankOnline(bankInvoiceId: String) {
    val intent = Intent("android.intent.action.VIEW")
    val scheme = "sberpay"
    val host = "invoicing"
    val path = "v2"
    val params = mapOf(
        "operationType" to "app2App",
        "bankInvoiceId" to bankInvoiceId
    )
    intent.data = Uri.parse(
        "$scheme://$host/$path" +
                params.entries.joinToString(prefix = "?", separator = "&") { (k, v) -> "$k=$v" }
    )
    intent.component = ComponentName(
        "ru.sberbankmobile",
        "ru.sberbank.mobile.core.deeplink.impl.view.DeeplinkActivity"
    )
    startActivity(intent)
}
