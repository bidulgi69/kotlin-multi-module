package kr.dove.api.exceptions

class OperationFailedException: Throwable {
    constructor(message: String): super(message)
    constructor(cause: Throwable): super(cause)
}