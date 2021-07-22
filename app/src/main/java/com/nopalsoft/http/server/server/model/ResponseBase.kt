package com.nopalsoft.http.server.server.model

data class ResponseBase<T>(
    val status: Int = 0,
    val data: T? = null,
    val message: String = "Success"
)