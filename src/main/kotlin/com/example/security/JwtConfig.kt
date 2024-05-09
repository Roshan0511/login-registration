package com.example.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.config.tryGetString
import io.ktor.server.config.yaml.YamlConfig
import java.util.Calendar
import java.util.Date

class JwtConfig private constructor(secret: String) {
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier = JWT
        .require(algorithm)
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .build()

    companion object {
        private val yaml = YamlConfig("application.yaml")
        private val ISSUER = yaml?.tryGetString("jwt.issuer")
        private val AUDIENCE = yaml?.tryGetString("jwt.audience")
        val CLAIM = yaml?.tryGetString("jwt.claim")

        lateinit var instance: JwtConfig
            private set

        fun initialize(secret: String) {
            synchronized(this) {
                if (!this::instance.isInitialized) {
                    instance = JwtConfig(secret)
                }
            }
        }
    }


    fun createAccessToken(id: Int): String = JWT
        .create()
        .withIssuer(ISSUER)
        .withIssuedAt(getCurrentTime())
        .withExpiresAt(expireDate())
        .withAudience(AUDIENCE)
        .withClaim(CLAIM, id)
        .sign(algorithm)


    private fun getCurrentTime() : Date {
        return Date(System.currentTimeMillis())
    }

    private fun expireDate() : Date {
        val calendar = Calendar.getInstance()
        calendar.time = Date(System.currentTimeMillis())
        calendar.add(Calendar.DATE, 1)
        // 86400000
        return calendar.time
    }
}