package com.kirillyemets.catapp.mylibs

// Created by Kyryl Yemets

/**
 * Represents the various states of a network request.
 * This enum class defines states such as Loading, Success, and Failure,
 * which indicate the different stages a network call can be in.
 * The state Nothing is used to indicate that there is no specific state.
 */
enum class RequestState {
    /**
     * Represents the absence of a specific network request state.
     */
    Nothing,

    /**
     * Represents the state when a network request is in progress or loading.
     */
    Loading,

    /**
     * Represents the state when a network request has been successfully completed.
     */
    Success,

    /**
     * Represents the state when a network request has encountered a failure or error.
     */
    Failure
}


fun<T> Result<T>.toRequestState() = when {
    isSuccess -> RequestState.Success
    else -> RequestState.Failure
}