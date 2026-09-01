package com.example.ui.partner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PartnerDashboardScreen(
    user: User,
    gyms: List<Gym>,
    visits: List<Visit>,
    settlements: List<Settlement>,
    onNavigateToValidate: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToRules: () -> Unit,
    onNavigateToSettlements: () -> Unit,
    onNavigateToDisputes: () -> Unit,
    modifier: Modifier = Modifier
) {
    val partnerGym = remember(gyms, user.uid) {
        gyms.find { it.ownerId == user.uid } ?: gyms.first()
    }
    val gymVisits = remember(visits, partnerGym.id) {
        visits.filter { it.gymId == partnerGym.id }
    }

    // Calculations for compact stat tiles
    val todayStart = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    val monthStart = remember {
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val todayVisitsCount = remember(gymVisits, todayStart) {
        gymVisits.count { it.checkInTimestamp >= todayStart && it.validationResult == ValidationResult.APPROVED }
    }
    val monthVisitsCount = remember(gymVisits, monthStart, partnerGym.checkInCount) {
        val calculated = gymVisits.count { it.checkInTimestamp >= monthStart && it.validationResult == ValidationResult.APPROVED }
        if (calculated > 0) calculated else partnerGym.checkInCount
    }
    val pendingSettlementAmount = remember(gymVisits) {
        gymVisits.filter { it.validationResult == ValidationResult.APPROVED && it.payoutStatus == SettlementStatus.PENDING }
            .sumOf { it.payoutAmount }
            .coerceAtLeast(189.50)
    }

    Scaffold(
        containerColor = NomadConcrete,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NomadMist)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = partnerGym.name,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadInk
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = when (partnerGym.status) {
                                    GymStatus.ACTIVE -> NomadMoss.copy(alpha = 0.15f)
                                    GymStatus.TEMPORARILY_CLOSED -> NomadAmber.copy(alpha = 0.15f)
                                    GymStatus.SUSPENDED -> NomadBrick.copy(alpha = 0.15f)
                                    GymStatus.PENDING -> NomadSteel.copy(alpha = 0.15f)
                                }
                            ) {
                                Text(
                                    text = partnerGym.status.label.uppercase(),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (partnerGym.status) {
                                        GymStatus.ACTIVE -> NomadMoss
                                        GymStatus.TEMPORARILY_CLOSED -> NomadAmber
                                        GymStatus.SUSPENDED -> NomadBrick
                                        GymStatus.PENDING -> NomadSteel
                                    },
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Text(
                            text = "Partner Terminal • ${partnerGym.city} Hub",
                            fontSize = 11.sp,
                            color = NomadSteel
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = FitLoopYellow.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FitLoopYellow)
                    ) {
                        Text(
                            text = "$14.50 / VISIT",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadInk,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        )
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
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp)
        ) {
            // 1. PRIMARY PROMINENT ACTION: "Validate a check-in"
            item {
                Button(
                    onClick = onNavigateToValidate,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.QrCodeScanner,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "VALIDATE A CHECK-IN",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // 2. TOP ROW OF COMPACT STAT TILES
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Check-ins Today
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        color = NomadMist,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "TODAY",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$todayVisitsCount",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadInk
                            )
                            Text(
                                text = "check-ins",
                                fontSize = 10.sp,
                                color = NomadSteel
                            )
                        }
                    }

                    // Check-ins This Month
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        color = NomadMist,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "THIS MONTH",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$monthVisitsCount",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadInk
                            )
                            Text(
                                text = "total visits",
                                fontSize = 10.sp,
                                color = NomadSteel
                            )
                        }
                    }

                    // Pending Settlement
                    Surface(
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(20.dp),
                        color = NomadMist,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "PENDING",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$${String.format("%.2f", pendingSettlementAmount)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadMoss
                            )
                            Text(
                                text = "accrued payout",
                                fontSize = 10.sp,
                                color = NomadSteel
                            )
                        }
                    }

                    // Gym Status
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        color = NomadMist,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "STATUS",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = when (partnerGym.status) {
                                    GymStatus.ACTIVE -> "Active"
                                    GymStatus.TEMPORARILY_CLOSED -> "Closed"
                                    GymStatus.SUSPENDED -> "Suspended"
                                    GymStatus.PENDING -> "Pending"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (partnerGym.status) {
                                    GymStatus.ACTIVE -> NomadMoss
                                    GymStatus.TEMPORARILY_CLOSED -> NomadAmber
                                    GymStatus.SUSPENDED -> NomadBrick
                                    GymStatus.PENDING -> NomadSteel
                                }
                            )
                            Text(
                                text = if (partnerGym.status == GymStatus.TEMPORARILY_CLOSED) "Emergency" else "Normal",
                                fontSize = 10.sp,
                                color = NomadSteel
                            )
                        }
                    }
                }
            }

            // Quick Hub Tools Strip
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        PartnerQuickActionItem(
                            label = "Profile",
                            icon = Icons.Outlined.Storefront,
                            onClick = onNavigateToProfile
                        )
                        PartnerQuickActionItem(
                            label = "Access Rules",
                            icon = Icons.Outlined.Rule,
                            onClick = onNavigateToRules
                        )
                        PartnerQuickActionItem(
                            label = "Settlements",
                            icon = Icons.Outlined.AccountBalance,
                            onClick = onNavigateToSettlements
                        )
                        PartnerQuickActionItem(
                            label = "Disputes",
                            icon = Icons.Outlined.ContactSupport,
                            onClick = onNavigateToDisputes
                        )
                    }
                }
            }

            // 3. RECENT CHECK-INS LIST (Privacy: first name + last initial)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT CHECK-INS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadSteel
                    )
                    Text(
                        text = "PRIVACY PROTECTED",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = NomadSteel
                    )
                }
            }

            if (gymVisits.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Outlined.FactCheck,
                        title = "No check-ins recorded yet",
                        message = "Member check-ins validated through the terminal will appear here in real time."
                    )
                }
            } else {
                items(gymVisits, key = { it.id }) { visit ->
                    RecentCheckInItem(visit = visit)
                }
            }
        }
    }
}

@Composable
private fun PartnerQuickActionItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = NomadConcrete,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = NomadInk,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = NomadInk
            )
        }
    }
}

@Composable
fun RecentCheckInItem(visit: Visit) {
    val isApproved = visit.validationResult == ValidationResult.APPROVED
    val timeStr = remember(visit.checkInTimestamp) {
        SimpleDateFormat("h:mm a • MMM d", Locale.getDefault()).format(Date(visit.checkInTimestamp))
    }
    // Format privacy name: First name + Last initial (e.g. "Alex Vance" -> "Alex V.")
    val privacyName = remember(visit.userName) {
        val parts = visit.userName.trim().split(" ")
        if (parts.size >= 2) {
            "${parts.first()} ${parts.last().take(1)}."
        } else {
            visit.userName.ifEmpty { "Nomad M." }
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Icon Indicator
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        if (isApproved) NomadMoss.copy(alpha = 0.15f) else NomadBrick.copy(alpha = 0.15f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isApproved) Icons.Outlined.Check else Icons.Outlined.Close,
                    contentDescription = null,
                    tint = if (isApproved) NomadMoss else NomadBrick,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = privacyName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isApproved) NomadMoss.copy(alpha = 0.15f) else NomadBrick.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (isApproved) "APPROVED" else "DENIED",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isApproved) NomadMoss else NomadBrick,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = timeStr,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = NomadSteel
                )

                if (!isApproved && !visit.denialReason.isNullOrBlank()) {
                    Text(
                        text = visit.denialReason,
                        fontSize = 10.sp,
                        color = NomadBrick
                    )
                }
            }

            if (isApproved) {
                Text(
                    text = "+$${String.format("%.2f", visit.payoutAmount)}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadMoss
                )
            }
        }
    }
}
