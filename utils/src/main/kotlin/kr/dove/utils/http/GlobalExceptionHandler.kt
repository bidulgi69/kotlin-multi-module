package kr.dove.utils.http

import kr.dove.api.exceptions.OperationFailedException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebInputException

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(OperationFailedException::class)
    @ResponseBody fun handleOperationFailedException(exchange: ServerWebExchange, exception: OperationFailedException): Error {
        val path: String = extractRequestPath(exchange)
        logger.debug("OperationFailedException was Occurred in $path")
        return Error(
            path,
            exception.message ?: exception.localizedMessage,
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServerWebInputException::class)
    @ResponseBody fun handleServerWebInputException(exchange: ServerWebExchange, exception: ServerWebInputException): Error {
        val path: String = extractRequestPath(exchange)
        logger.debug("ServerWebInputException was Occurred in $path")
        return Error(
            path,
            exception.message,
            HttpStatus.BAD_REQUEST.value()
        )
    }

    private fun extractRequestPath(exchange: ServerWebExchange): String
        = exchange.request.path.pathWithinApplication().value()
}

data class Error(
    val path: String,
    val message: String,
    val status: Int,
)