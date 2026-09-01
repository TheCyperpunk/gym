package com.example.model

enum class UserRole(val value: String, val label: String) {
    MEMBER("member", "Member"),
    GYM_OWNER("gymOwner", "Gym Partner"),
    ADMIN("admin", "Platform Admin");

    companion object {
        fun fromValue(value: String): UserRole =
            entries.find { it.value.equals(value, ignoreCase = true) } ?: MEMBER
    }
}

enum class AccountStatus(val value: String) {
    ACTIVE("active"),
    SUSPENDED("suspended")
}

enum class SubscriptionStatus(val value: String, val label: String) {
    ACTIVE("active", "Active"),
    PAUSED("paused", "Paused"),
    CANCELLED("cancelled", "Cancelled"),
    EXPIRED("expired", "Expired"),
    PAST_DUE("pastDue", "Past Due")
}

enum class GymTier(val value: String, val label: String) {
    STANDARD("standard", "Standard"),
    PREMIUM("premium", "Premium")
}

enum class GymStatus(val value: String, val label: String) {
    PENDING("pending", "Pending Review"),
    ACTIVE("active", "Active Network"),
    SUSPENDED("suspended", "Suspended"),
    TEMPORARILY_CLOSED("temporarilyClosed", "Temp Closed")
}

enum class KycStatus(val value: String, val label: String) {
    PENDING("pending", "Pending Verification"),
    VERIFIED("verified", "Verified Partner"),
    REJECTED("rejected", "Rejected")
}

enum class ValidationResult(val value: String) {
    APPROVED("approved"),
    DENIED("denied")
}

enum class PaymentStatus(val value: String, val label: String) {
    SUCCEEDED("succeeded", "Succeeded"),
    FAILED("failed", "Failed"),
    REFUNDED("refunded", "Refunded")
}

enum class SettlementStatus(val value: String, val label: String) {
    PENDING("pending", "Pending Payout"),
    PAID("paid", "Paid & Settled")
}

enum class TicketStatus(val value: String, val label: String) {
    OPEN("open", "Open"),
    IN_PROGRESS("inProgress", "In Progress"),
    RESOLVED("resolved", "Resolved")
}

data class User(
    val uid: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val photoUrl: String,
    val role: UserRole,
    val homeCity: String,
    val status: AccountStatus = AccountStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis()
)

data class MembershipPlan(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val currency: String = "USD",
    val billingCycle: String = "monthly", // "monthly" | "annual"
    val visitAllowance: Int = -1, // -1 means unlimited
    val eligibleGymTiers: List<String> = listOf("standard"),
    val citiesIncluded: List<String> = listOf("all"),
    val isActive: Boolean = true
) {
    val isUnlimited: Boolean get() = visitAllowance <= 0
}

data class Subscription(
    val id: String,
    val userId: String,
    val planId: String,
    val status: SubscriptionStatus,
    val startDate: Long,
    val currentPeriodEnd: Long,
    val renewalDate: Long,
    val visitsUsedThisCycle: Int,
    val visitsAllowance: Int, // -1 for unlimited
    val paymentMethodLast4: String
) {
    val isUnlimited: Boolean get() = visitsAllowance <= 0
    val visitsRemaining: Int get() = if (isUnlimited) 999 else (visitsAllowance - visitsUsedThisCycle).coerceAtLeast(0)
    val isActive: Boolean get() = status == SubscriptionStatus.ACTIVE
}

data class Gym(
    val id: String,
    val ownerId: String,
    val name: String,
    val description: String,
    val city: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val facilities: List<String>,
    val photos: List<String>,
    val operatingHours: String,
    val tier: GymTier,
    val status: GymStatus,
    val checkInCount: Int = 0
)

data class GymPartner(
    val id: String,
    val userId: String,
    val businessName: String,
    val kycStatus: KycStatus,
    val payoutPerVisit: Double,
    val currency: String = "USD",
    val payoutMethod: String = "Stripe Connect Direct"
)

data class AccessRules(
    val id: String,
    val gymId: String,
    val eligiblePlanIds: List<String>,
    val maxVisitsPerDay: Int = 1,
    val maxVisitsPerMonthPerMember: Int = 12,
    val bookingRequired: Boolean = false
)

data class Visit(
    val id: String,
    val userId: String,
    val userName: String = "",
    val gymId: String,
    val gymName: String = "",
    val gymCity: String = "",
    val subscriptionId: String,
    val checkInTimestamp: Long = System.currentTimeMillis(),
    val credentialCode: String,
    val validationResult: ValidationResult,
    val denialReason: String? = null,
    val payoutAmount: Double = 14.50,
    val payoutStatus: SettlementStatus = SettlementStatus.PENDING
)

data class Payment(
    val id: String,
    val userId: String,
    val subscriptionId: String,
    val amount: Double,
    val currency: String = "USD",
    val status: PaymentStatus,
    val createdAt: Long = System.currentTimeMillis(),
    val cardLast4: String = "4242",
    val description: String = "Membership Subscription"
)

data class Settlement(
    val id: String,
    val gymId: String,
    val gymName: String = "",
    val periodStart: Long,
    val periodEnd: Long,
    val totalVisits: Int,
    val totalAmount: Double,
    val status: SettlementStatus,
    val paidAt: Long? = null
)

data class SupportTicket(
    val id: String,
    val raisedByUserId: String,
    val raisedByUserName: String = "",
    val raisedByRole: UserRole,
    val category: String,
    val subject: String,
    val description: String,
    val status: TicketStatus,
    val relatedVisitId: String? = null,
    val assignedTo: String? = null,
    val resolutionNote: String? = null,
    val resolvedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class PromoCode(
    val id: String,
    val code: String,
    val discountType: String, // "percent" | "fixed"
    val value: Double, // e.g. 20.0 for 20% or $20.00
    val usageCount: Int = 0,
    val maxUsage: Int? = null,
    val expiryTimestamp: Long = System.currentTimeMillis() + 86400000L * 90,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

data class AuditLogEntry(
    val id: String,
    val actorAdminId: String,
    val actorAdminName: String,
    val targetType: String, // "USER", "GYM", "PLAN", "PROMO", "PAYMENT", "SETTLEMENT", "CHECKIN", "TICKET"
    val targetId: String,
    val targetName: String,
    val action: String, // "SUSPEND_USER", "REACTIVATE_USER", "RESET_PASSWORD", "KYC_APPROVE", "KYC_REJECT", "REFUND", "OVERRIDE_CHECKIN", "RESOLVE_TICKET", "BULK_SETTLEMENT"
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class AnomalyType(val label: String, val severity: String) {
    IMPOSSIBLE_TRAVEL("Impossible Travel Window", "CRITICAL"),
    REPEATED_FAILED_ACCESS("Repeated Terminal Denials", "HIGH"),
    MULTIPLE_ACTIVE_CREDENTIALS("Simultaneous Access Conflict", "MEDIUM")
}

data class CheckInAnomaly(
    val visitId: String,
    val type: AnomalyType,
    val details: String,
    val detectedAt: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false,
    val resolutionReason: String? = null
)


data class NotificationItem(
    val id: String,
    val userId: String,
    val type: String, // "checkin", "billing", "system", "kyc"
    val title: String,
    val body: String,
    val read: Boolean = false,
    val targetRoute: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class NotificationPreferences(
    val membershipUpdates: Boolean = true,
    val checkInConfirmations: Boolean = true,
    val paymentReceipts: Boolean = true
)

data class CityLeadCapture(
    val city: String,
    val email: String,
    val timestamp: Long = System.currentTimeMillis()
)

