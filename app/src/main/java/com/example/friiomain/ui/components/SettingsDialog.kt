package com.example.friiomain.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.friiomain.ui.profile.ProfileViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.friiomain.data.UserDao
import com.example.friiomain.data.UserEntity
import androidx.navigation.NavController

import kotlinx.coroutines.withContext


@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    userDao: UserDao,
    userEmail: String,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // читаем данные из ViewModel
    val name by viewModel.userName.collectAsState()
    val email by viewModel.userEmail.collectAsState()

    // локальное состояние для пароля (его нет в DataStore, поэтому подгружаем из Room)
    var password by remember { mutableStateOf("") }

    // состояния для редактирования
    var editingName by remember { mutableStateOf(false) }
    var editingPassword by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(name) }
    var newPassword by remember { mutableStateOf("") }

    var notificationsEnabled by remember { mutableStateOf(true) }
    var interfaceEnabled by remember { mutableStateOf(false) }


    // Загружаем пароль из БД при открытии
    LaunchedEffect(userEmail) {
        if (userEmail.isNotBlank()) {
            withContext(Dispatchers.IO) {
                userDao.getUserByEmail(userEmail)?.let {
                    password = it.password
                }
            }
        }
    }


    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Заголовок
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Настройки", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Закрыть")
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Email (не редактируется)
                Text("Почта: $email")

                Spacer(Modifier.height(8.dp))

                // Имя
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (editingName) {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            viewModel.updateUserName(newName)
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    userDao.getUserByEmail(userEmail)?.let {
                                        userDao.update(it.copy(name = newName))
                                    }
                                }
                            }
                            editingName = false
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Сохранить")
                        }
                    } else {
                        Text("Имя: $name", Modifier.weight(1f))
                        IconButton(onClick = {
                            newName = name
                            editingName = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Пароль
                // Пароль
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (editingPassword) {
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Новый пароль") }
                        )
                        IconButton(onClick = {
                            scope.launch {
                                if (newPassword.isNotBlank()) {
                                    // обновляем в Room
                                    withContext(Dispatchers.IO) {
                                        userDao.updatePassword(userEmail, newPassword)
                                    }

                                    // обновляем в DataStore / ViewModel
                                    viewModel.updateUserPassword(newPassword)

                                    // локальное состояние
                                    password = newPassword
                                    editingPassword = false

                                    Toast.makeText(context, "Пароль обновлён", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Пароль не может быть пустым",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Сохранить")
                        }
                    } else {
                        Text("Пароль: ${"*".repeat(password.length)}", Modifier.weight(1f))
                        IconButton(onClick = {
                            newPassword = password
                            editingPassword = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))


                // Уведомления
                        @Composable
                        fun NotificationRow(
                            title: String,
                            checked: Boolean,
                            onCheckedChange: (Boolean) -> Unit
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Уведомления", modifier = Modifier.weight(1f))
                                Switch(
                                    checked = notificationsEnabled,
                                    onCheckedChange = { notificationsEnabled = it }
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Spacer(Modifier.height(16.dp))

                        //тема
                        @Composable
                        fun SettingSwitchRow(
                            title: String,
                            checked: Boolean,
                            onCheckedChange: (Boolean) -> Unit
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            ) {
                                Text(title, modifier = Modifier.weight(1f))
                                Switch(
                                    checked = checked,
                                    onCheckedChange = onCheckedChange
                                )
                            }
                        }

                        SettingSwitchRow(
                            title = "Уведомления",
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it }
                        )

                        Divider()

                        SettingSwitchRow(
                            title = "Тёмная тема",
                            checked = interfaceEnabled,
                            onCheckedChange = { interfaceEnabled = it }
                        )


                        // Выйти
                        Button(
                            onClick = {
                                scope.launch {
                                    // удаляем/очищаем в БД в IO-потоке
                                    withContext(Dispatchers.IO) {

                                    }

                                    // чистим DataStore / ViewModel / сессию
                                    viewModel.clearAll()

                                    Toast.makeText(
                                        context,
                                        "Вы вышли из аккаунта",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    onDismiss()

                                    // Навигация на экран логина, очищаем стек
                                    navController.navigate("login") {
                                        popUpTo("home/{email}/{name}") { inclusive = true }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Выйти из аккаунта")
                        }

                        Spacer(Modifier.height(12.dp))

                        // Удалить аккаунт
                        Button(
                            onClick = {
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        userDao.getUserByEmail(userEmail)
                                            ?.let { userDao.delete(it) }
                                    }
                                    viewModel.clearAll()
                                    Toast.makeText(context, "Аккаунт удален", Toast.LENGTH_SHORT)
                                        .show()
                                    onDismiss()
                                    navController.navigate("login") {
                                        popUpTo("home/{email}/{name}") { inclusive = true }
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











