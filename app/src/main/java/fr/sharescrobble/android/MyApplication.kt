package fr.sharescrobble.android

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import fr.sharescrobble.android.core.Constants
import fr.sharescrobble.android.core.Constants.CHANNEL_ID

class MyApplication : Application() {
    /**
     * Get a hold of Application's Context
     */
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var ctx: Context? = null
        fun getCtx(): Context {
            return ctx!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        ctx = applicationContext

        this.createNotificationChannel()
    }

    /**
     * Initialize the notification channel
     */
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = Constants.NAME
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}