package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NomadFitRepository
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    user: User,
    gyms: List<Gym>,
    partners: List<GymPartner>,
    plans: List<MembershipPlan>,
    settlements: List<Settlement>,
    tickets: List<SupportTicket>
) {
    val allUsers by NomadFitRepository.allUsers.collectAsState()
    val subscriptions by NomadFitRepository.subscriptions.collectAsState()
    val visits by NomadFitRepository.visits.collectAsState()
    val payments by NomadFitRepository.payments.collectAsState()
    val anomalies by NomadFitRepository.anomalies.collectAsState()
    val promoCodes by NomadFitRepository.promoCodes.collectAsState()
    val auditLogs by NomadFitRepository.auditLogs.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

    // Direct routing actions from overview "Needs Attention" panel
    var initialShowKycApprovalQueue by remember { mutableStateOf(false) }

    val urgentAttentionCount = remember(anomalies, tickets, partners) {
        val unresolvedAnomalies = anomalies.count { !it.isResolved }
        val urgentTickets = tickets.count { it.status == TicketStatus.OPEN && (System.currentTimeMillis() - it.createdAt > 86400000L * 2) }
        val pendingKyc = partners.count { it.kycStatus == KycStatus.PENDING }
        unresolvedAnomalies + urgentTickets + pendingKyc
    }

    Scaffold(
        containerColor = NomadConcrete,
        topBar = {
            Surface(
                color = Color(0xFF1E2024),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3238))
            ) {
                Column {
                    // Top Bar Brand & Quick Info
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = NomadSignal,
                                modifier = Modifier.size(16.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("A", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FIT LOOP / OPERATIONS CONSOLE",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (urgentAttentionCount > 0) {
                                Surface(
                                    shape = RoundedCornerShape(3.dp),
                                    color = NomadSignal,
                                    modifier = Modifier.clickable { selectedTab = 0 }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color.White,
                                            modifier = Modifier.size(5.dp)
                                        ) {}
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "$urgentAttentionCount URGENT",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Admin: ${user.fullName.take(12)}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = NomadFog
                            )
                        }
                    }

                    // Dense Horizontal Operations Tabs
                    val scrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        AdminConsoleTab(0, "Overview", Icons.Outlined.Dashboard, selectedTab == 0) { selectedTab = 0 }
                        AdminConsoleTab(1, "Users", Icons.Outlined.People, selectedTab == 1, count = allUsers.size) { selectedTab = 1 }
                        AdminConsoleTab(
                            2,
                            "Gyms",
                            Icons.Outlined.Storefront,
                            selectedTab == 2,
                            alertCount = partners.count { it.kycStatus == KycStatus.PENDING }
                        ) {
                            initialShowKycApprovalQueue = false
                            selectedTab = 2
                        }
                        AdminConsoleTab(3, "Plans", Icons.Outlined.CardMembership, selectedTab == 3) { selectedTab = 3 }
                        AdminConsoleTab(4, "Promo Codes", Icons.Outlined.Discount, selectedTab == 4) { selectedTab = 4 }
                        AdminConsoleTab(
                            5,
                            "Check-Ins",
                            Icons.Outlined.QrCodeScanner,
                            selectedTab == 5,
                            alertCount = anomalies.count { !it.isResolved }
                        ) { selectedTab = 5 }
                        AdminConsoleTab(6, "Payments", Icons.Outlined.ReceiptLong, selectedTab == 6) { selectedTab = 6 }
                        AdminConsoleTab(
                            7,
                            "Settlements",
                            Icons.Outlined.AccountBalance,
                            selectedTab == 7,
                            count = settlements.count { it.status == SettlementStatus.PENDING }
                        ) { selectedTab = 7 }
                        AdminConsoleTab(
                            8,
                            "Disputes",
                            Icons.Outlined.SupportAgent,
                            selectedTab == 8,
                            alertCount = tickets.count { it.status == TicketStatus.OPEN && (System.currentTimeMillis() - it.createdAt > 86400000L * 2) }
                        ) { selectedTab = 8 }
                        AdminConsoleTab(9, "Analytics", Icons.Outlined.QueryStats, selectedTab == 9) { selectedTab = 9 }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> AdminOverviewTab(
                    allUsers = allUsers,
                    gyms = gyms,
                    partners = partners,
                    visits = visits,
                    anomalies = anomalies,
                    tickets = tickets,
                    onNavigateTab = { tabIdx -> selectedTab = tabIdx },
                    onReviewAnomaly = { selectedTab = 5 },
                    onReviewPartnerKyc = {
                        initialShowKycApprovalQueue = true
                        selectedTab = 2
                    },
                    onReviewTicket = { selectedTab = 8 }
                )
                1 -> AdminUsersTab(
                    users = allUsers,
                    subscriptions = subscriptions,
                    plans = plans,
                    visits = visits,
                    payments = payments,
                    tickets = tickets,
                    auditLogs = auditLogs
                )
                2 -> AdminGymsTab(
                    gyms = gyms,
                    partners = partners,
                    allUsers = allUsers,
                    initialShowApprovalQueue = initialShowKycApprovalQueue
                )
                3 -> AdminPlansTab(
                    plans = plans
                )
                4 -> AdminPromoTab(
                    promoCodes = promoCodes
                )
                5 -> AdminCheckInsTab(
                    visits = visits,
                    anomalies = anomalies
                )
                6 -> AdminPaymentsTab(
                    payments = payments,
                    allUsers = allUsers
                )
                7 -> AdminSettlementsTab(
                    settlements = settlements,
                    gyms = gyms,
                    partners = partners
                )
                8 -> AdminSupportTab(
                    tickets = tickets,
                    adminUser = user
                )
                9 -> AdminAnalyticsTab(
                    auditLogs = auditLogs
                )
            }
        }
    }
}

@Composable
private fun AdminConsoleTab(
    index: Int,
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    count: Int? = null,
    alertCount: Int = 0,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(3.dp),
        color = if (isSelected) NomadSignal else Color.Transparent,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else NomadFog,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else NomadFog
            )

            if (alertCount > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) Color.White else NomadSignal,
                    modifier = Modifier.size(14.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = alertCount.toString(),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) NomadSignal else Color.White
                        )
                    }
                }
            } else if (count != null && !isSelected) {
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "($count)",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = NomadSteel
                )
            }
        }
    }
}
