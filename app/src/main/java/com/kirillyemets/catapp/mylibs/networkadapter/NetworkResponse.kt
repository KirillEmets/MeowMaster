package com.kirillyemets.catapp.mylibs.networkadapter

import java.io.IOException

typealias EmptyResponse = NetworkResponse<Unit, Unit>
typealias GeneralResponse<T> = NetworkResponse<T, Unit>

sealed class NetworkResponse<out T : Any, out U : Any> {
    /**
     * Success response with body
     */
    data class Success<T : Any>(val body: T?) : NetworkResponse<T, Nothing>()

    /**
     * Failure response with body
     */
    data class ApiError<U : Any>(val body: U, val code: Int) : NetworkResponse<Nothing, U>()

    /**
     * Network error
     */
    data class NetworkError(val error: IOException) : NetworkResponse<Nothing, Nothing>()

    /**
     * For example, json parsing error
     */
    data class UnknownError(val error: Throwable) : NetworkResponse<Nothing, Nothing>()
}

fun <T : Any, R> GeneralResponse<T>.toResult(successMap: (T) -> R): Result<R> = when (this) {
    is NetworkResponse.Success -> this.body?.let {
        try {
            Result.success(successMap(it))
        } catch (e: Exception) {
            Result.failure(e)
        }
    } ?: Result.failure(Throwable("Trying to get body from an Empty Response"))

    is NetworkResponse.ApiError -> Result.failure(Throwable(this.toString()))
    is NetworkResponse.NetworkError -> Result.failure(this.error)
    is NetworkResponse.UnknownError -> Result.failure(this.error)
}

fun EmptyResponse.toEmptyResult(): Result<Unit> = when (this) {
    is NetworkResponse.Success -> Result.success(Unit)
    is NetworkResponse.ApiError -> Result.failure(Throwable(this.toString()))
    is NetworkResponse.NetworkError -> Result.failure(this.error)
    is NetworkResponse.UnknownError -> Result.failure(this.error)
}