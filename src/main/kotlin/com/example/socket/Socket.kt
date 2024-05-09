package com.example.socket

import com.example.models.Message
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import java.time.Duration
import java.util.Collections
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Application.configureSockets() {

    val sdf = SimpleDateFormat("dd MMM yyyy hh:mm:ss aa", Locale.ENGLISH)

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

        webSocket("/chat") {
            println("Adding user!")
            val objectMapper = ObjectMapper()

            val thisConnection = Connection(this)
            connections += thisConnection
            try {
                val initialMessage = Message("info", "You are connected! There are ${connections.count()} users here.")
                val initialMessageJson = objectMapper.writeValueAsString(initialMessage)
                send(initialMessageJson)
//                send("You are connected! There are ${connections.count()} users here.")
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    val textWithUsername = "[${thisConnection.name}]: $receivedText"
                    connections.forEach {connection ->
                        val messageToSend = if (textWithUsername.contains("active")) {
                            Message("info", "There are ${connections.count()} users here.")
                        } else {
                            Message("chat", textWithUsername, mapOf("time" to  sdf.format(Date()), "id" to connection.name))
                        }

                        val messageJson = objectMapper.writeValueAsString(messageToSend)
                        connection.session.send(messageJson)

                        if (textWithUsername.contains("bye")) {
                            connection.session.close(CloseReason(CloseReason.Codes.NORMAL, "User Exited the chat"))
                        }
                    }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                println("Removing $thisConnection!")
                connections -= thisConnection
            }
        }
    }
}
