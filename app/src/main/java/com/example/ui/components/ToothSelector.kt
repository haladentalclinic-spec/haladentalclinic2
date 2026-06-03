package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ClinicalTeal
import com.example.ui.theme.ClinicalTealLight

@Composable
fun ToothSelector(
    selectedTeeth: List<Int>,
    onToothToggled: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val quadrants = listOf(
        QuadrantInfo("Upper Right", (18 downTo 11).toList(), isUpper = true),
        QuadrantInfo("Upper Left", (21..28).toList(), isUpper = true),
        QuadrantInfo("Lower Right", (48 downTo 41).toList(), isUpper = false),
        QuadrantInfo("Lower Left", (31..38).toList(), isUpper = false)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Clinical Arch Chart (FDI)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${selectedTeeth.size} selected",
                style = MaterialTheme.typography.labelMedium,
                color = if (selectedTeeth.isNotEmpty()) ClinicalTealLight else Color.Gray,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Upper jaw structure
        Text(
            text = "Upper Jaw (Maxilla)",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Quadrant 1 (Upper Right)
            Box(modifier = Modifier.weight(1f)) {
                QuadrantGrid(
                    quadrant = quadrants[0],
                    selectedTeeth = selectedTeeth,
                    onToothToggled = onToothToggled
                )
            }
            // Divider line
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(68.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
            )
            // Quadrant 2 (Upper Left)
            Box(modifier = Modifier.weight(1f)) {
                QuadrantGrid(
                    quadrant = quadrants[1],
                    selectedTeeth = selectedTeeth,
                    onToothToggled = onToothToggled
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Lower jaw structure
        Text(
            text = "Lower Jaw (Mandible)",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Quadrant 4 (Lower Right/FDI Q4)
            Box(modifier = Modifier.weight(1f)) {
                QuadrantGrid(
                    quadrant = quadrants[2],
                    selectedTeeth = selectedTeeth,
                    onToothToggled = onToothToggled
                )
            }
            // Divider line
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(68.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
            )
            // Quadrant 3 (Lower Left/FDI Q3)
            Box(modifier = Modifier.weight(1f)) {
                QuadrantGrid(
                    quadrant = quadrants[3],
                    selectedTeeth = selectedTeeth,
                    onToothToggled = onToothToggled
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Legend / Quick Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(ClinicalTeal, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Target restoration", fontSize = 11.sp, color = Color.Gray)

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Healthy/Untouched", fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun QuadrantGrid(
    quadrant: QuadrantInfo,
    selectedTeeth: List<Int>,
    onToothToggled: (Int) -> Unit
) {
    // 4 teeth per row for beautiful compact alignment
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
    ) {
        items(quadrant.teeth) { toothNumber ->
            val isSelected = selectedTeeth.contains(toothNumber)
            ToothItem(
                toothNumber = toothNumber,
                isSelected = isSelected,
                onClick = { onToothToggled(toothNumber) }
            )
        }
    }
}

@Composable
private fun ToothItem(
    toothNumber: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) ClinicalTeal else MaterialTheme.colorScheme.surface
    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
    val borderColor = if (isSelected) ClinicalTeal else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Cute clinical tooth representation
            Text(
                text = "🦷",
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 1.dp)
            )
            Text(
                text = toothNumber.toString(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

private data class QuadrantInfo(
    val name: String,
    val teeth: List<Int>,
    val isUpper: Boolean
)
