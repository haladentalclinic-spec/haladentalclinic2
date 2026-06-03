package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.ClinicalTeal
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.HalaDentalViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainLayout()
            }
        }
    }
}

@Composable
fun MainLayout() {
    val navController = rememberNavController()
    val viewModel: HalaDentalViewModel = viewModel()
    
    // Track the currently active navigation route
    var currentRoute by remember { mutableStateOf("dashboard") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Display Bottom Navigation Bar if we are not on the sub screens
            if (currentRoute != "patient_detail" && currentRoute != "patients" && currentRoute != "works") {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("app_bottom_nav")
                ) {
                    // 1. HOME Tab
                    NavigationBarItem(
                        selected = currentRoute == "dashboard",
                        onClick = {
                            currentRoute = "dashboard"
                            navController.navigate("dashboard") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home Hub") },
                        label = { Text("Home") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.secondary,
                            unselectedTextColor = Color.Gray,
                            unselectedIconColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_home_tab")
                    )

                    // 2. CLINICS Tab
                    NavigationBarItem(
                        selected = currentRoute == "clinics",
                        onClick = {
                            currentRoute = "clinics"
                            navController.navigate("clinics") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.LocationOn, contentDescription = "Clinic Branches") },
                        label = { Text("Clinics") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.secondary,
                            unselectedTextColor = Color.Gray,
                            unselectedIconColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_clinics_tab")
                    )

                    // 3. DRS Tab
                    NavigationBarItem(
                        selected = currentRoute == "drs",
                        onClick = {
                            currentRoute = "drs"
                            navController.navigate("drs") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Specialist Doctors") },
                        label = { Text("Drs") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.secondary,
                            unselectedTextColor = Color.Gray,
                            unselectedIconColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_drs_tab")
                    )

                    // 4. SHARE Tab
                    NavigationBarItem(
                        selected = currentRoute == "share",
                        onClick = {
                            currentRoute = "share"
                            navController.navigate("share") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Share, contentDescription = "Share App Link") },
                        label = { Text("Share") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.secondary,
                            unselectedTextColor = Color.Gray,
                            unselectedIconColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_share_tab")
                    )

                    // 5. MORE-LIST Tab
                    NavigationBarItem(
                        selected = currentRoute == "more",
                        onClick = {
                            currentRoute = "more"
                            navController.navigate("more") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Menu, contentDescription = "More Options") },
                        label = { Text("More-list") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.secondary,
                            unselectedTextColor = Color.Gray,
                            unselectedIconColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_more_tab")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            composable("dashboard") {
                ClinicHubScreen(
                    viewModel = viewModel,
                    onNavigateToPatients = {
                        currentRoute = "patients"
                        navController.navigate("patients") {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToWorks = {
                        currentRoute = "works"
                        navController.navigate("works") {
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.testTag("hub_screen_view")
                )
            }

            composable("clinics") {
                ClinicsScreen(
                    modifier = Modifier.testTag("clinics_screen_view")
                )
            }

            composable("drs") {
                DoctorsScreen(
                    modifier = Modifier.testTag("drs_screen_view")
                )
            }

            composable("share") {
                ShareScreen(
                    modifier = Modifier.testTag("share_screen_view")
                )
            }

            composable("more") {
                MoreListScreen(
                    viewModel = viewModel,
                    onNavigateToPatients = {
                        currentRoute = "patients"
                        navController.navigate("patients")
                    },
                    onNavigateToWorks = {
                        currentRoute = "works"
                        navController.navigate("works")
                    },
                    modifier = Modifier.testTag("more_screen_view")
                )
            }

            composable("patients") {
                PatientListScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = {
                        currentRoute = "patient_detail"
                        navController.navigate("patient_detail")
                    },
                    onNavigateBack = {
                        currentRoute = "more"
                        navController.popBackStack()
                    },
                    modifier = Modifier.testTag("patients_screen_view")
                )
            }

            composable("patient_detail") {
                PatientDetailScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        currentRoute = "patients"
                        navController.popBackStack()
                    },
                    modifier = Modifier.testTag("detail_screen_view")
                )
            }

            composable("works") {
                WorksScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        currentRoute = "more"
                        navController.popBackStack()
                    },
                    modifier = Modifier.testTag("works_screen_view")
                )
            }
        }
    }
}
