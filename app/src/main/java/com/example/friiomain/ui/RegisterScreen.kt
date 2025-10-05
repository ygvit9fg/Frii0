package com.example.friiomain.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.friiomain.data.DataStoreManager
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.example.friiomain.data.AppDatabase
import com.example.friiomain.data.UserEntity
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



@Composable
fun RegisterScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Регистрация",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            var username by remember { mutableStateOf("") }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        val db = AppDatabase.getDatabase(context)
                        val userDao = db.userDao()

                        // Проверяем, есть ли уже аккаунт с таким email
                        val existingUser = withContext(Dispatchers.IO) {
                            userDao.getUserByEmail(email)
                        }

                        if (existingUser != null) {
                            // Если почта уже зарегистрирована — показываем ошибку
                            Toast.makeText(
                                context,
                                "Аккаунт с такой почтой уже существует",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            // Если почта свободна — регистрируем пользователя
                            val newUser = UserEntity(
                                email = email,
                                name = name,
                                username = "",
                                password = password,
                                preferences = ""
                            )

                            withContext(Dispatchers.IO) {
                                userDao.insert(newUser)
                            }

                            // сохраняем email и имя в DataStore
                            dataStoreManager.saveUserEmail(email)
                            dataStoreManager.saveUserName(name)
                            dataStoreManager.saveUserPreferences(emptyList())

                            // переход к экрану username
                            navController.navigate("username?name=$name&email=$email&password=$password")
                        }
                    }
                }
            ) {
                Text("Далее")
            }


            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                TextButton(onClick = { navController.navigate("login") }) {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

