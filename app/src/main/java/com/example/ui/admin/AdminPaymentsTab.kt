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
fun AdminPaymentsTab(
    payments: List<Payment>,
    allUsers: List<User>
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("ALL") }

    // Dialog state for refund
    var paymentToRefund by remember { mutableStateOf<Payment?>(null) }
    var refundReason by remember { mutableStateOf("") }

    val filteredPayments = remember(payments, searchQuery, selectedStatusFilter) {
        payments.filter { pay ->
            val user = allUsers.find { it.uid == pay.userId }
            val matchesQuery = pay.description.contains(searchQuery, ignoreCase = true) ||
                    (user?.fullName?.contains(searchQuery, ignoreCase = true) ?: false) ||
                    (user?.email?.contains(searchQuery, ignoreCase = true) ?: false)

            val matchesStatus = when (selectedStatusFilter) {
                "SUCCEEDED" -> pay.status == PaymentStatus.SUCCEEDED
                "REFUNDED" -> pay.status == PaymentStatus.REFUNDED
                "FAILED" -> pay.status == PaymentStatus.FAILED
                else -> true
            }

            matchesQuery && matchesStatus
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Controls Row: Search & Filters
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
                    placeholder = { Text("Search transactions by member, description...", fontSize = 12.sp, color = NomadSteel) },
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

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    AdminFilterPill("All (${payments.size})", selectedStatusFilter == "ALL") { selectedStatusFilter = "ALL" }
                    AdminFilterPill("Succeeded", selectedStatusFilter == "SUCCEEDED") { selectedStatusFilter = "SUCCEEDED" }
                    AdminFilterPill("Refunded", selectedStatusFilter == "REFUNDED") { selectedStatusFilter = "REFUNDED" }
                    AdminFilterPill("Failed", selectedStatusFilter == "FAILED") { selectedStatusFilter = "FAILED" }
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
                Text(text = "MEMBER & DATE", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1.8f))
                Text(text = "DESCRIPTION", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1.6f))
                Text(text = "AMOUNT", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1f))
                Text(text = "STATUS & ACTIONS", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1.4f))
            }
        }

        // Transactions Table
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(filteredPayments, key = { it.id }) { payment ->
                val user = allUsers.find { it.uid == payment.userId }

                PaymentTableRow(
                    payment = payment,
                    user = user,
                    onRefund = {
                        refundReason = ""
                        paymentToRefund = payment
                    }
                )
            }
        }
    }

    // Refund Confirmation Dialog
    paymentToRefund?.let { pay ->
        AlertDialog(
            onDismissRequest = { paymentToRefund = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CurrencyExchange, contentDescription = null, tint = NomadBrick, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Process Customer Refund", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Refund $${String.format("%.2f", pay.amount)} USD to original payment method (card ending in ${pay.cardLast4})? Any active subscription associated with this charge will be cancelled.",
                        fontSize = 12.sp,
                        color = NomadInk
                    )
                    OutlinedTextField(
                        value = refundReason,
                        onValueChange = { refundReason = it },
                        placeholder = { Text("Mandatory refund reason for audit compliance...", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        NomadFitRepository.processRefund(pay.id, refundReason)
                        paymentToRefund = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NomadBrick),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text("Confirm Refund ($${String.format("%.2f", pay.amount)})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { paymentToRefund = null }) {
                    Text("Cancel", fontSize = 11.sp, color = NomadSteel)
                }
            }
        )
    }
}

@Composable
private fun PaymentTableRow(
    payment: Payment,
    user: User?,
    onRefund: () -> Unit
) {
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
            // Column 1: Member & Date
            Column(modifier = Modifier.weight(1.8f)) {
                Text(
                    text = user?.fullName ?: "Member",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk,
                    maxLines = 1
                )
                Text(
                    text = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()).format(Date(payment.createdAt)),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = NomadSteel
                )
            }

            // Column 2: Description & Method
            Column(modifier = Modifier.weight(1.6f)) {
                Text(
                    text = payment.description,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = NomadInk,
                    maxLines = 1
                )
                Text(
                    text = "Card •••• ${payment.cardLast4}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = NomadSteel
                )
            }

            // Column 3: Amount
            Text(
                text = "$${String.format("%.2f", payment.amount)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NomadInk,
                modifier = Modifier.weight(1f)
            )

            // Column 4: Status & Refund Action
            Row(
                modifier = Modifier.weight(1.4f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = when (payment.status) {
                        PaymentStatus.SUCCEEDED -> NomadMoss.copy(alpha = 0.12f)
                        PaymentStatus.REFUNDED -> NomadBrick.copy(alpha = 0.12f)
                        else -> NomadAmber.copy(alpha = 0.12f)
                    }
                ) {
                    Text(
                        text = payment.status.name,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (payment.status) {
                            PaymentStatus.SUCCEEDED -> NomadMoss
                            PaymentStatus.REFUNDED -> NomadBrick
                            else -> NomadAmber
                        },
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                if (payment.status == PaymentStatus.SUCCEEDED) {
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onRefund,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CurrencyExchange,
                            contentDescription = "Refund",
                            tint = NomadBrick,
                            modifier = Modifier.size(14.dp)
                        )
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
