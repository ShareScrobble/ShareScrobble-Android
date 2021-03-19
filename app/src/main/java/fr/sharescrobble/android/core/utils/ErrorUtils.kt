package fr.sharescrobble.android.core.utils

import fr.sharescrobble.android.network.ApiClient
import fr.sharescrobble.android.network.models.ErrorModel
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException


object ErrorUtils {
    fun parseError(response: Response<*>?): ErrorModel? {
        val converter: Converter<ResponseBody, ErrorModel> = ApiClient.getApiClient()
            .responseBodyConverter(ErrorModel::class.java, arrayOfNulls<Annotation>(0))
        return try {
            converter.convert(response?.errorBody())
        } catch (e: IOException) {
            return ErrorModel(0, "", "")
        }
    }
}