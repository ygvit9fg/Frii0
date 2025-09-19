package com.example.friiomain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.friiomain.ui.*
import com.example.friiomain.utils.SessionManager
import androidx.compose.runtime.*



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)

        setContent {
            val navController = rememberNavController()

            val startDestination = if (sessionManager.isLoggedIn()) {
                "home/${sessionManager.getUserEmail()}/${sessionManager.getUserName()}"
            } else {
                "login"
            }

            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable("login") {
                    LoginScreen(navController) { email, name ->
                        sessionManager.saveUser(email, name)
                    }
                }
                composable("register") {
                    RegisterScreen(navController)
                }
                composable("home/{email}/{name}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    HomeScreen(navController, email, name)
                }
                composable("addFriend/{email}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    AddFriendScreen(navController, email)
                }
                composable("friends/{email}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    FriendsScreen(navController, email)
                }
            }
        }
    }
}



