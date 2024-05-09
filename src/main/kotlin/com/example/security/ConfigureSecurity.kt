package com.example.security

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.application.Application
import io.ktor.server.application.install

fun Application.configureSecurity() {
    JwtConfig.initialize("My-Story-App")
    install(Authentication) {
        jwt {
            verifier(JwtConfig.instance.verifier)
            validate {
                val claim = it.payload.getClaim(JwtConfig.CLAIM).asInt()
                if (claim != null) {
                    UserIdPrincipalForUser(claim)
                } else {
                    null
                }
            }
        }
    }
}