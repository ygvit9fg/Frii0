package com.example.friiomain.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.friiomain.data.AppDatabase
import com.example.friiomain.data.UserEntity
import com.example.friiomain.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.friiomain.data.DataStoreManager
import java.net.URLDecoder


@Composable
fun WeatherPreferencesScreen(
    navController: NavController,
    name: String,
    email: String,
    password: String,
    currentPreferences: String? = null,
    username: String,
    isEditMode: Boolean = false
)  {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }

    val db = AppDatabase.getDatabase(context)
    val userDao = db.userDao()
    val sessionManager = remember { SessionManager(context) }
    val coroutineScope = rememberCoroutineScope()

    var currentUser by remember { mutableStateOf<UserEntity?>(null) }

    //Room
    LaunchedEffect(email) {
        currentUser = withContext(Dispatchers.IO) {
            userDao.getUserByEmail(email)
        }
    }


    var selectedPreferences = remember { mutableStateListOf<String>() }

    LaunchedEffect(currentUser, currentPreferences) {
        val prefsString = currentUser?.preferences ?: currentPreferences
        if (!prefsString.isNullOrBlank()) {
            val decoded = URLDecoder.decode(prefsString, "UTF-8")
            selectedPreferences.clear()
            selectedPreferences.addAll(
                decoded.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
            )
        }
    }



    val canSave = isEditMode || selectedPreferences.isNotEmpty()

    val preferencesList = listOf(
        "Loves rainy weather",
        "Prefers Sunny Days",
        "Enjoys cold weather",
        "Likes cloudy skies",
        "Enjoys windy conditions",
        "Very outdoorsy person",
        "Nature lover",
        "Social and outgoing",
        "Enjoys photography",
        "Environmentally conscious"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Выберите интересы",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(preferencesList) { pref ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = pref in selectedPreferences,
                            onCheckedChange = {
                                if (it) selectedPreferences.add(pref)
                                else selectedPreferences.remove(pref)
                            }
                        )
                        Text(text = pref)
                    }
                }
            }

            Button(
                onClick = {
                    if (selectedPreferences.isEmpty()) {
                        Toast.makeText(context, "Выберите хотя бы один пункт", Toast.LENGTH_LONG).show()
                        return@Button
                    }



                    coroutineScope.launch {
                        dataStoreManager.saveUserPreferences(selectedPreferences)
                        try {
                            val updatedUser = withContext(Dispatchers.IO) {
                                val user = currentUser?.copy(
                                    preferences = selectedPreferences.joinToString(", ")
                                ) ?: UserEntity(
                                    email = email,
                                    name = name,
                                    password = password,
                                    username = username,
                                    preferences = selectedPreferences.joinToString(", ")
                                )

                                if (currentUser == null) {
                                    userDao.insert(user)
                                } else {
                                    userDao.update(user)
                                }

                                sessionManager.saveUser(email, user.name)

                                user // ← возвращаем
                            }

                            withContext(Dispatchers.Main) {
                                navController.navigate("home/$email/${updatedUser.name}") {
                                    popUpTo("home/{email}/{name}") { inclusive = true }
                                }
                            }

                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Ошибка при сохранении: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Сохранить")
            }

        }
    }
}
