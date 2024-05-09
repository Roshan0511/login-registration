package com.example.routes

import com.example.models.LoginModel
import com.example.models.UpdateUserModel
import com.example.repository.UserRepository
import com.example.service.CreateUserParams
import com.example.utils.BaseResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.authRoutes(repository: UserRepository) {
    routing {
        route("/auth") {
            post("/register") {
                val params = call.receive<CreateUserParams>()
                val result = repository.registerUser(params)
                call.respond(result.statusCode, result)
            }

            post("/login") {
                val params = call.receive<LoginModel>()
                if (params.email == null) {
                    call.respond(HttpStatusCode.OK,
                        BaseResponse.ErrorResponse<Any>(message = "Email field not found", isSuccess = false))
                } else if (params.password == null) {
                    call.respond(HttpStatusCode.OK,
                        BaseResponse.ErrorResponse<Any>(message = "Password field not found", isSuccess = false))
                } else {
                    val result = repository.loginUser(params)
                    call.respond(result.statusCode, result)
                }
            }

            authenticate {
                delete("/delete_user") {
                    val params = call.receive<LoginModel>()
                    val result = repository.deleteUser(params)
                    call.respond(result.statusCode, result)
                }

                get("/get_users") {
                    val result = repository.getAllUsers()
                    call.respond(result.statusCode, result)
                }

                patch("/update_user") {
                    val params = call.receive<UpdateUserModel>()
                    val result = repository.updateUser(params)
                    call.respond(result.statusCode, result)
                }

                get("/search_user/{text?}") {
                    val searchText = call.parameters["text"] ?: ""
                    val result = repository.searchUsers(searchText)
                    call.respond(result.statusCode, result)
                }
            }
        }
    }
}