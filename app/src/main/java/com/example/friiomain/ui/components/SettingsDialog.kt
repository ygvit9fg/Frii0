
package com.example.friiomain.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.friiomain.data.DataStoreManager
import com.example.friiomain.data.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.platform.LocalContext

@Composable
fun SettingsDialog(
    navController: NavController,
    userDao: UserDao,
    currentUserEmail: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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
                    Text("Настройки", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Закрыть")
                    }
                }

                Spacer(Modifier.weight(1f))

                // Выйти / Удалить
                Column {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    withContext(Dispatchers.IO) {
                                        val ds = DataStoreManager(context)
                                        ds.clearAll() // удаляем session/email/name
                                    }
                                    withContext(Dispatchers.Main) {
                                        navController.navigate("login") {
                                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                        }
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Ошибка при выходе: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Выйти из аккаунта")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    withContext(Dispatchers.IO) {
                                        val user = userDao.getUserByEmail(currentUserEmail)
                                        if (user != null) {
                                            userDao.delete(user)
                                        }
                                        val ds = DataStoreManager(context)
                                        ds.clearAll()
                                    }

                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Аккаунт удален", Toast.LENGTH_LONG).show()
                                        navController.navigate("register") {
                                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                        }
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Ошибка при удалении: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Удалить аккаунт")
                    }
                }
            }
        }
    }
}
