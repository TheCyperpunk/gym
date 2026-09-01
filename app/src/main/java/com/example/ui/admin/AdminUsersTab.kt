package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.NomadFitRepository
import com.example.model.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersTab(
    users: List<User>,
    subscriptions: List<Subscription>,
    plans: List<MembershipPlan>,
    visits: List<Visit>,
    payments: List<Payment>,
    tickets: List<SupportTicket>,
    auditLogs: List<AuditLogEntry>
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, ACTIVE, SUSPENDED, SUBSCRIBED
    var sortBy by remember { mutableStateOf("JOIN_DATE") } // JOIN_DATE, NAME, STATUS

    // Selected user for side panel / sliding sheet
    var selectedUserForDetail by remember { mutableStateOf<User?>(null) }

    // Dialog States
    var userToSuspend by remember { mutableStateOf<User?>(null) }
    var suspendReason by remember { mutableStateOf("") }

    var userToReactivate by remember { mutableStateOf<User?>(null) }
    var userToResetPassword by remember { mutableStateOf<User?>(null) }
    var generatedResetToken by remember { mutableStateOf<String?>(null) }

    // Filter & Sort Logic
    val filteredUsers = remember(users, subscriptions, searchQuery, selectedFilter, sortBy) {
        users.filter { user ->
            val matchesQuery = user.fullName.contains(searchQuery, ignoreCase = true) ||
                    user.email.contains(searchQuery, ignoreCase = true) ||
                    user.homeCity.contains(searchQuery, ignoreCase = true)

            val sub = subscriptions.find { it.userId == user.uid }
            val matchesFilter = when (selectedFilter) {
                "ACTIVE" -> user.status == AccountStatus.ACTIVE
                "SUSPENDED" -> user.status == AccountStatus.SUSPENDED
                "SUBSCRIBED" -> sub?.status == SubscriptionStatus.ACTIVE
                else -> true
            }

            matchesQuery && matchesFilter
        }.sortedWith { a, b ->
            when (sortBy) {
                "NAME" -> a.fullName.compareTo(b.fullName, ignoreCase = true)
                "STATUS" -> a.status.name.compareTo(b.status.name)
                else -> b.createdAt.compareTo(a.createdAt) // Default: newest first
            }
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
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search users by name, email, city...", fontSize = 12.sp, color = NomadSteel) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Search, contentDescription = null, tint = NomadSteel, modifier = Modifier.size(16.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Outlined.Close, contentDescription = "Clear", tint = NomadSteel, modifier = Modifier.size(14.dp))
                            }
                        }
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
                    // Filter Pills
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        AdminFilterPill("All (${users.size})", selectedFilter == "ALL") { selectedFilter = "ALL" }
                        AdminFilterPill("Active", selectedFilter == "ACTIVE") { selectedFilter = "ACTIVE" }
                        AdminFilterPill("Suspended", selectedFilter == "SUSPENDED") { selectedFilter = "SUSPENDED" }
                        AdminFilterPill("Subscribers", selectedFilter == "SUBSCRIBED") { selectedFilter = "SUBSCRIBED" }
                    }

                    // Sort Selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            sortBy = when (sortBy) {
                                "JOIN_DATE" -> "NAME"
                                "NAME" -> "STATUS"
                                else -> "JOIN_DATE"
                            }
                        }
                    ) {
                        Icon(Icons.Outlined.Sort, contentDescription = null, tint = NomadSteel, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = when (sortBy) {
                                "NAME" -> "Name A-Z"
                                "STATUS" -> "Status"
                                else -> "Joined (Newest)"
                            },
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

        // Results Table Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
            color = Color(0xFF282A2F)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "MEMBER / EMAIL", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(2f))
                Text(text = "CITY", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1.2f))
                Text(text = "STATUS", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1f))
                Text(text = "ACTIONS", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1.2f))
            }
        }

        // Users List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            if (filteredUsers.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        color = NomadMist,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                    ) {
                        Text(
                            text = "No users matching '$searchQuery'",
                            fontSize = 12.sp,
                            color = NomadSteel,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            }

            items(filteredUsers, key = { it.uid }) { user ->
                val sub = subscriptions.find { it.userId == user.uid }
                val plan = plans.find { it.id == sub?.planId }

                UserTableRow(
                    user = user,
                    subscription = sub,
                    plan = plan,
                    onViewDetail = { selectedUserForDetail = user },
                    onSuspend = {
                        suspendReason = ""
                        userToSuspend = user
                    },
                    onReactivate = {
                        userToReactivate = user
                    },
                    onResetPassword = {
                        userToResetPassword = user
                    }
                )
            }
        }
    }

    // Detail Modal / Sheet
    selectedUserForDetail?.let { user ->
        val sub = subscriptions.find { it.userId == user.uid }
        val plan = plans.find { it.id == sub?.planId }
        val userVisits = visits.filter { it.userId == user.uid }
        val userPayments = payments.filter { it.userId == user.uid }
        val userTickets = tickets.filter { it.raisedByUserId == user.uid }
        val userLogs = auditLogs.filter { it.targetId == user.uid }

        ModalBottomSheet(
            onDismissRequest = { selectedUserForDetail = null },
            containerColor = NomadConcrete,
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
        ) {
            UserDetailSheetContent(
                user = user,
                subscription = sub,
                plan = plan,
                visits = userVisits,
                payments = userPayments,
                tickets = userTickets,
                auditLogs = userLogs,
                onClose = { selectedUserForDetail = null }
            )
        }
    }

    // Suspend Dialog
    userToSuspend?.let { user ->
        AlertDialog(
            onDismissRequest = { userToSuspend = null },
            title = {
                Text(
                    text = "Suspend User Account",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = NomadInk
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Are you sure you want to suspend access for ${user.fullName} (${user.email})? All active pass credentials will be immediately revoked.",
                        fontSize = 12.sp,
                        color = NomadSteel
                    )
                    OutlinedTextField(
                        value = suspendReason,
                        onValueChange = { suspendReason = it },
                        placeholder = { Text("Reason for suspension (required for audit trail)...", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        NomadFitRepository.suspendUser(user.uid, suspendReason)
                        userToSuspend = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NomadBrick),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text("Confirm Suspension", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToSuspend = null }) {
                    Text("Cancel", fontSize = 11.sp, color = NomadSteel)
                }
            }
        )
    }

    // Reactivate Dialog
    userToReactivate?.let { user ->
        AlertDialog(
            onDismissRequest = { userToReactivate = null },
            title = { Text("Reactivate User Account", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Text("Restore network gym access and good standing status for ${user.fullName}?", fontSize = 12.sp, color = NomadSteel)
            },
            confirmButton = {
                Button(
                    onClick = {
                        NomadFitRepository.reactivateUser(user.uid, "Admin approved account restoration.")
                        userToReactivate = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NomadMoss),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text("Reactivate", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToReactivate = null }) {
                    Text("Cancel", fontSize = 11.sp, color = NomadSteel)
                }
            }
        )
    }

    // Reset Password / Credential Dialog
    userToResetPassword?.let { user ->
        AlertDialog(
            onDismissRequest = { userToResetPassword = null },
            title = { Text("Reset User Access Credential", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Generate a secure single-use temporary password and email reset dispatch link for ${user.fullName}?", fontSize = 12.sp, color = NomadSteel)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val token = NomadFitRepository.resetUserPassword(user.uid, "Admin dispatched manual password reset.")
                        generatedResetToken = token
                        userToResetPassword = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text("Dispatch Reset Key", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToResetPassword = null }) {
                    Text("Cancel", fontSize = 11.sp, color = NomadSteel)
                }
            }
        )
    }

    // Generated Token Success Modal
    generatedResetToken?.let { token ->
        AlertDialog(
            onDismissRequest = { generatedResetToken = null },
            title = { Text("Temporary Credential Dispatched", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Temporary verification token generated and logged to audit trail:", fontSize = 12.sp, color = NomadSteel)
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = NomadInk,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = token,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadSignal,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { generatedResetToken = null },
                    colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text("Done", fontSize = 11.sp, color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun UserTableRow(
    user: User,
    subscription: Subscription?,
    plan: MembershipPlan?,
    onViewDetail: () -> Unit,
    onSuspend: () -> Unit,
    onReactivate: () -> Unit,
    onResetPassword: () -> Unit
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
            // Column 1: Member / Email & Photo
            Row(
                modifier = Modifier.weight(2f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = user.photoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(NomadConcrete)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = user.fullName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk,
                        maxLines = 1
                    )
                    Text(
                        text = user.email,
                        fontSize = 10.sp,
                        color = NomadSteel,
                        maxLines = 1
                    )
                }
            }

            // Column 2: City & Plan Tag
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = user.homeCity,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = NomadInk
                )
                Text(
                    text = plan?.name?.take(14) ?: if (subscription != null) "Subscribed" else "No Plan",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = if (subscription?.status == SubscriptionStatus.ACTIVE) NomadMoss else NomadSteel,
                    maxLines = 1
                )
            }

            // Column 3: Account Status
            Box(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = if (user.status == AccountStatus.ACTIVE) NomadMoss.copy(alpha = 0.12f) else NomadBrick.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = user.status.name,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (user.status == AccountStatus.ACTIVE) NomadMoss else NomadBrick,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            // Column 4: Actions Menu
            Row(
                modifier = Modifier.weight(1.2f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onViewDetail,
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(Icons.Outlined.Visibility, contentDescription = "View", tint = NomadSteel, modifier = Modifier.size(15.dp))
                }

                Box {
                    IconButton(
                        onClick = { expandedMenu = true },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "More", tint = NomadSteel, modifier = Modifier.size(15.dp))
                    }

                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("View Full Profile", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            onClick = {
                                expandedMenu = false
                                onViewDetail()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Reset Password", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Outlined.LockReset, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            onClick = {
                                expandedMenu = false
                                onResetPassword()
                            }
                        )

                        if (user.status == AccountStatus.ACTIVE) {
                            DropdownMenuItem(
                                text = { Text("Suspend Account", fontSize = 12.sp, color = NomadBrick) },
                                leadingIcon = { Icon(Icons.Outlined.Block, contentDescription = null, tint = NomadBrick, modifier = Modifier.size(16.dp)) },
                                onClick = {
                                    expandedMenu = false
                                    onSuspend()
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Reactivate Account", fontSize = 12.sp, color = NomadMoss) },
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
private fun UserDetailSheetContent(
    user: User,
    subscription: Subscription?,
    plan: MembershipPlan?,
    visits: List<Visit>,
    payments: List<Payment>,
    tickets: List<SupportTicket>,
    auditLogs: List<AuditLogEntry>,
    onClose: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 60.dp)
    ) {
        // Header Profile Card
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = user.photoUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .border(1.dp, NomadLine, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = user.fullName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NomadInk)
                        Text(text = "${user.email} • UID: ${user.uid.take(12)}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = NomadSteel)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(3.dp),
                    color = if (user.status == AccountStatus.ACTIVE) NomadMoss.copy(alpha = 0.15f) else NomadBrick.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = user.status.name,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (user.status == AccountStatus.ACTIVE) NomadMoss else NomadBrick,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Section 1: Active Subscription & Tier
        item {
            DetailSectionCard(title = "ACTIVE SUBSCRIPTION & PASS") {
                if (subscription != null && plan != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = plan.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NomadInk)
                            Text(
                                text = "$${String.format("%.0f", plan.price)}/month • ${if (plan.isUnlimited) "Unlimited Passes" else "${subscription.visitsUsedThisCycle}/${subscription.visitsAllowance} used"}",
                                fontSize = 11.sp,
                                color = NomadSteel
                            )
                            Text(
                                text = "Eligible Tiers: ${plan.eligibleGymTiers.joinToString()}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = NomadSignal
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = NomadMoss.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = subscription.status.label.uppercase(),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadMoss,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                } else {
                    Text(text = "No active subscription recorded.", fontSize = 11.sp, color = NomadSteel)
                }
            }
        }

        // Section 2: Visit History
        item {
            DetailSectionCard(title = "CHECK-IN & VISIT HISTORY (${visits.size})") {
                if (visits.isEmpty()) {
                    Text(text = "No check-ins recorded yet.", fontSize = 11.sp, color = NomadSteel)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        visits.forEach { visit ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = visit.gymName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NomadInk)
                                    Text(
                                        text = "${visit.gymCity} • ${SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(visit.checkInTimestamp))}",
                                        fontSize = 10.sp,
                                        color = NomadSteel
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(2.dp),
                                    color = if (visit.validationResult == ValidationResult.APPROVED) NomadMoss.copy(alpha = 0.12f) else NomadBrick.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = visit.validationResult.name,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (visit.validationResult == ValidationResult.APPROVED) NomadMoss else NomadBrick,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Payment History
        item {
            DetailSectionCard(title = "PAYMENT TRANSACTIONS (${payments.size})") {
                if (payments.isEmpty()) {
                    Text(text = "No payment records.", fontSize = 11.sp, color = NomadSteel)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        payments.forEach { pay ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "$${String.format("%.2f", pay.amount)} USD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NomadInk)
                                    Text(text = pay.description, fontSize = 10.sp, color = NomadSteel)
                                }

                                Surface(
                                    shape = RoundedCornerShape(2.dp),
                                    color = when (pay.status) {
                                        PaymentStatus.SUCCEEDED -> NomadMoss.copy(alpha = 0.12f)
                                        PaymentStatus.REFUNDED -> NomadBrick.copy(alpha = 0.12f)
                                        else -> NomadAmber.copy(alpha = 0.12f)
                                    }
                                ) {
                                    Text(
                                        text = pay.status.name,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (pay.status) {
                                            PaymentStatus.SUCCEEDED -> NomadMoss
                                            PaymentStatus.REFUNDED -> NomadBrick
                                            else -> NomadAmber
                                        },
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Member Audit Trail
        item {
            DetailSectionCard(title = "MEMBER AUDIT LOG") {
                if (auditLogs.isEmpty()) {
                    Text(text = "No administrative interventions recorded.", fontSize = 11.sp, color = NomadSteel)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        auditLogs.forEach { log ->
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = log.action, fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NomadInk)
                                    Text(
                                        text = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(log.timestamp)),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        color = NomadSteel
                                    )
                                }
                                Text(text = "By ${log.actorAdminName}: ${log.reason}", fontSize = 10.sp, color = NomadSteel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = NomadSteel
            )
            Spacer(modifier = Modifier.height(6.dp))
            content()
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
