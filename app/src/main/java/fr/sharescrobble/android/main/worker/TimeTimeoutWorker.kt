package fr.sharescrobble.android.main.worker

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import fr.sharescrobble.android.MyApplication
import fr.sharescrobble.android.core.Constants
import fr.sharescrobble.android.core.utils.NotificationUtils
import fr.sharescrobble.android.network.repositories.ScrobbleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TimeTimeoutWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    private val privatePreferences =
        MyApplication.getCtx().getSharedPreferences("Scrobble", Context.MODE_PRIVATE)
    private val editor = privatePreferences?.edit()

    override fun doWork(): Result {
        val sourceScrobble = privatePreferences.getString("sourceScrobble", null)

        if (sourceScrobble != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    ScrobbleRepository.apiInterface.unsubscribe(sourceScrobble)
                    NotificationUtils.removeNotification(MyApplication.getCtx(), 1)
                    editor?.remove("sourceScrobble")
                    editor?.commit()

                    // Send a notification
                    with(NotificationManagerCompat.from(applicationContext)) {
                        notify(
                            2,
                            NotificationUtils.notificationBuilder(
                                applicationContext
                            )
                        )
                    }
                } catch (e: Throwable) {
                    Log.e(Constants.TAG, e.toString())
                }
            }
        }

        return Result.success()
    }
}