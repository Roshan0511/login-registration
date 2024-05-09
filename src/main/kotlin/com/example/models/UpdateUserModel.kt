package com.example.models

data class UpdateUserModel(
    val id: Int,
    val fullName: String?=null,
    val avatar: String?=null,
    val email: String?=null,
    val password: String?=null
)