package fr.sharescrobble.android.network.repositories

import fr.sharescrobble.android.network.ApiClient
import fr.sharescrobble.android.network.interfaces.UsersInterface

object UsersRepository {
    var apiInterface: UsersInterface = ApiClient.getApiClient().create(UsersInterface::class.java)
        private set
}