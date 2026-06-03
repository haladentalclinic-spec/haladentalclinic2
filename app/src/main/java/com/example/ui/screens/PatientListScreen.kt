package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserData
import com.example.ui.theme.ClinicalTeal
import com.example.ui.theme.ClinicalTealLight
import com.example.ui.theme.MedicalAlertRed
import com.example.ui.viewmodel.HalaDentalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientListScreen(
    viewModel: HalaDentalViewModel,
    onNavigateToDetail: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val patients by viewModel.patients.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    // Patient Form Variables
    var pName by remember { mutableStateOf("") }
    var pPhone by remember { mutableStateOf("") }
    var pAge by remember { mutableStateOf("") }
    var pGender by remember { mutableStateOf("Female") }
    var pDisease by remember { mutableStateOf("") }
    var pAllergies by remember { mutableStateOf("") }
    var pBloodType by remember { mutableStateOf("O+") }
    var pNote by remember { mutableStateOf("") }

    val filteredPatients = remember(patients, searchQuery) {
        if (searchQuery.isEmpty()) {
            patients
        } else {
            patients.filter {
                it.fullName.contains(searchQuery, ignoreCase = true) ||
                (it.patientCode ?: "").contains(searchQuery, ignoreCase = true) ||
                (it.phone ?: "").contains(searchQuery)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_patient_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Patient Card")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Display Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                if (onNavigateBack != null) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("patients_back_btn").padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to More Tools"
                        )
                    }
                }
                Text(
                    text = "Patient Records & Systemic History",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = "Manage dental patients, drug allergies, and active clinical folders.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Live Search Toolbar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name or code HN-...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .testTag("patient_search"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ClinicalTeal,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            // Results List
            if (filteredPatients.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📋", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No clinical patient entries found",
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            text = "Change your search query or add a patient using the + button.",
                            fontSize = 12.sp,
                            color = Color.Gray.copy(alpha = 0.8f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredPatients) { patient ->
                        PatientCard(
                            patient = patient,
                            onClick = {
                                viewModel.selectPatient(patient)
                                onNavigateToDetail()
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(88.dp))
                    }
                }
            }
        }

        // Add Patient Profile Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Register Dental Patient", fontWeight = FontWeight.Bold) },
                text = {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            OutlinedTextField(
                                value = pName,
                                onValueChange = { pName = it },
                                label = { Text("Patient Full Name") },
                                modifier = Modifier.fillMaxWidth().testTag("add_name_field")
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = pPhone,
                                onValueChange = { pPhone = it },
                                label = { Text("Phone Number (+964...)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = pAge,
                                    onValueChange = { pAge = it },
                                    label = { Text("Age (years)") },
                                    modifier = Modifier.weight(1f)
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Gender", style = MaterialTheme.typography.labelSmall)
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        listOf("Female", "Male").forEach { genderOption ->
                                            val selected = pGender == genderOption
                                            FilterChip(
                                                selected = selected,
                                                onClick = { pGender = genderOption },
                                                label = { Text(genderOption, fontSize = 11.sp) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            OutlinedTextField(
                                value = pBloodType,
                                onValueChange = { pBloodType = it },
                                label = { Text("ABO Blood Type") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = pAllergies,
                                onValueChange = { pAllergies = it },
                                label = { Text("DRUG ALLERGIES (e.g. Penicillin)") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MedicalAlertRed
                                )
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = pDisease,
                                onValueChange = { pDisease = it },
                                label = { Text("Chronic Diseases (e.g. Diabetes)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = pNote,
                                onValueChange = { pNote = it },
                                label = { Text("Special Practitioner Notes") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val parsedAge = pAge.toIntOrNull() ?: 25
                            if (pName.isNotEmpty()) {
                                viewModel.createPatient(
                                    name = pName,
                                    phone = pPhone,
                                    gender = pGender,
                                    age = parsedAge,
                                    disease = pDisease,
                                    allergies = pAllergies,
                                    bloodType = pBloodType,
                                    note = pNote
                                )
                                showAddDialog = false
                                // Reset fields
                                pName = ""
                                pPhone = ""
                                pAge = ""
                                pGender = "Female"
                                pDisease = ""
                                pAllergies = ""
                                pNote = ""
                            } else {
                                Toast.makeText(context, "Full Name is required.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ClinicalTeal)
                    ) {
                        Text("Create Clinical File")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun PatientCard(
    patient: UserData,
    onClick: () -> Unit
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    
    // Dynamic initials calculation
    val initials = remember(patient.fullName) {
        val parts = patient.fullName.trim().split("\\s+".toRegex())
        if (parts.size >= 2) {
            (parts[0].take(1) + parts[1].take(1)).uppercase()
        } else {
            patient.fullName.take(2).uppercase()
        }
    }
    
    // Aesthetic color pairs directly inspired by Professional Polish mockup
    val avatarBg = remember(patient.fullName) {
        val hash = patient.fullName.hashCode()
        val colors = listOf(Color(0xFFF2B8B5), Color(0xFFD3E3FD), Color(0xFFEADDFF), Color(0xFFC5E1A5))
        colors[kotlin.math.abs(hash) % colors.size]
    }
    val avatarTextColor = remember(patient.fullName) {
        val hash = patient.fullName.hashCode()
        val colors = listOf(Color(0xFF601410), Color(0xFF041E49), Color(0xFF21005D), Color(0xFF33691E))
        colors[kotlin.math.abs(hash) % colors.size]
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Highly polished initial avatar badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(avatarBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = avatarTextColor
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = patient.fullName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Compact blood type pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ClinicalTealLight.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = patient.bloodType ?: "O+",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = ClinicalTealLight
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = patient.patientCode ?: "HN-2026-N",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${patient.gender ?: "Female"} • ${patient.age ?: 30} yrs",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                if (!patient.allergies.isNullOrEmpty() && patient.allergies != "None declared") {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MedicalAlertRed.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                        ) {
                            Text("⚠️", fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Allergies: ${patient.allergies}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedicalAlertRed,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
