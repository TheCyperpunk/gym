package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Gym
import com.example.model.GymTier
import com.example.ui.theme.*

@Composable
fun MapVisual(
    gyms: List<Gym>,
    selectedGym: Gym?,
    onGymSelect: (Gym) -> Unit,
    selectedCity: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF1E2024))
            .border(1.dp, NomadLine, RoundedCornerShape(6.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(gyms, selectedCity) {
                    detectTapGestures { tapOffset ->
                        val width = size.width
                        val height = size.height

                        // Find closest gym pin to tap
                        val filteredGyms = if (selectedCity == "All") gyms else gyms.filter { it.city.equals(selectedCity, ignoreCase = true) }
                        val tappedGym = filteredGyms.minByOrNull { gym ->
                            val pinPos = calculatePinPosition(gym, width.toFloat(), height.toFloat(), selectedCity)
                            val dx = tapOffset.x - pinPos.x
                            val dy = tapOffset.y - pinPos.y
                            dx * dx + dy * dy
                        }

                        if (tappedGym != null) {
                            val pinPos = calculatePinPosition(tappedGym, width.toFloat(), height.toFloat(), selectedCity)
                            val dist = (tapOffset.x - pinPos.x) * (tapOffset.x - pinPos.x) + (tapOffset.y - pinPos.y) * (tapOffset.y - pinPos.y)
                            if (dist < 2400f) { // Within tap radius
                                onGymSelect(tappedGym)
                            }
                        }
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // 1. Draw Architectural Grid Lines
            val gridSpacing = 32.dp.toPx()
            var x = 0f
            while (x < canvasWidth) {
                drawLine(
                    color = Color(0xFF282B30),
                    start = Offset(x, 0f),
                    end = Offset(x, canvasHeight),
                    strokeWidth = 1f
                )
                x += gridSpacing
            }
            var y = 0f
            while (y < canvasHeight) {
                drawLine(
                    color = Color(0xFF282B30),
                    start = Offset(0f, y),
                    end = Offset(canvasWidth, y),
                    strokeWidth = 1f
                )
                y += gridSpacing
            }

            // 2. Draw Subtle City Contour / Road Vectors
            val roadPath = Path().apply {
                moveTo(0f, canvasHeight * 0.4f)
                cubicTo(
                    canvasWidth * 0.3f, canvasHeight * 0.25f,
                    canvasWidth * 0.6f, canvasHeight * 0.65f,
                    canvasWidth, canvasHeight * 0.5f
                )
                moveTo(canvasWidth * 0.2f, 0f)
                lineTo(canvasWidth * 0.8f, canvasHeight)
            }
            drawPath(
                path = roadPath,
                color = Color(0xFF2A2D33),
                style = Stroke(width = 2.dp.toPx())
            )

            // 3. Draw Network Links between gyms in the same city
            val currentGyms = if (selectedCity == "All") gyms else gyms.filter { it.city.equals(selectedCity, ignoreCase = true) }
            val positions = currentGyms.map { gym ->
                Pair(gym, calculatePinPosition(gym, canvasWidth, canvasHeight, selectedCity))
            }

            for (i in positions.indices) {
                for (j in (i + 1) until positions.size) {
                    val pos1 = positions[i].second
                    val pos2 = positions[j].second
                    drawLine(
                        color = Color(0xFF383C45),
                        start = pos1,
                        end = pos2,
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }

            // 4. Draw Gym Pins
            positions.forEach { (gym, pinPos) ->
                val isSelected = gym.id == selectedGym?.id
                val isPremium = gym.tier == GymTier.PREMIUM

                // Pulse ring for selected gym
                if (isSelected) {
                    drawCircle(
                        color = NomadSignal.copy(alpha = 0.35f * (1f - pulseRadius / 20f).coerceIn(0f, 1f)),
                        radius = pulseRadius * 2.dp.toPx(),
                        center = pinPos
                    )
                }

                // Outer halo
                drawCircle(
                    color = if (isSelected) NomadSignal else if (isPremium) NomadAmber else NomadMist,
                    radius = if (isSelected) 8.dp.toPx() else 6.dp.toPx(),
                    center = pinPos
                )

                // Inner core
                drawCircle(
                    color = if (isSelected) Color.White else Color(0xFF1E2024),
                    radius = if (isSelected) 4.dp.toPx() else 3.dp.toPx(),
                    center = pinPos
                )
            }
        }

        // Overlay: Map Mode Header & Network Count Indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF141518).copy(alpha = 0.9f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF32353C))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(NomadSignal, RoundedCornerShape(1.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (selectedCity == "All") "GLOBAL NETWORK MAP" else "${selectedCity.uppercase()} ACCESS MAP",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadMist
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF141518).copy(alpha = 0.9f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF32353C))
            ) {
                val activeCount = if (selectedCity == "All") gyms.size else gyms.count { it.city.equals(selectedCity, ignoreCase = true) }
                Text(
                    text = "$activeCount NODES LIVE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = NomadFog,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Map Legend at bottom right
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF141518).copy(alpha = 0.85f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(6.dp).background(NomadAmber, RoundedCornerShape(1.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Premium", fontSize = 9.sp, color = NomadFog)
                }
            }

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF141518).copy(alpha = 0.85f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(6.dp).background(NomadMist, RoundedCornerShape(1.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Standard", fontSize = 9.sp, color = NomadFog)
                }
            }
        }
    }
}

// Helper to project gym coordinates gracefully onto the visual canvas
private fun calculatePinPosition(gym: Gym, width: Float, height: Float, selectedCity: String): Offset {
    val paddingX = width * 0.15f
    val paddingY = height * 0.22f
    val availableW = width - (paddingX * 2)
    val availableH = height - (paddingY * 2)

    return if (selectedCity != "All") {
        // Local cluster projection by address hash
        val hash = gym.name.hashCode()
        val offsetX = (hash % 100).toFloat() / 100f
        val offsetY = ((hash / 100) % 100).toFloat() / 100f
        Offset(
            paddingX + (availableW * (0.2f + offsetX * 0.6f)),
            paddingY + (availableH * (0.2f + offsetY * 0.6f))
        )
    } else {
        // Global Hub layout:
        when (gym.city.lowercase()) {
            "tokyo" -> Offset(width * 0.82f + (gym.name.length % 5) * 10f, height * 0.45f + (gym.name.length % 3) * 12f)
            "london" -> Offset(width * 0.38f + (gym.name.length % 4) * 8f, height * 0.32f + (gym.name.length % 3) * 8f)
            "new york" -> Offset(width * 0.22f + (gym.name.length % 5) * 10f, height * 0.42f + (gym.name.length % 4) * 8f)
            "berlin" -> Offset(width * 0.48f + (gym.name.length % 4) * 8f, height * 0.30f + (gym.name.length % 2) * 8f)
            "barcelona" -> Offset(width * 0.42f + (gym.name.length % 4) * 8f, height * 0.52f + (gym.name.length % 3) * 8f)
            "singapore" -> Offset(width * 0.75f + (gym.name.length % 4) * 8f, height * 0.68f + (gym.name.length % 2) * 8f)
            else -> Offset(width * 0.5f, height * 0.5f)
        }
    }
}
