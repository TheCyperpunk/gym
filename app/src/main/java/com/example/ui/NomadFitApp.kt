package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NomadFitRepository
import com.example.model.Gym
import com.example.model.MembershipPlan
import com.example.model.UserRole
import com.example.model.Visit
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.OnboardingScreen
import com.example.ui.components.RoleSwitcherDialog
import com.example.ui.member.*
import com.example.ui.partner.*
import com.example.ui.theme.*

// Destination states for member sub-flows
sealed interface MemberDestination {
    data object Home : MemberDestination
    data object WorkoutCircuit : MemberDestination
    data object Discover : MemberDestination
    data object CheckIn : MemberDestination
    data object Membership : MemberDestination
    data object Plans : MemberDestination
    data object VisitHistory : MemberDestination
    data object Profile : MemberDestination
    data object Notifications : MemberDestination
    data class GymDetail(val gym: Gym) : MemberDestination
    data class Checkout(val plan: MembershipPlan, val isAnnual: Boolean) : MemberDestination
    data class Support(val prefilledSubject: String = "", val prefilledCategory: String = "Access issue") : MemberDestination
}

// Destination states for gym partner portal
sealed interface PartnerDestination {
    data object Dashboard : PartnerDestination
    data object Validate : PartnerDestination
    data object Profile : PartnerDestination
    data object Rules : PartnerDestination
    data object Settlements : PartnerDestination
    data object Disputes : PartnerDestination
}

@Composable
fun NomadFitApp() {
    val currentUser by NomadFitRepository.currentUser.collectAsState()
    val plans by NomadFitRepository.plans.collectAsState()
    val subscriptions by NomadFitRepository.subscriptions.collectAsState()
    val gyms by NomadFitRepository.gyms.collectAsState()
    val partners by NomadFitRepository.partners.collectAsState()
    val accessRules by NomadFitRepository.accessRules.collectAsState()
    val visits by NomadFitRepository.visits.collectAsState()
    val payments by NomadFitRepository.payments.collectAsState()
    val settlements by NomadFitRepository.settlements.collectAsState()
    val supportTickets by NomadFitRepository.supportTickets.collectAsState()
    val notifications by NomadFitRepository.notifications.collectAsState()
    val activeCredentialCode by NomadFitRepository.activeCredentialCode.collectAsState()
    val hasCompletedOnboarding by NomadFitRepository.hasCompletedOnboarding.collectAsState()
    val notificationPrefs by NomadFitRepository.notificationPreferences.collectAsState()

    var isAuthenticated by remember { mutableStateOf(true) }
    var showDevRoleSwitcher by remember { mutableStateOf(false) }

    // Member Flow Navigation Stack
    var memberDestination by remember { mutableStateOf<MemberDestination>(MemberDestination.Home) }

    // Partner Flow Navigation
    var partnerDestination by remember { mutableStateOf<PartnerDestination>(PartnerDestination.Dashboard) }

    val activeSubscription = remember(subscriptions, currentUser.uid) {
        subscriptions.find { it.userId == currentUser.uid }
    }
    val unreadNotifs = remember(notifications, currentUser.uid) {
        notifications.count { it.userId == currentUser.uid && !it.read }
    }

    // 1. Onboarding Screen Flow (First-run or user initiated replay)
    if (!hasCompletedOnboarding) {
        OnboardingScreen(
            onFinish = { NomadFitRepository.completeOnboarding() }
        )
        return
    }

    // 2. Auth Screen (Sign Up / Log In)
    if (!isAuthenticated) {
        AuthScreen(
            onLoginSuccess = { email, role ->
                NomadFitRepository.loginAs(email, role)
                isAuthenticated = true
                memberDestination = MemberDestination.Home
            }
        )
        return
    }

    Scaffold(
        containerColor = NomadConcrete,
        topBar = {
            // Global Header with LoopFit Yellow Accent & Dev Role Switcher
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NomadMist)
                    .statusBarsPadding()
            ) {
                // Energetic yellow top highlight stripe
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(FitLoopYellow)
                )

                Surface(
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Logo & Current Shell Indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showDevRoleSwitcher = true }
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = NomadInk,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "FL",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = FitLoopYellow
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Fit loop",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = NomadInk
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(NomadSignal)
                                    )
                                }
                                Text(
                                    text = currentUser.role.label.uppercase(),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NomadSteel
                                )
                            }
                        }

                        // Actions: Role Switcher Dev Pill + Notification Bell
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Role Switcher Dev Pill
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = NomadConcrete,
                                border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                                modifier = Modifier.clickable { showDevRoleSwitcher = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.SwapHoriz,
                                        contentDescription = "Switch Role",
                                        tint = NomadSignal,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "SWITCH ROLE",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NomadInk
                                    )
                                }
                            }

                            // Notification Icon
                            if (currentUser.role == UserRole.MEMBER) {
                                IconButton(
                                    onClick = { memberDestination = MemberDestination.Notifications },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(NomadConcrete)
                                ) {
                                    BadgedBox(
                                        badge = {
                                            if (unreadNotifs > 0) {
                                                Badge(
                                                    containerColor = NomadSignal,
                                                    contentColor = Color.White
                                                ) {
                                                    Text("$unreadNotifs", fontSize = 9.sp)
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Notifications,
                                            contentDescription = "Notifications",
                                            tint = NomadInk,
                                            modifier = Modifier.size(19.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Role-Specific Curvy Bottom Navigation
            Surface(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = NomadMist,
                border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                shadowElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                when (currentUser.role) {
                    UserRole.MEMBER -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            NavTabButton(
                                label = "Home",
                                icon = Icons.Outlined.Home,
                                isSelected = memberDestination is MemberDestination.Home
                            ) { memberDestination = MemberDestination.Home }

                            NavTabButton(
                                label = "Workouts",
                                icon = Icons.Outlined.FitnessCenter,
                                isSelected = memberDestination is MemberDestination.WorkoutCircuit
                            ) { memberDestination = MemberDestination.WorkoutCircuit }

                            NavTabButton(
                                label = "Discover",
                                icon = Icons.Outlined.Map,
                                isSelected = memberDestination is MemberDestination.Discover
                            ) { memberDestination = MemberDestination.Discover }

                            NavTabButton(
                                label = "Check In",
                                icon = Icons.Outlined.QrCodeScanner,
                                isSelected = memberDestination is MemberDestination.CheckIn,
                                isSignal = true
                            ) { memberDestination = MemberDestination.CheckIn }

                            NavTabButton(
                                label = "Pass",
                                icon = Icons.Outlined.CardMembership,
                                isSelected = memberDestination is MemberDestination.Membership || memberDestination is MemberDestination.Plans
                            ) { memberDestination = MemberDestination.Membership }

                            NavTabButton(
                                label = "Profile",
                                icon = Icons.Outlined.Person,
                                isSelected = memberDestination is MemberDestination.Profile
                            ) { memberDestination = MemberDestination.Profile }
                        }
                    }
                    UserRole.GYM_OWNER -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            NavTabButton("Dashboard", Icons.Outlined.Dashboard, partnerDestination is PartnerDestination.Dashboard) {
                                partnerDestination = PartnerDestination.Dashboard
                            }
                            NavTabButton("Validate", Icons.Outlined.QrCodeScanner, partnerDestination is PartnerDestination.Validate, isSignal = true) {
                                partnerDestination = PartnerDestination.Validate
                            }
                            NavTabButton("Profile", Icons.Outlined.Storefront, partnerDestination is PartnerDestination.Profile) {
                                partnerDestination = PartnerDestination.Profile
                            }
                            NavTabButton("Rules", Icons.Outlined.Rule, partnerDestination is PartnerDestination.Rules) {
                                partnerDestination = PartnerDestination.Rules
                            }
                            NavTabButton("Settlements", Icons.Outlined.AccountBalance, partnerDestination is PartnerDestination.Settlements) {
                                partnerDestination = PartnerDestination.Settlements
                            }
                            NavTabButton("Disputes", Icons.Outlined.ContactSupport, partnerDestination is PartnerDestination.Disputes) {
                                partnerDestination = PartnerDestination.Disputes
                            }
                        }
                    }
                    UserRole.ADMIN -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = NomadConcrete,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Outlined.AdminPanelSettings,
                                            contentDescription = null,
                                            tint = NomadSignal,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Admin: ${currentUser.fullName}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NomadInk
                                )
                            }

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
                                            .clip(CircleShape)
                                            .background(NomadMoss)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "Global Cluster OK",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = NomadMoss
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentUser.role) {
                UserRole.MEMBER -> {
                    when (val dest = memberDestination) {
                        is MemberDestination.Home -> MemberHomeScreen(
                            user = currentUser,
                            activeSubscription = activeSubscription,
                            plans = plans,
                            gyms = gyms,
                            visits = visits,
                            notifications = notifications,
                            onNavigateToCheckIn = { memberDestination = MemberDestination.CheckIn },
                            onNavigateToPlans = { memberDestination = MemberDestination.Plans },
                            onNavigateToGymDetail = { gym -> memberDestination = MemberDestination.GymDetail(gym) },
                            onNavigateToDiscover = { memberDestination = MemberDestination.Discover },
                            onNavigateToActivity = { memberDestination = MemberDestination.VisitHistory },
                            onNavigateToNotifications = { memberDestination = MemberDestination.Notifications },
                            onNavigateToWorkoutCircuit = { memberDestination = MemberDestination.WorkoutCircuit }
                        )
                        is MemberDestination.WorkoutCircuit -> MemberWorkoutCircuitScreen(
                            onNavigateBack = { memberDestination = MemberDestination.Home },
                            onNavigateToCheckIn = { memberDestination = MemberDestination.CheckIn }
                        )
                        is MemberDestination.Discover -> MemberDiscoverScreen(
                            user = currentUser,
                            gyms = gyms,
                            activeSubscription = activeSubscription,
                            plans = plans,
                            onSelectGym = { gym -> memberDestination = MemberDestination.GymDetail(gym) },
                            onNavigateToPlans = { memberDestination = MemberDestination.Plans }
                        )
                        is MemberDestination.GymDetail -> GymDetailScreen(
                            gym = dest.gym,
                            user = currentUser,
                            activeSubscription = activeSubscription,
                            plans = plans,
                            onBack = { memberDestination = MemberDestination.Discover },
                            onNavigateToCheckIn = { memberDestination = MemberDestination.CheckIn },
                            onNavigateToPlans = { memberDestination = MemberDestination.Plans }
                        )
                        is MemberDestination.CheckIn -> MemberCheckInScreen(
                            user = currentUser,
                            activeSubscription = activeSubscription,
                            plans = plans,
                            gyms = gyms,
                            activeCredentialCode = activeCredentialCode,
                            onClose = { memberDestination = MemberDestination.Home },
                            onNavigateToPlans = { memberDestination = MemberDestination.Plans }
                        )
                        is MemberDestination.Membership -> MyMembershipScreen(
                            user = currentUser,
                            activeSubscription = activeSubscription,
                            plans = plans,
                            onNavigateToPlans = { memberDestination = MemberDestination.Plans },
                            onNavigateToCheckIn = { memberDestination = MemberDestination.CheckIn }
                        )
                        is MemberDestination.Plans -> MemberPlansScreen(
                            user = currentUser,
                            plans = plans,
                            activeSubscription = activeSubscription,
                            onSelectPlanForCheckout = { plan, isAnnual ->
                                memberDestination = MemberDestination.Checkout(plan, isAnnual)
                            }
                        )
                        is MemberDestination.Checkout -> MemberCheckoutScreen(
                            plan = dest.plan,
                            isAnnual = dest.isAnnual,
                            user = currentUser,
                            onBack = { memberDestination = MemberDestination.Plans },
                            onComplete = { memberDestination = MemberDestination.Membership }
                        )
                        is MemberDestination.VisitHistory -> MemberVisitHistoryScreen(
                            user = currentUser,
                            visits = visits,
                            onReportIssueForVisit = { visit ->
                                memberDestination = MemberDestination.Support(
                                    prefilledSubject = "Issue with check-in at ${visit.gymName}",
                                    prefilledCategory = "Access issue"
                                )
                            }
                        )
                        is MemberDestination.Profile -> MemberProfileScreen(
                            user = currentUser,
                            notificationPrefs = notificationPrefs,
                            onNavigateToSupport = { memberDestination = MemberDestination.Support() },
                            onReplayOnboarding = { NomadFitRepository.resetOnboarding() },
                            onLogout = {
                                isAuthenticated = false
                                memberDestination = MemberDestination.Home
                            }
                        )
                        is MemberDestination.Support -> MemberSupportScreen(
                            user = currentUser,
                            supportTickets = supportTickets,
                            prefilledSubject = dest.prefilledSubject,
                            prefilledCategory = dest.prefilledCategory,
                            onBack = { memberDestination = MemberDestination.Profile }
                        )
                        is MemberDestination.Notifications -> MemberNotificationsScreen(
                            user = currentUser,
                            notifications = notifications,
                            onBack = { memberDestination = MemberDestination.Home },
                            onNavigateToRoute = { route ->
                                memberDestination = when (route) {
                                    "activity" -> MemberDestination.VisitHistory
                                    "membership" -> MemberDestination.Membership
                                    else -> MemberDestination.Home
                                }
                            }
                        )
                    }
                }
                UserRole.GYM_OWNER -> {
                    when (partnerDestination) {
                        is PartnerDestination.Dashboard -> PartnerDashboardScreen(
                            user = currentUser,
                            gyms = gyms,
                            visits = visits,
                            settlements = settlements,
                            onNavigateToValidate = { partnerDestination = PartnerDestination.Validate },
                            onNavigateToProfile = { partnerDestination = PartnerDestination.Profile },
                            onNavigateToRules = { partnerDestination = PartnerDestination.Rules },
                            onNavigateToSettlements = { partnerDestination = PartnerDestination.Settlements },
                            onNavigateToDisputes = { partnerDestination = PartnerDestination.Disputes }
                        )
                        is PartnerDestination.Validate -> PartnerValidateScreen(
                            user = currentUser,
                            gyms = gyms,
                            visits = visits,
                            onBack = { partnerDestination = PartnerDestination.Dashboard }
                        )
                        is PartnerDestination.Profile -> PartnerGymProfileScreen(
                            user = currentUser,
                            gyms = gyms,
                            onBack = { partnerDestination = PartnerDestination.Dashboard }
                        )
                        is PartnerDestination.Rules -> PartnerAccessRulesScreen(
                            user = currentUser,
                            gyms = gyms,
                            plans = plans,
                            accessRules = accessRules,
                            onBack = { partnerDestination = PartnerDestination.Dashboard }
                        )
                        is PartnerDestination.Settlements -> PartnerSettlementScreen(
                            user = currentUser,
                            gyms = gyms,
                            visits = visits,
                            settlements = settlements,
                            onBack = { partnerDestination = PartnerDestination.Dashboard }
                        )
                        is PartnerDestination.Disputes -> PartnerDisputesScreen(
                            user = currentUser,
                            gyms = gyms,
                            supportTickets = supportTickets,
                            onBack = { partnerDestination = PartnerDestination.Dashboard }
                        )
                    }
                }
                UserRole.ADMIN -> {
                    AdminDashboardScreen(
                        user = currentUser,
                        gyms = gyms,
                        partners = partners,
                        plans = plans,
                        settlements = settlements,
                        tickets = supportTickets
                    )
                }
            }
        }
    }

    // Role Switcher Dialog
    if (showDevRoleSwitcher) {
        RoleSwitcherDialog(
            currentUser = currentUser,
            onSelectRole = { newRole ->
                NomadFitRepository.switchRole(newRole)
                memberDestination = MemberDestination.Home
                partnerDestination = PartnerDestination.Dashboard
            },
            onDismiss = { showDevRoleSwitcher = false }
        )
    }
}

@Composable
private fun NavTabButton(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    isSignal: Boolean = false,
    isDark: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = when {
            isSelected && isSignal -> NomadSignal.copy(alpha = 0.15f)
            isSelected && isDark -> Color(0xFF2E3138)
            isSelected -> NomadConcrete
            else -> Color.Transparent
        },
        border = if (isSelected) androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSignal) NomadSignal.copy(alpha = 0.4f) else if (isDark) Color(0xFF4A4F59) else NomadLine
        ) else null,
        modifier = Modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = when {
                    isSelected && isSignal -> NomadSignal
                    isSelected && isDark -> Color.White
                    isSelected -> NomadInk
                    isDark -> NomadFog
                    else -> NomadSteel
                },
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = when {
                    isSelected && isSignal -> NomadSignal
                    isSelected && isDark -> Color.White
                    isSelected -> NomadInk
                    isDark -> NomadFog
                    else -> NomadSteel
                }
            )
        }
    }
}
