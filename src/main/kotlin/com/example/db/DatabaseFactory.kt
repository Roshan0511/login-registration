package com.example.db

import com.example.db.table.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.tryGetString
import io.ktor.server.config.yaml.YamlConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        Database.connect(hikary())
        transaction {
            SchemaUtils.create(UserTable)
        }
    }

    private fun hikary() : HikariDataSource {
        val yaml = YamlConfig("application.yaml")

        val config = HikariConfig()
        config.driverClassName = yaml?.tryGetString("database.driverClassName")
        config.jdbcUrl = yaml?.tryGetString("database.jdbcUrl")
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = yaml?.tryGetString("database.transactionIsolation")
        config.validate()
        return HikariDataSource(config)
    }


    suspend fun <T> dbQuery(block: ()-> T): T = withContext(Dispatchers.IO) {
        transaction {
            block()
        }
    }
}