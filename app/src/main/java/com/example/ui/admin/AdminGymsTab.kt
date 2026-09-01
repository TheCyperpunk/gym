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

@Composable
fun AdminGymsTab(
    gyms: List<Gym>,
    partners: List<GymPartner>,
    allUsers: List<User>,
    initialShowApprovalQueue: Boolean = false
) {
    var selectedSubTab by remember { mutableStateOf(if (initialShowApprovalQueue) 1 else 0) } // 0: All Gyms Table, 1: KYC Approval Queue
    var searchQuery by remember { mutableStateOf("") }

    // Dialogs
    var partnerToReject by remember { mutableStateOf<GymPartner?>(null) }
    var rejectReason by remember { mutableStateOf("") }

    var gymToSuspend by remember { mutableStateOf<Gym?>(null) }
    var suspendReason by remember { mutableStateOf("") }

    var gymToReactivate by remember { mutableStateOf<Gym?>(null) }

    val pendingPartners = partners.filter { it.kycStatus == KycStatus.PENDING }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Sub-Navigation: All Gyms vs KYC Approval Queue
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            color = NomadMist,
            border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
        ) {
            Row(modifier = Modifier.padding(3.dp)) {
                AdminSubNavButton(
                    label = "Partner Gyms (${gyms.size})",
                    isSelected = selectedSubTab == 0,
                    modifier = Modifier.weight(1f)
                ) { selectedSubTab = 0 }

                AdminSubNavButton(
                    label = "KYC Approval Queue (${pendingPartners.size})",
                    isSelected = selectedSubTab == 1,
                    isAlert = pendingPartners.isNotEmpty(),
                    modifier = Modifier.weight(1.3f)
                ) { selectedSubTab = 1 }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedSubTab) {
            0 -> {
                // Table of All Gyms
                val filteredGyms = gyms.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.city.contains(searchQuery, ignoreCase = true) ||
                            it.address.contains(searchQuery, ignoreCase = true)
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search gym facilities by name, city, address...", fontSize = 12.sp, color = NomadSteel) },
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

                Spacer(modifier = Modifier.height(8.dp))

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
                        Text(text = "FACILITY & CITY", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(2f))
                        Text(text = "TIER", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1f))
                        Text(text = "STATUS", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1f))
                        Text(text = "ACTIONS", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1.2f))
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredGyms, key = { it.id }) { gym ->
                        val partner = partners.find { it.userId == gym.ownerId }
                        val owner = allUsers.find { it.uid == gym.ownerId }

                        GymTableRow(
                            gym = gym,
                            partner = partner,
                            owner = owner,
                            onSuspend = {
                                suspendReason = ""
                                gymToSuspend = gym
                            },
                            onReactivate = {
                                gymToReactivate = gym
                            }
                        )
                    }
                }
            }
            1 -> {
                // KYC Approval Queue View
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    if (pendingPartners.isEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(4.dp),
                                color = NomadMist,
                                border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                            ) {
                                Text(
                                    text = "No pending KYC applications. All partner onboarding submissions are up to date.",
                                    fontSize = 12.sp,
                                    color = NomadSteel,
                                    modifier = Modifier.padding(20.dp)
                                )
                            }
                        }
                    }

                    items(pendingPartners, key = { it.id }) { partner ->
                        val owner = allUsers.find { it.uid == partner.userId }
                        KycQueueCard(
                            partner = partner,
                            owner = owner,
                            onApprove = {
                                NomadFitRepository.approveGymPartnerKyc(partner.id)
                            },
                            onReject = {
                                rejectReason = ""
                                partnerToReject = partner
                            }
                        )
                    }
                }
            }
        }
    }

    // Rejection Dialog
    partnerToReject?.let { partner ->
        AlertDialog(
            onDismissRequest = { partnerToReject = null },
            title = { Text("Reject Partner KYC Application", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter specific rejection reason for ${partner.businessName} (logged to audit trail & notified):", fontSize = 12.sp, color = NomadSteel)
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        placeholder = { Text("e.g. Commercial liability insurance missing or unverified business tax ID...", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        NomadFitRepository.rejectGymPartnerKyc(partner.id, rejectReason)
                        partnerToReject = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NomadBrick),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text("Reject Application", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { partnerToReject = null }) {
                    Text("Cancel", fontSize = 11.sp, color = NomadSteel)
                }
            }
        )
    }

    // Suspend Gym Dialog
    gymToSuspend?.let { gym ->
        AlertDialog(
            onDismissRequest = { gymToSuspend = null },
            title = { Text("Suspend Partner Gym", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Temporarily suspend check-ins at ${gym.name}? Facility will immediately disappear from member discovery.", fontSize = 12.sp, color = NomadSteel)
                    OutlinedTextField(
                        value = suspendReason,
                        onValueChange = { suspendReason = it },
                        placeholder = { Text("Reason for suspension...", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        NomadFitRepository.suspendGym(gym.id, suspendReason)
                        gymToSuspend = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NomadBrick),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text("Suspend Gym", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { gymToSuspend = null }) {
                    Text("Cancel", fontSize = 11.sp, color = NomadSteel)
                }
            }
        )
    }

    // Reactivate Gym Dialog
    gymToReactivate?.let { gym ->
        AlertDialog(
            onDismissRequest = { gymToReactivate = null },
            title = { Text("Reactivate Partner Gym", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Text("Restore active check-in status and map visibility for ${gym.name}?", fontSize = 12.sp, color = NomadSteel)
            },
            confirmButton = {
                Button(
                    onClick = {
                        NomadFitRepository.reactivateGym(gym.id, "Admin restored facility access.")
                        gymToReactivate = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NomadMoss),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text("Reactivate", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { gymToReactivate = null }) {
                    Text("Cancel", fontSize = 11.sp, color = NomadSteel)
                }
            }
        )
    }
}

@Composable
private fun GymTableRow(
    gym: Gym,
    partner: GymPartner?,
    owner: User?,
    onSuspend: () -> Unit,
    onReactivate: () -> Unit
) {
    var expandedMenu by remember { mutableStateOf(false) }

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
            // Column 1: Facility & City
            Column(modifier = Modifier.weight(2f)) {
                Text(text = gym.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NomadInk, maxLines = 1)
                Text(
                    text = "${gym.city} • ${gym.checkInCount} visits/mo • Owner: ${owner?.fullName ?: "Partner"}",
                    fontSize = 10.sp,
                    color = NomadSteel,
                    maxLines = 1
                )
            }

            // Column 2: Tier
            Box(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = if (gym.tier == GymTier.PREMIUM) NomadInk else NomadConcrete
                ) {
                    Text(
                        text = gym.tier.name,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (gym.tier == GymTier.PREMIUM) NomadSignal else NomadSteel,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            // Column 3: Status
            Box(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = if (gym.status == GymStatus.ACTIVE) NomadMoss.copy(alpha = 0.12f) else NomadBrick.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = gym.status.name,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (gym.status == GymStatus.ACTIVE) NomadMoss else NomadBrick,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            // Column 4: Actions
            Row(
                modifier = Modifier.weight(1.2f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    IconButton(
                        onClick = { expandedMenu = true },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "Actions", tint = NomadSteel, modifier = Modifier.size(15.dp))
                    }

                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        if (gym.status == GymStatus.ACTIVE) {
                            DropdownMenuItem(
                                text = { Text("Suspend Gym", fontSize = 12.sp, color = NomadBrick) },
                                leadingIcon = { Icon(Icons.Outlined.Block, contentDescription = null, tint = NomadBrick, modifier = Modifier.size(16.dp)) },
                                onClick = {
                                    expandedMenu = false
                                    onSuspend()
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Reactivate Gym", fontSize = 12.sp, color = NomadMoss) },
                                leadingIcon = { Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = NomadMoss, modifier = Modifier.size(16.dp)) },
                                onClick = {
                                    expandedMenu = false
                                    onReactivate()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KycQueueCard(
    partner: GymPartner,
    owner: User?,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
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
                Text(
                    text = partner.businessName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )

                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = NomadAmber.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "PENDING REVIEW",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadAmber,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Contact: ${owner?.fullName ?: "Owner"} • ${owner?.email ?: "N/A"} • ${owner?.phone ?: "N/A"}",
                fontSize = 11.sp,
                color = NomadSteel
            )
            Text(
                text = "Payout Terms: $${String.format("%.2f", partner.payoutPerVisit)} per check-in • Settlement Method: ${partner.payoutMethod}",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = NomadInk
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = NomadMoss),
                    shape = RoundedCornerShape(3.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Approve Partner KYC", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NomadBrick),
                    shape = RoundedCornerShape(3.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Reject Application", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun AdminSubNavButton(
    label: String,
    isSelected: Boolean,
    isAlert: Boolean = false,
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
            if (isAlert && !isSelected) {
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = NomadSignal,
                    modifier = Modifier.size(5.dp)
                ) {}
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else NomadSteel
            )
        }
    }
}
