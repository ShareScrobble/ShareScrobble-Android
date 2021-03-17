package fr.sharescrobble.android.network.repositories

import fr.sharescrobble.android.network.ApiClient
import fr.sharescrobble.android.network.interfaces.ScrobbleInterface

object ScrobbleRepository {
    var apiInterface: ScrobbleInterface = ApiClient.getApiClient().create(ScrobbleInterface::class.java)
        private set
}