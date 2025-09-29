package com.example.friiomain.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.example.friiomain.utils.bitmapToBase64
import com.example.friiomain.utils.base64ToBitmap

import androidx.hilt.navigation.compose.hiltViewModel
import com.example.friiomain.ui.profile.ProfileViewModel
import androidx.compose.runtime.getValue





@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileDialog(
    viewModel: ProfileViewModel,
    avatarBase64: String?,
    onAvatarChange: (String?) -> Unit,
    onDismiss: () -> Unit,
    onEditPreferences: () -> Unit = {}
) {
    val name by viewModel.userName.collectAsState()
    val email by viewModel.userEmail.collectAsState()
    val username by viewModel.userUsername.collectAsState()
    val preferences by viewModel.userPreferences.collectAsState()

    var showAllPrefsDialog by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Заголовок
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.Gray)
                        Spacer(Modifier.width(4.dp))
                        Text("Profile", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Аватарка
                AvatarSection(
                    name = name,
                    avatarBase64 = avatarBase64,
                    onAvatarChange = onAvatarChange
                )

                Spacer(Modifier.height(8.dp))

                // Username
                val displayUsername = if (username.isNotBlank()) "@$username" else ""
                Text(displayUsername, fontSize = 14.sp, color = Color.Gray)

                // Имя и почта
                Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(email, fontSize = 14.sp, color = Color.Gray)

                Spacer(Modifier.height(24.dp))

                // Preferences
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, contentDescription = "Prefs", tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Your Preferences", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val shown = preferences.take(3)
                    shown.forEach { PreferenceChip(it) }

                    if (preferences.size > 3) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.LightGray.copy(alpha = 0.4f))
                                .clickable { showAllPrefsDialog = true }
                                .size(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Show all preferences",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                if (showAllPrefsDialog) {
                    AlertDialog(
                        onDismissRequest = { showAllPrefsDialog = false },
                        confirmButton = {
                            TextButton(onClick = { showAllPrefsDialog = false }) {
                                Text("Close")
                            }
                        },
                        title = { Text("All Preferences", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                        text = {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                preferences.forEach { PreferenceChip(it) }
                            }
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                    val monthlyKm = 50.0
                    val monthlyCO2 = 7.2
                    // Экология
                    val treesSaved = (monthlyKm / 75.0 + monthlyCO2 / 15.0) / 2
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("$monthlyKm km", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                Text("Walked this month", fontSize = 12.sp, color = Color.Gray)
                            }
                            Column {
                                Text("$monthlyCO2 kg", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                Text("CO2 saved", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "🌱 You’ve saved the equivalent of ${"%.1f".format(treesSaved)} tree saplings this month",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Ачивки
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏅", fontSize = 18.sp)
                        Spacer(Modifier.width(4.dp))
                        Text("Recent Achievements", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.LightGray.copy(alpha = 0.3f))
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onEditPreferences,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Edit Preferences")
                }
            }
        }
    }
}


@Composable
private fun StatBlock(value: Int, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun PreferenceChip(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = Color.Black,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.LightGray.copy(alpha = 0.3f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}



