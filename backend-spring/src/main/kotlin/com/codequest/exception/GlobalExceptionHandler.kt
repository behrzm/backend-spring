package com.codequest.exception

import io.github.microutils.kotlin.logging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

private val logger = KotlinLogging.logger {}

data class ErrorResponse(
    val status: Int,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn { "Validation error: ${ex.message}" }
        return ResponseEntity(
            ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                message = ex.message ?: "Invalid argument"
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(
        ex: IllegalStateException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn { "State error: ${ex.message}" }
        return ResponseEntity(
            ErrorResponse(
                status = HttpStatus.UNAUTHORIZED.value(),
                message = ex.message ?: "Unauthorized"
            ),
            HttpStatus.UNAUTHORIZED
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error(ex) { "Unexpected error occurred" }
        return ResponseEntity(
            ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                message = "Internal server error"
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}

