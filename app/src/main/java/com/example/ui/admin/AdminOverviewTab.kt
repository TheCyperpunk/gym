package com.example.ui.admin

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminOverviewTab(
    allUsers: List<User>,
    gyms: List<Gym>,
    partners: List<GymPartner>,
    visits: List<Visit>,
    anomalies: List<CheckInAnomaly>,
    tickets: List<SupportTicket>,
    onNavigateTab: (Int) -> Unit,
    onReviewAnomaly: (CheckInAnomaly) -> Unit,
    onReviewPartnerKyc: (GymPartner) -> Unit,
    onReviewTicket: (SupportTicket) -> Unit
) {
    val pendingPartners = partners.filter { it.kycStatus == KycStatus.PENDING }
    val unresolvedAnomalies = anomalies.filter { !it.isResolved }
    val urgentTickets = tickets.filter {
        it.status == TicketStatus.OPEN && (System.currentTimeMillis() - it.createdAt > 86400000L * 2)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        // 1. Top-Level KPI Grid (Dense, Steel & Ink)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "NETWORK OPERATIONS METRICS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadSteel
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AdminKpiCard(
                        title = "ACTIVE MEMBERS",
                        value = "1,480",
                        sub = "+12% MoM",
                        modifier = Modifier.weight(1f)
                    )
                    AdminKpiCard(
                        title = "ACTIVE GYMS",
                        value = "${gyms.count { it.status == GymStatus.ACTIVE }}",
                        sub = "${partners.size} partners",
                        modifier = Modifier.weight(1f)
                    )
                    AdminKpiCard(
                        title = "ACTIVE CITIES",
                        value = "5",
                        sub = "Tokyo, Ldn, NY, Ber, Sgp",
                        modifier = Modifier.weight(1.1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AdminKpiCard(
                        title = "VISITS THIS MONTH",
                        value = "844",
                        sub = "~28 check-ins/day",
                        modifier = Modifier.weight(1f)
                    )
                    AdminKpiCard(
                        title = "REVENUE (GMV)",
                        value = "$142.8k",
                        sub = "MRR: $128.4k",
                        modifier = Modifier.weight(1f)
                    )
                    AdminKpiCard(
                        title = "PARTNER PAYOUTS",
                        value = "$48.2k",
                        sub = "Margin: 66.2%",
                        isMarginHighlight = true,
                        modifier = Modifier.weight(1.1f)
                    )
                }
            }
        }

        // 2. 30-Day Visits Trend Line Chart (Greyscale with NomadSignal on Current Value)
        item {
            AdminVisitsChartCard(totalVisitsThisMonth = 844)
        }

        // 3. "Needs Attention" Panel (Signal strictly for urgent items)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NomadSignal,
                            modifier = Modifier.size(8.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "NEEDS ATTENTION QUEUE",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadInk
                        )
                    }

                    Text(
                        text = "${unresolvedAnomalies.size + urgentTickets.size + pendingPartners.size} urgent items",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = NomadSteel
                    )
                }

                if (unresolvedAnomalies.isEmpty() && urgentTickets.isEmpty() && pendingPartners.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        color = NomadMist,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                    ) {
                        Text(
                            text = "All clear. No flagged anomalies, pending KYC verifications, or tickets older than 48h.",
                            fontSize = 12.sp,
                            color = NomadSteel,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                // Flagged Anomalies
                unresolvedAnomalies.forEach { anomaly ->
                    AttentionItemRow(
                        tag = "ANOMALY • ${anomaly.type.severity}",
                        tagColor = NomadSignal,
                        title = anomaly.type.label,
                        description = anomaly.details,
                        time = SimpleDateFormat("HH:mm, MMM d", Locale.getDefault()).format(Date(anomaly.detectedAt)),
                        actionLabel = "Review Anomaly",
                        onAction = { onReviewAnomaly(anomaly) }
                    )
                }

                // Open Disputes / Tickets Older Than 48 Hours
                urgentTickets.forEach { ticket ->
                    AttentionItemRow(
                        tag = "ESCALATED TICKET (>48h)",
                        tagColor = NomadAmber,
                        title = "${ticket.category}: ${ticket.subject}",
                        description = "Raised by ${ticket.raisedByUserName}. ${ticket.description}",
                        time = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(ticket.createdAt)),
                        actionLabel = "Review Ticket",
                        onAction = { onReviewTicket(ticket) }
                    )
                }

                // Gyms Pending KYC Approval
                pendingPartners.forEach { partner ->
                    AttentionItemRow(
                        tag = "KYC APPROVAL REQUIRED",
                        tagColor = NomadSteel,
                        title = partner.businessName,
                        description = "Payout: $${String.format("%.2f", partner.payoutPerVisit)}/visit via ${partner.payoutMethod}. Banking details submitted.",
                        time = "Pending Review",
                        actionLabel = "Review Partner",
                        onAction = { onReviewPartnerKyc(partner) }
                    )
                }
            }
        }

        // 4. Quick Module Launchers
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "OPERATIONS MODULE SHORTCUTS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadSteel
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AdminShortcutButton("Users Directory", Icons.Outlined.People, Modifier.weight(1f)) { onNavigateTab(1) }
                    AdminShortcutButton("Gym Partners", Icons.Outlined.Storefront, Modifier.weight(1f)) { onNavigateTab(2) }
                    AdminShortcutButton("Check-in Feed", Icons.Outlined.QrCodeScanner, Modifier.weight(1f)) { onNavigateTab(5) }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AdminShortcutButton("Settlements", Icons.Outlined.AccountBalance, Modifier.weight(1f)) { onNavigateTab(7) }
                    AdminShortcutButton("Disputes & Tickets", Icons.Outlined.SupportAgent, Modifier.weight(1f)) { onNavigateTab(8) }
                    AdminShortcutButton("Margin Analytics", Icons.Outlined.QueryStats, Modifier.weight(1f)) { onNavigateTab(9) }
                }
            }
        }
    }
}

@Composable
private fun AdminKpiCard(
    title: String,
    value: String,
    sub: String,
    isMarginHighlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = if (isMarginHighlight) NomadInk else NomadMist,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isMarginHighlight) NomadSignal else NomadLine
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = title,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = if (isMarginHighlight) NomadFog else NomadSteel,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isMarginHighlight) Color.White else NomadInk
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = sub,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                color = if (isMarginHighlight) NomadSignal else NomadSteel,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AdminVisitsChartCard(totalVisitsThisMonth: Int) {
    // 30-day mock visit points representing daily check-in volume trend
    val points = remember {
        listOf(
            18f, 22f, 20f, 25f, 28f, 31f, 26f,
            24f, 29f, 33f, 30f, 28f, 35f, 38f,
            34f, 32f, 37f, 41f, 39f, 36f, 42f,
            45f, 40f, 38f, 43f, 46f, 44f, 48f,
            50f, 52f
        )
    }

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
                Column {
                    Text(
                        text = "30-DAY CHECK-IN VOLUME TREND",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk
                    )
                    Text(
                        text = "Aggregated across all global partner terminals",
                        fontSize = 10.sp,
                        color = NomadSteel
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = NomadSignal,
                        modifier = Modifier.size(7.dp)
                    ) {}
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Today: 52 visits",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadSignal
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Greyscale Line Chart Canvas with NomadSignal current point
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val maxVal = 60f
                    val minVal = 10f

                    // Grid lines (subtle greyscale)
                    val gridSteps = 3
                    for (i in 0..gridSteps) {
                        val y = h * (i.toFloat() / gridSteps)
                        drawLine(
                            color = Color(0xFFD4D8DD),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                        )
                    }

                    // Build path
                    val path = Path()
                    val stepX = w / (points.size - 1)

                    points.forEachIndexed { index, value ->
                        val x = index * stepX
                        val normalized = (value - minVal) / (maxVal - minVal)
                        val y = h - (normalized * h)
                        if (index == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                    }

                    // Draw greyscale line
                    drawPath(
                        path = path,
                        color = Color(0xFF6B7280),
                        style = Stroke(width = 2.5f)
                    )

                    // Draw signal dot on the last/current day
                    val lastX = w
                    val lastY = h - (((points.last() - minVal) / (maxVal - minVal)) * h)

                    // Outer pulse ring
                    drawCircle(
                        color = NomadSignal.copy(alpha = 0.25f),
                        radius = 8f,
                        center = Offset(lastX, lastY)
                    )
                    // Inner signal dot
                    drawCircle(
                        color = NomadSignal,
                        radius = 4f,
                        center = Offset(lastX, lastY)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "30d ago", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = NomadSteel)
                Text(text = "15d ago", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = NomadSteel)
                Text(text = "Today (Peak)", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadSignal)
            }
        }
    }
}

@Composable
private fun AttentionItemRow(
    tag: String,
    tagColor: Color,
    title: String,
    description: String,
    time: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(1.dp, tagColor.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // High-visibility left color stripe
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(tagColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = tagColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = tag,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = tagColor,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = time,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = NomadSteel
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )

                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = NomadSteel
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onAction,
                        shape = RoundedCornerShape(3.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = actionLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminShortcutButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(3.dp),
        color = Color(0xFF282A2F),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3E434D))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NomadFog,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                maxLines = 1
            )
        }
    }
}
