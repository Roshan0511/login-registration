package com.example.repository

import com.example.models.LoginModel
import com.example.models.UpdateUserModel
import com.example.security.JwtConfig
import com.example.service.CreateUserParams
import com.example.service.UserService
import com.example.utils.BaseResponse

class UserRepositoryImpl(
    private val userService: UserService
): UserRepository {
    override suspend fun registerUser(params: CreateUserParams): BaseResponse<Any> {
        return if (isEmailExist(params.email)) {
            BaseResponse.ErrorResponse(message = "Email already registered", isSuccess = false)
        } else {
            if (params.email.isEmpty()) {
                BaseResponse.ErrorResponse(message = "Email id not found", isSuccess = false)
            } else if (params.fullName.isEmpty()) {
                BaseResponse.ErrorResponse(message = "Name not found", isSuccess = false)
            } else if (params.password.isEmpty()) {
                BaseResponse.ErrorResponse(message = "Password not found", isSuccess = false)
            } else if (params.avatar.isEmpty()){
                BaseResponse.ErrorResponse(message = "Avatar not found", isSuccess = false)
            } else {
                val user = userService.registerUser(params)
                if (user != null) {
                    // Authenticate User
                    val token = JwtConfig.instance.createAccessToken(user.id)
                    user.authToken = token

                    BaseResponse.SuccessResponse(data = user, message = "Registration Successful", isSuccess = true)
                } else {
                    BaseResponse.ErrorResponse(isSuccess = false)
                }
            }
        }
    }

    override suspend fun loginUser(params: LoginModel): BaseResponse<Any> {
        return if (isEmailExist(params.email!!)) {
            val user = userService.loginUser(params)
            if (user != null) {
                // Authenticate User
                val token = JwtConfig.instance.createAccessToken(user.id)
                user.authToken = token

                BaseResponse.SuccessResponse(data = user, message = "Login Successful", isSuccess = true)
            } else {
                BaseResponse.ErrorResponse(message = "Invalid Credentials", isSuccess = false)
            }
        } else {
            if (params.email.isEmpty()) {
                BaseResponse.ErrorResponse(message = "Email not found", isSuccess = false)
            } else if (params.password!!.isEmpty()) {
                BaseResponse.ErrorResponse(message = "Password not found", isSuccess = false)
            } else {
                BaseResponse.ErrorResponse(message = "User Not Found", isSuccess = false)
            }
        }
    }



    override suspend fun deleteUser(params: LoginModel): BaseResponse<Any> {
        return if (isEmailExist(params.email!!)) {
            val user = userService.deleteUser(params)
            if (user == 1) {
                // Authenticate User
                BaseResponse.SuccessResponse(message = "User Deleted Successfully", isSuccess = true)
            } else {
                BaseResponse.ErrorResponse(message = "Invalid Credentials", isSuccess = false)
            }
        } else {
            BaseResponse.ErrorResponse(message = "User Not Found", isSuccess = false)
        }
    }


    override suspend fun getAllUsers(): BaseResponse<Any> {
        val list = userService.getAllUsers()

        return if (!list.isNullOrEmpty()) {
            BaseResponse.SuccessResponse(data = list, message = "Get Data Successfully", isSuccess = true)
        } else {
            BaseResponse.SuccessResponse(message = "No Records", isSuccess = false)
        }
    }



    override suspend fun updateUser(params: UpdateUserModel): BaseResponse<Any> {
        return if (searchUserById(params.id)) {
            val user = userService.updateUser(params)
            if (user != null) {
                BaseResponse.SuccessResponse(data = user, message = "User Updated Successfully", isSuccess = true)
            } else {
                BaseResponse.ErrorResponse(message = "Invalid Credentials", isSuccess = false)
            }
        } else {
            BaseResponse.ErrorResponse(message = "User Not Found", isSuccess = false)
        }
    }



    override suspend fun searchUsers(params: String): BaseResponse<Any> {
        val users = userService.searchUsers(params)

        return if (!users.isNullOrEmpty()) {
            BaseResponse.SuccessResponse(data = users, message = "Get Data Successfully", isSuccess = true)
        } else {
            BaseResponse.SuccessResponse(message = "No Records", isSuccess = false)
        }
    }


    private suspend fun isEmailExist(email: String): Boolean {
        return userService.findUserByEmail(email) != null
    }

    private suspend fun searchUserById(id: Int): Boolean {
        return userService.getUserById(id) != null
    }
}