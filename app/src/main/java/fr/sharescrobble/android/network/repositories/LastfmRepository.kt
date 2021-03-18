package fr.sharescrobble.android.network.repositories

import fr.sharescrobble.android.network.ApiClient
import fr.sharescrobble.android.network.interfaces.LastfmInterface

object LastfmRepository {
    var apiInterface: LastfmInterface = ApiClient.getApiClient().create(LastfmInterface::class.java)
        private set
}