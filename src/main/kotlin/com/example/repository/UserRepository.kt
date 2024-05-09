package com.example.repository

import com.example.models.LoginModel
import com.example.models.UpdateUserModel
import com.example.service.CreateUserParams
import com.example.utils.BaseResponse

interface UserRepository {
    suspend fun registerUser(params: CreateUserParams): BaseResponse<Any>
    suspend fun loginUser(params: LoginModel): BaseResponse<Any>
    suspend fun deleteUser(params: LoginModel): BaseResponse<Any>

    suspend fun getAllUsers(): BaseResponse<Any>

    suspend fun updateUser(params: UpdateUserModel) : BaseResponse<Any>

    suspend fun searchUsers(params: String) : BaseResponse<Any>
}