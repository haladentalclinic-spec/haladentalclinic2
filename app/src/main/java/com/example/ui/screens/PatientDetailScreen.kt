package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ClinicalTeal
import com.example.ui.theme.ClinicalTealLight
import com.example.ui.theme.MedicalAlertRed
import com.example.ui.viewmodel.HalaDentalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailScreen(
    viewModel: HalaDentalViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val patient by viewModel.selectedPatient.collectAsState()
    val works by viewModel.works.collectAsState()

    if (patient == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Select a patient to view details.")
        }
        return
    }

    val currentPatient = patient!!
    // Filter lab orders specific to this patient
    val patientWorks = works.filter { it.patientId == currentPatient.id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentPatient.fullName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("detail_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to patient database")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                // Patient Main profile header
                PatientHeaderBadge(
                    fullName = currentPatient.fullName,
                    code = currentPatient.patientCode ?: "HN-2026-N",
                    phone = currentPatient.phone ?: "No phone registered",
                    bloodType = currentPatient.bloodType ?: "O+"
                )
            }

            // WhatsApp & Voice Quick call actions
            item {
                QuickContactUtilities(
                    phone = currentPatient.phone ?: "",
                    fullName = currentPatient.fullName,
                    onWhatsAppClick = { phone, message ->
                        try {
                            val url = "https://api.whatsapp.com/send?phone=$phone&text=${Uri.encode(message)}"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Opening browser messenger...", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            // Severe High priority allergies alert
            item {
                MedicalWarningBoard(
                    allergies = currentPatient.allergies ?: "None declared",
                    systemicDisease = currentPatient.disease ?: "None declared"
                )
            }

            // General demographics
            item {
                DemographicSpecificsCard(
                    age = currentPatient.age ?: 30,
                    gender = currentPatient.gender ?: "Female",
                    address = currentPatient.address ?: "Karrada, Baghdad",
                    notes = currentPatient.note ?: "No special dental parameters recorded."
                )
            }

            // Linked Prosthesis lab orders section
            item {
                Text(
                    text = "Active Dental Lab Orders (${patientWorks.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (patientWorks.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔬", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No prosthetic orders registered",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Go to the Lab Orders tab to draft clinical zirconia, crown preps or bridge fittings.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            } else {
                items(patientWorks) { work ->
                    WorkOrderCard(work = work, patientName = currentPatient.fullName)
                }
            }

            item {
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun PatientHeaderBadge(
    fullName: String,
    code: String,
    phone: String,
    bloodType: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(ClinicalTeal.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = fullName.take(2).uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = ClinicalTeal
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Clinical Code: $code",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClinicalTealLight
                )
                Text(
                    text = "Tel: $phone",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Blood Type Pill Badge
            Card(
                colors = CardDefaults.cardColors(containerColor = ClinicalTealLight),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "BLOOD", fontSize = 7.sp, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Black)
                    Text(text = bloodType, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
fun QuickContactUtilities(
    phone: String,
    fullName: String,
    onWhatsAppClick: (String, String) -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // WhatsApp button
        Button(
            onClick = {
                val message = "Dear $fullName, this is Hala Dental Clinic contacting you regarding your ongoing clinical treatments prep. Please respond when available."
                onWhatsAppClick(phone.ifEmpty { "+9647701234567" }, message)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("💬 WhatsApp Patient", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        // Native Call dialer
        Button(
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Dialiing: $phone", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.weight(0.81f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Call Phone", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
    }
}

@Composable
fun MedicalWarningBoard(
    allergies: String,
    systemicDisease: String
) {
    val hasAllergies = allergies.isNotEmpty() && allergies != "None declared"
    val warningColor = if (hasAllergies) MedicalAlertRed else ClinicalTeal

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasAllergies) MedicalAlertRed.copy(alpha = 0.08f) else ClinicalTeal.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.dp, warningColor.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Medical alert badge",
                    tint = warningColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CRITICAL PATHOLOGY & SYSTEMS LIST",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = warningColor,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Drug allergies highlight
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Systemic Pathologies:", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                Text(text = systemicDisease, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Contraindicated Drugs / Allergies:", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                Text(text = allergies, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = warningColor)
            }
        }
    }
}

@Composable
fun DemographicSpecificsCard(
    age: Int,
    gender: String,
    address: String,
    notes: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "PATIENT FILE METRICS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Patient Age:", fontSize = 12.sp, color = Color.Gray)
                Text(text = "$age years", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Gender:", fontSize = 12.sp, color = Color.Gray)
                Text(text = gender, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Registered Address:", fontSize = 12.sp, color = Color.Gray)
                Text(text = address, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = "Clinical Case Notes",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = ClinicalTeal
            )
            Text(
                text = notes,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
        }
    }
}
