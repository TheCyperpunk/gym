package com.example.ui.partner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.ui.theme.*

@Composable
fun PartnerAccessRulesScreen(
    user: User,
    gyms: List<Gym>,
    plans: List<MembershipPlan>,
    accessRules: List<AccessRules>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val partnerGym = remember(gyms, user.uid) {
        gyms.find { it.ownerId == user.uid } ?: gyms.first()
    }
    val existingRule = remember(accessRules, partnerGym.id) {
        accessRules.find { it.gymId == partnerGym.id } ?: AccessRules(
            id = "rule_default",
            gymId = partnerGym.id,
            eligiblePlanIds = listOf("plan_global_unlimited", "plan_city_flex"),
            maxVisitsPerDay = 1,
            maxVisitsPerMonthPerMember = 12,
            bookingRequired = false
        )
    }

    var selectedPlanIds by remember(existingRule) {
        mutableStateOf(existingRule.eligiblePlanIds.toSet())
    }
    var maxVisitsPerDay by remember(existingRule) {
        mutableIntStateOf(existingRule.maxVisitsPerDay)
    }
    var maxVisitsPerMonth by remember(existingRule) {
        mutableIntStateOf(existingRule.maxVisitsPerMonthPerMember)
    }
    var bookingRequired by remember(existingRule) {
        mutableStateOf(existingRule.bookingRequired)
    }

    var showSaveBanner by remember { mutableStateOf(false) }

    // Dynamic Plain-language summary sentence:
    // e.g. "Members on Starter and Premium plans can visit up to 2 times per month, no booking required."
    val plainSummarySentence = remember(selectedPlanIds, maxVisitsPerDay, maxVisitsPerMonth, bookingRequired, plans) {
        val planNames = plans.filter { it.id in selectedPlanIds }.map { it.name }
        val plansText = when {
            planNames.isEmpty() -> "No membership plans"
            planNames.size == 1 -> planNames.first()
            planNames.size == 2 -> "${planNames[0]} and ${planNames[1]}"
            else -> "${planNames.dropLast(1).joinToString(", ")}, and ${planNames.last()}"
        }
        val bookingText = if (bookingRequired) "advance booking required." else "no booking required."
        "Members on $plansText plans can visit up to $maxVisitsPerDay time${if (maxVisitsPerDay > 1) "s" else ""} per day and $maxVisitsPerMonth times per month, $bookingText"
    }

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
                                text = "Access Rules & Policies",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadInk
                            )
                            Text(
                                text = "${partnerGym.name} • Admission Limits",
                                fontSize = 11.sp,
                                color = NomadSteel
                            )
                        }
                    }

                    Button(
                        onClick = {
                            NomadFitRepository.updateAccessRules(
                                gymId = partnerGym.id,
                                eligiblePlanIds = selectedPlanIds.toList(),
                                maxVisitsPerDay = maxVisitsPerDay,
                                maxVisitsPerMonth = maxVisitsPerMonth,
                                bookingRequired = bookingRequired
                            )
                            showSaveBanner = true
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text("Save Rules", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
            if (showSaveBanner) {
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
                                    text = "Access policies updated. Rule engine synced.",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                            IconButton(
                                onClick = { showSaveBanner = false },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Outlined.Close, contentDescription = "Dismiss", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            // 1. ACCEPTED MEMBERSHIP PLANS CHECKLIST
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ACCEPTED MEMBERSHIP PLANS",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadSteel
                        )
                        Text(
                            text = "Check the plans your facility admits for credential validation:",
                            fontSize = 11.sp,
                            color = NomadSteel,
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                        )

                        plans.forEach { plan ->
                            val isChecked = selectedPlanIds.contains(plan.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedPlanIds = if (isChecked) {
                                            selectedPlanIds - plan.id
                                        } else {
                                            selectedPlanIds + plan.id
                                        }
                                    }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = {
                                        selectedPlanIds = if (it) {
                                            selectedPlanIds + plan.id
                                        } else {
                                            selectedPlanIds - plan.id
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = NomadInk,
                                        uncheckedColor = NomadLine
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = plan.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NomadInk
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = NomadLine.copy(alpha = 0.5f)
                                        ) {
                                            Text(
                                                text = "$${plan.price.toInt()}/${plan.billingCycle.take(2)}",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NomadSteel,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = plan.description,
                                        fontSize = 11.sp,
                                        color = NomadSteel
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. VISIT LIMITS (PER DAY / PER MONTH)
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "VISIT FREQUENCY LIMITS",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadSteel
                        )

                        // Max Visits Per Day
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Max visits per day",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NomadInk
                                )
                                Text(
                                    text = "Limit repeat check-ins within 24 hours",
                                    fontSize = 11.sp,
                                    color = NomadSteel
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FilledIconButton(
                                    onClick = { if (maxVisitsPerDay > 1) maxVisitsPerDay-- },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = NomadConcrete),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Outlined.Remove, contentDescription = "Decrease", tint = NomadInk, modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "$maxVisitsPerDay",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NomadInk,
                                    modifier = Modifier.padding(horizontal = 14.dp)
                                )
                                FilledIconButton(
                                    onClick = { if (maxVisitsPerDay < 5) maxVisitsPerDay++ },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = NomadConcrete),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Outlined.Add, contentDescription = "Increase", tint = NomadInk, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        HorizontalDivider(color = NomadLine.copy(alpha = 0.5f), thickness = 0.5.dp)

                        // Max Visits Per Month
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Max visits per month",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NomadInk
                                )
                                Text(
                                    text = "Per individual member monthly cycle",
                                    fontSize = 11.sp,
                                    color = NomadSteel
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FilledIconButton(
                                    onClick = { if (maxVisitsPerMonth > 1) maxVisitsPerMonth-- },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = NomadConcrete),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Outlined.Remove, contentDescription = "Decrease", tint = NomadInk, modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "$maxVisitsPerMonth",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NomadInk,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                                FilledIconButton(
                                    onClick = { if (maxVisitsPerMonth < 31) maxVisitsPerMonth++ },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = NomadConcrete),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Outlined.Add, contentDescription = "Increase", tint = NomadInk, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            // 3. BOOKING REQUIREMENT TOGGLE
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "BOOKING POLICY",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NomadSteel
                                )
                                Text(
                                    text = if (bookingRequired) "Advance booking required for access" else "Walk-in & check-in only (no advance reservation needed)",
                                    fontSize = 12.sp,
                                    color = NomadInk,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Switch(
                                checked = bookingRequired,
                                onCheckedChange = { bookingRequired = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = NomadInk,
                                    uncheckedThumbColor = NomadFog,
                                    uncheckedTrackColor = NomadMist
                                )
                            )
                        }
                    }
                }
            }

            // 4. PLAIN-LANGUAGE SUMMARY SENTENCE (Mandatory requirement)
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = FitLoopYellow.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FitLoopYellow)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "RULE SUMMARY SENTENCE",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadSteel
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "“$plainSummarySentence”",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadInk,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        }
    }
}
