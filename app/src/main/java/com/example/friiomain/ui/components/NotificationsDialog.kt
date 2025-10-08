package com.example.friiomain.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.friiomain.ui.profile.NotificationsViewModel

@Composable
fun NotificationsDialog(
    onDismiss: () -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState(initial = emptyList())

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row {
                TextButton(onClick = { viewModel.clearNotifications() }) { Text("Очистить") }
                TextButton(onClick = onDismiss) { Text("Закрыть") }
            }
        },
        text = {
            if (notifications.isEmpty()) {
                Text("У вас пока нет уведомлений 📭", modifier = Modifier.padding(8.dp))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(notifications) { notif ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when (notif.type) {
                                    "weather" -> Color(0xFFD0E8FF)
                                    "streak" -> Color(0xFFFFD6D6)
                                    "news" -> Color(0xFFD6FFD6)
                                    else -> Color.LightGray
                                }
                            )
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(text = notif.title, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text(text = notif.message)
                            }
                        }
                    }
                }
            }
        }
    )
}

