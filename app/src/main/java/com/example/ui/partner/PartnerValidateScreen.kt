package com.example.ui.partner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NomadFitRepository
import com.example.model.*
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PartnerValidateScreen(
    user: User,
    gyms: List<Gym>,
    visits: List<Visit>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val partnerGym = remember(gyms, user.uid) {
        gyms.find { it.ownerId == user.uid } ?: gyms.first()
    }
    val gymVisits = remember(visits, partnerGym.id) {
        visits.filter { it.gymId == partnerGym.id }
    }
    val activeCredentialCode by NomadFitRepository.activeCredentialCode.collectAsState()

    var inputCode by remember { mutableStateOf("") }
    var isValidating by remember { mutableStateOf(false) }
    var validationBanner by remember { mutableStateOf<ValidationBannerState?>(null) }

    val todayStart = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    val todayVisits = remember(gymVisits, todayStart) {
        gymVisits.filter { it.checkInTimestamp >= todayStart }
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
                                text = "Front Desk Terminal",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadInk
                            )
                            Text(
                                text = "${partnerGym.name} • 6-Digit Code Scanner",
                                fontSize = 11.sp,
                                color = NomadSteel
                            )
                        }
                    }

                    // Auto-sync indicator
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NomadMoss.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadMoss.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(NomadMoss, RoundedCornerShape(3.dp))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "READY",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadMoss
                            )
                        }
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
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
        ) {
            // Validation Card Box
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ENTER 6-DIGIT PASS CODE",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomadSteel
                            )

                            // Paste test member code convenience
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = FitLoopYellow.copy(alpha = 0.25f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, FitLoopYellow),
                                modifier = Modifier.clickable {
                                    val cleaned = activeCredentialCode.replace("-", "").replace("NF", "")
                                    inputCode = cleaned.take(6)
                                }
                            ) {
                                Text(
                                    text = "Use Code ($activeCredentialCode)",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NomadInk,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large Numeric Code Display Box (Digit Slots)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                        ) {
                            for (i in 0 until 6) {
                                val digit = inputCode.getOrNull(i)?.toString() ?: ""
                                val isCurrent = inputCode.length == i
                                Surface(
                                    modifier = Modifier
                                        .size(46.dp, 58.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.White,
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isCurrent) 2.dp else 1.dp,
                                        color = if (isCurrent) NomadSignal else NomadLine
                                    )
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = digit,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NomadInk
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Big Touch Target Keypad (for Front-Desk Tablet usage)
                        KeypadGrid(
                            onDigitPress = { digit ->
                                if (inputCode.length < 6) {
                                    inputCode += digit
                                    validationBanner = null
                                }
                            },
                            onBackspace = {
                                if (inputCode.isNotEmpty()) {
                                    inputCode = inputCode.dropLast(1)
                                    validationBanner = null
                                }
                            },
                            onClear = {
                                inputCode = ""
                                validationBanner = null
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Button (Signal accent for check-in action)
                        Button(
                            onClick = {
                                if (inputCode.length == 6 && !isValidating) {
                                    isValidating = true
                                    validationBanner = null
                                    coroutineScope.launch {
                                        val (status, message) = NomadFitRepository.validateCheckIn(
                                            rawCode = inputCode,
                                            gymId = partnerGym.id
                                        )
                                        isValidating = false
                                        val isApproved = status == ValidationResult.APPROVED
                                        validationBanner = ValidationBannerState(
                                            isApproved = isApproved,
                                            title = if (isApproved) "Approved — Alex Vance, Global Unlimited" else "Denied — $message"
                                        )
                                        if (isApproved) {
                                            inputCode = ""
                                        }
                                    }
                                }
                            },
                            enabled = inputCode.length == 6 && !isValidating,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            if (isValidating) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Verifying with Global Network...", color = Color.White)
                            } else {
                                Text(
                                    text = if (inputCode.length == 6) "VALIDATE CHECK-IN" else "ENTER 6 DIGITS (${inputCode.length}/6)",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Full-Width Result Banner (Moss or Brick)
                        AnimatedVisibility(
                            visible = validationBanner != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            if (validationBanner != null) {
                                val banner = validationBanner!!
                                Spacer(modifier = Modifier.height(14.dp))
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (banner.isApproved) NomadMoss else NomadBrick
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (banner.isApproved) Icons.Outlined.CheckCircle else Icons.Outlined.ErrorOutline,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = if (banner.isApproved) "CHECK-IN APPROVED" else "CHECK-IN DENIED",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White.copy(alpha = 0.9f)
                                            )
                                            Text(
                                                text = banner.title,
                                                fontSize = 13.sp,
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
            }

            // Running List of Today's Validations directly below input
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TODAY'S VALIDATIONS (${todayVisits.size})",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadSteel
                    )
                    Text(
                        text = "LIVE STREAM",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = NomadMoss
                    )
                }
            }

            if (todayVisits.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Outlined.CheckCircleOutline,
                        title = "No validations performed today",
                        message = "Validations performed on this terminal will log here immediately."
                    )
                }
            } else {
                items(todayVisits, key = { it.id }) { visit ->
                    RecentCheckInItem(visit = visit)
                }
            }
        }
    }
}

private data class ValidationBannerState(
    val isApproved: Boolean,
    val title: String
)

@Composable
private fun KeypadGrid(
    onDigitPress: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val rows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("CLEAR", "0", "DEL")
        )

        for (row in rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (key in row) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clickable {
                                when (key) {
                                    "CLEAR" -> onClear()
                                    "DEL" -> onBackspace()
                                    else -> onDigitPress(key)
                                }
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = when (key) {
                            "CLEAR" -> NomadConcrete
                            "DEL" -> NomadConcrete
                            else -> Color.White
                        },
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (key == "DEL") {
                                Icon(
                                    imageVector = Icons.Outlined.Backspace,
                                    contentDescription = "Delete",
                                    tint = NomadSteel,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = key,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = if (key.length > 1) 11.sp else 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (key == "CLEAR") NomadSteel else NomadInk
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
