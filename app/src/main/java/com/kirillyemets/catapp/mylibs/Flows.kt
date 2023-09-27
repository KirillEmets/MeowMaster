package com.kirillyemets.catapp.mylibs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import java.io.Serializable

// Created by Anton Proshchenko

fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R
): Flow<R> = combine(
    combine(flow, flow2, flow3, ::Triple),
    combine(flow4, flow5, flow6, ::Triple)
) { t1, t2 ->
    transform(
        t1.first,
        t1.second,
        t1.third,
        t2.first,
        t2.second,
        t2.third
    )
}

fun <T1, T2, T3, T4, T5, T6, T7, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7) -> R
): Flow<R> = combine(
    combine(flow, flow2, flow3, ::Triple),
    combine(flow4, flow5, flow6, flow7, ::Quad)
) { t1, t2 ->
    transform(
        t1.first,
        t1.second,
        t1.third,
        t2.first,
        t2.second,
        t2.third,
        t2.fourth
    )
}

data class Quad<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
) : Serializable

inline fun <reified T1, R> ViewModel.combineStateFlow(
    flow1: StateFlow<T1>,
    scope: CoroutineScope = viewModelScope,
    started: SharingStarted = SharingStarted.Eagerly,
    crossinline transform: (T1) -> R
): StateFlow<R> = combine(flow1) { (o1) ->
    transform.invoke(o1)
}.stateIn(scope, started, transform.invoke(flow1.value))

fun <T1, T2, R> ViewModel.combineStateFlow(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    scope: CoroutineScope = viewModelScope,
    started: SharingStarted = SharingStarted.Eagerly,
    transform: (T1, T2) -> R
): StateFlow<R> = combine(flow1, flow2) { o1, o2 ->
    transform.invoke(o1, o2)
}.stateIn(scope, started, transform.invoke(flow1.value, flow2.value))

fun <T1, T2, T3, R> ViewModel.combineStateFlow(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    scope: CoroutineScope = viewModelScope,
    started: SharingStarted = SharingStarted.Eagerly,
    transform: (T1, T2, T3) -> R
): StateFlow<R> = combine(flow1, flow2, flow3) { o1, o2, o3 ->
    transform.invoke(o1, o2, o3)
}.stateIn(scope, started, transform.invoke(flow1.value, flow2.value, flow3.value))

fun <T1, T2, T3, T4, R> ViewModel.combineStateFlow(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    flow4: StateFlow<T4>,
    scope: CoroutineScope = viewModelScope,
    started: SharingStarted = SharingStarted.Eagerly,
    transform: (T1, T2, T3, T4) -> R
): StateFlow<R> = combine(flow1, flow2, flow3, flow4) { o1, o2, o3, o4 ->
    transform.invoke(o1, o2, o3, o4)
}.stateIn(scope, started, transform.invoke(flow1.value, flow2.value, flow3.value, flow4.value))

fun <T1, T2, T3, T4, T5, R> ViewModel.combineStateFlow(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    flow4: StateFlow<T4>,
    flow5: StateFlow<T5>,
    scope: CoroutineScope = viewModelScope,
    started: SharingStarted = SharingStarted.Eagerly,
    transform: (T1, T2, T3, T4, T5) -> R
): StateFlow<R> = combine(flow1, flow2, flow3, flow4, flow5) { o1, o2, o3, o4, o5 ->
    transform.invoke(o1, o2, o3, o4, o5)
}.stateIn(scope, started, transform.invoke(flow1.value, flow2.value, flow3.value, flow4.value, flow5.value))

fun <T1, T2, T3, T4, T5, T6, R> ViewModel.combineStateFlow(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    flow4: StateFlow<T4>,
    flow5: StateFlow<T5>,
    flow6: StateFlow<T6>,
    scope: CoroutineScope = viewModelScope,
    started: SharingStarted = SharingStarted.Eagerly,
    transform: (T1, T2, T3, T4, T5, T6) -> R
): StateFlow<R> = combine(flow1, flow2, flow3, flow4, flow5, flow6) { o1, o2, o3, o4, o5, o6 ->
    transform.invoke(o1, o2, o3, o4, o5, o6)
}.stateIn(scope, started, transform.invoke(flow1.value, flow2.value, flow3.value, flow4.value, flow5.value, flow6.value))

fun <T1, T2, T3, T4, T5, T6, T7, R> ViewModel.combineStateFlow(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    flow4: StateFlow<T4>,
    flow5: StateFlow<T5>,
    flow6: StateFlow<T6>,
    flow7: StateFlow<T7>,
    scope: CoroutineScope = viewModelScope,
    started: SharingStarted = SharingStarted.Eagerly,
    transform: (T1, T2, T3, T4, T5, T6, T7) -> R
): StateFlow<R> = combine(flow1, flow2, flow3, flow4, flow5, flow6, flow7) { o1, o2, o3, o4, o5, o6, o7 ->
    transform.invoke(o1, o2, o3, o4, o5, o6, o7)
}.stateIn(scope, started, transform.invoke(flow1.value, flow2.value, flow3.value, flow4.value, flow5.value, flow6.value, flow7.value))
