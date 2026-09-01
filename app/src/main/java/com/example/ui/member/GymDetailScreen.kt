package com.example.ui.member

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GymDetailScreen(
    gym: Gym,
    user: User,
    activeSubscription: Subscription?,
    plans: List<MembershipPlan>,
    onBack: () -> Unit,
    onNavigateToCheckIn: () -> Unit,
    onNavigateToPlans: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activePlan = remember(plans, activeSubscription) {
        plans.find { it.id == activeSubscription?.planId }
    }

    val isEligible = remember(activePlan, gym.tier) {
        activeSubscription?.status == SubscriptionStatus.ACTIVE &&
                activePlan?.eligibleGymTiers?.contains(gym.tier.name.lowercase()) == true
    }

    var selectedPhotoIndex by remember { mutableIntStateOf(0) }

    val todayDayOfWeek = remember {
        SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
    }

    Scaffold(
        containerColor = NomadConcrete,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NomadMist)
                    .border(1.dp, NomadLine)
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = NomadInk
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = gym.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk,
                    maxLines = 1
                )
            }
        },
        bottomBar = {
            // Sticky Bottom Bar with Eligibility Aware Check-in Action
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NomadLine),
                color = NomadMist
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    if (isEligible) {
                        Button(
                            onClick = onNavigateToCheckIn,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.QrCodeScanner,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Check in here",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    } else {
                        Button(
                            onClick = onNavigateToPlans,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = if (activeSubscription == null) "Subscribe to check in" else "Upgrade Plan to Access",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
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
        ) {
            // Photo Carousel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(NomadConcrete)
            ) {
                if (gym.photos.isNotEmpty()) {
                    AsyncImage(
                        model = gym.photos[selectedPhotoIndex % gym.photos.size],
                        contentDescription = gym.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.FitnessCenter,
                            contentDescription = null,
                            tint = NomadFog,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Photo Indicators
                if (gym.photos.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        gym.photos.indices.forEach { index ->
                            val isSelected = selectedPhotoIndex == index
                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 20.dp else 6.dp, 6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (isSelected) NomadSignal else Color.White.copy(alpha = 0.6f))
                                    .clickable { selectedPhotoIndex = index }
                            )
                        }
                    }
                }

                // Tier Badge overlay
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (gym.tier == GymTier.PREMIUM) NomadSignal else NomadInk,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                ) {
                    Text(
                        text = gym.tier.label.uppercase(),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Details
                Column {
                    Text(
                        text = gym.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = NomadSignal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${gym.address}, ${gym.city}",
                            fontSize = 13.sp,
                            color = NomadSteel
                        )
                    }
                    Text(
                        text = "1.2 km from your current location",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = NomadMoss,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Eligibility Banner
                if (isEligible) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
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
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Included in your ${activePlan?.name ?: "active pass"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadMoss
                            )
                        }
                    }
                } else {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = NomadAmber.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadAmber)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = NomadAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Requires Premium tier access",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NomadAmber
                                )
                            }
                            Text(
                                text = "Upgrade",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSignal,
                                modifier = Modifier.clickable { onNavigateToPlans() }
                            )
                        }
                    }
                }

                // Description
                Text(
                    text = gym.description,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = NomadSteel
                )

                // Facilities (Icon + Label Clean Grid)
                Column {
                    Text(
                        text = "FACILITIES & AMENITIES",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadSteel
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = NomadMist,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            gym.facilities.chunked(2).forEach { pair ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    pair.forEach { facility ->
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = getFacilityIcon(facility),
                                                contentDescription = null,
                                                tint = NomadSignal,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = facility,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = NomadInk
                                            )
                                        }
                                    }
                                    if (pair.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }

                // Operating Hours Table
                Column {
                    Text(
                        text = "OPERATING HOURS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadSteel
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = NomadMist,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val schedules = gym.operatingHours.split(",").map { it.trim() }
                            schedules.forEach { schedule ->
                                val parts = schedule.split(":", limit = 2)
                                val day = parts.getOrNull(0)?.trim() ?: schedule
                                val hours = parts.getOrNull(1)?.trim() ?: ""

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = day,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NomadInk
                                    )
                                    if (hours.isNotEmpty()) {
                                        Text(
                                            text = hours,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = NomadSteel
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Access Rules (Plain Language)
                Column {
                    Text(
                        text = "ACCESS RULES & POLICIES",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadSteel
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = NomadMist,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AccessRuleBullet("Up to 1 verified check-in per calendar day.")
                            AccessRuleBullet("No advance booking required — walk in and show your dynamic pass.")
                            AccessRuleBullet("Clean indoor athletic shoes and workout towel required.")
                            AccessRuleBullet("Locker room & shower access included with entry.")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun AccessRuleBullet(text: String) {
    Row(verticalAlignment = Alignment.Top) {
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

private fun getFacilityIcon(name: String): ImageVector {
    val lower = name.lowercase()
    return when {
        lower.contains("sauna") || lower.contains("bath") -> Icons.Outlined.HotTub
        lower.contains("lift") || lower.contains("weight") || lower.contains("barbell") -> Icons.Outlined.FitnessCenter
        lower.contains("pool") || lower.contains("swim") -> Icons.Outlined.Pool
        lower.contains("towel") || lower.contains("shower") -> Icons.Outlined.Shower
        lower.contains("24") || lower.contains("hour") -> Icons.Outlined.Schedule
        lower.contains("class") || lower.contains("yoga") -> Icons.Outlined.SelfImprovement
        else -> Icons.Outlined.CheckCircle
    }
}
