package com.example.friiomain.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.friiomain.data.AppDatabase
import com.example.friiomain.data.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val userDao = db.userDao()
    val dataStoreManager = DataStoreManager(context)
    val scope = rememberCoroutineScope() // ✅ добавили

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                modifier = Modifier.padding(8.dp)
            )

            Button(
                onClick = {
                    scope.launch(Dispatchers.IO) { // ✅ теперь scope есть
                        val user = userDao.login(email, password)
                        if (user != null) {
                            dataStoreManager.saveUserEmail(user.email) // сохраняем email
                            withContext(Dispatchers.Main) {
                                navController.navigate("home/${user.email}") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                message = "Неправильный логин или пароль"
                            }
                        }
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Войти")
            }

            TextButton(onClick = { navController.navigate("register") }) {
                Text("Нет аккаунта? Зарегистрироваться")
            }

            if (message.isNotEmpty()) {
                Text(message, modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}


