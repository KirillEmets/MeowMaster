package com.kirillyemets.catapp.mylibs

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update

// Created by Kyryl Yemets

/**
 * One class to rule them all. Represents the result of a network operation, encapsulating the states of success, error, and loading.
 * @param <T> The type of data held in the result.
 * @param error The throwable representing an error state, or null if no error occurred.
 * @param data The data result, or null if the operation is not successful.
 * @param loading Indicates whether the operation is in a loading state.
 */
data class Edl<T>(
    val error: Throwable? = null,
    val data: T? = null,
    val loading: Boolean = false
) {

    /**
     * Determines if the operation is successful, based on the presence of data.
     */
    val isSuccess: Boolean
        get() = data != null

    /**
     * Determines if the operation is in an error state, based on the presence of an error.
     */
    val isError
        get() = error != null

    /**
     * Maps the data in this result using the provided mapper function and returns a new Edl instance with the mapped data.
     *
     * @param mapper The function to map the data.
     * @return A new Edl instance with the mapped data.
     */
    fun <R> mapData(mapper: (T) -> R): Edl<R> = Edl(
        error = error,
        data = data?.let(mapper),
        loading = loading
    )

    companion object {
        /**
         * Creates an Edl instance representing a successful result with the provided data.
         *
         * @param data The successful data.
         * @return An Edl instance representing a successful result.
         */
        fun <T> success(data: T) = Edl(data = data)

        /**
         * Creates an Edl instance representing a failure result with the provided error.
         *
         * @param error The error that occurred.
         * @return An Edl instance representing a failure result.
         */
        fun <T> failure(error: Throwable) = Edl<T>(error = error)

        /**
         * Creates an Edl instance representing a loading state.
         *
         * @return An Edl instance representing a loading state.
         */
        fun <T> loading() = Edl<T>(loading = true)
    }
}

fun <T> Result<T>.toEdl() = Edl(exceptionOrNull(), getOrNull())

/**
 * Collects values from the given Flow of type [T] and updates the provided MutableStateFlow of type [Edl] with the collected data.
 * This function handles loading, data, and error states, reflecting them in the MutableStateFlow.
 *
 * @param edlFlow The MutableStateFlow to update with the collected [Edl] instances.
 * @throws Throwable If an error occurs during collection, it will be reflected in the [Edl] state.
 */
suspend inline fun <reified T> Flow<T>.collectToEdlFlow(edlFlow: MutableStateFlow<Edl<T>>) {
    this.catch { error ->
        Log.e("Edl", "threw error while collecting for ${T::class}", error)
        edlFlow.update { prev -> prev.copy(loading = false, error = error) }
    }.collect { data ->
        edlFlow.update { prev -> prev.copy(loading = false, data = data) }
    }
}

/**
 * Collects values from the given Flow of type [T], applies a transformation to each value using the provided
 * [transform] function, and updates the provided MutableStateFlow of type [Edl] with the transformed data.
 * This function handles loading, data, and error states, reflecting them in the MutableStateFlow.
 *
 * @param edlFlow The MutableStateFlow to update with the collected [Edl] instances.
 * @param transform A transformation function to be applied to each collected data item before updating the [Edl] state.
 * @throws Throwable If an error occurs during collection, it will be logged and reflected in the [Edl] state.
 */
suspend inline fun <reified T> Flow<T>.collectToEdlFlow(
    edlFlow: MutableStateFlow<Edl<T>>,
    crossinline transform: (T) -> T
) {
    this.catch { error ->
        Log.e("Edl", "Threw error while collecting for ${T::class}", error)
        edlFlow.update { prev -> prev.copy(loading = false, error = error) }
    }.collect { data ->
        edlFlow.update { prev -> prev.copy(loading = false, data = transform(data)) }
    }
}

/**
 * Combines two [Edl] instances of types [T1] and [T2] into a new [Edl] instance of type [R] using the provided
 * [reduce] function. This function handles combining loading, data, and error states from the input [Edl] instances.
 *
 * @param edl1 The first [Edl] instance of type [T1].
 * @param edl2 The second [Edl] instance of type [T2].
 * @param reduce A function that takes the data from [edl1] and [edl2] and produces a result of type [R].
 * @return An [Edl] instance of type [R] that represents the combined state of [edl1] and [edl2].
 */
fun <T1, T2, R> combineEdl(
    edl1: Edl<T1>,
    edl2: Edl<T2>,
    reduce: (T1, T2) -> R
): Edl<R> {
    val error = edl1.error ?: edl2.error
    if (error != null)
        return Edl.failure(error)

    val loading = edl1.loading || edl2.loading
    if (loading)
        return Edl.loading()

    return Edl.success(
        reduce(edl1.data!!, edl2.data!!)
    )
}

/**
 * Combines three [Edl] instances of types [T1], [T2], and [T3] into a new [Edl] instance of type [R] using the provided
 * [reduce] function. This function handles combining loading, data, and error states from the input [Edl] instances.
 *
 * @param edl1 The first [Edl] instance of type [T1].
 * @param edl2 The second [Edl] instance of type [T2].
 * @param edl3 The third [Edl] instance of type [T3].
 * @param reduce A function that takes the data from [edl1], [edl2], and [edl3] and produces a result of type [R].
 * @return An [Edl] instance of type [R] that represents the combined state of [edl1], [edl2], and [edl3].
 */
fun <T1, T2, T3, R> combineEdl(
    edl1: Edl<T1>,
    edl2: Edl<T2>,
    edl3: Edl<T3>,
    reduce: (T1, T2, T3) -> R
): Edl<R> {
    val error = edl1.error ?: edl2.error ?: edl3.error
    if (error != null)
        return Edl.failure(error)

    val loading = edl1.loading || edl2.loading || edl3.loading
    if (loading)
        return Edl.loading()

    return Edl.success(
        reduce(edl1.data!!, edl2.data!!, edl3.data!!)
    )
}