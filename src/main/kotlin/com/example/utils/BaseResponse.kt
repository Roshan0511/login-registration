package com.example.utils

import io.ktor.http.HttpStatusCode

sealed class BaseResponse<T>(
    val statusCode: HttpStatusCode = HttpStatusCode.OK
) {
    data class SuccessResponse<T>(
        val isSuccess: Boolean,
        val message: String? = null,
        val data: T? = null
    ) : BaseResponse<T>()

    data class ErrorResponse<T>(
        val isSuccess: Boolean,
        val message: String? = null,
        val data: T? = null
    ) : BaseResponse<T>()
}