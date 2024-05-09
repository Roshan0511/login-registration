package com.example.service

import com.example.db.DatabaseFactory.dbQuery
import com.example.db.table.UserTable
import com.example.models.LoginModel
import com.example.models.UpdateUserModel
import com.example.models.User
import com.example.security.hash
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class UserServiceImpl: UserService {
    override suspend fun registerUser(params: CreateUserParams): User? {
        var statement: InsertStatement<Number>? = null

        val encPassword = hash(params.password)

        dbQuery {
            statement = UserTable.insert {
                it[email] = params.email
                it[password] = encPassword
                it[fullName] = params.fullName
                it[avatar] = params.avatar
            }
        }

        return rowToUser(statement?.resultedValues?.get(0))
    }

    override suspend fun findUserByEmail(email: String): User? {
        val user = dbQuery {
            UserTable.select {
                UserTable.email.lowerCase().eq(email.lowercase())
            }.map { rowToUser(it) }.singleOrNull()
        }

        return user
    }


    override suspend fun getUserById(id: Int): User? {
        val user = dbQuery {
            UserTable.select {
                UserTable.id.eq(id)
            }.map { rowToUser(it) }.singleOrNull()
        }

        return user
    }

    override suspend fun loginUser(loginModel: LoginModel): User? {
        val user = dbQuery {
            UserTable.select {
                UserTable.email.lowerCase().eq(loginModel.email!!.lowercase()) and
                UserTable.password.eq(hash(loginModel.password!!))
            }.limit(1).map { rowToUser(it) }.singleOrNull()
        }

        return user
    }


    override suspend fun deleteUser(loginModel: LoginModel): Int {
        return dbQuery {
            UserTable.deleteWhere {
                UserTable.email.lowerCase().eq(loginModel.email!!.lowercase()) and
                UserTable.password.eq(hash(loginModel.password!!))
            }
        }
    }



    override suspend fun getAllUsers(): List<User?> {
        return transaction {
            UserTable.selectAll().orderBy(UserTable.id, SortOrder.ASC).mapNotNull { rowToUser(it) }
        }
    }





    override suspend fun updateUser(params: UpdateUserModel): User? {
        dbQuery {
            UserTable.update({ UserTable.id eq params.id }) {
                if (params.email != null) {
                    it[email] = params.email
                }
                if (params.password != null) {
                    it[password] = hash(params.password)
                }
                if (params.fullName != null) {
                    it[fullName] = params.fullName
                }
                if (params.avatar != null) {
                    it[avatar] = params.avatar
                }
            }
        }

        return getUserById(params.id)
    }



    override suspend fun searchUsers(searchText: String): List<User?> {
        val users = dbQuery {
            UserTable.select {
                (UserTable.fullName.lowerCase() like "%${searchText.lowercase()}%") or (UserTable.email.lowerCase() like "%${searchText.lowercase()}%")
            }.orderBy(UserTable.id, SortOrder.ASC).map { rowToUser(it) }
        }

        return users
    }




    private fun rowToUser(row: ResultRow?) : User? {
        return if(row == null) null
        else User(
            id = row[UserTable.id],
            fullName = row[UserTable.fullName],
            avatar = row[UserTable.avatar],
            email = row[UserTable.email],
            createdAt = row[UserTable.createdAt].toString()
        )
    }
}