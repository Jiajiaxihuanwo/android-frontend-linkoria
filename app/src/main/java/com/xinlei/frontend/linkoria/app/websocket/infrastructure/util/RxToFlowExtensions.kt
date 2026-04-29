package com.xinlei.frontend.linkoria.app.websocket.infrastructure.util

import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx2.asFlow

fun <T : Any> Observable<T>.toFlow(): Flow<T> {
    return this.asFlow();
}