package com.example.ui.member

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NomadFitRepository
import com.example.model.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMembershipScreen(
    user: User,
    activeSubscription: Subscription?,
    plans: List<MembershipPlan>,
    onNavigateToPlans: () -> Unit,
    onNavigateToCheckIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activePlan = remember(plans, activeSubscription) {
        plans.find { it.id == activeSubscription?.planId }
    }

    var showPauseSheet by remember { mutableStateOf(false) }
    var showCancelSheet by remember { mutableStateOf(false) }

    val renewalDateStr = remember(activeSubscription?.renewalDate) {
        if (activeSubscription != null) {
            SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(activeSubscription.renewalDate))
        } else "N/A"
    }

    val isPaused = activeSubscription?.status == SubscriptionStatus.PAUSED
    val isCancelled = activeSubscription?.status == SubscriptionStatus.CANCELLED

    Scaffold(
        containerColor = NomadConcrete,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NomadMist)
                    .border(1.dp, NomadLine)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "My Membership",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )
                Text(
                    text = "Credential pass and digital access management",
                    fontSize = 12.sp,
                    color = NomadSteel
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (activeSubscription == null || activePlan == null) {
                // Empty state if member is not subscribed
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CreditCardOff,
                            contentDescription = null,
                            tint = NomadFog,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Active Membership",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadInk
                        )
                        Text(
                            text = "Subscribe to an all-access plan to generate your personal NFC credential card.",
                            fontSize = 13.sp,
                            color = NomadSteel,
                            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                        )
                        Button(
                            onClick = onNavigateToPlans,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Browse Plans", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Centerpiece: Literal Physical Credential Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp),
                    shape = RoundedCornerShape(26.dp),
                    color = NomadInk,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383C45)),
                    shadowElevation = 6.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Microgrid texture canvas
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val step = 16.dp.toPx()
                            for (x in 0..(size.width / step).toInt()) {
                                drawLine(
                                    color = Color.White.copy(alpha = 0.04f),
                                    start = Offset(x * step, 0f),
                                    end = Offset(x * step, size.height),
                                    strokeWidth = 1f
                                )
                            }
                            for (y in 0..(size.height / step).toInt()) {
                                drawLine(
                                    color = Color.White.copy(alpha = 0.04f),
                                    start = Offset(0f, y * step),
                                    end = Offset(size.width, y * step),
                                    strokeWidth = 1f
                                )
                            }
                        }

                        // Physical Card Content
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top Row: Brand & NFC Icon
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = "FIT LOOP PASS",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = NomadSignal
                                    )
                                    Text(
                                        text = activePlan.name.uppercase(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Nfc,
                                        contentDescription = "NFC Contactless Pass",
                                        tint = NomadSignal,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            // Middle: Embossed Chip & Pass ID
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Chip Graphic
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFC98A2C).copy(alpha = 0.8f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5B060)),
                                    modifier = Modifier.size(36.dp, 28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "NF",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    }
                                }

                                Text(
                                    text = "ID: NF-MBR-8839",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }

                            // Bottom Row: Member Name & Expiry Date
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text(
                                        text = "CARDHOLDER",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NomadFog
                                    )
                                    Text(
                                        text = user.fullName.uppercase(),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "STATUS",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NomadFog
                                    )
                                    Text(
                                        text = when {
                                            isPaused -> "PAUSED"
                                            isCancelled -> "CANCELLED"
                                            else -> "ACTIVE"
                                        },
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            isPaused -> NomadAmber
                                            isCancelled -> NomadBrick
                                            else -> NomadMoss
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // If paused, show resume banner
                if (isPaused) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = NomadAmber.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadAmber),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your pass is paused. Resume anytime to check in.",
                                fontSize = 12.sp,
                                color = NomadAmber,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { NomadFitRepository.resumeSubscription(user.uid) },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NomadAmber)
                            ) {
                                Text("Resume Pass", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Usage Progress Bar & Details Card
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CYCLE USAGE",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel
                            )
                            Text(
                                text = if (activePlan.isUnlimited) "Unlimited Plan" else "${activeSubscription.visitsUsedThisCycle} of ${activeSubscription.visitsAllowance} used",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadInk
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress Bar
                        val progress = if (activePlan.isUnlimited) 0.5f else {
                            (activeSubscription.visitsUsedThisCycle.toFloat() / activeSubscription.visitsAllowance.toFloat()).coerceIn(0f, 1f)
                        }
                        LinearProgressIndicator(
                            progress = { progress },
                            color = NomadSignal,
                            trackColor = NomadConcrete,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Renewal & Payment Method rows
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Next renewal date", fontSize = 12.sp, color = NomadSteel)
                            Text(text = renewalDateStr, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NomadInk)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Payment method", fontSize = 12.sp, color = NomadSteel)
                            Text(text = "Visa ending in •••• ${activeSubscription.paymentMethodLast4}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NomadInk)
                        }
                    }
                }

                // Check-in Quick Action
                Button(
                    onClick = onNavigateToCheckIn,
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.QrCodeScanner,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open Check-in Pass", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Row: Plain Text Links (Not Buttons)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isPaused) {
                        Text(
                            text = "Pause membership",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NomadSteel,
                            modifier = Modifier
                                .clickable { showPauseSheet = true }
                                .padding(vertical = 6.dp)
                        )
                    }

                    Text(
                        text = "Change plan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NomadSignal,
                        modifier = Modifier
                            .clickable { onNavigateToPlans() }
                            .padding(vertical = 6.dp)
                    )

                    if (!isCancelled) {
                        Text(
                            text = "Cancel membership",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NomadBrick,
                            modifier = Modifier
                                .clickable { showCancelSheet = true }
                                .padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }

    // Pause Membership Confirmation Sheet
    if (showPauseSheet) {
        AlertDialog(
            onDismissRequest = { showPauseSheet = false },
            containerColor = NomadMist,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Pause Membership", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NomadInk)
            },
            text = {
                Text(
                    text = "Pausing stops billing and access from $renewalDateStr. You can resume anytime without paying re-activation fees.",
                    fontSize = 13.sp,
                    color = NomadSteel,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        NomadFitRepository.pauseSubscription(user.uid)
                        showPauseSheet = false
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NomadAmber)
                ) {
                    Text("Confirm Pause", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPauseSheet = false }) {
                    Text("Keep Active", color = NomadSteel)
                }
            }
        )
    }

    // Cancel Membership Confirmation Sheet
    if (showCancelSheet) {
        AlertDialog(
            onDismissRequest = { showCancelSheet = false },
            containerColor = NomadMist,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Cancel Membership", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NomadInk)
            },
            text = {
                Text(
                    text = "Cancelling ends your access at the end of your billing cycle on $renewalDateStr. You will not be billed again.",
                    fontSize = 13.sp,
                    color = NomadSteel,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        NomadFitRepository.cancelSubscription(user.uid)
                        showCancelSheet = false
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NomadBrick)
                ) {
                    Text("Confirm Cancellation", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelSheet = false }) {
                    Text("Keep Membership", color = NomadSteel)
                }
            }
        )
    }
}
