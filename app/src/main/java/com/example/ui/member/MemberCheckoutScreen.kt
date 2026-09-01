package com.example.ui.member

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NomadFitRepository
import com.example.model.MembershipPlan
import com.example.model.User
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class CheckoutState {
    INPUT,
    PROCESSING,
    SUCCESS,
    DECLINED
}

@Composable
fun MemberCheckoutScreen(
    plan: MembershipPlan,
    isAnnual: Boolean,
    user: User,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var checkoutState by remember { mutableStateOf(CheckoutState.INPUT) }

    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvc by remember { mutableStateOf("") }
    var billingName by remember { mutableStateOf(user.fullName) }

    var cardNumberError by remember { mutableStateOf<String?>(null) }
    var expiryError by remember { mutableStateOf<String?>(null) }
    var cvcError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }

    val monthlyPrice = plan.price
    val calculatedPrice = if (isAnnual) monthlyPrice * 0.8 else monthlyPrice
    val totalAmountDue = if (isAnnual) calculatedPrice * 12 else calculatedPrice

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
                if (checkoutState == CheckoutState.INPUT) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = NomadInk
                        )
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Checkout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (checkoutState) {
                CheckoutState.INPUT -> {
                    // Order Summary Card
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = NomadMist,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "ORDER SUMMARY",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = plan.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NomadInk
                                    )
                                    Text(
                                        text = if (isAnnual) "Billed annually (20% discount)" else "Billed monthly",
                                        fontSize = 12.sp,
                                        color = NomadSteel
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "$${String.format("%.2f", totalAmountDue)}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NomadInk
                                    )
                                    Text(
                                        text = if (isAnnual) "/ year" else "/ month",
                                        fontSize = 11.sp,
                                        color = NomadSteel
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Card Entry Form
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = NomadMist,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PAYMENT METHOD",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NomadSteel
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Lock,
                                        contentDescription = null,
                                        tint = NomadMoss,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "256-BIT ENCRYPTED",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NomadMoss
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Card Number with auto grouping
                            Text(
                                text = "CARD NUMBER",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = { input ->
                                    val digits = input.filter { it.isDigit() }.take(16)
                                    cardNumber = digits
                                    cardNumberError = null
                                },
                                visualTransformation = CardNumberVisualTransformation(),
                                placeholder = { Text("4242 •••• •••• 4242") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                isError = cardNumberError != null,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NomadSignal,
                                    unfocusedBorderColor = NomadLine,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )
                            if (cardNumberError != null) {
                                Text(cardNumberError.orEmpty(), color = NomadBrick, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Expiry MM/YY
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "EXPIRY (MM/YY)",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NomadSteel
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = expiry,
                                        onValueChange = { input ->
                                            val digits = input.filter { it.isDigit() }.take(4)
                                            expiry = digits
                                            expiryError = null
                                        },
                                        visualTransformation = ExpiryDateVisualTransformation(),
                                        placeholder = { Text("12/28") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        isError = expiryError != null,
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = NomadSignal,
                                            unfocusedBorderColor = NomadLine,
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White
                                        )
                                    )
                                    if (expiryError != null) {
                                        Text(expiryError.orEmpty(), color = NomadBrick, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                                    }
                                }

                                // CVC
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "CVC",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NomadSteel
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = cvc,
                                        onValueChange = { input ->
                                            val digits = input.filter { it.isDigit() }.take(4)
                                            cvc = digits
                                            cvcError = null
                                        },
                                        placeholder = { Text("384") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        isError = cvcError != null,
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = NomadSignal,
                                            unfocusedBorderColor = NomadLine,
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White
                                        )
                                    )
                                    if (cvcError != null) {
                                        Text(cvcError.orEmpty(), color = NomadBrick, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Name on card
                            Text(
                                text = "NAME ON CARD",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = billingName,
                                onValueChange = {
                                    billingName = it
                                    nameError = null
                                },
                                placeholder = { Text("Alex Vance") },
                                singleLine = true,
                                isError = nameError != null,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NomadSignal,
                                    unfocusedBorderColor = NomadLine,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )
                            if (nameError != null) {
                                Text(nameError.orEmpty(), color = NomadBrick, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Subscribe Action Button
                    Button(
                        onClick = {
                            var hasErr = false
                            if (cardNumber.length < 13) {
                                cardNumberError = "Please enter a valid 16-digit card number."
                                hasErr = true
                            }
                            if (expiry.length < 4) {
                                expiryError = "Valid MM/YY required."
                                hasErr = true
                            }
                            if (cvc.length < 3) {
                                cvcError = "Valid CVC required."
                                hasErr = true
                            }
                            if (billingName.isBlank()) {
                                nameError = "Cardholder name is required."
                                hasErr = true
                            }
                            if (hasErr) return@Button

                            checkoutState = CheckoutState.PROCESSING

                            coroutineScope.launch {
                                delay(1200) // Realistic processing simulation
                                if (cardNumber.startsWith("4000")) {
                                    checkoutState = CheckoutState.DECLINED
                                } else {
                                    NomadFitRepository.processCardPayment(
                                        cardNumber = cardNumber,
                                        plan = plan,
                                        userId = user.uid
                                    )
                                    checkoutState = CheckoutState.SUCCESS
                                }
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(
                            text = "Subscribe • $${String.format("%.2f", totalAmountDue)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "By subscribing, you agree to automatic recurring billing. Cancel anytime from your account settings.",
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = NomadFog,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                CheckoutState.PROCESSING -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = NomadSignal,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Confirming your payment…",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadInk
                        )
                        Text(
                            text = "Securing credential pass and provisioning gym access.",
                            fontSize = 12.sp,
                            color = NomadSteel,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                CheckoutState.SUCCESS -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = NomadMoss.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(2.dp, NomadMoss),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = NomadMoss,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Membership Activated!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadInk
                        )
                        Text(
                            text = "Your ${plan.name} pass is now ready to use at all partner locations.",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = NomadSteel,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Physical Credential Card Reveal Preview
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp),
                            shape = RoundedCornerShape(24.dp),
                            color = NomadInk,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383C45))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column {
                                        Text(
                                            text = "FIT LOOP PASS",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NomadSignal
                                        )
                                        Text(
                                            text = plan.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Outlined.Nfc,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Column {
                                        Text(
                                            text = user.fullName.uppercase(),
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "ID: NF-MBR-8839",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            color = NomadFog
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = NomadMoss
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onComplete,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "View My Membership",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                CheckoutState.DECLINED -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = NomadBrick.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(2.dp, NomadBrick),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.ErrorOutline,
                                    contentDescription = null,
                                    tint = NomadBrick,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Payment Declined",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadInk
                        )
                        Text(
                            text = "Your card was declined by the issuing bank. Please double check the card details or try a different card.",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = NomadSteel,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { checkoutState = CheckoutState.INPUT },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "Try a different card",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

private class CardNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i % 4 == 3 && i != 15) {
                out += " "
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                if (offset <= 4) return offset
                if (offset <= 8) return offset + 1
                if (offset <= 12) return offset + 2
                if (offset <= 16) return offset + 3
                return 19
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                if (offset <= 4) return offset
                if (offset <= 9) return offset - 1
                if (offset <= 14) return offset - 2
                if (offset <= 19) return offset - 3
                return 16
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

private class ExpiryDateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 4) text.text.substring(0..3) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1 && trimmed.length > 2) {
                out += "/"
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 4) return offset + 1
                return 5
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                return 4
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}
