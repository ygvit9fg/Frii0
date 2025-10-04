package com.example.friiomain.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.friiomain.data.DataStoreManager
import kotlinx.coroutines.launch

@Composable
fun UsernameScreen(navController: NavController, name: String, email: String, password: String) {
    var username by remember { mutableStateOf("") }

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
                text = "Придумайте юзер",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                scope.launch {
                    val db = com.example.friiomain.data.AppDatabase.getDatabase(context)
                    val userDao = db.userDao()

                    // 1️⃣ обновляем username в БД
                    userDao.updateUsernameByEmail(email, username)

                    // 2️⃣ сохраняем username в DataStore
                    dataStoreManager.saveUserUsername(username)

                    // 3️⃣ переходим на следующий экран
                    navController.navigate("preferences/$email/$password/$name/$username") {
                        popUpTo("username") { inclusive = true }
                    }
                }
            }) {
                Text("Далее")
            }
        }
    }
}



