package com.example.ui.partner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.model.*
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PartnerSettlementScreen(
    user: User,
    gyms: List<Gym>,
    visits: List<Visit>,
    settlements: List<Settlement>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val partnerGym = remember(gyms, user.uid) {
        gyms.find { it.ownerId == user.uid } ?: gyms.first()
    }
    val gymVisits = remember(visits, partnerGym.id) {
        visits.filter { it.gymId == partnerGym.id }
    }

    var selectedDateRange by remember { mutableStateOf("All Time") }
    var selectedSettlementFilter by remember { mutableStateOf("All") }
    var showCsvExportDialog by remember { mutableStateOf(false) }

    // Date filtering logic
    val now = System.currentTimeMillis()
    val filteredVisits = remember(gymVisits, selectedDateRange, selectedSettlementFilter) {
        gymVisits.filter { visit ->
            val matchesDate = when (selectedDateRange) {
                "Today" -> now - visit.checkInTimestamp <= 86400000L
                "This Week" -> now - visit.checkInTimestamp <= 86400000L * 7
                "This Month" -> now - visit.checkInTimestamp <= 86400000L * 30
                else -> true
            }
            val matchesStatus = when (selectedSettlementFilter) {
                "Pending" -> visit.payoutStatus == SettlementStatus.PENDING
                "Paid" -> visit.payoutStatus == SettlementStatus.PAID
                else -> true
            }
            matchesDate && matchesStatus
        }
    }

    // Summary calculations for period
    val totalVisitsPeriod = filteredVisits.size
    val totalPendingPayout = filteredVisits.filter { it.payoutStatus == SettlementStatus.PENDING && it.validationResult == ValidationResult.APPROVED }
        .sumOf { it.payoutAmount }
    val totalPaidPayout = filteredVisits.filter { it.payoutStatus == SettlementStatus.PAID && it.validationResult == ValidationResult.APPROVED }
        .sumOf { it.payoutAmount }

    // CSV generation content
    val csvContent = remember(filteredVisits) {
        val header = "Visit ID,Date,Time,Member,Result,Payout Amount,Status\n"
        val rows = filteredVisits.joinToString("\n") { v ->
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(v.checkInTimestamp))
            val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(v.checkInTimestamp))
            val nameParts = v.userName.split(" ")
            val privName = if (nameParts.size >= 2) "${nameParts.first()} ${nameParts.last().take(1)}." else v.userName
            "${v.id},$dateStr,$timeStr,\"$privName\",${v.validationResult.name},${v.payoutAmount},${v.payoutStatus.name}"
        }
        header + rows
    }

    Scaffold(
        containerColor = NomadConcrete,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NomadMist)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = NomadInk
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Visits & Settlements",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadInk
                            )
                            Text(
                                text = "${partnerGym.name} • Ledger & Payout Tracking",
                                fontSize = 11.sp,
                                color = NomadSteel
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { showCsvExportDialog = true },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NomadInk),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Outlined.FileDownload, contentDescription = null, modifier = Modifier.size(15.dp), tint = NomadInk)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export CSV", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = NomadInk)
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp)
        ) {
            // 1. SUMMARY STRIP ABOVE THE TABLE
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "PERIOD VISITS",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel
                            )
                            Text(
                                text = "$totalVisitsPeriod",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadInk
                            )
                        }

                        Column {
                            Text(
                                text = "PENDING PAYOUT",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel
                            )
                            Text(
                                text = "$${String.format("%.2f", totalPendingPayout)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadMoss
                            )
                        }

                        Column {
                            Text(
                                text = "ALREADY PAID",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel
                            )
                            Text(
                                text = "$${String.format("%.2f", totalPaidPayout)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadInk
                            )
                        }
                    }
                }
            }

            // 2. FILTER STRIP (Date Range & Settlement Status)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Date range chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All Time", "Today", "This Week", "This Month").forEach { range ->
                            val isSelected = selectedDateRange == range
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) NomadInk else NomadMist,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NomadInk else NomadLine),
                                modifier = Modifier.clickable { selectedDateRange = range }
                            ) {
                                Text(
                                    text = range,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else NomadSteel,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Status filter chips
                        listOf("All", "Pending", "Paid").forEach { status ->
                            val isSelected = selectedSettlementFilter == status
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) FitLoopYellow else NomadMist,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) FitLoopYellow else NomadLine),
                                modifier = Modifier.clickable { selectedSettlementFilter = status }
                            ) {
                                Text(
                                    text = "Status: $status",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) NomadInk else NomadSteel,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 3. TABLE VIEW (Dense, line borders, no card chrome)
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        // Table Header Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE2E6E9).copy(alpha = 0.6f))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DATE/TIME",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel,
                                modifier = Modifier.weight(1.2f)
                            )
                            Text(
                                text = "MEMBER",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel,
                                modifier = Modifier.weight(1.2f)
                            )
                            Text(
                                text = "RESULT",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "PAYOUT",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel,
                                modifier = Modifier.weight(0.9f)
                            )
                            Text(
                                text = "STATUS",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        HorizontalDivider(color = NomadLine, thickness = 1.dp)

                        if (filteredVisits.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No visit records found matching filters.",
                                    fontSize = 12.sp,
                                    color = NomadSteel
                                )
                            }
                        } else {
                            filteredVisits.forEachIndexed { index, visit ->
                                val dateStr = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(Date(visit.checkInTimestamp))
                                val isApproved = visit.validationResult == ValidationResult.APPROVED
                                val nameParts = visit.userName.split(" ")
                                val privName = if (nameParts.size >= 2) "${nameParts.first()} ${nameParts.last().take(1)}." else visit.userName.ifEmpty { "Nomad M." }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (index % 2 == 0) Color.White else NomadMist)
                                        .padding(horizontal = 12.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dateStr,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = NomadInk,
                                        modifier = Modifier.weight(1.2f)
                                    )
                                    Text(
                                        text = privName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = NomadInk,
                                        modifier = Modifier.weight(1.2f)
                                    )
                                    // Result tag
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isApproved) NomadMoss.copy(alpha = 0.15f) else NomadBrick.copy(alpha = 0.15f),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = if (isApproved) "APPROVED" else "DENIED",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isApproved) NomadMoss else NomadBrick,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = if (isApproved) "$${String.format("%.2f", visit.payoutAmount)}" else "$0.00",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isApproved) NomadInk else NomadSteel,
                                        modifier = Modifier.weight(0.9f)
                                    )
                                    Text(
                                        text = when (visit.payoutStatus) {
                                            SettlementStatus.PAID -> "Paid"
                                            SettlementStatus.PENDING -> "Pending"
                                        },
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (visit.payoutStatus) {
                                            SettlementStatus.PAID -> NomadMoss
                                            SettlementStatus.PENDING -> NomadAmber
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (index < filteredVisits.lastIndex) {
                                    HorizontalDivider(color = NomadLine.copy(alpha = 0.4f), thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // CSV Export Modal Dialog
    if (showCsvExportDialog) {
        AlertDialog(
            onDismissRequest = { showCsvExportDialog = false },
            shape = RoundedCornerShape(28.dp),
            title = {
                Text("Settlement CSV Export", fontWeight = FontWeight.Bold, color = NomadInk)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "The ledger records have been formatted in standard CSV format:",
                        fontSize = 12.sp,
                        color = NomadSteel
                    )
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E2024),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        Text(
                            text = csvContent,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color.White,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCsvExportDialog = false },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NomadInk)
                ) {
                    Text("Done / Exported", color = Color.White)
                }
            }
        )
    }
}
