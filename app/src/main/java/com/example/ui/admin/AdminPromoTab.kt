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
import com.example.model.PromoCode
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminPromoTab(
    promoCodes: List<PromoCode>
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    // Form fields
    var formCode by remember { mutableStateOf("") }
    var formDiscountType by remember { mutableStateOf("percent") } // "percent" | "fixed"
    var formValue by remember { mutableStateOf("20") }
    var formMaxUsage by remember { mutableStateOf("100") }
    var formExpiryDays by remember { mutableStateOf("60") }
    var formIsActive by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Header & Launch Promo Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PROMOTIONAL VOUCHERS & DISCOUNTS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )
                Text(
                    text = "Referral campaigns, seasonal vouchers & corporate discounts",
                    fontSize = 11.sp,
                    color = NomadSteel
                )
            }

            Button(
                onClick = {
                    formCode = ""
                    formDiscountType = "percent"
                    formValue = "20"
                    formMaxUsage = "100"
                    formExpiryDays = "60"
                    formIsActive = true
                    showCreateDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                shape = RoundedCornerShape(3.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Code", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Table Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
            color = Color(0xFF282A2F)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "PROMO CODE", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1.5f))
                Text(text = "DISCOUNT", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1f))
                Text(text = "USAGE / CAP", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1.2f))
                Text(text = "EXPIRY", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(1f))
                Text(text = "ACTIVE", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NomadFog, modifier = Modifier.weight(0.8f))
            }
        }

        // Table Rows
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(promoCodes, key = { it.id }) { promo ->
                PromoTableRow(
                    promo = promo,
                    onToggleActive = {
                        NomadFitRepository.togglePromoCode(promo.id)
                    }
                )
            }
        }
    }

    // Create Modal Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(
                    text = "Create Promo Code",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = NomadInk
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = formCode,
                        onValueChange = { formCode = it.uppercase() },
                        label = { Text("Coupon Code (e.g. TOKYO2026)", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            RadioButton(
                                selected = formDiscountType == "percent",
                                onClick = { formDiscountType = "percent" }
                            )
                            Text("Percentage (%)", fontSize = 11.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            RadioButton(
                                selected = formDiscountType == "fixed",
                                onClick = { formDiscountType = "fixed" }
                            )
                            Text("Fixed USD ($)", fontSize = 11.sp)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = formValue,
                            onValueChange = { formValue = it },
                            label = { Text(if (formDiscountType == "percent") "Discount (%)" else "Discount ($)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = formMaxUsage,
                            onValueChange = { formMaxUsage = it },
                            label = { Text("Max Redemptions", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = formExpiryDays,
                        onValueChange = { formExpiryDays = it },
                        label = { Text("Valid For (Days)", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Active Immediately", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = NomadInk)
                        Switch(
                            checked = formIsActive,
                            onCheckedChange = { formIsActive = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedVal = formValue.toDoubleOrNull() ?: 20.0
                        val parsedMax = formMaxUsage.toIntOrNull()
                        val days = formExpiryDays.toLongOrNull() ?: 60L

                        val newPromo = PromoCode(
                            id = "promo_${UUID.randomUUID().toString().take(8)}",
                            code = formCode.ifEmpty { "NOMAD${(100..999).random()}" },
                            discountType = formDiscountType,
                            value = parsedVal,
                            usageCount = 0,
                            maxUsage = parsedMax,
                            expiryTimestamp = System.currentTimeMillis() + 86400000L * days,
                            isActive = formIsActive
                        )
                        NomadFitRepository.savePromoCode(newPromo)
                        showCreateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text("Create Promo", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", fontSize = 11.sp, color = NomadSteel)
                }
            }
        )
    }
}

@Composable
private fun PromoTableRow(
    promo: PromoCode,
    onToggleActive: () -> Unit
) {
    val isExpired = System.currentTimeMillis() > promo.expiryTimestamp

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(3.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Column 1: Code
            Row(
                modifier = Modifier.weight(1.5f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(3.dp),
                    color = NomadInk
                ) {
                    Text(
                        text = promo.code,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadSignal,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            // Column 2: Discount
            Text(
                text = if (promo.discountType == "percent") "${promo.value.toInt()}% OFF" else "$${promo.value.toInt()} OFF",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NomadInk,
                modifier = Modifier.weight(1f)
            )

            // Column 3: Usage / Cap
            Text(
                text = "${promo.usageCount} / ${promo.maxUsage ?: "∞"}",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = NomadSteel,
                modifier = Modifier.weight(1.2f)
            )

            // Column 4: Expiry
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(promo.expiryTimestamp)),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = if (isExpired) NomadBrick else NomadSteel
                )
                if (isExpired) {
                    Text(text = "EXPIRED", fontFamily = FontFamily.Monospace, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = NomadBrick)
                }
            }

            // Column 5: Active Switch
            Box(
                modifier = Modifier.weight(0.8f),
                contentAlignment = Alignment.CenterEnd
            ) {
                Switch(
                    checked = promo.isActive && !isExpired,
                    onCheckedChange = { onToggleActive() },
                    enabled = !isExpired,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = NomadSignal,
                        uncheckedThumbColor = NomadFog,
                        uncheckedTrackColor = NomadConcrete
                    )
                )
            }
        }
    }
}
