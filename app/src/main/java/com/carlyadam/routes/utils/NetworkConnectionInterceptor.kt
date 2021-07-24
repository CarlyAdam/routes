package com.carlyadam.routes.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.carlyadam.routes.R
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NetworkConnectionInterceptor(
    private val context: Context
) : Interceptor {

    private val applicationContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!checkConnection())
            throw IOException(context.getString(R.string.no_connection))
        return chain.proceed(chain.request())
    }

    private fun checkConnection(): Boolean {
        val result: Boolean
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNet =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            activeNet.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNet.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNet.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return result
    }
}