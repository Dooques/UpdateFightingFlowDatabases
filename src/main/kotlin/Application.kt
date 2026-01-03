package com.dooques.fightingflow

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    runBlocking {
        val client = HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 120_000
                connectTimeoutMillis = 20_000
                socketTimeoutMillis = 120_000
            }
        }
        val backendUrl = "http://host.docker.internal:8080"

        val runFighterUpdate = args.contains("--fighters")
        val runMoveUpdate = args.contains("--moves")


        if (runFighterUpdate) {
            try {
                println("Updating fighters...")
                fetchAndForwardFighterData(client, "$backendUrl/fighters")
                println("Successfully fetched fighters from Google Sheets.")
            } catch (e: Exception) {
                println("Failed to fetch fighters from Google Sheets: ${e.message}")
            }
        } else if (runMoveUpdate) {
            try {
                println("Updating moves...")
                updateMoveData(client, "$backendUrl/moves")
                println("Successfully updated moves.")
            } catch (e: Exception) {
                println("Failed to update moves: ${e.message}")
            }
        }
        client.close()
    }
    exitProcess(0)
}
