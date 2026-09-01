package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MembershipPlan
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardPaymentSheet(
    plan: MembershipPlan,
    onPaymentSubmit: suspend (String) -> Result<Unit>,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var cardNumber by remember { mutableStateOf("4242424242424242") }
    var expiry by remember { mutableStateOf("12/28") }
    var cvc by remember { mutableStateOf("888") }
    var zipCode by remember { mutableStateOf("10001") }

    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        containerColor = NomadMist,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Subscribe to ${plan.name}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk
                    )
                    Text(
                        text = "$${String.format("%.2f", plan.price)} / ${plan.billingCycle}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NomadSteel
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = NomadConcrete,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Text(
                        text = "TEST MODE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadFog,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isSuccess) {
                // Success State View
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    color = NomadMoss.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadMoss)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Success",
                            tint = NomadMoss,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Subscription Activated",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadMoss
                            )
                            Text(
                                text = "Your physical access pass is live and ready for gym check-ins.",
                                fontSize = 12.sp,
                                color = NomadInk
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NomadSignal)
                ) {
                    Text("View Access Pass", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            } else {
                // Card Input Form
                if (errorMessage != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        color = NomadBrick.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadBrick)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ErrorOutline,
                                contentDescription = "Error",
                                tint = NomadBrick,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Payment Failed",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NomadBrick
                                )
                                Text(
                                    text = errorMessage.orEmpty(),
                                    fontSize = 12.sp,
                                    color = NomadInk
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Card Number Field
                Text(
                    text = "CARD NUMBER",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = NomadSteel
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { if (it.length <= 19) cardNumber = it },
                    placeholder = { Text("4242 4242 4242 4242", color = NomadFog) },
                    visualTransformation = CardNumberVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NomadSignal,
                        unfocusedBorderColor = NomadLine,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    leadingIcon = {
                        Icon(Icons.Outlined.CreditCard, contentDescription = "Card", tint = NomadSteel)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Expiration & CVC Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "EXPIRY (MM/YY)",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = NomadSteel
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = expiry,
                            onValueChange = { if (it.length <= 5) expiry = it },
                            placeholder = { Text("MM/YY", color = NomadFog) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(6.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NomadSignal,
                                unfocusedBorderColor = NomadLine,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CVC",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = NomadSteel
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = cvc,
                            onValueChange = { if (it.length <= 4) cvc = it },
                            placeholder = { Text("123", color = NomadFog) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(6.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NomadSignal,
                                unfocusedBorderColor = NomadLine,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ZIP",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = NomadSteel
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = zipCode,
                            onValueChange = { if (it.length <= 8) zipCode = it },
                            placeholder = { Text("10001", color = NomadFog) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(6.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NomadSignal,
                                unfocusedBorderColor = NomadLine,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Test Mode Hint
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    color = NomadConcrete
                ) {
                    Text(
                        text = "Test Rule: Cards starting with 4000 test bank decline. Any other card number simulates success.",
                        fontSize = 11.sp,
                        color = NomadSteel,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Pay Button
                Button(
                    onClick = {
                        if (cardNumber.isBlank()) {
                            errorMessage = "Please enter a valid card number."
                            return@Button
                        }
                        isProcessing = true
                        errorMessage = null
                        coroutineScope.launch {
                            val result = onPaymentSubmit(cardNumber)
                            isProcessing = false
                            if (result.isSuccess) {
                                isSuccess = true
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Transaction failed."
                            }
                        }
                    },
                    enabled = !isProcessing,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NomadSignal,
                        disabledContainerColor = NomadFog
                    )
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Authorizing...", color = Color.White, fontSize = 14.sp)
                    } else {
                        Text(
                            text = "Pay $${String.format("%.2f", plan.price)} and activate pass",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// Visual Transformation for formatting 16 digits into 4-digit chunks
class CardNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if ((i + 1) % 4 == 0 && i + 1 != trimmed.length) {
                out += " "
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val spaces = (offset - 1) / 4
                return (offset + spaces).coerceAtMost(out.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val spaces = offset / 5
                return (offset - spaces).coerceIn(0, text.length)
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}
