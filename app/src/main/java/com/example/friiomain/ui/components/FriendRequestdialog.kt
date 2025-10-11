package com.example.friiomain.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.friiomain.data.FriendRequestEntity
import com.example.friiomain.utils.base64ToBitmap

@Composable
fun FriendRequestsDialog(
    requests: List<FriendRequestEntity>,
    onAccept: (FriendRequestEntity) -> Unit,
    onDecline: (FriendRequestEntity) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {},
        title = { Text("Заявки в друзья 👥") },
        text = {
            if (requests.isEmpty()) {
                Text("У вас пока нет заявок")
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    requests.forEach { request ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Аватарка
                                val avatar = base64ToBitmap(request.avatarBase64 ?: "")
                                if (avatar != null) {
                                    Image(
                                        bitmap = avatar.asImageBitmap(),
                                        contentDescription = "Avatar",
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(request.username.take(1).uppercase())
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(request.username)
                            }
                            Row {
                                IconButton(onClick = { onAccept(request) }) {
                                    Icon(Icons.Default.Check, contentDescription = "Принять")
                                }
                                IconButton(onClick = { onDecline(request) }) {
                                    Icon(Icons.Default.Close, contentDescription = "Отклонить")
                                }
                            }
                        }
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Закрыть")
            }
        }
    )
}
