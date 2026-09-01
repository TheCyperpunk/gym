package com.example.ui.member

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import com.example.ui.components.CredentialCard
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MemberPassScreen(
    user: User,
    subscription: Subscription?,
    plan: MembershipPlan?,
    credentialCode: String,
    onRefreshCode: () -> Unit,
    onNavigateToPlans: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isCheckingInSim by remember { mutableStateOf(false) }
    var checkInSuccessMoment by remember { mutableStateOf(false) }
    var simulationMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = NomadConcrete,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NomadMist)
                    .border(1.dp, NomadLine)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Access Credential",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadInk
                        )
                        Text(
                            text = "Digital keycard for partner gym entry",
                            fontSize = 12.sp,
                            color = NomadSteel
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (subscription?.isActive == true) NomadMoss.copy(alpha = 0.15f) else NomadBrick.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (subscription?.isActive == true) NomadMoss else NomadBrick
                        )
                    ) {
                        Text(
                            text = if (subscription?.isActive == true) "READY FOR USE" else "INACTIVE",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (subscription?.isActive == true) NomadMoss else NomadBrick,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
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
            // Main Physical Credential Card
            item {
                CredentialCard(
                    user = user,
                    subscription = subscription,
                    plan = plan,
                    credentialCode = credentialCode,
                    onRefreshCode = onRefreshCode,
                    showSuccessRing = checkInSuccessMoment
                )
            }

            // Simulated Check-in Confirmation Banner
            if (simulationMessage != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = NomadMoss.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadMoss)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = NomadMoss,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = simulationMessage.orEmpty(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NomadMoss
                            )
                        }
                    }
                }
            }

            // Quick Actions & Simulation
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "FRONT DESK VERIFICATION PREVIEW",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadSteel
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Test the check-in verification flow directly to see the signal ring animation and live visit logging:",
                            fontSize = 12.sp,
                            color = NomadSteel
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                isCheckingInSim = true
                                simulationMessage = null
                                coroutineScope.launch {
                                    val (result, msg) = NomadFitRepository.validateCheckIn(
                                        rawCode = credentialCode,
                                        gymId = "gym_ironforge_tokyo"
                                    )
                                    isCheckingInSim = false
                                    if (result == ValidationResult.APPROVED) {
                                        checkInSuccessMoment = true
                                        simulationMessage = "Check-in validated at IronForge Athletic Club!"
                                        delay(3500)
                                        checkInSuccessMoment = false
                                    } else {
                                        simulationMessage = msg
                                    }
                                }
                            },
                            enabled = !isCheckingInSim && subscription?.isActive == true,
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NomadInk)
                        ) {
                            if (isCheckingInSim) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Validating credential...", color = Color.White, fontSize = 12.sp)
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Simulate check-in at IronForge",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Usage & Allowance Summary Card
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CURRENT BILLING CYCLE",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel
                            )

                            Text(
                                text = if (subscription != null) "Renews in 12 days" else "No cycle active",
                                fontSize = 11.sp,
                                color = NomadFog
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Cycle visits used
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                color = NomadConcrete
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "VISITS USED",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        color = NomadFog
                                    )
                                    Text(
                                        text = "${subscription?.visitsUsedThisCycle ?: 0}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NomadInk
                                    )
                                }
                            }

                            // Allowance left
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                color = NomadConcrete
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "REMAINING",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        color = NomadFog
                                    )
                                    Text(
                                        text = if (subscription?.isUnlimited == true) "Unlimited" else "${subscription?.visitsRemaining ?: 0}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NomadMoss
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = onNavigateToPlans,
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Text(
                                text = "Manage plan & payment method",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NomadInk
                            )
                        }
                    }
                }
            }

            // How it works steps
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "HOW CHECK-IN WORKS",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadSteel
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        CheckInStepItem(
                            stepNumber = "01",
                            title = "Walk into any network gym",
                            description = "No prior booking required at standard locations."
                        )
                        CheckInStepItem(
                            stepNumber = "02",
                            title = "Show or read your 6-digit code",
                            description = "Front desk enters your code in their Partner Portal."
                        )
                        CheckInStepItem(
                            stepNumber = "03",
                            title = "Instant access confirmation",
                            description = "Your pass confirms immediately and unlocks entry."
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckInStepItem(
    stepNumber: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = NomadConcrete,
            border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
        ) {
            Text(
                text = stepNumber,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = NomadSteel,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = NomadInk
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = NomadFog
            )
        }
    }
}
