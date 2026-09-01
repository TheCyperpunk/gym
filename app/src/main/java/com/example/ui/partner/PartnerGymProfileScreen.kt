package com.example.ui.partner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.NomadFitRepository
import com.example.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PartnerGymProfileScreen(
    user: User,
    gyms: List<Gym>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val partnerGym = remember(gyms, user.uid) {
        gyms.find { it.ownerId == user.uid } ?: gyms.first()
    }

    var name by remember(partnerGym) { mutableStateOf(partnerGym.name) }
    var description by remember(partnerGym) { mutableStateOf(partnerGym.description) }
    var address by remember(partnerGym) { mutableStateOf(partnerGym.address) }
    var lat by remember(partnerGym) { mutableDoubleStateOf(partnerGym.lat) }
    var lng by remember(partnerGym) { mutableDoubleStateOf(partnerGym.lng) }
    var isEmergencyDisabled by remember(partnerGym) {
        mutableStateOf(partnerGym.status == GymStatus.TEMPORARILY_CLOSED)
    }

    val availableFacilities = remember {
        listOf(
            "24/7 Access",
            "Olympic Barbells",
            "Sauna",
            "Showers",
            "Locker Rooms",
            "Turf Area",
            "Cold Plunge",
            "Coworking Lounge",
            "Towel Service",
            "Chalk Permitted",
            "Platform Lifting",
            "Free WiFi"
        )
    }
    var selectedFacilities by remember(partnerGym) {
        mutableStateOf(partnerGym.facilities.toSet())
    }

    var photosList by remember(partnerGym) {
        mutableStateOf(partnerGym.photos.toMutableList())
    }

    // Days operating schedule state
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    var scheduleState by remember(partnerGym) {
        mutableStateOf(
            daysOfWeek.associateWith { day ->
                val isOpen = if (day in listOf("Sat", "Sun")) true else true
                val hours = if (day in listOf("Sat", "Sun")) "08:00 - 20:00" else "06:00 - 23:00"
                Pair(isOpen, hours)
            }.toMutableMap()
        )
    }

    var showSaveSuccessBanner by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = NomadConcrete,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NomadMist)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = NomadInk
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Facility Profile",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadInk
                            )
                            Text(
                                text = "Public Listing & Operating Hours",
                                fontSize = 11.sp,
                                color = NomadSteel
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val formattedHours = scheduleState.entries
                                .filter { it.value.first }
                                .joinToString(", ") { "${it.key}: ${it.value.second}" }

                            NomadFitRepository.updateGymProfile(
                                gymId = partnerGym.id,
                                name = name,
                                description = description,
                                address = address,
                                lat = lat,
                                lng = lng,
                                facilities = selectedFacilities.toList(),
                                operatingHours = formattedHours,
                                photos = photosList
                            )
                            NomadFitRepository.setGymTemporarilyDisabled(
                                gymId = partnerGym.id,
                                disabled = isEmergencyDisabled
                            )
                            showSaveSuccessBanner = true
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text("Save Changes", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
            contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp)
        ) {
            // Save Success Banner
            if (showSaveSuccessBanner) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = NomadMoss
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Facility profile updated across the global network.",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                            IconButton(
                                onClick = { showSaveSuccessBanner = false },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Outlined.Close, contentDescription = "Dismiss", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            // 1. EMERGENCY KILL-SWITCH: "Temporarily disable access"
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = if (isEmergencyDisabled) NomadAmber.copy(alpha = 0.15f) else NomadMist,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isEmergencyDisabled) 2.dp else 1.dp,
                        color = if (isEmergencyDisabled) NomadAmber else NomadLine
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isEmergencyDisabled) Icons.Outlined.WarningAmber else Icons.Outlined.LockClock,
                                    contentDescription = null,
                                    tint = if (isEmergencyDisabled) NomadAmber else NomadSteel,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "TEMPORARILY DISABLE ACCESS",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isEmergencyDisabled) NomadAmber else NomadInk
                                    )
                                    Text(
                                        text = if (isEmergencyDisabled)
                                            "ACTIVE: Gym marked as closed (burst pipe/emergency). Members cannot check in."
                                        else
                                            "Enable immediately in emergencies (e.g. burst pipe, power outage).",
                                        fontSize = 11.sp,
                                        color = if (isEmergencyDisabled) NomadInk else NomadSteel
                                    )
                                }
                            }

                            Switch(
                                checked = isEmergencyDisabled,
                                onCheckedChange = {
                                    isEmergencyDisabled = it
                                    NomadFitRepository.setGymTemporarilyDisabled(partnerGym.id, it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = NomadAmber,
                                    uncheckedThumbColor = NomadFog,
                                    uncheckedTrackColor = NomadMist
                                )
                            )
                        }
                    }
                }
            }

            // 2. CORE DETAILS: Name & Description
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "GENERAL INFORMATION",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadSteel
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Gym / Facility Name", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NomadSteel,
                                unfocusedBorderColor = NomadLine,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Public Description", fontSize = 12.sp) },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NomadSteel,
                                unfocusedBorderColor = NomadLine,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }
                }
            }

            // 3. LOCATION & MAP PIN PICKER
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "LOCATION & MAP COORDINATES",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadSteel
                        )

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Street Address & Landmark", fontSize = 12.sp) },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NomadSteel,
                                unfocusedBorderColor = NomadLine,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        // Visual Map Pin Picker Simulator
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF1E2024),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Simulated grid
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val step = 20.dp.toPx()
                                    var x = 0f
                                    while (x < size.width) {
                                        drawLine(Color(0xFF2E3238), androidx.compose.ui.geometry.Offset(x, 0f), androidx.compose.ui.geometry.Offset(x, size.height), 1f)
                                        x += step
                                    }
                                    var y = 0f
                                    while (y < size.height) {
                                        drawLine(Color(0xFF2E3238), androidx.compose.ui.geometry.Offset(0f, y), androidx.compose.ui.geometry.Offset(size.width, y), 1f)
                                        y += step
                                    }
                                }

                                // Center Map Pin Marker
                                Column(
                                    modifier = Modifier.align(Alignment.Center),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocationOn,
                                        contentDescription = "Map Pin",
                                        tint = NomadSignal,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Text(
                                        text = "${String.format("%.4f", lat)}, ${String.format("%.4f", lng)}",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Color.White
                                    )
                                }

                                Text(
                                    text = "MAP PIN POSITION: ${partnerGym.city.uppercase()} HUB",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.sp,
                                    color = NomadFog,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 4. FACILITIES (Multi-select chips)
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "FACILITIES & AMENITIES",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadSteel
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableFacilities.forEach { facility ->
                                val isSelected = selectedFacilities.contains(facility)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedFacilities = if (isSelected) {
                                            selectedFacilities - facility
                                        } else {
                                            selectedFacilities + facility
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = facility,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NomadInk,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.White,
                                        labelColor = NomadSteel
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = if (isSelected) NomadInk else NomadLine,
                                        enabled = true,
                                        selected = isSelected
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 5. OPERATING HOURS (Per-day table with open/closed toggle)
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "OPERATING HOURS (PER-DAY SCHEDULE)",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadSteel
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        daysOfWeek.forEach { day ->
                            val current = scheduleState[day] ?: Pair(true, "06:00 - 23:00")
                            val isOpen = current.first
                            val hours = current.second

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = day,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOpen) NomadInk else NomadFog,
                                    modifier = Modifier.width(40.dp)
                                )

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isOpen) NomadMoss.copy(alpha = 0.12f) else NomadLine.copy(alpha = 0.5f),
                                    modifier = Modifier.clickable {
                                        scheduleState = scheduleState.toMutableMap().apply {
                                            put(day, Pair(!isOpen, hours))
                                        }
                                    }
                                ) {
                                    Text(
                                        text = if (isOpen) "OPEN" else "CLOSED",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isOpen) NomadMoss else NomadSteel,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                if (isOpen) {
                                    Text(
                                        text = hours,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        color = NomadInk
                                    )
                                } else {
                                    Text(
                                        text = "Facility Closed",
                                        fontSize = 11.sp,
                                        color = NomadFog
                                    )
                                }
                            }
                            HorizontalDivider(color = NomadLine.copy(alpha = 0.5f), thickness = 0.5.dp)
                        }
                    }
                }
            }

            // 6. PHOTO GALLERY & UPLOAD GRID
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "FACILITY PHOTOS (${photosList.size})",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel
                            )
                            Text(
                                text = "+ ADD PHOTO",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSignal,
                                modifier = Modifier.clickable {
                                    val samplePhotos = listOf(
                                        "https://images.unsplash.com/photo-1540497077202-7c8a3999166f?w=800",
                                        "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=800",
                                        "https://images.unsplash.com/photo-1574680096145-d05b474e2155?w=800"
                                    )
                                    val nextPhoto = samplePhotos.firstOrNull { it !in photosList } ?: samplePhotos.random()
                                    photosList = (photosList + nextPhoto).toMutableList()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(photosList) { photoUrl ->
                                Box(
                                    modifier = Modifier
                                        .size(110.dp, 80.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                ) {
                                    AsyncImage(
                                        model = photoUrl,
                                        contentDescription = "Gym Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
