package com.example.ui.admin

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
import java.util.UUID

@Composable
fun AdminPlansTab(
    plans: List<MembershipPlan>
) {
    var editingPlan by remember { mutableStateOf<MembershipPlan?>(null) }
    var isCreatingNew by remember { mutableStateOf(false) }

    // Form Fields
    var formName by remember { mutableStateOf("") }
    var formDescription by remember { mutableStateOf("") }
    var formPrice by remember { mutableStateOf("") }
    var formAnnualPrice by remember { mutableStateOf("") }
    var formIsUnlimited by remember { mutableStateOf(false) }
    var formVisitAllowance by remember { mutableStateOf("10") }
    var formIncludeStandard by remember { mutableStateOf(true) }
    var formIncludePremium by remember { mutableStateOf(false) }
    var formIsActive by remember { mutableStateOf(true) }

    // Price change grandfathering confirmation dialog
    var pendingPriceChangeOldPlan by remember { mutableStateOf<MembershipPlan?>(null) }
    var pendingPriceChangeNewPlan by remember { mutableStateOf<MembershipPlan?>(null) }

    fun openEditForm(plan: MembershipPlan) {
        editingPlan = plan
        isCreatingNew = false
        formName = plan.name
        formDescription = plan.description
        formPrice = plan.price.toInt().toString()
        formAnnualPrice = (plan.price * 10).toInt().toString()
        formIsUnlimited = plan.isUnlimited
        formVisitAllowance = if (plan.isUnlimited) "0" else plan.visitAllowance.toString()
        formIncludeStandard = plan.eligibleGymTiers.contains("standard")
        formIncludePremium = plan.eligibleGymTiers.contains("premium")
        formIsActive = plan.isActive
    }

    fun openCreateForm() {
        editingPlan = null
        isCreatingNew = true
        formName = ""
        formDescription = ""
        formPrice = "89"
        formAnnualPrice = "890"
        formIsUnlimited = false
        formVisitAllowance = "12"
        formIncludeStandard = true
        formIncludePremium = false
        formIsActive = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Header & Create Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MEMBERSHIP PLAN PRODUCTS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )
                Text(
                    text = "Global tier allocations, allowances & pricing structures",
                    fontSize = 11.sp,
                    color = NomadSteel
                )
            }

            Button(
                onClick = { openCreateForm() },
                colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                shape = RoundedCornerShape(3.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create Plan", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Plans List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(plans, key = { it.id }) { plan ->
                PlanCard(
                    plan = plan,
                    onEdit = { openEditForm(plan) },
                    onToggleActive = {
                        NomadFitRepository.togglePlanActive(plan.id)
                    }
                )
            }
        }
    }

    // Edit / Create Dialog Form
    if (isCreatingNew || editingPlan != null) {
        AlertDialog(
            onDismissRequest = {
                isCreatingNew = false
                editingPlan = null
            },
            title = {
                Text(
                    text = if (isCreatingNew) "Create Membership Plan" else "Edit Plan: ${editingPlan?.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = NomadInk
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = formName,
                            onValueChange = { formName = it },
                            label = { Text("Plan Name", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = formDescription,
                            onValueChange = { formDescription = it },
                            label = { Text("Description", fontSize = 11.sp) },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = formPrice,
                                onValueChange = { formPrice = it },
                                label = { Text("Monthly Price ($)", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = formAnnualPrice,
                                onValueChange = { formAnnualPrice = it },
                                label = { Text("Annual Price ($)", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Unlimited Check-Ins", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = NomadInk)
                            Switch(
                                checked = formIsUnlimited,
                                onCheckedChange = { formIsUnlimited = it }
                            )
                        }
                    }

                    if (!formIsUnlimited) {
                        item {
                            OutlinedTextField(
                                value = formVisitAllowance,
                                onValueChange = { formVisitAllowance = it },
                                label = { Text("Visits per Month", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    item {
                        Text("Eligible Gym Tiers:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NomadSteel)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = formIncludeStandard, onCheckedChange = { formIncludeStandard = it })
                                Text("Standard", fontSize = 12.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = formIncludePremium, onCheckedChange = { formIncludePremium = it })
                                Text("Premium", fontSize = 12.sp)
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Active for New Subscribers", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = NomadInk)
                            Switch(
                                checked = formIsActive,
                                onCheckedChange = { formIsActive = it }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val tiers = mutableListOf<String>()
                        if (formIncludeStandard) tiers.add("standard")
                        if (formIncludePremium) tiers.add("premium")
                        if (tiers.isEmpty()) tiers.add("standard")

                        val parsedPrice = formPrice.toDoubleOrNull() ?: 99.0
                        val allowance = if (formIsUnlimited) -1 else (formVisitAllowance.toIntOrNull() ?: 10)

                        val newPlanObj = MembershipPlan(
                            id = editingPlan?.id ?: "plan_${UUID.randomUUID().toString().take(8)}",
                            name = formName.ifEmpty { "Nomad Tier Pass" },
                            description = formDescription.ifEmpty { "Network pass." },
                            price = parsedPrice,
                            currency = "USD",
                            billingCycle = "monthly",
                            visitAllowance = allowance,
                            eligibleGymTiers = tiers,
                            citiesIncluded = listOf("Tokyo", "London", "New York", "Berlin", "Singapore"),
                            isActive = formIsActive
                        )

                        // Check if price changed on an active plan
                        if (editingPlan != null && editingPlan!!.isActive && editingPlan!!.price != parsedPrice) {
                            pendingPriceChangeOldPlan = editingPlan
                            pendingPriceChangeNewPlan = newPlanObj
                            isCreatingNew = false
                            editingPlan = null
                        } else {
                            NomadFitRepository.saveMembershipPlan(newPlanObj)
                            isCreatingNew = false
                            editingPlan = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text("Save Plan", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    isCreatingNew = false
                    editingPlan = null
                }) {
                    Text("Cancel", fontSize = 11.sp, color = NomadSteel)
                }
            }
        )
    }

    // Price Change Confirmation Modal (Grandfathering notice)
    if (pendingPriceChangeOldPlan != null && pendingPriceChangeNewPlan != null) {
        val oldP = pendingPriceChangeOldPlan!!
        val newP = pendingPriceChangeNewPlan!!

        AlertDialog(
            onDismissRequest = {
                pendingPriceChangeOldPlan = null
                pendingPriceChangeNewPlan = null
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.WarningAmber, contentDescription = null, tint = NomadSignal, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Confirm Active Plan Price Change", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Changing this active plan's price from $${oldP.price.toInt()} to $${newP.price.toInt()} will apply to new signups immediately. Existing subscribers will continue at their current grandfathered rate until their next renewal cycle.",
                        fontSize = 12.sp,
                        color = NomadInk
                    )
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = NomadConcrete,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("• New Signups: $${newP.price.toInt()}/mo", fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NomadSignal)
                            Text("• Existing Members: Grandfathered at $${oldP.price.toInt()}/mo", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = NomadSteel)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        NomadFitRepository.saveMembershipPlan(
                            plan = newP,
                            priceChangeGrandfatheredNote = "Price updated from $${oldP.price.toInt()} to $${newP.price.toInt()}. Existing subscribers grandfathered."
                        )
                        pendingPriceChangeOldPlan = null
                        pendingPriceChangeNewPlan = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text("Confirm & Apply Price Change", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    pendingPriceChangeOldPlan = null
                    pendingPriceChangeNewPlan = null
                }) {
                    Text("Cancel", fontSize = 11.sp, color = NomadSteel)
                }
            }
        )
    }
}

@Composable
private fun PlanCard(
    plan: MembershipPlan,
    onEdit: () -> Unit,
    onToggleActive: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = plan.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = if (plan.isActive) NomadMoss.copy(alpha = 0.12f) else NomadSteel.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = if (plan.isActive) "ACTIVE" else "INACTIVE",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (plan.isActive) NomadMoss else NomadSteel,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "$${plan.price.toInt()}/mo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${if (plan.isUnlimited) "Unlimited Passes" else "${plan.visitAllowance} passes/month"} • Annual: $${(plan.price * 10).toInt()}/yr • Tiers: ${plan.eligibleGymTiers.joinToString()}",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = NomadSteel
            )

            Text(
                text = plan.description,
                fontSize = 11.sp,
                color = NomadSteel
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Active Toggle:", fontSize = 11.sp, color = NomadSteel)
                    Switch(
                        checked = plan.isActive,
                        onCheckedChange = { onToggleActive() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NomadSignal,
                            uncheckedThumbColor = NomadFog,
                            uncheckedTrackColor = NomadConcrete
                        )
                    )
                }

                Button(
                    onClick = onEdit,
                    shape = RoundedCornerShape(3.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Edit Plan", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
