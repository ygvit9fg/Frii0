package com.example.friiomain.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.friiomain.ui.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navController: NavController? = null,
    showBackButton: Boolean = false,
    defaultTitle: String = "Профиль",
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val name = viewModel.userName.collectAsState().value

    CenterAlignedTopAppBar(
        title = { Text(text = if (name.isNotEmpty()) name else defaultTitle) },
        navigationIcon = {
            if (showBackButton && navController != null) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                }
            }
        }
    )
}
