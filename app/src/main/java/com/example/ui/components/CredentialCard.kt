package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MembershipPlan
import com.example.model.Subscription
import com.example.model.User
import com.example.ui.theme.*

@Composable
fun CredentialCard(
    user: User,
    subscription: Subscription?,
    plan: MembershipPlan?,
    credentialCode: String,
    onRefreshCode: () -> Unit,
    showSuccessRing: Boolean = false,
    modifier: Modifier = Modifier
) {
    val ringProgress = remember { Animatable(0f) }

    LaunchedEffect(showSuccessRing) {
        if (showSuccessRing) {
            ringProgress.snapTo(0f)
            ringProgress.animateTo(1f, animationSpec = tween(durationMillis = 800))
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E2026),
                        Color(0xFF141518)
                    )
                )
            )
            .border(1.dp, Color(0xFF32353E), RoundedCornerShape(26.dp))
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Card Header: Brand + Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                if (subscription?.isActive == true) NomadSignal else NomadFog,
                                CircleShape
                            )
                            .border(
                                2.dp,
                                if (subscription?.isActive == true) NomadSignal.copy(alpha = 0.3f) else Color.Transparent,
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FIT LOOP ACCESS PASS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp,
                        color = NomadFog
                    )
                }

                // Tier / Active Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (subscription?.isActive == true) NomadSignal.copy(alpha = 0.15f) else Color(0xFF2B2D33),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (subscription?.isActive == true) NomadSignal.copy(alpha = 0.4f) else Color(0xFF3F424A)
                    )
                ) {
                    Text(
                        text = if (subscription?.isActive == true) "ACTIVE" else "INACTIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = if (subscription?.isActive == true) NomadSignal else NomadFog,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Middle Section: 6-Digit Code + QR Visual
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CREDENTIAL CODE",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp,
                        color = NomadFog
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = credentialCode,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Show or read aloud to gym front desk",
                        fontSize = 11.sp,
                        color = NomadFog
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    // Rotate code button
                    OutlinedButton(
                        onClick = onRefreshCode,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A3D45)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 5.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Refresh Code",
                            modifier = Modifier.size(14.dp),
                            tint = NomadFog
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Refresh code",
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // QR Pattern Matrix inside access box
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.clip(RoundedCornerShape(18.dp))
                ) {
                    QrCodeVisual(
                        code = credentialCode,
                        size = 100.dp,
                        qrColor = Color(0xFF1A1B1E),
                        backgroundColor = Color(0xFFF5F4F2)
                    )

                    // Success Confirmation Ring Animation
                    if (showSuccessRing && ringProgress.value > 0f) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .drawBehind {
                                    drawArc(
                                        color = NomadSignal,
                                        startAngle = -90f,
                                        sweepAngle = 360f * ringProgress.value,
                                        useCenter = false,
                                        style = Stroke(width = 4.dp.toPx())
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = NomadSignal,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = "Approved",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Card Footer: Member Name, Plan, and Allowance
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF2C2F36))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = user.fullName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = plan?.name ?: "No Active Plan",
                        fontSize = 12.sp,
                        color = NomadFog
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "ALLOWANCE",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = NomadFog
                    )
                    Text(
                        text = when {
                            subscription == null -> "0 passes"
                            subscription.isUnlimited -> "Unlimited"
                            else -> "${subscription.visitsRemaining} left this cycle"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (subscription?.isActive == true) NomadMoss else NomadFog
                    )
                }
            }
        }
    }
}
