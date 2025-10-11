package com.example.friioserver

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDTO(
    val id: Long? = null,
    val type: String,
    val title: String,
    val body: String,
    val createdAt: Long,
    val seen: Boolean = false,
    val friendEmail: String? = null
)

fun Application.configureRouting() {

    val notifications = mutableListOf<NotificationDTO>()

    routing {
        // Проверка сервера
        get("/") {
            call.respondText("Friio Server is running 🚀")
        }

        // Получение всех уведомлений
        get("/api/notifications") {
            call.respond(notifications)
        }

        // Добавление нового уведомления
        post("/api/notifications") {
            val newNotification = call.receive<NotificationDTO>()
            val saved = newNotification.copy(id = (notifications.size + 1).toLong())
            notifications.add(saved)
            call.respond(saved)
        }
    }
}
