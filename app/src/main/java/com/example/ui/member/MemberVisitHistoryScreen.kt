package com.example.ui.member

import androidx.compose.animation.*
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
import com.example.model.User
import com.example.model.ValidationResult
import com.example.model.Visit
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MemberVisitHistoryScreen(
    user: User,
    visits: List<Visit>,
    onReportIssueForVisit: (Visit) -> Unit,
    modifier: Modifier = Modifier
) {
    val userVisits = remember(visits, user.uid) {
        visits.filter { it.userId == user.uid }.sortedByDescending { it.checkInTimestamp }
    }

    // Group visits chronologically by Month/Year
    val groupedVisits = remember(userVisits) {
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        userVisits.groupBy { visit ->
            monthFormat.format(Date(visit.checkInTimestamp))
        }
    }

    var expandedVisitId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = NomadConcrete,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NomadMist)
                    .border(1.dp, NomadLine)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Visit History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )
                Text(
                    text = "All gym check-in records and entrance validation logs",
                    fontSize = 12.sp,
                    color = NomadSteel
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        if (userVisits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    icon = Icons.Outlined.History,
                    title = "No visits logged yet",
                    message = "Your visits will show up here once you check in for the first time."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
            ) {
                groupedVisits.forEach { (monthHeader, visitsInMonth) ->
                    item(key = "header_$monthHeader") {
                        Text(
                            text = monthHeader.uppercase(),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadFog,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    items(visitsInMonth, key = { it.id }) { visit ->
                        val isExpanded = expandedVisitId == visit.id
                        val isApproved = visit.validationResult == ValidationResult.APPROVED
                        val dateStr = remember(visit.checkInTimestamp) {
                            SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(visit.checkInTimestamp))
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            color = NomadMist,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedVisitId = if (isExpanded) null else visit.id
                                    }
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
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
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = visit.gymName.ifEmpty { "Partner Gym" },
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NomadInk
                                        )
                                        Text(
                                            text = "${visit.gymCity} • $dateStr",
                                            fontSize = 11.sp,
                                            color = NomadSteel
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isApproved) NomadMoss.copy(alpha = 0.15f) else NomadBrick.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = if (isApproved) "APPROVED" else "DENIED",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isApproved) NomadMoss else NomadBrick,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                // Expanded in-place details
                                AnimatedVisibility(visible = isExpanded) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 12.dp)
                                            .border(1.dp, NomadLine, RoundedCornerShape(16.dp))
                                            .background(NomadConcrete, RoundedCornerShape(16.dp))
                                            .padding(14.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Credential Code:", fontSize = 11.sp, color = NomadSteel)
                                            Text(visit.credentialCode, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = NomadInk)
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Visit ID:", fontSize = 11.sp, color = NomadSteel)
                                            Text(visit.id, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = NomadFog)
                                        }

                                        if (!isApproved && !visit.denialReason.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Denial Reason: ${visit.denialReason}",
                                                fontSize = 11.sp,
                                                color = NomadBrick,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Report an issue button pre-filled with this visit
                                        OutlinedButton(
                                            onClick = { onReportIssueForVisit(visit) },
                                            shape = RoundedCornerShape(16.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                                            modifier = Modifier.fillMaxWidth().height(38.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.ReportProblem,
                                                contentDescription = null,
                                                tint = NomadInk,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Report an Issue for this Visit", fontSize = 11.sp, color = NomadInk)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
