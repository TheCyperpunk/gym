package com.example.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class OnboardingSlideData(
    val headline: String,
    val body: String,
    val subtitle: String,
    val icon: ImageVector,
    val badgeText: String
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSlide by remember { mutableIntStateOf(0) }

    val slides = listOf(
        OnboardingSlideData(
            headline = "One membership,\nevery gym.",
            body = "Access hundreds of top independent strength clubs, Olympic lifting facilities, and recovery spas worldwide with a single pass.",
            subtitle = "GLOBAL NETWORK",
            icon = Icons.Outlined.FitnessCenter,
            badgeText = "ALL-ACCESS PASS"
        ),
        OnboardingSlideData(
            headline = "Find a gym\nwherever you are.",
            body = "Explore curated partner gyms across Tokyo, London, New York, Berlin, Barcelona, Singapore and beyond.",
            subtitle = "LOCATION INDEPENDENT",
            icon = Icons.Outlined.Public,
            badgeText = "7+ GLOBAL HUBS"
        ),
        OnboardingSlideData(
            headline = "Check in with a code,\nno paperwork.",
            body = "Show your dynamic 6-digit credential pass at the front desk for immediate, seamless turnstile entry.",
            subtitle = "INSTANT CREDENTIAL",
            icon = Icons.Outlined.QrCodeScanner,
            badgeText = "6-DIGIT NFC/KEY"
        )
    )

    Scaffold(
        containerColor = NomadConcrete,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fit loop Logo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = NomadInk,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "FL",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSignal
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Fit loop",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk
                    )
                }

                // Skip Text Link (Top Right on every slide)
                Text(
                    text = "Skip",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NomadSteel,
                    modifier = Modifier
                        .clickable { onFinish() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Slide Illustration Area (Line-art illustration)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                shape = RoundedCornerShape(24.dp),
                color = NomadMist,
                border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Line-art blueprint grid background
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val step = 24.dp.toPx()
                        for (x in 0..(size.width / step).toInt()) {
                            drawLine(
                                color = NomadLine.copy(alpha = 0.5f),
                                start = Offset(x * step, 0f),
                                end = Offset(x * step, size.height),
                                strokeWidth = 1f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                            )
                        }
                        for (y in 0..(size.height / step).toInt()) {
                            drawLine(
                                color = NomadLine.copy(alpha = 0.5f),
                                start = Offset(0f, y * step),
                                end = Offset(size.width, y * step),
                                strokeWidth = 1f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                            )
                        }
                    }

                    // Main Focal Line-Art Centerpiece
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = NomadInk,
                            border = androidx.compose.foundation.BorderStroke(2.dp, NomadSignal),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = slides[currentSlide].icon,
                                    contentDescription = null,
                                    tint = NomadSignal,
                                    modifier = Modifier.size(38.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NomadConcrete,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                        ) {
                            Text(
                                text = slides[currentSlide].badgeText,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Slide Text Information
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = slides[currentSlide].subtitle,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = NomadSignal
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = slides[currentSlide].headline,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp,
                    textAlign = TextAlign.Center,
                    color = NomadInk
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = slides[currentSlide].body,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    color = NomadSteel,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dot Progress Indicator & Bottom Actions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Dot progress indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    slides.indices.forEach { index ->
                        val isSelected = currentSlide == index
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 24.dp else 8.dp, 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) NomadSignal else NomadFog.copy(alpha = 0.4f))
                                .clickable { currentSlide = index }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Action Button
                if (currentSlide == slides.size - 1) {
                    Button(
                        onClick = onFinish,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Get started",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Outlined.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (currentSlide > 0) {
                            OutlinedButton(
                                onClick = { currentSlide-- },
                                shape = RoundedCornerShape(24.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text("Back", color = NomadInk, fontSize = 14.sp)
                            }
                        }
                        Button(
                            onClick = { currentSlide++ },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                            modifier = Modifier
                                .weight(if (currentSlide > 0) 2f else 1f)
                                .height(48.dp)
                        ) {
                            Text("Next", color = Color.White, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Outlined.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
