package com.example.mycomposeapp.core.data.common

import com.example.mycomposeapp.core.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HandleResponse @Inject constructor() {

    fun <T> safeApiCall(apiCall: suspend () -> T): Flow<Resource<T>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(apiCall()))
        } catch (e: Exception) {
            emit(
                when (e) {
                    is UnknownHostException -> Resource.Error("No Internet Connection")
                    is HttpException -> Resource.Error("Server Error: ${e.code()}")
                    else -> Resource.Error("Unexpected Error: ${e.localizedMessage}")
                }
            )
        }

    }

}