package com.example.ui.admin

import androidx.compose.foundation.Canvas
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
import com.example.model.AuditLogEntry
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminAnalyticsTab(
    auditLogs: List<AuditLogEntry>
) {
    var selectedSubTab by remember { mutableStateOf(0) } // 0: Financial & Growth Analytics, 1: System Audit Trail
    var auditSearchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Sub-Navigation
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            color = NomadMist,
            border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
        ) {
            Row(modifier = Modifier.padding(3.dp)) {
                AdminSubNavButton(
                    label = "Operations Analytics",
                    isSelected = selectedSubTab == 0,
                    modifier = Modifier.weight(1f)
                ) { selectedSubTab = 0 }

                AdminSubNavButton(
                    label = "System Audit Trail (${auditLogs.size})",
                    isSelected = selectedSubTab == 1,
                    modifier = Modifier.weight(1f)
                ) { selectedSubTab = 1 }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedSubTab) {
            0 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    // 1. Revenue vs. Partner Payouts (Contribution Margin)
                    item {
                        ContributionMarginCard()
                    }

                    // 2. Members by City Distribution
                    item {
                        CityDistributionCard()
                    }

                    // 3. Visits by Gym Tier
                    item {
                        TierVisitsCard()
                    }

                    // 4. Cohort Retention & Churn Rate
                    item {
                        CohortRetentionCard()
                    }
                }
            }
            1 -> {
                // System Audit Trail
                val filteredLogs = auditLogs.filter {
                    it.action.contains(auditSearchQuery, ignoreCase = true) ||
                            it.targetName.contains(auditSearchQuery, ignoreCase = true) ||
                            it.actorAdminName.contains(auditSearchQuery, ignoreCase = true) ||
                            it.reason.contains(auditSearchQuery, ignoreCase = true)
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = auditSearchQuery,
                        onValueChange = { auditSearchQuery = it },
                        placeholder = { Text("Search audit trail by actor, action, target...", fontSize = 12.sp, color = NomadSteel) },
                        leadingIcon = {
                            Icon(Icons.Outlined.Search, contentDescription = null, tint = NomadSteel, modifier = Modifier.size(16.dp))
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = NomadInk,
                            unfocusedBorderColor = NomadLine
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
                        color = Color(0xFF282A2F)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "ACTION & ACTOR", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1.8f))
                            Text(text = "TARGET & REASON", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(2.2f))
                            Text(text = "TIME", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1f))
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredLogs, key = { it.id }) { log ->
                            AuditLogRow(log = log)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContributionMarginCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "CONTRIBUTION MARGIN & PAYOUT TREND",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NomadInk
            )
            Text(
                text = "Gross subscription GMV vs partner facility disbursement",
                fontSize = 10.sp,
                color = NomadSteel
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Revenue (GMV)", fontSize = 10.sp, color = NomadSteel)
                    Text("$142.8k", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NomadInk)
                }
                Column {
                    Text("Partner Payouts", fontSize = 10.sp, color = NomadSteel)
                    Text("$48.2k", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NomadInk)
                }
                Column {
                    Text("Net Margin", fontSize = 10.sp, color = NomadSteel)
                    Text("$94.6k (66.2%)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NomadSignal)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Historical Comparative Bars
            val months = listOf(
                Triple("Jan", 98, 34),
                Triple("Feb", 112, 39),
                Triple("Mar", 128, 44),
                Triple("Apr", 143, 48)
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                months.forEach { (month, rev, payout) ->
                    val margin = rev - payout
                    val marginPct = (margin.toFloat() / rev * 100).toInt()

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = month, fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NomadInk)
                            Text(
                                text = "Rev: $${rev}k | Payout: $${payout}k | Margin: $${margin}k ($marginPct%)",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = NomadSteel
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Stacked proportion bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(margin.toFloat())
                                    .fillMaxHeight()
                                    .background(NomadInk, RoundedCornerShape(topStart = 2.dp, bottomStart = 2.dp))
                            )
                            Box(
                                modifier = Modifier
                                    .weight(payout.toFloat())
                                    .fillMaxHeight()
                                    .background(NomadSignal, RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(NomadInk))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Platform Margin", fontSize = 9.sp, color = NomadSteel)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(NomadSignal))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Partner Payout", fontSize = 9.sp, color = NomadSteel)
                }
            }
        }
    }
}

@Composable
private fun CityDistributionCard() {
    val cities = listOf(
        Pair("Tokyo", 480),
        Pair("London", 390),
        Pair("New York", 340),
        Pair("Berlin", 170),
        Pair("Singapore", 100)
    )
    val totalMembers = cities.sumOf { it.second }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "ACTIVE MEMBERS BY METROPOLITAN HUB",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NomadInk
            )
            Text(
                text = "Geographic base of 1,480 active passport holders",
                fontSize = 10.sp,
                color = NomadSteel
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                cities.forEach { (city, count) ->
                    val pct = (count.toFloat() / totalMembers * 100)
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = city, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NomadInk)
                            Text(
                                text = "$count members (${String.format("%.1f", pct)}%)",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = NomadSteel
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        LinearProgressIndicator(
                            progress = { count.toFloat() / totalMembers },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = NomadInk,
                            trackColor = NomadConcrete,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TierVisitsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "CHECK-IN VOLUME BY GYM TIER",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NomadInk
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("STANDARD FACILITY", fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NomadSteel)
                    Text("58%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NomadInk)
                    Text("490 check-ins • $12.00 avg payout", fontSize = 10.sp, color = NomadSteel)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("PREMIUM CLUBS", fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NomadSignal)
                    Text("42%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NomadInk)
                    Text("354 check-ins • $18.50 avg payout", fontSize = 10.sp, color = NomadSteel)
                }
            }
        }
    }
}

@Composable
private fun CohortRetentionCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MONTHLY COHORT CHURN RATE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk
                    )
                    Text(
                        text = "Industry benchmark: ~4.5% | Fit loop: 1.8%",
                        fontSize = 10.sp,
                        color = NomadSteel
                    )
                }

                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = NomadMoss.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "-25% vs Q3",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadMoss,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CohortPill("Q3 2025", "2.4% churn")
                CohortPill("Q4 2025", "2.1% churn")
                CohortPill("Q1 2026", "1.9% churn")
                CohortPill("Q2 2026 (Now)", "1.8% churn", isHighlight = true)
            }
        }
    }
}

@Composable
private fun CohortPill(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(3.dp),
        color = if (isHighlight) NomadInk else NomadConcrete,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isHighlight) NomadInk else NomadLine)
    ) {
        Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontFamily = FontFamily.Monospace, fontSize = 8.sp, color = if (isHighlight) NomadFog else NomadSteel)
            Text(text = value, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isHighlight) Color.White else NomadInk)
        }
    }
}

@Composable
private fun AuditLogRow(log: AuditLogEntry) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(3.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Column 1: Action & Actor
            Column(modifier = Modifier.weight(1.8f)) {
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = NomadInk
                ) {
                    Text(
                        text = log.action,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "By ${log.actorAdminName}",
                    fontSize = 10.sp,
                    color = NomadSteel,
                    maxLines = 1
                )
            }

            // Column 2: Target & Reason
            Column(modifier = Modifier.weight(2.2f)) {
                Text(
                    text = "${log.targetType}: ${log.targetName}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk,
                    maxLines = 1
                )
                Text(
                    text = log.reason,
                    fontSize = 10.sp,
                    color = NomadSteel,
                    maxLines = 2
                )
            }

            // Column 3: Time
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(
                    text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp)),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )
                Text(
                    text = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(log.timestamp)),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = NomadSteel
                )
            }
        }
    }
}

@Composable
private fun AdminSubNavButton(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(3.dp),
        color = if (isSelected) NomadInk else Color.Transparent,
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else NomadSteel
            )
        }
    }
}
