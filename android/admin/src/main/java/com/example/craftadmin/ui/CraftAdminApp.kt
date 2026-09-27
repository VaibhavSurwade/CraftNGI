package com.example.craftadmin.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.craftadmin.ui.screens.AdminHomeScreen
import com.example.craftadmin.ui.screens.RegisterProductScreen
import com.example.craftadmin.ui.theme.CraftAdminTheme
import com.example.craftadmin.viewmodel.AdminViewModel

enum class AdminScreen {
    HOME,
    REGISTER
}

@Composable
fun CraftAdminApp(
    viewModel: AdminViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf(AdminScreen.HOME) }

    // Intercept Android System Navigation Back button when on any screen other than HOME
    BackHandler(enabled = currentScreen != AdminScreen.HOME) {
        currentScreen = AdminScreen.HOME
    }

    CraftAdminTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                AdminScreen.HOME -> {
                    AdminHomeScreen(
                        viewModel = viewModel,
                        onNavigateToRegister = {
                            viewModel.resetRegisterState()
                            currentScreen = AdminScreen.REGISTER
                        }
                    )
                }
                AdminScreen.REGISTER -> {
                    RegisterProductScreen(
                        viewModel = viewModel,
                        onBack = {
                            currentScreen = AdminScreen.HOME
                        }
                    )
                }
            }
        }
    }
}
