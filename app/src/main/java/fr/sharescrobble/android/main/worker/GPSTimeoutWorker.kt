package fr.sharescrobble.android.main.worker

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import fr.sharescrobble.android.MyApplication
import fr.sharescrobble.android.core.Constants
import fr.sharescrobble.android.core.utils.NotificationUtils
import fr.sharescrobble.android.network.repositories.ScrobbleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class GPSTimeoutWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    private var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
    private val privatePreferences =
        MyApplication.getCtx().getSharedPreferences("Scrobble", Context.MODE_PRIVATE)
    private val editor = privatePreferences?.edit()
    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(appContext)

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        val sourceScrobble = privatePreferences.getString("sourceScrobble", null)
        val maxDistance = sharedPreferences.getInt("gpsRange", 50)
        val latitude = privatePreferences.getLong("latitude", 0)
        val longitude = privatePreferences.getLong("longitude", 0)

        val originalLocation = Location("")
        originalLocation.latitude = latitude.toDouble()
        originalLocation.longitude = longitude.toDouble()

        val currentLocation = Location("")

        // Get current location and store it
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            run {
                Log.d(Constants.TAG, location?.latitude.toString())
                Log.d(Constants.TAG, location?.longitude.toString())

                currentLocation.latitude = location?.latitude ?: 0.00
                currentLocation.longitude = location?.longitude ?: 0.00

                val distance = originalLocation.distanceTo(currentLocation)

                if (distance > maxDistance.toFloat() && sourceScrobble != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            ScrobbleRepository.apiInterface.unsubscribe(sourceScrobble)
                            NotificationUtils.removeNotification(MyApplication.getCtx(), 1)

                            editor?.remove("sourceScrobble")
                            editor?.remove("latitude")
                            editor?.remove("longitude")
                            editor?.commit()

                            // Cancel job
                            cancel()

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
            }
        }

        return Result.success()
    }
}