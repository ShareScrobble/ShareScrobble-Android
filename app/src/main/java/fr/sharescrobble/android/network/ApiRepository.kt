package fr.sharescrobble.android.network

object ApiRepository {
    var apiInterface: ApiInterface? = null
        private set

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }
}