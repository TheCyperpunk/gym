package com.example.ui.partner

import androidx.compose.animation.AnimatedVisibility
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
fun PartnerDisputesScreen(
    user: User,
    gyms: List<Gym>,
    supportTickets: List<SupportTicket>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val partnerGym = remember(gyms, user.uid) {
        gyms.find { it.ownerId == user.uid } ?: gyms.first()
    }
    val gymTickets = remember(supportTickets, user.uid, partnerGym.name) {
        supportTickets.filter {
            it.raisedByUserId == user.uid ||
            it.description.contains(partnerGym.name, ignoreCase = true) ||
            it.subject.contains(partnerGym.name, ignoreCase = true)
        }
    }

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Payout discrepancy") }
    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showSuccessBanner by remember { mutableStateOf(false) }

    val categories = listOf("Payout discrepancy", "Member dispute", "Terminal malfunction", "Platform issue", "Other")

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
                                text = "Issues & Disputes",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadInk
                            )
                            Text(
                                text = "${partnerGym.name} • Partner Escalation Desk",
                                fontSize = 11.sp,
                                color = NomadSteel
                            )
                        }
                    }

                    Button(
                        onClick = { showCreateDialog = true },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Raise Issue", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
            if (showSuccessBanner) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = NomadMoss
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Dispute ticket submitted. Operations team notified.",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                            IconButton(
                                onClick = { showSuccessBanner = false },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Outlined.Close, contentDescription = "Dismiss", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PARTNER TICKETS (${gymTickets.size})",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadSteel
                    )
                    Text(
                        text = "SLA: < 24 HOURS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadSteel
                    )
                }
            }

            if (gymTickets.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Outlined.ContactSupport,
                        title = "No active disputes",
                        message = "All payouts and check-in verifications are operating smoothly. Raise an issue if you detect any discrepancy."
                    )
                }
            } else {
                items(gymTickets, key = { it.id }) { ticket ->
                    PartnerTicketItem(ticket = ticket)
                }
            }
        }
    }

    // Raise Issue Dialog Form
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            shape = RoundedCornerShape(28.dp),
            title = {
                Text(
                    text = "Raise a Partner Issue",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Select Category:",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NomadSteel
                    )

                    // Category radio/chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.take(3).forEach { cat ->
                            val isSel = selectedCategory == cat
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSel) NomadInk else NomadMist,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) NomadInk else NomadLine),
                                modifier = Modifier.clickable { selectedCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) Color.White else NomadSteel,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject / Reference", fontSize = 12.sp) },
                        placeholder = { Text("e.g. Missing settlement for visit #001") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NomadSteel,
                            unfocusedBorderColor = NomadLine,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Dispute Details", fontSize = 12.sp) },
                        placeholder = { Text("Describe the discrepancy with visit timestamp or amount...") },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NomadSteel,
                            unfocusedBorderColor = NomadLine,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (subject.isNotBlank()) {
                            NomadFitRepository.createSupportTicket(
                                category = selectedCategory,
                                subject = "[Partner Escalation] $subject",
                                description = description
                            )
                            showCreateDialog = false
                            subject = ""
                            description = ""
                            showSuccessBanner = true
                        }
                    },
                    enabled = subject.isNotBlank(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NomadSignal)
                ) {
                    Text("Submit Dispute", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = NomadSteel)
                }
            }
        )
    }
}

@Composable
private fun PartnerTicketItem(ticket: SupportTicket) {
    val dateStr = remember(ticket.createdAt) {
        SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(ticket.createdAt))
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = NomadLine.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = ticket.category.uppercase(),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadSteel,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when (ticket.status) {
                        TicketStatus.OPEN -> NomadAmber.copy(alpha = 0.15f)
                        TicketStatus.IN_PROGRESS -> Color(0xFFE2F0FD)
                        TicketStatus.RESOLVED -> NomadMoss.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = ticket.status.label.uppercase(),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (ticket.status) {
                            TicketStatus.OPEN -> NomadAmber
                            TicketStatus.IN_PROGRESS -> Color(0xFF1E64D4)
                            TicketStatus.RESOLVED -> NomadMoss
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = ticket.subject,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NomadInk
            )

            Text(
                text = ticket.description,
                fontSize = 12.sp,
                color = NomadSteel,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = "Logged: $dateStr • ID: ${ticket.id}",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = NomadFog
            )
        }
    }
}
