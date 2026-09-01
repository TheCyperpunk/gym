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
fun AdminSettlementsTab(
    settlements: List<Settlement>,
    gyms: List<Gym>,
    partners: List<GymPartner>
) {
    var selectedIds by remember { mutableStateOf(setOf<String>()) }
    var showBulkSettleDialog by remember { mutableStateOf(false) }

    val pendingSettlements = settlements.filter { it.status == SettlementStatus.PENDING }
    val totalPendingAmount = pendingSettlements.filter { it.id in selectedIds }.sumOf { it.totalAmount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Bulk Action Bar & Stats
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            color = NomadMist,
            border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PARTNER SETTLEMENT DISBURSEMENTS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk
                    )
                    Text(
                        text = "${pendingSettlements.size} pending partner payout cycles",
                        fontSize = 10.sp,
                        color = NomadSteel
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (selectedIds.size == pendingSettlements.size && pendingSettlements.isNotEmpty()) {
                        TextButton(onClick = { selectedIds = emptySet() }) {
                            Text("Deselect All", fontSize = 10.sp, color = NomadSteel)
                        }
                    } else if (pendingSettlements.isNotEmpty()) {
                        TextButton(onClick = { selectedIds = pendingSettlements.map { it.id }.toSet() }) {
                            Text("Select All (${pendingSettlements.size})", fontSize = 10.sp, color = NomadInk)
                        }
                    }

                    Button(
                        onClick = { showBulkSettleDialog = true },
                        enabled = selectedIds.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                        shape = RoundedCornerShape(3.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(
                            text = "Disburse (${selectedIds.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
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
                Text(text = "PARTNER GYM & PERIOD", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(2f))
                Text(text = "VISITS", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(0.8f))
                Text(text = "TOTAL OWED", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1.2f))
                Text(text = "STATUS", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1.2f))
            }
        }

        // Settlements List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(settlements, key = { it.id }) { settlement ->
                val gym = gyms.find { it.id == settlement.gymId }
                val partner = partners.find { it.userId == gym?.ownerId }
                val isSelected = selectedIds.contains(settlement.id)
                val isPending = settlement.status == SettlementStatus.PENDING

                SettlementTableRow(
                    settlement = settlement,
                    gym = gym,
                    partner = partner,
                    isSelected = isSelected,
                    onToggleSelect = {
                        selectedIds = if (isSelected) {
                            selectedIds - settlement.id
                        } else {
                            selectedIds + settlement.id
                        }
                    }
                )
            }
        }
    }

    // Bulk Settle Confirmation Dialog
    if (showBulkSettleDialog) {
        AlertDialog(
            onDismissRequest = { showBulkSettleDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Payments, contentDescription = null, tint = NomadMoss, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Confirm Bulk Settlement Payout", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "You are about to disburse funds to ${selectedIds.size} partner gyms totaling $${String.format("%.2f", totalPendingAmount)} USD.",
                        fontSize = 12.sp,
                        color = NomadInk
                    )
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = NomadConcrete,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("• Payout Channels: Stripe Connect Direct & Automated Clearing House (ACH)", fontSize = 10.sp, color = NomadSteel)
                            Text("• Audit Entry: System will log disbursement reference token for each facility.", fontSize = 10.sp, color = NomadSteel)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        NomadFitRepository.bulkSettlePayouts(selectedIds.toList())
                        selectedIds = emptySet()
                        showBulkSettleDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NomadMoss),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text("Disburse $${String.format("%.2f", totalPendingAmount)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkSettleDialog = false }) {
                    Text("Cancel", fontSize = 11.sp, color = NomadSteel)
                }
            }
        )
    }
}

@Composable
private fun SettlementTableRow(
    settlement: Settlement,
    gym: Gym?,
    partner: GymPartner?,
    isSelected: Boolean,
    onToggleSelect: () -> Unit
) {
    val isPending = settlement.status == SettlementStatus.PENDING

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(3.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox for selection (only if pending)
            if (isPending) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelect() },
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            } else {
                Spacer(modifier = Modifier.width(28.dp))
            }

            // Column 1: Facility & Period
            val periodLabel = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date(settlement.periodStart))
            Column(modifier = Modifier.weight(2f)) {
                Text(
                    text = gym?.name ?: settlement.gymName.ifEmpty { "Gym Partner" },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk,
                    maxLines = 1
                )
                Text(
                    text = "$periodLabel • Payout: ${partner?.payoutMethod ?: "Stripe Connect"}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = NomadSteel
                )
            }

            // Column 2: Total Visits
            Text(
                text = "${settlement.totalVisits}",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = NomadInk,
                modifier = Modifier.weight(0.8f)
            )

            // Column 3: Total Owed
            Text(
                text = "$${String.format("%.2f", settlement.totalAmount)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NomadInk,
                modifier = Modifier.weight(1.2f)
            )

            // Column 4: Status Tag
            Box(
                modifier = Modifier.weight(1.2f),
                contentAlignment = Alignment.CenterEnd
            ) {
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = if (isPending) NomadAmber.copy(alpha = 0.12f) else NomadMoss.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (isPending) "PENDING" else "PAID",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPending) NomadAmber else NomadMoss,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
