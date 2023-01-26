package test.android.sberpay

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class App : Application() {
    companion object {
        var context: Context? = null
        var preferences: SharedPreferences? = null
        val scope = CoroutineScope(Dispatchers.Main)
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        preferences = getSharedPreferences("sberpaysample", Context.MODE_PRIVATE)
    }
}
