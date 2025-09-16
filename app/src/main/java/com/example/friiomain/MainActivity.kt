package com.example.friiomain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.friiomain.data.DataStoreManager
import com.example.friiomain.ui.*
import com.example.friiomain.ui.theme.FriioMainTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStoreManager = DataStoreManager(this)

        setContent {
            FriioMainTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val userEmail by dataStoreManager.userEmail.collectAsState(initial = null)

                    NavHost(
                        navController = navController,
                        startDestination = if (userEmail == null) "login" else "home/$userEmail"
                    ) {
                        // Экран входа
                        composable("login") {
                            LoginScreen(
                                navController = navController,
                                onLoginSuccess = { email ->
                                    lifecycleScope.launch {
                                        dataStoreManager.saveUserEmail(email)
                                    }
                                    navController.navigate("home/$email") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Главная
                        composable(
                            "home/{email}",
                            arguments = listOf(navArgument("email") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: return@composable
                            HomeScreen(navController = navController, currentUserEmail = email)
                        }

                        // Список друзей
                        composable(
                            "friends/{email}",
                            arguments = listOf(navArgument("email") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: return@composable
                            FriendsScreen(navController = navController, currentUserEmail = email)
                        }

                        // Добавить друга
                        composable(
                            "addFriend/{email}",
                            arguments = listOf(navArgument("email") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: return@composable
                            AddFriendScreen(navController = navController, currentUserEmail = email)
                        }
                    }
                }
            }
        }
    }
}







