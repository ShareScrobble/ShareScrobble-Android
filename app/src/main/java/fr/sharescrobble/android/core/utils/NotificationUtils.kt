package fr.sharescrobble.android.core.utils

import android.app.Notification
import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import fr.sharescrobble.android.R
import fr.sharescrobble.android.core.Constants
import fr.sharescrobble.android.main.ui.MainActivity


object NotificationUtils {
    fun persistentNotificationBuilder(ctx: Context, sourceScrobble: String): Notification {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(ctx, Constants.CHANNEL_ID)

        val intent = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val tapIntent: PendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0)

        val unsubscribeIntent = Intent(ctx, MainActivity::class.java).apply {
            action = "UNSUBSCRIBE"
            putExtra(EXTRA_NOTIFICATION_ID, 0)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val unsubscribePendingIntent: PendingIntent =
            PendingIntent.getActivity(ctx, 0, unsubscribeIntent, 0)

        builder.setContentTitle(ctx.getString(R.string.permanent_notification_running))
        builder.setContentText(ctx.getString(R.string.permanent_notification_running_from, sourceScrobble))
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
        builder.addAction(
            R.drawable.ic_baseline_power_settings_new_24,
            ctx.getString(R.string.permanent_notification_button_stop),
            unsubscribePendingIntent
        )
        builder.setContentIntent(tapIntent)
        builder.priority = NotificationCompat.PRIORITY_MAX
        builder.setOngoing(true)

        return builder.build()
    }

    fun notificationBuilder(ctx: Context): Notification {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(ctx, Constants.CHANNEL_ID)

        val intent = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val tapIntent: PendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0)

        builder.setContentTitle(context.getString(R.string.permanent_notification_stopped))
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
        builder.setContentIntent(tapIntent)
        builder.priority = NotificationCompat.PRIORITY_MAX

        return builder.build()
    }

    fun removeNotification(ctx: Context, notificationId: Int) {
        with(NotificationManagerCompat.from(ctx)) {
            cancel(
                1
            )
        }
    }
}