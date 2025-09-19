package com.example.friiomain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.friiomain.data.DataStoreManager
import com.example.friiomain.ui.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreManager = DataStoreManager(this)

        setContent {
            val navController = rememberNavController()
            var startDestination by remember { mutableStateOf<String?>(null) }

            // Загружаем email и name из DataStore
            LaunchedEffect(Unit) {
                val email = dataStoreManager.userEmail.first()
                val name = dataStoreManager.userName.first()
                startDestination = if (email.isNullOrEmpty()) "login" else "home/$email/$name"
            }

            if (startDestination != null) {
                NavHost(navController = navController, startDestination = startDestination!!) {
                    composable("register") {
                        RegisterScreen(navController)
                    }

                    composable("login") {
                        LoginScreen(navController) { email, name ->
                            lifecycleScope.launch {
                                dataStoreManager.saveUserEmail(email)
                                dataStoreManager.saveUserName(name)
                            }
                            navController.navigate("home/$email/$name") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }

                    composable(
                        "home/{email}/{name}",
                        arguments = listOf(
                            navArgument("email") { type = NavType.StringType },
                            navArgument("name") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        val name = backStackEntry.arguments?.getString("name") ?: ""
                        HomeScreen(navController, email, name)
                    }

                    // Остальные экраны
                    composable(
                        "friends/{email}",
                        arguments = listOf(navArgument("email") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        FriendsScreen(navController, email)
                    }

                    composable(
                        "addFriend/{email}",
                        arguments = listOf(navArgument("email") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        AddFriendScreen(navController, email)
                    }

                    composable("qrScanner") {
                        QrScannerScreen(navController) {}
                    }
                }
            } else {
                // Можно показать пустой экран или Splash
                Box(modifier = Modifier.fillMaxSize())
            }
        }
    }
}



