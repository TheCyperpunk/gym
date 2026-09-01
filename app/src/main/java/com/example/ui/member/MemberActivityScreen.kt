package com.example.ui.member

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberActivityScreen(
    user: User,
    visits: List<Visit>,
    payments: List<Payment>,
    supportTickets: List<SupportTicket>,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Visits, 1: Payments, 2: Support
    var showCreateTicketDialog by remember { mutableStateOf(false) }

    val userVisits = remember(visits, user.uid) { visits.filter { it.userId == user.uid } }
    val userPayments = remember(payments, user.uid) { payments.filter { it.userId == user.uid } }
    val userTickets = remember(supportTickets, user.uid) { supportTickets.filter { it.raisedByUserId == user.uid } }

    Scaffold(
        containerColor = NomadConcrete,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NomadMist)
                    .border(1.dp, NomadLine)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Activity & History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )
                Text(
                    text = "Track check-in passes, invoices and support requests",
                    fontSize = 12.sp,
                    color = NomadSteel
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Custom Tab Bar
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = NomadConcrete,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        TabPill(
                            label = "Visits (${userVisits.size})",
                            isSelected = selectedTab == 0,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 0 }
                        )
                        TabPill(
                            label = "Invoices (${userPayments.size})",
                            isSelected = selectedTab == 1,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 1 }
                        )
                        TabPill(
                            label = "Support (${userTickets.size})",
                            isSelected = selectedTab == 2,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 2 }
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 80.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // Visits History Tab
                    if (userVisits.isEmpty()) {
                        item {
                            EmptyStateView(
                                icon = Icons.Outlined.History,
                                title = "Zero visits yet",
                                message = "Your gym check-ins will appear here once verified by partner locations."
                            )
                        }
                    } else {
                        items(userVisits, key = { it.id }) { visit ->
                            VisitItemCard(visit = visit)
                        }
                    }
                }
                1 -> {
                    // Invoices Tab
                    if (userPayments.isEmpty()) {
                        item {
                            EmptyStateView(
                                icon = Icons.Outlined.ReceiptLong,
                                title = "No invoices found",
                                message = "Payment history for subscriptions will be recorded here."
                            )
                        }
                    } else {
                        items(userPayments, key = { it.id }) { payment ->
                            PaymentItemCard(payment = payment)
                        }
                    }
                }
                2 -> {
                    // Support Tickets Tab
                    item {
                        Button(
                            onClick = { showCreateTicketDialog = true },
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContactSupport,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open New Support Ticket", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    if (userTickets.isEmpty()) {
                        item {
                            EmptyStateView(
                                icon = Icons.Outlined.QuestionAnswer,
                                title = "No tickets opened",
                                message = "Need help with a check-in or gym access? Open a ticket above."
                            )
                        }
                    } else {
                        items(userTickets, key = { it.id }) { ticket ->
                            TicketItemCard(ticket = ticket)
                        }
                    }
                }
            }
        }
    }

    // Support Ticket Dialog
    if (showCreateTicketDialog) {
        CreateTicketDialog(
            onDismiss = { showCreateTicketDialog = false },
            onSubmit = { category, subject, description ->
                NomadFitRepository.createSupportTicket(category, subject, description)
                showCreateTicketDialog = false
            }
        )
    }
}

@Composable
private fun TabPill(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) NomadInk else Color.Transparent,
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else NomadSteel,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
private fun VisitItemCard(visit: Visit) {
    val dateStr = remember(visit.checkInTimestamp) {
        SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(visit.checkInTimestamp))
    }
    val isApproved = visit.validationResult == ValidationResult.APPROVED

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
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
                if (!isApproved && !visit.denialReason.isNullOrBlank()) {
                    Text(
                        text = "Reason: ${visit.denialReason}",
                        fontSize = 11.sp,
                        color = NomadBrick,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
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
    }
}

@Composable
private fun PaymentItemCard(payment: Payment) {
    val dateStr = remember(payment.createdAt) {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(payment.createdAt))
    }
    val isSuccess = payment.status == PaymentStatus.SUCCEEDED

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(NomadConcrete, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CreditCard,
                    contentDescription = null,
                    tint = NomadSteel,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = payment.description,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )
                Text(
                    text = "Card ending in *${payment.cardLast4} • $dateStr",
                    fontSize = 11.sp,
                    color = NomadSteel
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format("%.2f", payment.amount)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )
                Text(
                    text = payment.status.label,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isSuccess) NomadMoss else NomadBrick
                )
            }
        }
    }
}

@Composable
private fun TicketItemCard(ticket: SupportTicket) {
    val dateStr = remember(ticket.createdAt) {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(ticket.createdAt))
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ticket.category.uppercase(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadFog
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (ticket.status) {
                        TicketStatus.OPEN -> NomadAmber.copy(alpha = 0.15f)
                        TicketStatus.IN_PROGRESS -> NomadSignal.copy(alpha = 0.15f)
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
                            TicketStatus.IN_PROGRESS -> NomadSignal
                            TicketStatus.RESOLVED -> NomadMoss
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

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
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Opened on $dateStr",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = NomadFog
            )
        }
    }
}

@Composable
private fun CreateTicketDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String) -> Unit
) {
    var category by remember { mutableStateOf("Check-in Credential") }
    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NomadMist,
        shape = RoundedCornerShape(26.dp),
        title = {
            Text("Open Support Ticket", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NomadInk)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("CATEGORY", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = NomadSteel)
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("SUBJECT", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = NomadSteel)
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    placeholder = { Text("Short description of the issue") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("DETAILS", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = NomadSteel)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("What happened?") },
                    minLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank() && description.isNotBlank()) {
                        onSubmit(category, subject, description)
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NomadInk)
            ) {
                Text("Submit Ticket", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = NomadSteel)
            }
        }
    )
}
