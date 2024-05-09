package com.example.security

import io.ktor.server.auth.Principal

data class UserIdPrincipalForUser(val id: Int): Principal
