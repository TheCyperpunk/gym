package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.User
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun RoleSwitcherDialog(
    currentUser: User,
    onSelectRole: (UserRole) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NomadMist,
        shape = RoundedCornerShape(28.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NomadConcrete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.SwapHoriz,
                                contentDescription = "Switch Role",
                                tint = NomadSignal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Dev Role Switcher",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = FitLoopYellow.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FitLoopYellow)
                ) {
                    Text(
                        text = "PROTOTYPE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Switch between the three marketplace roles instantly without logging out:",
                    fontSize = 12.sp,
                    color = NomadSteel
                )

                RoleOptionCard(
                    role = UserRole.MEMBER,
                    title = "Member Portal",
                    subtitle = "Alex Vance • Global All-Access",
                    description = "Browse gyms on map, view access card, 6-digit check-in code, and plans.",
                    icon = Icons.Outlined.Person,
                    isSelected = currentUser.role == UserRole.MEMBER,
                    onClick = {
                        onSelectRole(UserRole.MEMBER)
                        onDismiss()
                    }
                )

                RoleOptionCard(
                    role = UserRole.GYM_OWNER,
                    title = "Gym Partner Portal",
                    subtitle = "Sarah Connor • IronForge Club",
                    description = "Validate member codes, view live check-in feed, accrued payouts & capacity.",
                    icon = Icons.Outlined.FitnessCenter,
                    isSelected = currentUser.role == UserRole.GYM_OWNER,
                    onClick = {
                        onSelectRole(UserRole.GYM_OWNER)
                        onDismiss()
                    }
                )

                RoleOptionCard(
                    role = UserRole.ADMIN,
                    title = "Platform Admin Panel",
                    subtitle = "Marcus Drake • System Ops",
                    description = "Dense tables for KYC approvals, plans manager, financial settlements & disputes.",
                    icon = Icons.Outlined.AdminPanelSettings,
                    isSelected = currentUser.role == UserRole.ADMIN,
                    onClick = {
                        onSelectRole(UserRole.ADMIN)
                        onDismiss()
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                modifier = Modifier.height(44.dp).padding(horizontal = 8.dp)
            ) {
                Text("Close", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    )
}

@Composable
private fun RoleOptionCard(
    role: UserRole,
    title: String,
    subtitle: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) NomadSignal.copy(alpha = 0.08f) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) NomadSignal else NomadLine
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        if (isSelected) NomadSignal else NomadConcrete,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else NomadInk,
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
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadInk
                    )
                    if (isSelected) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NomadSignal
                        ) {
                            Text(
                                text = "ACTIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = NomadSteel
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = NomadSteel,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
