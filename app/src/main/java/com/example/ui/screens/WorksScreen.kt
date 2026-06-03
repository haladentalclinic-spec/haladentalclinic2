package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
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
import com.example.data.model.Work
import com.example.ui.components.ToothSelector
import com.example.ui.theme.*
import com.example.ui.viewmodel.HalaDentalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorksScreen(
    viewModel: HalaDentalViewModel,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val works by viewModel.works.collectAsState()
    val patients by viewModel.patients.collectAsState()

    var isAddingWork by remember { mutableStateOf(false) }

    // Lab Work Form State
    var workTitle by remember { mutableStateOf("") }
    var selectedWorkType by remember { mutableStateOf("Crown") }
    var selectedTeeth = remember { mutableStateListOf<Int>() }
    var selectedShade by remember { mutableStateOf("A2") }
    var labNote by remember { mutableStateOf("") }
    var patientIdForWork by remember { mutableStateOf("") }
    var deliveryDays by remember { mutableStateOf(7) }

    val workTypes = listOf("Crown", "Bridge", "Inlay", "Onlay", "Veneer", "Denture", "Implant")
    val shadesList = listOf("A1", "A2", "A3", "B1", "B2", "C1", "C2", "D1")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (!isAddingWork) {
                ExtendedFloatingActionButton(
                    onClick = { isAddingWork = true },
                    icon = { Icon(Icons.Default.Add, "New Lab order") },
                    text = { Text("Log Lab Order") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("log_work_fab")
                )
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

            // Main Titles
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onNavigateBack != null && !isAddingWork) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("works_back_btn").padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to More Tools"
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isAddingWork) "Draft Lab Restoration" else "Lab & Prosthetic Orders",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isAddingWork) "Design custom porcelain, zirconia, or metal prosthesis works." else "Track crown prep timelines, teeth shades, and laboratory structures.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                if (isAddingWork) {
                    IconButton(onClick = { isAddingWork = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel add")
                    }
                }
            }

            if (isAddingWork) {
                // RENDER WORK ORDER FORM
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Patient Selection dropdown/list representation
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "1. Select Dental Patient:*",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ClinicalTeal
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(patients) { p ->
                                        val isSelected = patientIdForWork == p.id
                                        val cardColor = if (isSelected) ClinicalTealLight else MaterialTheme.colorScheme.background
                                        val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(cardColor)
                                                .border(1.dp, if (isSelected) ClinicalTeal else Color.LightGray, RoundedCornerShape(8.dp))
                                                .clickable { patientIdForWork = p.id ?: "" }
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = p.fullName,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Simple Info Card
                    item {
                        OutlinedTextField(
                            value = workTitle,
                            onValueChange = { workTitle = it },
                            label = { Text("Prosthesis Order Title (e.g. Crown cementation molar)") },
                            modifier = Modifier.fillMaxWidth().testTag("work_title_field")
                        )
                    }

                    // Choice Chips for Work type
                    item {
                        Text(
                            text = "2. Work Type Choice:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(workTypes) { wt ->
                                val isSelected = selectedWorkType == wt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedWorkType = wt },
                                    label = { Text(wt) }
                                )
                            }
                        }
                    }

                    // FDI Custom Interactive Selector Component! Includes full upper/lower quadrants
                    item {
                        Text(
                            text = "3. Select Affected Teeth on FDI Dental Arc Chart:*",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ToothSelector(
                            selectedTeeth = selectedTeeth,
                            onToothToggled = { toothNumber ->
                                if (selectedTeeth.contains(toothNumber)) {
                                    selectedTeeth.remove(toothNumber)
                                } else {
                                    selectedTeeth.add(toothNumber)
                                }
                            }
                        )
                    }

                    // Shade Selector Choice Chips
                    item {
                        Text(
                            text = "4. Prosthodontic Color Shade Guide (Vita Classic):",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(shadesList) { shade ->
                                val isSelected = selectedShade == shade
                                val colorMatch = when (shade) {
                                    "A1" -> TeethPorcelainA1
                                    "A2" -> TeethPorcelainA2
                                    "B1" -> TeethPorcelainB1
                                    else -> TeethPorcelainA1.copy(alpha = 0.5f)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ClinicalTealLight else MaterialTheme.colorScheme.surface)
                                        .border(2.dp, if (isSelected) ClinicalTeal else colorMatch, RoundedCornerShape(8.dp))
                                        .clickable { selectedShade = shade }
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(colorMatch, RoundedCornerShape(2.dp))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = shade,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Timeline Slider & Delivery options
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "5. Tech Lab Delivery Timeline:",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "$deliveryDays working days",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ClinicalTeal
                                    )
                                }
                                Slider(
                                    value = deliveryDays.toFloat(),
                                    onValueChange = { deliveryDays = it.toInt() },
                                    valueRange = 1f..21f,
                                    steps = 20,
                                    colors = SliderDefaults.colors(
                                        activeTrackColor = ClinicalTealLight,
                                        thumbColor = ClinicalTeal
                                    )
                                )
                            }
                        }
                    }

                    // Lab Note
                    item {
                        OutlinedTextField(
                            value = labNote,
                            onValueChange = { labNote = it },
                            label = { Text("Clinical Technicians parameters (Veneer thickness, Screw torque)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }

                    // Submit actions
                    item {
                        Button(
                            onClick = {
                                if (workTitle.isNotEmpty() && patientIdForWork.isNotEmpty() && selectedTeeth.isNotEmpty()) {
                                    viewModel.createWorkOrder(
                                        title = workTitle,
                                        workType = selectedWorkType,
                                        teeth = selectedTeeth.toList(),
                                        teethColor = selectedShade,
                                        note = labNote,
                                        patientId = patientIdForWork,
                                        deliveryDays = deliveryDays
                                    )
                                    isAddingWork = false
                                    // Reset fields
                                    workTitle = ""
                                    selectedTeeth.clear()
                                    labNote = ""
                                    patientIdForWork = ""
                                } else {
                                    Toast.makeText(context, "Title, Patient, and at least 1 Tooth selection are mandatory.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ClinicalTeal),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("save_work_btn")
                        ) {
                            Text("Dispatch Lab Order to Supabase", fontWeight = FontWeight.Bold)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            } else {
                // RENDER ACTIVE WORKS LIST FROM SUPABASE
                if (works.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Build, "Empty Works", modifier = Modifier.size(48.dp), tint = Color.LightGray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No laboratory orders tracked",
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = "Use Log Lab Order fab below to draft custom veneers or implants.",
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
                        items(works) { work ->
                            // Look up patient name locally from cached state
                            val pName = patients.find { it.id == work.patientId }?.fullName ?: "Allied Patient"
                            WorkOrderCard(work = work, patientName = pName)
                        }
                        item {
                            Spacer(modifier = Modifier.height(88.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkOrderCard(
    work: Work,
    patientName: String
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val cardBg = if (isDark) Color(0xFF22202E) else Color(0xFFFEF7FF)
    val cardBorderColor = if (isDark) Color(0xFF3B3846) else Color(0xFFEADDFF)
    val brandPurple = Color(0xFF6750A4)
    val brandLightPurple = Color(0xFFEADDFF)
    val borderGray = Color(0xFFCAC4D0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Urgent/Timeline pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(brandLightPurple)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = (work.workType ?: "Tech lab order").uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = brandPurple,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = patientName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF1C1B1F)
                    )

                    Text(
                        text = "Work: ${work.title}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) Color.LightGray else Color(0xFF49454F),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Tooth badge representation from the mockup
                val firstTooth = work.teeth?.firstOrNull()?.toString() ?: "ALL"
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderGray),
                    modifier = Modifier.width(56.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tooth",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            text = firstTooth,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = brandPurple
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stats footer/Timeline row with divider
            HorizontalDivider(
                color = cardBorderColor,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Delivery Due
                Column {
                    Text(
                        text = "DELIVERY DUE",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.Gray else Color(0xFF74777F)
                    )
                    Text(
                        text = work.deliveryDate ?: "In 5 days",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else Color(0xFF1C1B1F)
                    )
                }

                // Vertical separator
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .background(cardBorderColor)
                )

                // Shade Info
                Column {
                    Text(
                        text = "SHADE",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.Gray else Color(0xFF74777F)
                    )
                    Text(
                        text = work.teethColor ?: "A2",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = brandPurple
                    )
                }

                if (!work.teeth.isNullOrEmpty() && work.teeth.size > 1) {
                    // Vertical separator
                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .width(1.dp)
                            .background(cardBorderColor)
                    )

                    // Multiple teeth listed
                    Column {
                        Text(
                            text = "ALL SEATS",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.Gray else Color(0xFF74777F)
                        )
                        Text(
                            text = work.teeth.joinToString(", "),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) Color.White else Color(0xFF49454F)
                        )
                    }
                }
            }

            // Note Box if exist
            if (!work.note.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) Color.Black.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.5f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Instructions: ${work.note}",
                        fontSize = 11.sp,
                        color = if (isDark) Color.LightGray else Color.DarkGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
