package com.example.ui.member

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Nfc
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MembershipPlan
import com.example.model.Subscription
import com.example.model.User
import com.example.ui.theme.*

@Composable
fun MemberPlansScreen(
    user: User,
    plans: List<MembershipPlan>,
    activeSubscription: Subscription?,
    onSelectPlanForCheckout: (MembershipPlan, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Billing cycle toggle: Monthly vs Annual (Save 20%)
    var isAnnual by remember { mutableStateOf(false) }

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
                    text = "Membership Plans",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )
                Text(
                    text = "One subscription for curated independent gyms worldwide",
                    fontSize = 12.sp,
                    color = NomadSteel
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Billing Cycle Toggle (Monthly / Annual with 20% savings)
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = NomadConcrete,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (!isAnnual) NomadInk else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isAnnual = false }
                        ) {
                            Text(
                                text = "Monthly Billing",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isAnnual) Color.White else NomadSteel,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isAnnual) NomadInk else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isAnnual = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Annual",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAnnual) Color.White else NomadSteel
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = NomadMoss
                                ) {
                                    Text(
                                        text = "SAVE 20%",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 80.dp)
        ) {
            // Render each plan as a physical credential-style card
            items(plans, key = { it.id }) { plan ->
                val isCurrentPlan = activeSubscription?.planId == plan.id
                val basePrice = plan.price
                val displayPrice = if (isAnnual) basePrice * 0.8 else basePrice

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(
                        if (isCurrentPlan) 1.5.dp else 1.dp,
                        if (isCurrentPlan) NomadMoss else NomadLine
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = plan.name,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NomadInk
                                    )
                                    if (plan.id == "plan_global_unlimited") {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = NomadSignal
                                        ) {
                                            Text(
                                                text = "POPULAR",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = plan.description,
                                    fontSize = 12.sp,
                                    color = NomadSteel,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            if (isCurrentPlan) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = NomadMoss.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "CURRENT PLAN",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NomadMoss,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Calculated Price display
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$${String.format("%.0f", displayPrice)}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadInk
                            )
                            Text(
                                text = if (isAnnual) " / month (billed $${String.format("%.0f", displayPrice * 12)}/yr)" else " / month",
                                fontSize = 12.sp,
                                color = NomadSteel,
                                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Feature bullet rows
                        PlanFeatureRow(
                            text = if (plan.isUnlimited) "Unlimited monthly check-ins" else "${plan.visitAllowance} check-ins per cycle"
                        )
                        PlanFeatureRow(
                            text = if (plan.eligibleGymTiers.contains("premium")) "Standard & Premium partner gyms included" else "Standard partner gyms included"
                        )
                        PlanFeatureRow(
                            text = "Access across all 7 participating global cities"
                        )
                        PlanFeatureRow(
                            text = "No lock-in contracts; cancel or pause anytime"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isCurrentPlan) {
                            OutlinedButton(
                                onClick = { /* Already active */ },
                                enabled = false,
                                shape = RoundedCornerShape(22.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                                modifier = Modifier.fillMaxWidth().height(46.dp)
                            ) {
                                Text(
                                    text = "Active Subscription",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NomadFog
                                )
                            }
                        } else {
                            Button(
                                onClick = { onSelectPlanForCheckout(plan, isAnnual) },
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (plan.id == "plan_global_unlimited") NomadSignal else NomadInk
                                ),
                                modifier = Modifier.fillMaxWidth().height(46.dp)
                            ) {
                                Text(
                                    text = "Select ${plan.name}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Plain-Language Comparison List Below the Cards
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "NETWORK MEMBERSHIP STANDARDS",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadSteel
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        ComparisonBullet("All plans include 24/7 digital access pass with offline sync.")
                        ComparisonBullet("No joining fees, contracts, or cancellation penalties.")
                        ComparisonBullet("Partner gyms vetted for equipment quality and hygiene.")
                        ComparisonBullet("Visits reset on the 1st of every calendar billing cycle.")
                        ComparisonBullet("Global roaming across all network cities included automatically.")
                    }
                }
            }
        }
    }
}

@Composable
private fun PlanFeatureRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = null,
            tint = NomadMoss,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = NomadInk
        )
    }
}

@Composable
private fun ComparisonBullet(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            color = NomadSignal,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 6.dp)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = NomadInk,
            lineHeight = 16.sp
        )
    }
}
