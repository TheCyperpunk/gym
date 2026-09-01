package com.example.ui.member

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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import com.example.model.SupportTicket
import com.example.model.TicketStatus
import com.example.model.User
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MemberSupportScreen(
    user: User,
    supportTickets: List<SupportTicket>,
    prefilledSubject: String = "",
    prefilledCategory: String = "Access issue",
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userTickets = remember(supportTickets, user.uid) {
        supportTickets.filter { it.raisedByUserId == user.uid }.sortedByDescending { it.createdAt }
    }

    val categories = listOf("Access issue", "Payment issue", "Gym quality issue", "Something else")
    var selectedCategory by remember { mutableStateOf(prefilledCategory) }
    var subject by remember { mutableStateOf(prefilledSubject) }
    var description by remember { mutableStateOf("") }
    var submitSuccessMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = NomadConcrete,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NomadMist)
                    .border(1.dp, NomadLine)
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = NomadInk
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Support Desk",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )
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
            contentPadding = PaddingValues(top = 14.dp, bottom = 80.dp)
        ) {
            // New Ticket Form Section
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "WHAT DO YOU NEED HELP WITH?",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadSteel
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category Chips Horizontal Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { category ->
                                val isSelected = selectedCategory == category
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) NomadInk else NomadConcrete,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) NomadInk else NomadLine
                                    ),
                                    modifier = Modifier.clickable { selectedCategory = category }
                                ) {
                                    Text(
                                        text = category,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else NomadInk,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("SUBJECT", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = NomadSteel)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = subject,
                            onValueChange = {
                                subject = it
                                submitSuccessMessage = null
                            },
                            placeholder = { Text("Short summary of what happened") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NomadSignal,
                                unfocusedBorderColor = NomadLine,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("DETAILS", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = NomadSteel)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = {
                                description = it
                                submitSuccessMessage = null
                            },
                            placeholder = { Text("Provide any details about the gym or issue...") },
                            minLines = 3,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NomadSignal,
                                unfocusedBorderColor = NomadLine,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (subject.isNotBlank() && description.isNotBlank()) {
                                    NomadFitRepository.createSupportTicket(
                                        category = selectedCategory,
                                        subject = subject,
                                        description = description
                                    )
                                    subject = ""
                                    description = ""
                                    submitSuccessMessage = "Ticket submitted. Our concierge team will respond within 4 hours."
                                }
                            },
                            enabled = subject.isNotBlank() && description.isNotBlank(),
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Text("Submit Request", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }

                        if (submitSuccessMessage != null) {
                            Text(
                                text = submitSuccessMessage.orEmpty(),
                                color = NomadMoss,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            // Ticket History Header
            item {
                Text(
                    text = "YOUR TICKETS (${userTickets.size})",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadFog
                )
            }

            if (userTickets.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Outlined.CheckCircle,
                        title = "No support tickets",
                        message = "All good! You have no unresolved issues."
                    )
                }
            } else {
                items(userTickets, key = { it.id }) { ticket ->
                    val dateStr = remember(ticket.createdAt) {
                        SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(ticket.createdAt))
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
                                    shape = RoundedCornerShape(10.dp),
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
            }
        }
    }
}
