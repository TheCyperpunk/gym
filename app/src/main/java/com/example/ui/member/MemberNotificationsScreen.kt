package com.example.ui.member

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import com.example.model.NotificationItem
import com.example.model.User
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MemberNotificationsScreen(
    user: User,
    notifications: List<NotificationItem>,
    onBack: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val userNotifications = remember(notifications, user.uid) {
        notifications.filter { it.userId == user.uid }.sortedByDescending { it.createdAt }
    }

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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = NomadInk
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Notifications",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk
                    )
                }

                if (userNotifications.any { !it.read }) {
                    Text(
                        text = "Mark all read",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NomadSignal,
                        modifier = Modifier
                            .clickable { NomadFitRepository.markAllNotificationsRead() }
                            .padding(8.dp)
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        if (userNotifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    icon = Icons.Outlined.NotificationsNone,
                    title = "No notifications",
                    message = "You're all caught up on membership and check-in updates."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
            ) {
                items(userNotifications, key = { it.id }) { notif ->
                    val dateStr = remember(notif.createdAt) {
                        val diffHours = (System.currentTimeMillis() - notif.createdAt) / (1000 * 60 * 60)
                        when {
                            diffHours < 1 -> "Just now"
                            diffHours < 24 -> "${diffHours}h ago"
                            diffHours < 48 -> "Yesterday"
                            else -> "${diffHours / 24}d ago"
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                NomadFitRepository.markNotificationRead(notif.id)
                                notif.targetRoute?.let { onNavigateToRoute(it) }
                            },
                        shape = RoundedCornerShape(22.dp),
                        color = NomadMist,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (!notif.read) NomadSignal.copy(alpha = 0.5f) else NomadLine
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Type Icon Badge
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(NomadConcrete, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (notif.type) {
                                        "checkin" -> Icons.Outlined.QrCodeScanner
                                        "billing" -> Icons.Outlined.CreditCard
                                        else -> Icons.Outlined.Notifications
                                    },
                                    contentDescription = null,
                                    tint = if (notif.type == "checkin") NomadMoss else NomadSignal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notif.title,
                                        fontSize = 14.sp,
                                        fontWeight = if (!notif.read) FontWeight.Bold else FontWeight.Medium,
                                        color = NomadInk
                                    )
                                    if (!notif.read) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(NomadSignal)
                                        )
                                    }
                                }

                                Text(
                                    text = notif.body,
                                    fontSize = 12.sp,
                                    color = NomadSteel,
                                    modifier = Modifier.padding(top = 2.dp)
                                )

                                Text(
                                    text = dateStr,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = NomadFog,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
