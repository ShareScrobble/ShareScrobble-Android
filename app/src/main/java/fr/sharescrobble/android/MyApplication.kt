package fr.sharescrobble.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var ctx: Context? = null
        fun getCtx(): Context {
            return ctx!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        MyApplication.ctx = applicationContext
    }
}