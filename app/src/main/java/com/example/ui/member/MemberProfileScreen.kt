package com.example.ui.member

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NomadFitRepository
import com.example.model.NotificationPreferences
import com.example.model.User
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberProfileScreen(
    user: User,
    notificationPrefs: NotificationPreferences,
    onNavigateToSupport: () -> Unit,
    onReplayOnboarding: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf(user.fullName) }
    var email by remember { mutableStateOf(user.email) }
    var phone by remember { mutableStateOf(user.phone) }
    var homeCity by remember { mutableStateOf(user.homeCity) }
    var cityDropdownExpanded by remember { mutableStateOf(false) }

    var saveFeedback by remember { mutableStateOf<String?>(null) }

    var membershipUpdates by remember { mutableStateOf(notificationPrefs.membershipUpdates) }
    var checkInConfirmations by remember { mutableStateOf(notificationPrefs.checkInConfirmations) }
    var paymentReceipts by remember { mutableStateOf(notificationPrefs.paymentReceipts) }

    val cities = listOf("Tokyo", "London", "New York", "Berlin", "Barcelona", "Singapore", "Paris", "Sydney")

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
                    text = "Profile & Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )
                Text(
                    text = "Personal details, preferences and account controls",
                    fontSize = 12.sp,
                    color = NomadSteel
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card Header with Avatar
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = NomadMist,
                border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = NomadInk,
                        border = androidx.compose.foundation.BorderStroke(2.dp, NomadSignal),
                        modifier = Modifier.size(58.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = user.fullName.take(2).uppercase(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = user.fullName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadInk
                        )
                        Text(
                            text = user.email,
                            fontSize = 12.sp,
                            color = NomadSteel
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = NomadConcrete,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "MEMBER ID: NF-8839",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadFog,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Editable Personal Details Form
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = NomadMist,
                border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "ACCOUNT DETAILS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadSteel
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("FULL NAME", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = NomadSteel)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NomadSignal,
                            unfocusedBorderColor = NomadLine,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("EMAIL ADDRESS", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = NomadSteel)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NomadSignal,
                            unfocusedBorderColor = NomadLine,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("PHONE NUMBER", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = NomadSteel)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NomadSignal,
                            unfocusedBorderColor = NomadLine,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("HOME HUB CITY", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = NomadSteel)
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = cityDropdownExpanded,
                        onExpandedChange = { cityDropdownExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = homeCity,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityDropdownExpanded) },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NomadSignal,
                                unfocusedBorderColor = NomadLine,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = cityDropdownExpanded,
                            onDismissRequest = { cityDropdownExpanded = false }
                        ) {
                            cities.forEach { city ->
                                DropdownMenuItem(
                                    text = { Text(city, fontSize = 13.sp) },
                                    onClick = {
                                        homeCity = city
                                        cityDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            NomadFitRepository.updateProfile(
                                fullName = fullName,
                                email = email,
                                phone = phone,
                                homeCity = homeCity
                            )
                            saveFeedback = "Profile updated successfully."
                        },
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Text("Save Changes", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }

                    if (saveFeedback != null) {
                        Text(
                            text = saveFeedback.orEmpty(),
                            color = NomadMoss,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Notification Preferences
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = NomadMist,
                border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "NOTIFICATIONS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadSteel
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    PreferenceToggleRow(
                        title = "Membership & Renewal Notices",
                        subtitle = "Receive billing receipts and upcoming renewal alerts",
                        checked = membershipUpdates,
                        onCheckedChange = {
                            membershipUpdates = it
                            NomadFitRepository.updateNotificationPreferences(
                                NotificationPreferences(membershipUpdates = it, checkInConfirmations = checkInConfirmations, paymentReceipts = paymentReceipts)
                            )
                        }
                    )

                    HorizontalDivider(color = NomadLine, modifier = Modifier.padding(vertical = 8.dp))

                    PreferenceToggleRow(
                        title = "Check-in Confirmations",
                        subtitle = "Instant push notification upon entrance verification",
                        checked = checkInConfirmations,
                        onCheckedChange = {
                            checkInConfirmations = it
                            NomadFitRepository.updateNotificationPreferences(
                                NotificationPreferences(membershipUpdates = membershipUpdates, checkInConfirmations = it, paymentReceipts = paymentReceipts)
                            )
                        }
                    )

                    HorizontalDivider(color = NomadLine, modifier = Modifier.padding(vertical = 8.dp))

                    PreferenceToggleRow(
                        title = "Payment Receipts",
                        subtitle = "Email copies of subscription and pass transactions",
                        checked = paymentReceipts,
                        onCheckedChange = {
                            paymentReceipts = it
                            NomadFitRepository.updateNotificationPreferences(
                                NotificationPreferences(membershipUpdates = membershipUpdates, checkInConfirmations = checkInConfirmations, paymentReceipts = it)
                            )
                        }
                    )
                }
            }

            // Quick App Actions
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = NomadMist,
                border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "SUPPORT & RESOURCES",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadSteel
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToSupport() }
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.SupportAgent, contentDescription = null, tint = NomadInk, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Open Support Desk Ticket", fontSize = 13.sp, color = NomadInk, fontWeight = FontWeight.Medium)
                        }
                        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = NomadFog, modifier = Modifier.size(16.dp))
                    }

                    HorizontalDivider(color = NomadLine)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onReplayOnboarding() }
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.HelpOutline, contentDescription = null, tint = NomadInk, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("View Onboarding Guide", fontSize = 13.sp, color = NomadInk, fontWeight = FontWeight.Medium)
                        }
                        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = NomadFog, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Logout Link as Plain Text
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Log out of Fit loop",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NomadBrick,
                    modifier = Modifier
                        .clickable { onLogout() }
                        .padding(8.dp)
                )
                Text(
                    text = "Version 1.2.0 • Build 2026.08",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = NomadFog
                )
            }
        }
    }
}

@Composable
private fun PreferenceToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NomadInk)
            Text(text = subtitle, fontSize = 11.sp, color = NomadSteel, modifier = Modifier.padding(top = 2.dp))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = NomadSignal,
                uncheckedThumbColor = NomadFog,
                uncheckedTrackColor = NomadConcrete
            )
        )
    }
}
