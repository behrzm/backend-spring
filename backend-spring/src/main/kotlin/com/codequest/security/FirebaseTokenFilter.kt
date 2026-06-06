package com.codequest.security

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import io.github.microutils.kotlin.logging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

private val logger = KotlinLogging.logger {}

@Component
class FirebaseTokenFilter(
    private val firebaseAuth: FirebaseAuth
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = extractToken(request)

            if (token != null) {
                try {
                    val decodedToken = firebaseAuth.verifyIdToken(token)
                    val uid = decodedToken.uid
                    SecurityContext.setUserId(uid)
                    logger.debug { "User authenticated: $uid" }
                } catch (e: FirebaseAuthException) {
                    logger.warn { "Invalid Firebase token: ${e.message}" }
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token")
                    return
                }
            }

            filterChain.doFilter(request, response)
        } finally {
            SecurityContext.clear()
        }
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val authHeader = request.getHeader("Authorization") ?: return null
        return if (authHeader.startsWith("Bearer ")) {
            authHeader.substring(7)
        } else {
            null
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val requestUri = request.requestURI
        return requestUri.contains("swagger") ||
                requestUri.contains("docs") ||
                requestUri == "/health"
    }
}

