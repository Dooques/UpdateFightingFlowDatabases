package com.dooques.fightingflow

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

suspend fun updateMoveData(
    client: HttpClient,
    backendUrl: String
){
    val url = "https://docs.google.com/spreadsheets/d/1n1M3CYJogaS0U7dHH5_--sWzi-_ZlfsbZnkwsaJmw8o/export?format=csv&gid=0"

    val googleResponse = try {
        val data = client.get(url)
        println("Successfully fetched moves from Google Sheets.")
        data
    } catch (e: Exception) {
        println("Failed to fetch moves from Google Sheets: ${e.message}")
        throw e
    }

    val contentType = googleResponse.headers["Content-Type"] ?: ""
    if (!contentType.contains("text/csv") &&
        !contentType.contains("application/csv")) {
        throw Exception("Google returned HTML instead of CSV. Is the sheet shared 'Anyone with the link'?")
    }
    val csvContent = googleResponse.bodyAsText()

    val lines = csvContent.split("\n")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
    val moveList = lines.drop(1).map { line ->
        val cells = line.split(",")
        val move = MoveDto(
            name = cells[0].trim(),
            notation = cells[1].trim(),
            type = cells[2].trim(),
            fighter = cells[3].trim(),
            game = cells[4].trim()
        )
        move
    }

    val jsonData = Json.encodeToString(moveList)

    println()
    println("**********************************")
    println("Data to send:")
    moveList.forEach { move ->
        println(
            "Name: ${move.name}, " +
                    "Notation: ${move.notation}, " +
                    "Type: ${move.type}, " +
                    "Character: ${move.fighter}, " +
                    "Game: ${move.game}")
    }
    println("**********************************")
    println()

    val response = client.post(backendUrl) {
        contentType(ContentType.Application.Json)
        setBody(jsonData)
    }

    if (response.status != HttpStatusCode.OK)
        throw Exception("Failed to upload moves: ${response.status}, response: ${response.bodyAsText()}")
}

@Serializable
class MoveDto(
    val name: String,
    val notation: String,
    val type: String,
    val fighter : String,
    val game: String
)
