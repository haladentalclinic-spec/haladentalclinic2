package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.HalaDentalViewModel

// 1. CLINICS SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clinicsList = listOf(
        ClinicBranch("Karrada Main Branch", "Baghdad - Medical Towers, Karrada", "+9647701112233", "09:00 AM - 09:00 PM", "Oral Surgery, Ortho, Crowns", "Active"),
        ClinicBranch("Mansour Specialized Branch", "Baghdad - Al-Mansour, 14 Ramadhan St.", "+9647702223344", "10:00 AM - 08:00 PM", "Cosmetic Dentistry & Veneers", "Active"),
        ClinicBranch("Zayouna Digital Lab Center", "Baghdad - Al-Zayouna, Near Mall", "+9647703334455", "08:00 AM - 04:00 PM", "Porcelain CAD-CAM Milling", "Maintenance")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clinical Centers", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.testTag("clinics_top_bar")
            )
        },
        modifier = modifier.fillMaxSize()
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "HALA DENTAL BRANCHES & ASSOCIATES",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
            }

            items(clinicsList) { branch ->
                ClinicBranchCard(branch = branch, onCall = { phone ->
                    try {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Calling $phone...", Toast.LENGTH_SHORT).show()
                    }
                }, onWhatsApp = { phone ->
                    try {
                        val url = "https://api.whatsapp.com/send?phone=$phone&text=Hello, this is Hala Clinic System."
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "WhatsApp $phone...", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

data class ClinicBranch(
    val name: String,
    val address: String,
    val phone: String,
    val hours: String,
    val specialty: String,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicBranchCard(
    branch: ClinicBranch,
    onCall: (String) -> Unit,
    onWhatsApp: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("clinic_branch_card_${branch.name.replace(" ", "_").lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = branch.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                val badgeColor = if (branch.status == "Active") ClinicalTealLight else ClinicWarningAmber
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = branch.status.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "📍 Address: ${branch.address}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = "🕒 Working Hours: ${branch.hours}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = "🔬 Specialties: ${branch.specialty}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PolishPurple,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onCall(branch.phone) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("call_branch_${branch.name.replace(" ", "_").lowercase()}")
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call Branch", fontSize = 12.sp)
                }

                Button(
                    onClick = { onWhatsApp(branch.phone) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("whatsapp_branch_${branch.name.replace(" ", "_").lowercase()}")
                ) {
                    Text("💬 WhatsApp", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


// 2. DOCTORS / DRS SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val doctorsList = listOf(
        DoctorProfile("Dr. Alex Rivers", "Chief of Prosthodontics", "Crown & Bridge Preps, Implants Specialist", "Mon - Wed (02:00 PM - 08:00 PM)", "Available"),
        DoctorProfile("Dr. Hala Jamil", "Cosmetic Dentistry Director", "Veneers Designer, Smile Makeovers & Ortho", "Sat - Thu (10:00 AM - 04:00 PM)", "Available"),
        DoctorProfile("Dr. Mustafa Abbas", "Elite Implantologist", "Dental Implant Surgeries & Custom Abutments", "Sunday & Tuesday Only", "In Surgery"),
        DoctorProfile("Dr. Zainab Ahmed", "Pediatric & Preventive Dentistry", "Anxiety-Free Pediatric Dental Care", "Mon - Thu (09:00 AM - 01:00 PM)", "On Break")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Specialist Dentists", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.testTag("drs_top_bar")
            )
        },
        modifier = modifier.fillMaxSize()
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "HALA CLINICAL DENTAL EXPERTS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
            }

            items(doctorsList) { doctor ->
                DoctorProfileCard(doctor = doctor, onConsultClick = {
                    Toast.makeText(context, "Routing consultation request to ${doctor.name}...", Toast.LENGTH_SHORT).show()
                })
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

data class DoctorProfile(
    val name: String,
    val role: String,
    val description: String,
    val hours: String,
    val availability: String
)

@Composable
fun DoctorProfileCard(
    doctor: DoctorProfile,
    onConsultClick: () -> Unit
) {
    val initials = doctor.name.replace("Dr. ", "").trim().take(2).uppercase()
    val isAvailable = doctor.availability == "Available"
    val badgeColor = when (doctor.availability) {
        "Available" -> ClinicalTealLight
        "In Surgery" -> MedicalAlertRed
        else -> ClinicWarningAmber
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("doctor_card_${doctor.name.replace(" ", "_").lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle Doctor Initial Avatar
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(PolishSoftBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = PolishNavy
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
                        text = doctor.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = doctor.availability,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }

                Text(
                    text = doctor.role,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PolishPurple,
                    modifier = Modifier.padding(vertical = 1.dp)
                )

                Text(
                    text = doctor.description,
                    fontSize = 11.sp,
                    color = Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "🗓️ Sessions: ${doctor.hours}",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Button(
                    onClick = onConsultClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .height(34.dp)
                        .testTag("consult_${doctor.name.replace(" ", "_").lowercase()}"),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "Reserve Consultation",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PolishNavy
                    )
                }
            }
        }
    }
}


// 3. SHARE SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val shareLink = "https://haladentalclinic.com/system-invite"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Share Dental Portal", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.testTag("share_top_bar")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Beautiful Sharing graphic illustration
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(PolishLightPurple),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share Illustration",
                    tint = PolishPurple,
                    modifier = Modifier.size(48.dp)
                )
            }

            Text(
                text = "Hala Dental Portal Invite",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Share the Hala Dental Portal invitation with family and friends to manage dental appointments, customize laboratory tooth shade preps, and view digital treatment files instantly.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 14.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PORTAL ACCESS LINK",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = shareLink,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PolishPurple,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(shareLink))
                            Toast.makeText(context, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PolishSoftBlue),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        modifier = Modifier.testTag("copy_portal_link_btn")
                    ) {
                        Text("Copy", color = PolishNavy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    try {
                        val invitationText = "Hi! Check out the Hala Dental Patient Portal. Register, request dental checkups, and review your custom laboratory dental restorations on-the-go: $shareLink"
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Hala Dental Portal")
                            putExtra(Intent.EXTRA_TEXT, invitationText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Invitation Via:"))
                    } catch (e: Exception) {
                        Toast.makeText(context, "Executing share dialog...", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("share_portal_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = PolishNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share Portal Invitation", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


// 4. MORE-LIST SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreListScreen(
    viewModel: HalaDentalViewModel,
    onNavigateToPatients: () -> Unit,
    onNavigateToWorks: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val patients by viewModel.patients.collectAsState()
    val works by viewModel.works.collectAsState()
    val reminders by viewModel.reminders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dental Folders", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.testTag("more_top_bar")
            )
        },
        modifier = modifier.fillMaxSize()
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "MY CLOUD DENTAL FILES & LAB CODES",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
            }

            // Patients Database Navigation button
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPatients() }
                        .testTag("more_nav_patients"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(PolishSoftBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, "Patients", tint = PolishNavy)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Patients Directory",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "View registered portal profiles & health folders",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PolishLightPurple)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${patients.size} Profiles",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PolishPurple
                            )
                        }
                    }
                }
            }

            // Zirconia & Crown active works order screen navigation card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToWorks() }
                        .testTag("more_nav_works"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(PolishLightPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Build, "Lab Orders", tint = PolishPurple)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Lab Restoration Tracker",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Track crowns, bridges, shading & lab milling statuses",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PolishSoftBlue)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${works.size} Orders",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PolishNavy
                            )
                        }
                    }
                }
            }

            // Section for active appointments to preserve booked schedules visible
            item {
                Text(
                    text = "GLOBAL SCHEDULED APPOINTMENT LOGS (${reminders.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (reminders.isEmpty()) {
                item {
                    Text(
                        text = "No pending patient appointments registered.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } else {
                items(reminders) { reminder ->
                    ReminderItemView(reminder = reminder, onApprove = {
                        Toast.makeText(context, "Confirmed appointment!", Toast.LENGTH_SHORT).show()
                    })
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun ReminderItemView(reminder: Reminder, onApprove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reminder.title ?: "General checkup appointment",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                val badgeColor = if (reminder.status == "pending") ClinicWarningAmber else ClinicalTealLight
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = (reminder.status ?: "pending").uppercase(),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Scheduled: ${reminder.reminderDate}",
                fontSize = 11.sp,
                color = Color.Gray
            )

            if (!reminder.note.isNullOrBlank()) {
                Text(
                    text = reminder.note,
                    fontSize = 11.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
