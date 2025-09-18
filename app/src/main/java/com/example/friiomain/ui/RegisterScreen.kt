package com.example.friiomain.ui


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.friiomain.data.AppDatabase
import com.example.friiomain.data.UserEntity
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val userDao = db.userDao()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя") },
                modifier = Modifier.padding(8.dp)
            )
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

            Button(onClick = {
                scope.launch {
                    val existingUser = userDao.getUserByEmail(email)
                    if (existingUser != null) {
                        Toast.makeText(context, "Эта почта уже зарегистрирована", Toast.LENGTH_LONG).show()
                    } else {
                        val newUser = UserEntity(
                            name = name,
                            email = email,
                            password = password
                        )
                        userDao.insertUser(newUser)
                        Toast.makeText(context, "Регистрация успешна!", Toast.LENGTH_LONG).show()
                        navController.navigate("login")
                    }
                }
            }) {
                Text("Зарегистрироваться")
            }
        }
    }
}
