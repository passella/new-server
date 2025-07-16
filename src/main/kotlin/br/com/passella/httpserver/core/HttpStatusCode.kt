package br.com.passella.httpserver.core

enum class HttpStatusCode(val code: Int, val text: String) {
    OK(200, "OK"),
    CREATED(201, "Created"),
    NO_CONTENT(204, "No Content"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    companion object {
        private val codeMap = HttpStatusCode.entries.associateBy { it.code }
        
        fun fromCode(code: Int): HttpStatusCode {
            return codeMap[code] ?: throw IllegalArgumentException("Unknown HTTP status code: $code")
        }
        
        fun getStatusText(code: Int): String {
            return codeMap[code]?.text ?: "Unknown"
        }
    }
}