package fr.sharescrobble.android.network.repositories

import fr.sharescrobble.android.network.ApiClient
import fr.sharescrobble.android.network.interfaces.AuthInterface

object AuthRepository {
    var apiInterface: AuthInterface = ApiClient.getApiClient().create(AuthInterface::class.java)
        private set
}