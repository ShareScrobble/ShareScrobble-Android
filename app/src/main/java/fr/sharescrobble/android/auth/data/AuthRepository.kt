package fr.sharescrobble.android.auth.data

import android.util.Log
import com.google.gson.Gson
import fr.sharescrobble.android.auth.AuthService
import fr.sharescrobble.android.core.Globals
import fr.sharescrobble.android.network.ApiClient
import fr.sharescrobble.android.network.ApiInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {
    private var apiInterface: ApiInterface? = null;

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    fun getAuthUrl(): Unit? {
        return apiInterface?.getAuthUrl()?.enqueue(object : Callback<AuthModel> {
            override fun onResponse(call: Call<AuthModel>, response: Response<AuthModel>) {
                return AuthService.login(response.body())
            }

            override fun onFailure(call: Call<AuthModel>, t: Throwable) {
                // Display an error message
                Log.e(Globals.TAG, t.toString());
                TODO("Not yet implemented")
            }
        })
    }
}