package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NomadFitRepository
import com.example.model.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminCheckInsTab(
    visits: List<Visit>,
    anomalies: List<CheckInAnomaly>
) {
    var selectedAnomalyForModal by remember { mutableStateOf<CheckInAnomaly?>(null) }
    var selectedVisitForDetail by remember { mutableStateOf<Visit?>(null) }

    // Dialog state for Override
    var overrideVisitTarget by remember { mutableStateOf<Visit?>(null) }
    var overrideReason by remember { mutableStateOf("") }

    var filterOnlyAnomalies by remember { mutableStateOf(false) }

    val displayedVisits = remember(visits, anomalies, filterOnlyAnomalies) {
        if (filterOnlyAnomalies) {
            val anomalyVisitIds = anomalies.map { it.visitId }.toSet()
            visits.filter { it.id in anomalyVisitIds }
        } else {
            visits
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Controls Row
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            color = NomadMist,
            border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LIVE CHECK-IN SENTRY FEED",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk
                    )
                    Text(
                        text = "Real-time verification terminal telemetry & fraud detection",
                        fontSize = 10.sp,
                        color = NomadSteel
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Only Anomalies", fontSize = 11.sp, color = NomadSteel)
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = filterOnlyAnomalies,
                        onCheckedChange = { filterOnlyAnomalies = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NomadSignal,
                            uncheckedThumbColor = NomadFog,
                            uncheckedTrackColor = NomadConcrete
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Table Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
            color = Color(0xFF282A2F)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "TIMESTAMP & MEMBER", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1.8f))
                Text(text = "FACILITY & CITY", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1.8f))
                Text(text = "VERDICT", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1.2f))
            }
        }

        // Live Feed List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(displayedVisits, key = { it.id }) { visit ->
                val anomaly = anomalies.find { it.visitId == visit.id }

                CheckInFeedRow(
                    visit = visit,
                    anomaly = anomaly,
                    onClick = {
                        if (anomaly != null) {
                            selectedAnomalyForModal = anomaly
                        } else {
                            selectedVisitForDetail = visit
                        }
                    }
                )
            }
        }
    }

    // Anomaly Action & Inspection Modal
    selectedAnomalyForModal?.let { anomaly ->
        val relatedVisit = visits.find { it.id == anomaly.visitId }

        AlertDialog(
            onDismissRequest = { selectedAnomalyForModal = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = NomadSignal,
                        modifier = Modifier.size(10.dp)
                    ) {}
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Anomaly: ${anomaly.type.label}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NomadInk
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = NomadConcrete,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "DETECTION TELEMETRY",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel
                            )
                            Text(
                                text = anomaly.details,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = NomadInk
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Member: ${relatedVisit?.userName ?: "Unknown"} (UID: ${relatedVisit?.userId?.take(8)})",
                                fontSize = 11.sp,
                                color = NomadSteel
                            )
                            Text(
                                text = "Terminal: ${relatedVisit?.gymName} (${relatedVisit?.gymCity})",
                                fontSize = 11.sp,
                                color = NomadSteel
                            )
                            Text(
                                text = "Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(anomaly.detectedAt))}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = NomadSteel
                            )
                        }
                    }

                    if (anomaly.isResolved) {
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = NomadMoss.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "RESOLVED: ${anomaly.resolutionReason}",
                                fontSize = 11.sp,
                                color = NomadMoss,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "Choose an operational action:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadInk
                        )
                    }
                }
            },
            confirmButton = {
                if (!anomaly.isResolved) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = {
                                NomadFitRepository.escalateAnomalyToTicket(anomaly)
                                selectedAnomalyForModal = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                            shape = RoundedCornerShape(3.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Escalate Ticket", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = {
                                overrideVisitTarget = relatedVisit
                                overrideReason = ""
                                selectedAnomalyForModal = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                            shape = RoundedCornerShape(3.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Override & Approve", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedAnomalyForModal = null }) {
                    Text("Close", fontSize = 11.sp, color = NomadSteel)
                }
            }
        )
    }

    // Override Reason Modal
    overrideVisitTarget?.let { visit ->
        AlertDialog(
            onDismissRequest = { overrideVisitTarget = null },
            title = { Text("Grant Administrative Check-In Override", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Grant manual access to ${visit.userName} at ${visit.gymName}? This will approve partner payout and resolve active anomalies.",
                        fontSize = 12.sp,
                        color = NomadInk
                    )
                    OutlinedTextField(
                        value = overrideReason,
                        onValueChange = { overrideReason = it },
                        placeholder = { Text("Required override reason for audit log...", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        NomadFitRepository.overrideCheckIn(visit.id, overrideReason)
                        overrideVisitTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text("Confirm Override", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { overrideVisitTarget = null }) {
                    Text("Cancel", fontSize = 11.sp, color = NomadSteel)
                }
            }
        )
    }

    // Standard Visit Detail Modal
    selectedVisitForDetail?.let { visit ->
        AlertDialog(
            onDismissRequest = { selectedVisitForDetail = null },
            title = { Text("Check-In Verification Record", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Member: ${visit.userName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NomadInk)
                    Text("Gym: ${visit.gymName} (${visit.gymCity})", fontSize = 12.sp, color = NomadInk)
                    Text("Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(visit.checkInTimestamp))}", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = NomadSteel)
                    Text("Status: ${visit.validationResult.name}", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = if (visit.validationResult == ValidationResult.APPROVED) NomadMoss else NomadBrick)
                    if (visit.denialReason != null) {
                        Text("Denial Reason: ${visit.denialReason}", fontSize = 11.sp, color = NomadBrick)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedVisitForDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text("Close", fontSize = 11.sp, color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun CheckInFeedRow(
    visit: Visit,
    anomaly: CheckInAnomaly?,
    onClick: () -> Unit
) {
    val isAnomaly = anomaly != null && !anomaly.isResolved
    val isDenied = visit.validationResult == ValidationResult.DENIED

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(3.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isAnomaly) NomadSignal.copy(alpha = 0.6f) else NomadLine
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // High visibility visual stripe for anomalies
            if (isAnomaly) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(NomadSignal)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Column 1: Time & Member
                Column(modifier = Modifier.weight(1.8f)) {
                    Text(
                        text = SimpleDateFormat("HH:mm:ss, MMM d", Locale.getDefault()).format(Date(visit.checkInTimestamp)),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = NomadSteel
                    )
                    Text(
                        text = visit.userName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk,
                        maxLines = 1
                    )
                }

                // Column 2: Facility & City
                Column(modifier = Modifier.weight(1.8f)) {
                    Text(
                        text = visit.gymName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = NomadInk,
                        maxLines = 1
                    )
                    Text(
                        text = visit.gymCity,
                        fontSize = 10.sp,
                        color = NomadSteel
                    )
                }

                // Column 3: Verdict & Anomaly Flag
                Column(
                    modifier = Modifier.weight(1.2f),
                    horizontalAlignment = Alignment.End
                ) {
                    if (isAnomaly) {
                        Surface(
                            shape = RoundedCornerShape(2.dp),
                            color = NomadSignal.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "ANOMALY",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSignal,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(2.dp),
                            color = if (visit.validationResult == ValidationResult.APPROVED) NomadMoss.copy(alpha = 0.12f) else NomadBrick.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = visit.validationResult.name,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (visit.validationResult == ValidationResult.APPROVED) NomadMoss else NomadBrick,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
