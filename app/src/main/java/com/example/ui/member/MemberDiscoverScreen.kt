package com.example.ui.member

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.NomadFitRepository
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDiscoverScreen(
    user: User,
    gyms: List<Gym>,
    activeSubscription: Subscription?,
    plans: List<MembershipPlan>,
    onSelectGym: (Gym) -> Unit,
    onNavigateToPlans: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var selectedCity by remember { mutableStateOf(user.homeCity) }
    var selectedRadius by remember { mutableStateOf("10km") } // "3km", "5km", "10km", "Any"
    var selectedFacility by remember { mutableStateOf<String?>(null) } // "Pool", "Sauna", "Free weights", "Classes", "24-Hour"
    var isFullListView by remember { mutableStateOf(false) }
    var selectedGymId by remember { mutableStateOf<String?>(null) }

    // City Lead capture form state
    var leadEmail by remember { mutableStateOf(user.email) }
    var leadSubmitted by remember { mutableStateOf(false) }

    val activePlan = remember(plans, activeSubscription) {
        plans.find { it.id == activeSubscription?.planId }
    }

    val availableCities = listOf("Tokyo", "London", "New York", "Berlin", "Barcelona", "Singapore", "Paris", "Sydney", "Kyoto")
    val facilityFilters = listOf("All Facilities", "Sauna & Ice Bath", "Olympic Lifting", "Pool", "Classes", "24/7 Access")
    val radiusFilters = listOf("3km", "5km", "10km", "Any")

    // Filter gyms
    val filteredGyms = remember(gyms, selectedCity, selectedFacility) {
        gyms.filter { gym ->
            val matchesCity = gym.city.equals(selectedCity, ignoreCase = true)
            val matchesFacility = selectedFacility == null || selectedFacility == "All Facilities" ||
                    gym.facilities.any { it.contains(selectedFacility!!, ignoreCase = true) }
            matchesCity && matchesFacility
        }
    }

    Scaffold(
        containerColor = NomadConcrete,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NomadMist)
                    .border(1.dp, NomadLine)
                    .statusBarsPadding()
                    .padding(vertical = 8.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Discover Gyms",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk
                    )

                    // View Mode Toggle (Map / List)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = NomadConcrete,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                    ) {
                        Row(modifier = Modifier.padding(3.dp)) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (!isFullListView) NomadInk else Color.Transparent,
                                modifier = Modifier.clickable { isFullListView = false }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Map,
                                        contentDescription = null,
                                        tint = if (!isFullListView) Color.White else NomadSteel,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Map",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!isFullListView) Color.White else NomadSteel
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isFullListView) NomadInk else Color.Transparent,
                                modifier = Modifier.clickable { isFullListView = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.List,
                                        contentDescription = null,
                                        tint = if (isFullListView) Color.White else NomadSteel,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "List",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isFullListView) Color.White else NomadSteel
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // City Selector Horizontal Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableCities.forEach { city ->
                        val isSelected = selectedCity.equals(city, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) NomadInk else NomadConcrete,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NomadInk else NomadLine
                            ),
                            modifier = Modifier.clickable {
                                selectedCity = city
                                leadSubmitted = false
                            }
                        ) {
                            Text(
                                text = city,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else NomadInk,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Facility & Radius Filters Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Radius dropdown chips
                    radiusFilters.forEach { radius ->
                        val isSelected = selectedRadius == radius
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) NomadSignal.copy(alpha = 0.15f) else Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NomadSignal else NomadLine
                            ),
                            modifier = Modifier.clickable { selectedRadius = radius }
                        ) {
                            Text(
                                text = "< $radius",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) NomadSignal else NomadSteel,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(14.dp)
                            .background(NomadLine)
                    )

                    // Facility Filters
                    facilityFilters.forEach { facility ->
                        val isSelected = if (facility == "All Facilities") selectedFacility == null else selectedFacility == facility
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) NomadMoss.copy(alpha = 0.15f) else Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NomadMoss else NomadLine
                            ),
                            modifier = Modifier.clickable {
                                selectedFacility = if (facility == "All Facilities") null else facility
                            }
                        ) {
                            Text(
                                text = facility,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) NomadMoss else NomadSteel,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        if (filteredGyms.isEmpty()) {
            // Empty State: Lead-Capture for New Cities
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOff,
                            contentDescription = null,
                            tint = NomadSignal,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No gyms in $selectedCity yet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "We are currently scouting and vetting independent partner gyms in $selectedCity. Register your interest to get notified when we launch here.",
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = NomadSteel,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (leadSubmitted) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = NomadMoss.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadMoss)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
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
                                text = "We'll notify you when Fit loop goes live in $selectedCity!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadMoss
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NomadMist,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "GET NOTIFIED ON LAUNCH",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = leadEmail,
                                onValueChange = { leadEmail = it },
                                placeholder = { Text("your.email@nomad.com") },
                                singleLine = true,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NomadSignal,
                                    unfocusedBorderColor = NomadLine,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    val ok = NomadFitRepository.submitCityLeadCapture(selectedCity, leadEmail)
                                    if (ok) leadSubmitted = true
                                },
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Text("Notify Me When Live", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (!isFullListView) {
                    // Map Section (Top ~50-60% of viewport)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.1f)
                            .background(NomadConcrete)
                            .border(1.dp, NomadLine)
                    ) {
                        // Custom Interactive Map Canvas
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Street grid background
                            val step = 32.dp.toPx()
                            for (x in 0..(size.width / step).toInt()) {
                                drawLine(
                                    color = NomadLine.copy(alpha = 0.6f),
                                    start = Offset(x * step, 0f),
                                    end = Offset(x * step, size.height),
                                    strokeWidth = 1f
                                )
                            }
                            for (y in 0..(size.height / step).toInt()) {
                                drawLine(
                                    color = NomadLine.copy(alpha = 0.6f),
                                    start = Offset(0f, y * step),
                                    end = Offset(size.width, y * step),
                                    strokeWidth = 1f
                                )
                            }
                        }

                        // Map Pins for Filtered Gyms
                        Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                            filteredGyms.forEachIndexed { index, gym ->
                                val isSelected = gym.id == selectedGymId
                                val offsetX = when (index % 3) {
                                    0 -> 40.dp
                                    1 -> 160.dp
                                    else -> 280.dp
                                }
                                val offsetY = when ((index / 3) % 2) {
                                    0 -> 30.dp
                                    else -> 110.dp
                                }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) NomadSignal else if (gym.tier == GymTier.PREMIUM) NomadInk else NomadMist,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        if (isSelected) Color.White else NomadLine
                                    ),
                                    shadowElevation = 3.dp,
                                    modifier = Modifier
                                        .offset(x = offsetX, y = offsetY)
                                        .clickable {
                                            selectedGymId = gym.id
                                            val targetIndex = filteredGyms.indexOfFirst { it.id == gym.id }
                                            if (targetIndex >= 0) {
                                                coroutineScope.launch {
                                                    listState.animateScrollToItem(targetIndex)
                                                }
                                            }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.FitnessCenter,
                                            contentDescription = null,
                                            tint = if (isSelected || gym.tier == GymTier.PREMIUM) Color.White else NomadInk,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = gym.name.take(14),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected || gym.tier == GymTier.PREMIUM) Color.White else NomadInk
                                        )
                                    }
                                }
                            }
                        }

                        // Map center radar badge
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = NomadInk.copy(alpha = 0.85f),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "MAP VIEW • ${selectedCity.uppercase()} • ${filteredGyms.size} CLUSTERS",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Scrollable List of Results
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(if (isFullListView) 1f else 1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
                ) {
                    items(filteredGyms, key = { it.id }) { gym ->
                        val isSelected = gym.id == selectedGymId
                        val isIncludedInPlan = activePlan?.eligibleGymTiers?.contains(gym.tier.name.lowercase()) == true

                        DiscoverGymListItem(
                            gym = gym,
                            isSelected = isSelected,
                            isIncludedInPlan = isIncludedInPlan,
                            onClick = {
                                selectedGymId = gym.id
                                onSelectGym(gym)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiscoverGymListItem(
    gym: Gym,
    isSelected: Boolean,
    isIncludedInPlan: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = if (isSelected) NomadSignal.copy(alpha = 0.05f) else NomadMist,
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) NomadSignal else NomadLine
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo Thumbnail
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(16.dp))
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
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = gym.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk,
                        maxLines = 1
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (gym.tier == GymTier.PREMIUM) NomadSignal else NomadInk
                    ) {
                        Text(
                            text = gym.tier.label.uppercase(),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = gym.address,
                    fontSize = 12.sp,
                    color = NomadSteel,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Plan inclusion tag
                    if (isIncludedInPlan) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NomadMoss.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "INCLUDED IN PLAN",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadMoss,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NomadAmber.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "NOT INCLUDED",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadAmber,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "1.2 km away",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = NomadSteel
                    )
                }
            }
        }
    }
}
