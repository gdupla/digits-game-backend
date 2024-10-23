package digits.shared

sealed class BaseException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class InvalidArgument(message: String, cause: Throwable? = null) : BaseException(message = message, cause = cause)

class InvalidState(message: String, cause: Throwable? = null) : BaseException(message = message, cause = cause)

class Unauthorized(message: String, cause: Throwable? = null) : BaseException(message = message, cause = cause)

class AccessDenied(message: String, cause: Throwable? = null) : BaseException(message = message, cause = cause)

class NotFound(message: String, cause: Throwable? = null) : BaseException(message = message, cause = cause)