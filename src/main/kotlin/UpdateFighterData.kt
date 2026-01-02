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

suspend fun fetchAndForwardFighterData(
    client: HttpClient,
    backendUrl: String
) {
    val url = "https://docs.google.com/spreadsheets/d/18uzabWCeHdzwO1DQAX7roAh018nkUZ8z2GhyJUx7SEg/export?format=csv&gid=0"
    println(url)
    val googleResponse = try {
        client.get(url)
    } catch (e: Exception) {
        println("Failed to fetch fighters from Google Sheets: ${e.message}")
        throw e
    }

    val contentType = googleResponse.headers["Content-Type"] ?: ""
    if (!contentType.contains("text/csv") && !contentType.contains("application/csv")) {
        throw Exception("Google returned HTML instead of CSV. Is the sheet shared 'Anyone with the link'?")
    }
    val csvContent = googleResponse.bodyAsText()

    val lines = csvContent.split("\n")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
    val fighterList = lines.drop(1).map { line ->
        val cells = line.split(",")
        val fighter = FighterDto(
            name = cells[0].trim(),
            fightingStyle = cells[1].trim(),
            game = cells[2].trim(),
            imageUrl = cells[3].trim(),
        )
        fighter
    }

    val jsonData = Json.encodeToString(fighterList)


    println("""
        **********************************
            Data to send: ${fighterList.forEach {
                println(
                    "Name: ${it.name}, " +
                            "Image URL: ${it.imageUrl}, " +
                            "Fighting Style: ${it.fightingStyle}, " +
                            "Game: ${it.game}")
            }}
        **********************************
        """)

    val response = client.post(backendUrl) {
        contentType(ContentType.Application.Json)
        setBody(jsonData)
    }

    if (response.status != HttpStatusCode.OK)
        throw Exception("Failed to upload fighters: ${response.status}")
}

@Serializable
class FighterDto(
    val name: String,
    val fightingStyle: String,
    val game: String,
    val imageUrl: String,
)