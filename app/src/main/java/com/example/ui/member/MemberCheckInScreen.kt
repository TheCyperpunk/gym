package com.example.ui.member

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NomadFitRepository
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberCheckInScreen(
    user: User,
    activeSubscription: Subscription?,
    plans: List<MembershipPlan>,
    gyms: List<Gym>,
    activeCredentialCode: String,
    onClose: () -> Unit,
    onNavigateToPlans: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var secondsRemaining by remember { mutableIntStateOf(60) }
    var validationSuccessGym by remember { mutableStateOf<Gym?>(null) }
    var isSimulatingValidation by remember { mutableStateOf(false) }

    val activePlan = remember(plans, activeSubscription) {
        plans.find { it.id == activeSubscription?.planId }
    }

    val hasActivePass = activeSubscription?.status == SubscriptionStatus.ACTIVE
    val hasVisitsRemaining = if (activePlan?.isUnlimited == true) true else {
        (activeSubscription?.visitsAllowance ?: 0) > (activeSubscription?.visitsUsedThisCycle ?: 0)
    }

    // 60-Second Countdown Timer
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            if (secondsRemaining > 1) {
                secondsRemaining--
            } else {
                secondsRemaining = 60
                NomadFitRepository.refreshCredentialCode()
            }
        }
    }

    Scaffold(
        containerColor = NomadConcrete,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = NomadInk
                    )
                }

                Text(
                    text = "CHECK-IN PASS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = NomadSteel
                )

                // Refresh Code Button
                IconButton(
                    onClick = {
                        secondsRemaining = 60
                        NomadFitRepository.refreshCredentialCode()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Refresh Code",
                        tint = NomadInk
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (validationSuccessGym != null) {
                // Success Transition View (Signal Ring Burst + Confirmed Check-In)
                val gym = validationSuccessGym!!
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = NomadMoss.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(2.dp, NomadMoss),
                        modifier = Modifier.size(88.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = NomadMoss,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Access Approved!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk
                    )
                    Text(
                        text = "Welcome to ${gym.name}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NomadSteel,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "${gym.city} • Verified just now",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = NomadMoss,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NomadMist,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "CHECK-IN DETAILS",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Code verified: $activeCredentialCode", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = NomadInk)
                            Text(text = "Turnstile access unlocked for standard workout session.", fontSize = 12.sp, color = NomadSteel, modifier = Modifier.padding(top = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {
                            validationSuccessGym = null
                            onClose()
                        },
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text("Done", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            } else if (!hasActivePass || !hasVisitsRemaining) {
                // Inactive or Exhausted Visits State (Never shows a dead code!)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = NomadAmber.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, NomadAmber),
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = NomadAmber,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (!hasActivePass) "Pass Inactive" else "No Check-ins Remaining",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk
                    )
                    Text(
                        text = if (!hasActivePass) {
                            "Your subscription is currently paused or inactive. Renew or reactivate to generate your check-in pass."
                        } else {
                            "You have used all visits for this billing cycle. Upgrade to unlimited or wait for the cycle reset on the 1st."
                        },
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = NomadSteel,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onNavigateToPlans,
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text("Browse & Upgrade Plans", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Active Credential Display with Countdown Ring
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // QR Pattern & Countdown Visual Container
                    Box(
                        modifier = Modifier.size(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Countdown Ring Canvas
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 4.dp.toPx()
                            val progress = secondsRemaining.toFloat() / 60f
                            val sweepAngle = 360f * progress

                            // Background Track
                            drawCircle(
                                color = NomadLine,
                                radius = size.minDimension / 2 - strokeWidth,
                                style = Stroke(width = strokeWidth)
                            )

                            // Active Progress Arc
                            drawArc(
                                color = NomadSignal,
                                startAngle = -90f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                topLeft = Offset(strokeWidth, strokeWidth),
                                size = Size(size.width - strokeWidth * 2, size.height - strokeWidth * 2)
                            )
                        }

                        // Inner Credential Box
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = NomadMist,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                            modifier = Modifier.size(190.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.QrCode,
                                    contentDescription = null,
                                    tint = NomadInk,
                                    modifier = Modifier.size(64.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Dynamic 6-digit code
                                Text(
                                    text = activeCredentialCode,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    color = NomadInk
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Refreshes in ${secondsRemaining}s",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = NomadSteel
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Show this to gym staff",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk
                    )
                    Text(
                        text = "Front desk will scan or enter the 6-digit key to log your check-in.",
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = NomadSteel,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Front Desk Scan Simulation (For quick interactive testing)
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = NomadMist,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "TEST CHECK-IN AT PARTNER GYM",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            val testGym = gyms.firstOrNull { it.status == GymStatus.ACTIVE } ?: gyms.first()

                            Button(
                                onClick = {
                                    isSimulatingValidation = true
                                    coroutineScope.launch {
                                        delay(800)
                                        val (validationResult, _) = NomadFitRepository.validateCheckIn(
                                            rawCode = activeCredentialCode,
                                            gymId = testGym.id
                                        )
                                        isSimulatingValidation = false
                                        if (validationResult == ValidationResult.APPROVED) {
                                            validationSuccessGym = testGym
                                        }
                                    }
                                },
                                enabled = !isSimulatingValidation,
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                if (isSimulatingValidation) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                } else {
                                    Text("Simulate Scan at ${testGym.name.take(18)}", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
