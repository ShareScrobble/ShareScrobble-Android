package fr.sharescrobble.android.network.repositories

import fr.sharescrobble.android.network.ApiClient
import fr.sharescrobble.android.network.interfaces.ApiInterface

object ApiRepository {
    var apiInterface: ApiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
        private set
}