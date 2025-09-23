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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileDialog(
    name: String,
    username: String,
    email: String,
    preferences: List<String>,
    friendsCount: Int = 12,
    walksCount: Int = 34,
    climateMatches: Int = 8,
    monthlyKm: Double = 50.0,
    monthlyCO2: Double = 7.2,
    onDismiss: () -> Unit,
    onEditPreferences: () -> Unit = {}
) {
    var showAllPrefsDialog by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.95f),
            exit = fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.95f)
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
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.Gray
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Profile", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Аватарка
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.take(2).uppercase(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("@$username", fontSize = 14.sp, color = Color.Gray)
                    Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(email, fontSize = 14.sp, color = Color.Gray)

                    Spacer(Modifier.height(24.dp))

                    // Статистика
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatBlock(friendsCount, "Friends", Color(0xFF1976D2))
                        StatBlock(walksCount, "Walks", Color(0xFF2E7D32))
                        StatBlock(climateMatches, "Climate Actions", Color(0xFF9C27B0))
                    }

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

                    Spacer(Modifier.height(24.dp))

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
                            "You’ve saved the equivalent of ${"%.1f".format(treesSaved)} tree saplings this month",
                            fontSize = 12.sp,
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


