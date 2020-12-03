package atwork.controller

class JWTToken(token: String) {

    val value: String = if (token.contains("bearer", true)) {
        token.replace("bearer", "", true).trim()
    } else {
        token.trim()
    }

    override fun toString(): String = value
}
