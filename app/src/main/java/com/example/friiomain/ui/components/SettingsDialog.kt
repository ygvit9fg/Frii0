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


@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    userDao: UserDao,
    userEmail: String
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

    // Загружаем пароль из БД при открытии
    LaunchedEffect(userEmail) {
        withContext(Dispatchers.IO) {
            val currentUser = userDao.getUserByEmail(userEmail)
            if (currentUser != null) {
                password = currentUser.password
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
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (editingPassword) {
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            scope.launch {
                                // обновляем пароль в DataStore
                                viewModel.updateUserPassword(newPassword)

                                // сохраняем новый пароль в Room
                                withContext(Dispatchers.IO) {
                                    userDao.getUserByEmail(userEmail)?.let {
                                        userDao.update(it.copy(password = newPassword))
                                    }
                                }
                                password = newPassword
                                editingPassword = false
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

                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(16.dp))

                // Выйти
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.clearAll()
                            Toast.makeText(context, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
                            onDismiss()
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
                                userDao.getUserByEmail(userEmail)?.let {
                                    userDao.delete(it)
                                }
                            }
                            viewModel.clearAll()
                            Toast.makeText(context, "Аккаунт удален", Toast.LENGTH_SHORT).show()
                            onDismiss()
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







