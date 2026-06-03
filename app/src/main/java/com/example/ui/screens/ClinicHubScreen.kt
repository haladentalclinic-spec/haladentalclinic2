package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Reminder
import com.example.data.model.UserData
import com.example.data.model.Work
import com.example.ui.theme.*
import com.example.ui.viewmodel.HalaDentalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicHubScreen(
    viewModel: HalaDentalViewModel,
    onNavigateToPatients: () -> Unit,
    onNavigateToWorks: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val patients by viewModel.patients.collectAsState()
    val works by viewModel.works.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    val banners by viewModel.banners.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val selectedPatient by viewModel.selectedPatient.collectAsState()

    // Dialog & Flow controllers
    var showApptDialog by remember { mutableStateOf(false) }
    var showProfileSwitcher by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }

    // Register Patient Fields
    var regName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regGender by remember { mutableStateOf("Female") }
    var regAge by remember { mutableStateOf("30") }
    var regDisease by remember { mutableStateOf("None declared") }
    var regAllergies by remember { mutableStateOf("None declared") }
    var regBloodType by remember { mutableStateOf("O+") }
    var regNote by remember { mutableStateOf("") }

    // Edit Profile Fields (Initialize when dialog opens)
    var editAge by remember { mutableStateOf("") }
    var editDisease by remember { mutableStateOf("") }
    var editAllergies by remember { mutableStateOf("") }
    var editBloodType by remember { mutableStateOf("") }
    var editAddress by remember { mutableStateOf("") }
    var editNote by remember { mutableStateOf("") }

    // Appointment fields
    var apptTitle by remember { mutableStateOf("") }
    var apptDate by remember { mutableStateOf("Tomorrow - 11:30 AM") }
    var apptNote by remember { mutableStateOf("") }

    // Filtering data specifically to the currently selected active Patient Profile
    val activePatient = selectedPatient
    val personalWorks = works.filter { it.patientId == activePatient?.id }
    val personalReminders = reminders.filter { it.patientId == activePatient?.id }

    // Calculate greeting based on local time
    val calendar = java.util.Calendar.getInstance()
    val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
    val timeGreeting = when {
        hour < 12 -> "Good Morning"
        hour < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { 
                    if (activePatient != null) {
                        apptTitle = ""
                        apptNote = ""
                        showApptDialog = true
                    } else {
                        Toast.makeText(context, "Please configure or register your patient folder first.", Toast.LENGTH_SHORT).show()
                    }
                },
                icon = { Icon(Icons.Default.DateRange, "Book session") },
                text = { Text("Request Appointment") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("book_appt_fab")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Clinic Headline Banner
            item {
                Spacer(modifier = Modifier.height(12.dp))
                ClinicHeaderCard(
                    clinicName = settings?.clinicName ?: "Hala Dental Clinic & Lab Ecosystem",
                    clinicAddress = settings?.address ?: "Medical Towers, Karrada, Baghdad"
                )
            }

            // 2. Clinical Announcements & Offers Carousel (Banners)
            if (banners.isNotEmpty()) {
                item {
                    Text(
                        text = "Healthy Offers & Dental Advices",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        items(banners) { banner ->
                            BannerCard(
                                title = banner.title ?: "Promotion",
                                imageUrl = banner.imageUrl,
                                linkUrl = banner.linkUrl ?: "https://haladentalclinic.com"
                            )
                        }
                    }
                }
            }

            // 3. Patient Active Profile Switcher Panel
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Surface(
                                    color = ClinicalTealLight.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF25D366))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "PostgreSQL DB Live Sync",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ClinicalTealLight
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$timeGreeting! 👋",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                                Text(
                                    text = activePatient?.fullName ?: "Hala Dental Visitor",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Switch account & New register actions
                            Column(horizontalAlignment = Alignment.End) {
                                Button(
                                    onClick = { showProfileSwitcher = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = PolishSoftBlue),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(36.dp).testTag("switch_profile_btn")
                                ) {
                                    Text("Switch Profiler", color = PolishNavy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                TextButton(
                                    onClick = { 
                                        regName = ""
                                        regPhone = ""
                                        showRegisterDialog = true 
                                    },
                                    modifier = Modifier.height(30.dp).testTag("register_folder_btn")
                                ) {
                                    Text("+ Register File", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ClinicalTealLight)
                                }
                            }
                        }

                        if (activePatient != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Patient Access Code", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                                    Text(activePatient.patientCode ?: "N/A", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PolishPurple)
                                }
                                Column {
                                    Text("Mobile Number", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                                    Text(activePatient.phone ?: "N/A", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Column {
                                    Text("Clinical Folder", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                                    Text(if (activePatient.isActive == true) "Active" else "Archived", fontSize = 13.sp, color = Color(0xFF128C7E), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Systemic Medical History & Allergy Check Card
            if (activePatient != null) {
                item {
                    Text(
                        text = "My Medical File & Systemic Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⚡", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Clinical History Indicators",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PolishNavy
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        // Initialize edit values
                                        editAge = activePatient.age?.toString() ?: "30"
                                        editDisease = activePatient.disease ?: "None declared"
                                        editAllergies = activePatient.allergies ?: "None declared"
                                        editBloodType = activePatient.bloodType ?: "O+"
                                        editAddress = activePatient.address ?: ""
                                        editNote = activePatient.note ?: ""
                                        showEditProfileDialog = true
                                    }
                                ) {
                                    Text("Edit History", fontSize = 12.sp, color = PolishPurple, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Critical indicators grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Allergies Card
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (!activePatient.allergies.isNullOrEmpty() && !activePatient.allergies.contains("None", ignoreCase = true)) MedicalAlertRed.copy(alpha = 0.08f) else Color.Gray.copy(alpha = 0.05f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("🚫 DRUG ALLERGIES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (!activePatient.allergies.isNullOrEmpty() && !activePatient.allergies.contains("None", ignoreCase = true)) MedicalAlertRed else Color.Gray)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = activePatient.allergies ?: "None declared",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                // Diseases Card
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.05f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("🤒 CHRONIC ILLNESS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = activePatient.disease ?: "None declared",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Age, gender type details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Blood Group: ${activePatient.bloodType ?: "Unspecified"}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Age/Gender: ${activePatient.age ?: "N/A"} years • ${activePatient.gender ?: "N/A"}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.DarkGray
                                )
                            }

                            if (!activePatient.note.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "📝 Doctor's Advice: ${activePatient.note}",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // 4. Personal Lab Prosthetic Works / Crown status
            if (activePatient != null) {
                item {
                    Text(
                        text = "My Custom Teeth Restorations & Prostheses",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (personalWorks.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🔬 No custom zirconia crowns, implants or restorations are ordered at our laboratory yet.")
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            personalWorks.forEach { work ->
                                PersonalWorkCard(work = work)
                            }
                        }
                    }
                }
            }

            // 5. My Upcoming Appointments
            if (activePatient != null) {
                item {
                    Text(
                        text = "My Scheduled Medical Appointments",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (personalReminders.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("📅 No upcoming dental checkups scheduled. Click below to request.")
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            personalReminders.forEach { reminder ->
                                AppointmentItemRow(reminder = reminder)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // ================== DIALOGS ==================

        // A. Profile Switcher Dialog
        if (showProfileSwitcher) {
            AlertDialog(
                onDismissRequest = { showProfileSwitcher = false },
                title = { Text("Choose Patient Account") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Search or select your patient access folder linked in the Supabase clinic registry.")
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                .padding(4.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(patients) { p ->
                                val isSelected = activePatient?.id == p.id
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ClinicalTealLight.copy(alpha = 0.15f) else Color.Transparent)
                                        .clickable { 
                                            viewModel.selectPatient(p)
                                            showProfileSwitcher = false
                                        }
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(p.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("📞 ${p.phone ?: "No number"}", fontSize = 11.sp, color = Color.Gray)
                                    }
                                    Badge(containerColor = PolishSoftBlue) {
                                        Text(p.patientCode ?: "CODE", fontSize = 10.sp, color = PolishNavy, fontWeight = FontWeight.Bold, modifier = Modifier.padding(2.dp))
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showProfileSwitcher = false }) {
                        Text("Done")
                    }
                }
            )
        }

        // B. Edit Profile Medical Details Dialog
        if (showEditProfileDialog && activePatient != null) {
            AlertDialog(
                onDismissRequest = { showEditProfileDialog = false },
                title = { Text("Edit My Medical Folder") },
                text = {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            OutlinedTextField(
                                value = editAge,
                                onValueChange = { editAge = it },
                                label = { Text("Age") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = editBloodType,
                                onValueChange = { editBloodType = it },
                                label = { Text("Blood Group (e.g. A+, O+, B-)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = editAllergies,
                                onValueChange = { editAllergies = it },
                                label = { Text("Drug Allergies (e.g. Penicillin)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = editDisease,
                                onValueChange = { editDisease = it },
                                label = { Text("Systemic Chronic Conditions") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = editAddress,
                                onValueChange = { editAddress = it },
                                label = { Text("Address") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = editNote,
                                onValueChange = { editNote = it },
                                label = { Text("Personal Special Preference Notes") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.updatePatientProfile(
                                id = activePatient.id ?: "",
                                fullName = activePatient.fullName,
                                phone = activePatient.phone ?: "",
                                gender = activePatient.gender ?: "Female",
                                age = editAge.toIntOrNull() ?: activePatient.age ?: 30,
                                address = editAddress,
                                disease = editDisease,
                                allergies = editAllergies,
                                bloodType = editBloodType,
                                note = editNote
                            )
                            showEditProfileDialog = false
                            Toast.makeText(context, "Medical folder updated successfully in Supabase PostgreSQL database!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ClinicalTeal)
                    ) {
                        Text("Save History Details")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditProfileDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // C. Register New Profile Dialog
        if (showRegisterDialog) {
            AlertDialog(
                onDismissRequest = { showRegisterDialog = false },
                title = { Text("Register Your Patient Folder") },
                text = {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            Text("Create a lifetime dental record directly on the clinical database.", fontSize = 12.sp, color = Color.Gray)
                        }
                        item {
                            OutlinedTextField(
                                value = regName,
                                onValueChange = { regName = it },
                                label = { Text("Your Full Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = regPhone,
                                onValueChange = { regPhone = it },
                                label = { Text("Active Mobile Phone (e.g. +964...)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            Text("Select Gender:", style = MaterialTheme.typography.labelSmall)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Female", "Male").forEach { gr ->
                                    val active = regGender == gr
                                    ElevatedButton(
                                        onClick = { regGender = gr },
                                        colors = ButtonDefaults.elevatedButtonColors(
                                            containerColor = if (active) ClinicalTeal else MaterialTheme.colorScheme.surface,
                                            contentColor = if (active) Color.White else MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(gr)
                                    }
                                }
                            }
                        }
                        item {
                            OutlinedTextField(
                                value = regAge,
                                onValueChange = { regAge = it },
                                label = { Text("Birth Age") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = regBloodType,
                                onValueChange = { regBloodType = it },
                                label = { Text("Blood Type Group") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = regAllergies,
                                onValueChange = { regAllergies = it },
                                label = { Text("Drug Allergies (e.g. Penicillin)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = regDisease,
                                onValueChange = { regDisease = it },
                                label = { Text("Chronic Conditions (e.g. Asthma)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = regNote,
                                onValueChange = { regNote = it },
                                label = { Text("Patient note (Anxiety, preferences)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (regName.isNotBlank() && regPhone.isNotBlank()) {
                                viewModel.createPatient(
                                    name = regName,
                                    phone = regPhone,
                                    gender = regGender,
                                    age = regAge.toIntOrNull() ?: 30,
                                    disease = regDisease,
                                    allergies = regAllergies,
                                    bloodType = regBloodType,
                                    note = regNote
                                )
                                showRegisterDialog = false
                                Toast.makeText(context, "Registration successful on Supabase!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Please fill in your Name and Phone.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Register Profile")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRegisterDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // D. Create Patient Appointment Dialog
        if (showApptDialog && activePatient != null) {
            AlertDialog(
                onDismissRequest = { showApptDialog = false },
                title = { Text("Request Dental Appointment") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Booking for account profile: ${activePatient.fullName}", fontWeight = FontWeight.Bold, color = PolishPurple)
                        
                        OutlinedTextField(
                            value = apptTitle,
                            onValueChange = { apptTitle = it },
                            label = { Text("Purpose (e.g. Scaling treatment, Crown fitting)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = apptDate,
                            onValueChange = { apptDate = it },
                            label = { Text("Preferred Date & Time") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = apptNote,
                            onValueChange = { apptNote = it },
                            label = { Text("Additional symptoms or comments for Doctor") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (apptTitle.isNotEmpty()) {
                                viewModel.createAppointmentReminder(
                                    title = apptTitle,
                                    dateString = apptDate,
                                    patientId = activePatient.id ?: "",
                                    note = apptNote
                                )
                                showApptDialog = false
                                Toast.makeText(context, "Appointment request sent live to Supabase clinic scheduler!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Please enter treatment purpose.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ClinicalTeal)
                    ) {
                        Text("Send Request")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showApptDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun PersonalWorkCard(work: Work) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = work.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Badge(containerColor = PolishLightPurple) {
                    Text(
                        text = work.workType ?: "RESTORATION",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = PolishPurple,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Teeth Selected", fontSize = 10.sp, color = Color.Gray)
                    Text(
                        text = work.teeth?.joinToString(", ") ?: "Oral Examination",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Custom Tooth Shade", fontSize = 10.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    when (work.teethColor) {
                                        "A1" -> TeethPorcelainA1
                                        "A2" -> TeethPorcelainA2
                                        else -> TeethPorcelainB1
                                    }
                                )
                                .border(0.5.dp, Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = work.teethColor ?: "A1",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🛠️ Status: ", fontSize = 11.sp, color = Color.Gray)
                Text(
                    text = "Milling & glazing under custom specifications from Shade ${work.teethColor ?: "A1"}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ClinicalTealLight
                )
            }
            Text(
                text = "Scheduled Lab Delivery: ${work.deliveryDate ?: "Checking with Lab Tech"}",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun AppointmentItemRow(reminder: Reminder) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Badge(containerColor = ClinicWarningAmber) {
                        Text(
                            text = "APPOINTMENT",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = reminder.reminderDate,
                        fontSize = 11.sp,
                        color = ClinicalTeal,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reminder.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!reminder.note.isNullOrEmpty()) {
                    Text(
                        text = "Notes: ${reminder.note}",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (reminder.status == "pending") ClinicWarningAmber.copy(alpha = 0.12f)
                        else ClinicalTealLight.copy(alpha = 0.12f)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = (reminder.status ?: "pending").uppercase(),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (reminder.status == "pending") ClinicWarningAmber else ClinicalTealLight
                )
            }
        }
    }
}

@Composable
fun ClinicHeaderCard(clinicName: String, clinicAddress: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ClinicalTeal
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ClinicalTeal, PolishNavy)
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "HALA DENTAL SYSTEM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClinicalAccolade,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = clinicName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "📍 $clinicAddress",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🦷",
                        fontSize = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BannerCard(title: String, imageUrl: String?, linkUrl: String) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .clickable {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Opening promotional link...", Toast.LENGTH_SHORT).show()
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Ambient dim gradient over banner background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                            )
                        )
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(ClinicalTealLight, ClinicalTeal)
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Surface(
                    color = ClinicWarningAmber,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "PROMOTIONAL",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
