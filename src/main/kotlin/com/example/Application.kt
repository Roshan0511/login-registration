package com.example

import com.example.db.DatabaseFactory
import com.example.repository.UserRepositoryImpl
import com.example.routes.authRoutes
import com.example.security.configureSecurity
import com.example.service.UserServiceImpl
import com.example.socket.configureSockets
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun main(args: Array<String>) {
    DatabaseFactory.init()
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        jackson()
    }

    configureSecurity()


    val service = UserServiceImpl()
    val repository = UserRepositoryImpl(service)
    authRoutes(repository)

    configureSockets()

    routing {
        get("/test_url") {
            call.respond("Connection Established")
        }
    }
}
