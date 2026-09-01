package com.example.ui.member

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.graphics.Color
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
fun MemberHomeScreen(
    user: User,
    activeSubscription: Subscription?,
    plans: List<MembershipPlan>,
    gyms: List<Gym>,
    visits: List<Visit>,
    notifications: List<NotificationItem>,
    onNavigateToCheckIn: () -> Unit,
    onNavigateToPlans: () -> Unit,
    onNavigateToGymDetail: (Gym) -> Unit,
    onNavigateToDiscover: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToWorkoutCircuit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activePlan = remember(plans, activeSubscription) {
        plans.find { it.id == activeSubscription?.planId }
    }

    val userVisits = remember(visits, user.uid) {
        visits.filter { it.userId == user.uid }.sortedByDescending { it.checkInTimestamp }
    }

    val unreadNotifCount = remember(notifications, user.uid) {
        notifications.count { it.userId == user.uid && !it.read }
    }

    val nearbyGyms = remember(gyms, user.homeCity) {
        gyms.filter { it.city.equals(user.homeCity, ignoreCase = true) || it.status == GymStatus.ACTIVE }.take(5)
    }

    val hasActiveMembership = activeSubscription?.status == SubscriptionStatus.ACTIVE

    Scaffold(
        containerColor = NomadConcrete,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NomadMist)
                    .border(1.dp, NomadLine)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val firstName = user.fullName.split(" ").firstOrNull() ?: "Member"
                    Text(
                        text = "Good morning, $firstName",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk
                    )
                    Text(
                        text = "${user.homeCity} Hub • Nomad Pass",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = NomadSteel
                    )
                }

                // Notification Bell Icon with Signal Dot
                IconButton(onClick = onNavigateToNotifications) {
                    Box {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = NomadInk
                        )
                        if (unreadNotifCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(NomadSignal)
                                    .align(Alignment.TopEnd)
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
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Membership Status Section
            if (activeSubscription != null && activePlan != null) {
                // Calculate days to renewal
                val daysToRenewal = remember(activeSubscription.renewalDate) {
                    val diff = activeSubscription.renewalDate - System.currentTimeMillis()
                    (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
                }

                val isExpiringSoon = daysToRenewal in 1..5
                val isPastDue = activeSubscription.status != SubscriptionStatus.ACTIVE

                val statusChipBg = when {
                    isPastDue -> NomadBrick
                    isExpiringSoon -> NomadAmber
                    else -> NomadMoss
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(statusChipBg)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = activePlan.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NomadInk
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            val visitsText = if (activePlan.isUnlimited) {
                                "Unlimited check-ins • ${activeSubscription.visitsUsedThisCycle} logged"
                            } else {
                                "${activeSubscription.visitsAllowance - activeSubscription.visitsUsedThisCycle} of ${activeSubscription.visitsAllowance} visits remaining"
                            }
                            Text(
                                text = visitsText,
                                fontSize = 12.sp,
                                color = NomadSteel
                            )
                        }

                        // Compact Status Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = statusChipBg.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = when {
                                    isPastDue -> "INACTIVE"
                                    isExpiringSoon -> "RENEWS IN ${daysToRenewal}D"
                                    else -> "ACTIVE PASS"
                                },
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusChipBg,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            } else {
                // Empty state if member has no active subscription
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CardMembership,
                            contentDescription = null,
                            tint = NomadSignal,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "You don't have a membership yet",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadInk
                        )
                        Text(
                            text = "Get access to top independent gyms worldwide with zero lock-in contracts.",
                            fontSize = 12.sp,
                            color = NomadSteel,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )
                        Button(
                            onClick = onNavigateToPlans,
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Text("Browse Membership Plans", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Prominent "Check In" Primary Button (Centerpiece call-to-action)
            if (hasActiveMembership) {
                Button(
                    onClick = onNavigateToCheckIn,
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.QrCodeScanner,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Check in Now",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Nearby Gyms Horizontal Carousel
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nearby Gyms",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadInk
                        )
                        Text(
                            text = "View all",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NomadSignal,
                            modifier = Modifier.clickable { onNavigateToDiscover() }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        nearbyGyms.forEachIndexed { index, gym ->
                            val dist = when (index) {
                                0 -> "0.6 km away"
                                1 -> "1.2 km away"
                                2 -> "2.4 km away"
                                3 -> "3.1 km away"
                                else -> "4.0 km away"
                            }
                            NearbyGymCard(
                                gym = gym,
                                distance = dist,
                                onClick = { onNavigateToGymDetail(gym) }
                            )
                        }
                    }
                }
            }

            // Recent Activity Section
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Activity",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk
                    )
                    if (userVisits.isNotEmpty()) {
                        Text(
                            text = "All visits",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NomadSteel,
                            modifier = Modifier.clickable { onNavigateToActivity() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (userVisits.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = NomadMist,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                    ) {
                        Text(
                            text = "No recent visits logged yet.",
                            fontSize = 12.sp,
                            color = NomadFog,
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        userVisits.take(3).forEach { visit ->
                            CompactVisitRow(visit = visit)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun NearbyGymCard(
    gym: Gym,
    distance: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
        modifier = Modifier
            .width(230.dp)
            .clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                    .background(NomadConcrete)
            ) {
                if (gym.photos.isNotEmpty()) {
                    AsyncImage(
                        model = gym.photos.first(),
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
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Tier Badge in photo corner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (gym.tier == GymTier.PREMIUM) NomadSignal else NomadInk,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = gym.tier.label.uppercase(),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = gym.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk,
                    maxLines = 1
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${gym.city}",
                        fontSize = 11.sp,
                        color = NomadSteel
                    )
                    Text(
                        text = distance,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = NomadMoss
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactVisitRow(visit: Visit) {
    val isApproved = visit.validationResult == ValidationResult.APPROVED
    val relativeDate = remember(visit.checkInTimestamp) {
        val diffHours = (System.currentTimeMillis() - visit.checkInTimestamp) / (1000 * 60 * 60)
        when {
            diffHours < 24 -> "Today"
            diffHours < 48 -> "Yesterday"
            else -> "${diffHours / 24} days ago"
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = visit.gymName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )
                Text(
                    text = "${visit.gymCity} • $relativeDate",
                    fontSize = 11.sp,
                    color = NomadSteel
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isApproved) NomadMoss.copy(alpha = 0.15f) else NomadBrick.copy(alpha = 0.15f)
            ) {
                Text(
                    text = if (isApproved) "APPROVED" else "DENIED",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isApproved) NomadMoss else NomadBrick,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}
