package com.maxim.poteryashki.lost.controller

import com.maxim.poteryashki.lost.dto.ErrorResponse

fun Exception.toResponse() = ErrorResponse(
    message = this.message ?: "Unknown error",
    code = this::class.simpleName ?: "UnknownError",
    stacktrace = this.stackTraceToString()
)