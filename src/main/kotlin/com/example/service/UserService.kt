package com.example.service

import com.example.models.LoginModel
import com.example.models.UpdateUserModel
import com.example.models.User

interface UserService {
    suspend fun registerUser(params: CreateUserParams) : User?

    suspend fun findUserByEmail(email: String): User?
    suspend fun getUserById(id: Int): User?

    suspend fun loginUser(loginModel: LoginModel): User?

    suspend fun deleteUser(loginModel: LoginModel): Int?

    suspend fun getAllUsers() : List<User?>?

    suspend fun updateUser(params: UpdateUserModel) : User?

    suspend fun searchUsers(searchText: String): List<User?>?
}