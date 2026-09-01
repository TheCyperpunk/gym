package com.example.ui.admin

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
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminSupportTab(
    tickets: List<SupportTicket>,
    adminUser: User
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("OPEN") } // "OPEN" | "RESOLVED" | "ALL"
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    // Dialog state for resolving ticket
    var ticketToResolve by remember { mutableStateOf<SupportTicket?>(null) }
    var resolutionNote by remember { mutableStateOf("") }

    val filteredTickets = remember(tickets, searchQuery, selectedStatusFilter, selectedCategoryFilter) {
        tickets.filter { t ->
            val matchesQuery = t.subject.contains(searchQuery, ignoreCase = true) ||
                    t.description.contains(searchQuery, ignoreCase = true) ||
                    t.raisedByUserName.contains(searchQuery, ignoreCase = true)

            val matchesStatus = when (selectedStatusFilter) {
                "OPEN" -> t.status == TicketStatus.OPEN
                "RESOLVED" -> t.status == TicketStatus.RESOLVED
                else -> true
            }

            val matchesCat = if (selectedCategoryFilter == "ALL") true else t.category == selectedCategoryFilter

            matchesQuery && matchesStatus && matchesCat
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
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search disputes & support tickets...", fontSize = 12.sp, color = NomadSteel) },
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        AdminFilterPill("Open (${tickets.count { it.status == TicketStatus.OPEN }})", selectedStatusFilter == "OPEN") { selectedStatusFilter = "OPEN" }
                        AdminFilterPill("Resolved", selectedStatusFilter == "RESOLVED") { selectedStatusFilter = "RESOLVED" }
                        AdminFilterPill("All", selectedStatusFilter == "ALL") { selectedStatusFilter = "ALL" }
                    }

                    // Category Filter Dropdown or Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            selectedCategoryFilter = when (selectedCategoryFilter) {
                                "ALL" -> "Security Anomaly"
                                "Security Anomaly" -> "Access issue"
                                "Access issue" -> "Billing / Refund"
                                else -> "ALL"
                            }
                        }
                    ) {
                        Icon(Icons.Outlined.FilterList, contentDescription = null, tint = NomadSteel, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (selectedCategoryFilter == "ALL") "All Categories" else selectedCategoryFilter,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadInk
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tickets List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            if (filteredTickets.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        color = NomadMist,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                    ) {
                        Text(
                            text = "No tickets in current filter queue.",
                            fontSize = 12.sp,
                            color = NomadSteel,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            }

            items(filteredTickets, key = { it.id }) { ticket ->
                TicketQueueCard(
                    ticket = ticket,
                    adminName = adminUser.fullName,
                    onAssign = {
                        NomadFitRepository.assignTicket(ticket.id, adminUser.fullName)
                    },
                    onResolve = {
                        resolutionNote = ""
                        ticketToResolve = ticket
                    }
                )
            }
        }
    }

    // Resolve Ticket Dialog
    ticketToResolve?.let { tkt ->
        AlertDialog(
            onDismissRequest = { ticketToResolve = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = NomadMoss, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Resolve Ticket #${tkt.id.take(8)}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Provide mandatory resolution summary for '${tkt.subject}'. This note will be recorded into the ticket history and system audit trail:",
                        fontSize = 12.sp,
                        color = NomadInk
                    )
                    OutlinedTextField(
                        value = resolutionNote,
                        onValueChange = { resolutionNote = it },
                        placeholder = { Text("e.g. Verified member check-in timestamp with partner desk staff and credited $12 pass allowance.", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        NomadFitRepository.resolveTicketWithNote(tkt.id, resolutionNote)
                        ticketToResolve = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NomadMoss),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text("Mark as Resolved", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { ticketToResolve = null }) {
                    Text("Cancel", fontSize = 11.sp, color = NomadSteel)
                }
            }
        )
    }
}

@Composable
private fun TicketQueueCard(
    ticket: SupportTicket,
    adminName: String,
    onAssign: () -> Unit,
    onResolve: () -> Unit
) {
    val isOpen = ticket.status == TicketStatus.OPEN
    val isEscalated = isOpen && (System.currentTimeMillis() - ticket.createdAt > 86400000L * 2)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isEscalated) NomadSignal.copy(alpha = 0.5f) else NomadLine
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = when (ticket.category) {
                            "Security Anomaly" -> NomadSignal.copy(alpha = 0.15f)
                            "Billing / Refund" -> NomadMoss.copy(alpha = 0.15f)
                            else -> NomadSteel.copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = ticket.category.uppercase(),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (ticket.category) {
                                "Security Anomaly" -> NomadSignal
                                "Billing / Refund" -> NomadMoss
                                else -> NomadSteel
                            },
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "ID: ${ticket.id.take(10)}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = NomadSteel
                    )
                }

                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = if (isOpen) NomadAmber.copy(alpha = 0.12f) else NomadMoss.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (isOpen) "OPEN" else "RESOLVED",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOpen) NomadAmber else NomadMoss,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = ticket.subject,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NomadInk
            )

            Text(
                text = ticket.description,
                fontSize = 11.sp,
                color = NomadSteel
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Raised by ${ticket.raisedByUserName} (${ticket.raisedByRole.label}) • Created ${SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(ticket.createdAt))}",
                fontSize = 10.sp,
                color = NomadSteel
            )

            if (ticket.assignedTo != null) {
                Text(
                    text = "Assigned To: ${ticket.assignedTo}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )
            }

            if (ticket.resolutionNote != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(3.dp),
                    color = NomadConcrete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text(
                            text = "RESOLUTION NOTE:",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadSteel
                        )
                        Text(
                            text = ticket.resolutionNote,
                            fontSize = 10.sp,
                            color = NomadInk
                        )
                    }
                }
            }

            if (isOpen) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (ticket.assignedTo == null) {
                        OutlinedButton(
                            onClick = onAssign,
                            shape = RoundedCornerShape(3.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Assign to Me", fontSize = 10.sp, color = NomadInk)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Button(
                        onClick = onResolve,
                        colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                        shape = RoundedCornerShape(3.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Resolve Ticket", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminFilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(3.dp),
        color = if (isSelected) NomadInk else Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NomadInk else NomadLine),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else NomadSteel,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}
