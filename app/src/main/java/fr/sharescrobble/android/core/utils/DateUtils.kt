package fr.sharescrobble.android.core.utils

import android.content.Context
import android.os.Build
import android.util.Log
import fr.sharescrobble.android.MyApplication
import fr.sharescrobble.android.core.Constants
import java.util.*

object DateUtils {
    private const val SECOND_MILLIS = 1000
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = 24 * HOUR_MILLIS

    private fun getCurrentLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            //noinspection deprecation
            context.resources.configuration.locale
        }
    }

    /**
     * Get a textual representation of a time difference from a given [timeInput]
     */
    fun getTimeAgo(timeInput: Long): String? {
        var time = timeInput
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000
        }

        val now: Long = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return null
        }

        val loc = getCurrentLocale(MyApplication.getCtx())
        Log.d(Constants.TAG, loc.language)
        if (loc.language == "fr") {
            val diff: Long = now - time
            return when {
                diff < MINUTE_MILLIS -> {
                    "à l'instant"
                }
                diff < 2 * MINUTE_MILLIS -> {
                    "il y a une minute"
                }
                diff < 50 * MINUTE_MILLIS -> {
                    "il y a ${(diff / MINUTE_MILLIS)} minutes"
                }
                diff < 90 * MINUTE_MILLIS -> {
                    "il y a une heure"
                }
                diff < 24 * HOUR_MILLIS -> {
                    "il y a ${diff / HOUR_MILLIS} heures"
                }
                diff < 48 * HOUR_MILLIS -> {
                    "hier"
                }
                else -> {
                    "il y a ${(diff / DAY_MILLIS)} jours"
                }
            }
        }

        val diff: Long = now - time
        return when {
            diff < MINUTE_MILLIS -> {
                "just now"
            }
            diff < 2 * MINUTE_MILLIS -> {
                "a minute ago"
            }
            diff < 50 * MINUTE_MILLIS -> {
                "${(diff / MINUTE_MILLIS)} minutes ago"
            }
            diff < 90 * MINUTE_MILLIS -> {
                "an hour ago"
            }
            diff < 24 * HOUR_MILLIS -> {
                "${(diff / HOUR_MILLIS)} hours ago"
            }
            diff < 48 * HOUR_MILLIS -> {
                "yesterday"
            }
            else -> {
                "${(diff / DAY_MILLIS)} days ago"
            }
        }
    }
}
