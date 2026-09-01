package com.example.data

import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

object NomadFitRepository {
    private val scope = CoroutineScope(Dispatchers.Default)

    // Demo Users
    val memberUser = User(
        uid = "usr_member_alex",
        fullName = "Alex Vance",
        email = "alex.nomad@example.com",
        phone = "+1 (555) 382-9104",
        photoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
        role = UserRole.MEMBER,
        homeCity = "Tokyo",
        status = AccountStatus.ACTIVE,
        createdAt = System.currentTimeMillis() - 86400000L * 45
    )

    val partnerUser = User(
        uid = "usr_partner_sarah",
        fullName = "Sarah Connor",
        email = "sarah.partner@ironforge.com",
        phone = "+81 3-555-0192",
        photoUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=300&auto=format&fit=crop&q=80",
        role = UserRole.GYM_OWNER,
        homeCity = "Tokyo",
        status = AccountStatus.ACTIVE,
        createdAt = System.currentTimeMillis() - 86400000L * 120
    )

    val adminUser = User(
        uid = "usr_admin_marcus",
        fullName = "Marcus Drake",
        email = "admin@nomadfit.io",
        phone = "+1 (555) 998-2001",
        photoUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop&q=80",
        role = UserRole.ADMIN,
        homeCity = "London",
        status = AccountStatus.ACTIVE,
        createdAt = System.currentTimeMillis() - 86400000L * 300
    )

    // All Users Directory (Members & Partners)
    private val _allUsers = MutableStateFlow<List<User>>(
        listOf(
            memberUser,
            User(
                uid = "usr_member_elena",
                fullName = "Elena Rostova",
                email = "elena.r@voyager.org",
                phone = "+44 20 7946 0912",
                photoUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=300&auto=format&fit=crop&q=80",
                role = UserRole.MEMBER,
                homeCity = "London",
                status = AccountStatus.ACTIVE,
                createdAt = System.currentTimeMillis() - 86400000L * 80
            ),
            User(
                uid = "usr_member_david",
                fullName = "David Chen",
                email = "david.chen@fintech.sg",
                phone = "+65 9123 4567",
                photoUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=300&auto=format&fit=crop&q=80",
                role = UserRole.MEMBER,
                homeCity = "Singapore",
                status = AccountStatus.ACTIVE,
                createdAt = System.currentTimeMillis() - 86400000L * 25
            ),
            User(
                uid = "usr_member_liam",
                fullName = "Liam O'Connor",
                email = "liam.oc@crossfit.ie",
                phone = "+1 (212) 555-0144",
                photoUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&auto=format&fit=crop&q=80",
                role = UserRole.MEMBER,
                homeCity = "New York",
                status = AccountStatus.ACTIVE,
                createdAt = System.currentTimeMillis() - 86400000L * 15
            ),
            User(
                uid = "usr_member_chloe",
                fullName = "Chloe Bennett",
                email = "chloe.b@digitalnomad.de",
                phone = "+49 30 123456",
                photoUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&auto=format&fit=crop&q=80",
                role = UserRole.MEMBER,
                homeCity = "Berlin",
                status = AccountStatus.SUSPENDED,
                createdAt = System.currentTimeMillis() - 86400000L * 60
            ),
            User(
                uid = "usr_member_kenji",
                fullName = "Kenji Sato",
                email = "kenji.s@designlab.jp",
                phone = "+81 90 1234 5678",
                photoUrl = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=300&auto=format&fit=crop&q=80",
                role = UserRole.MEMBER,
                homeCity = "Tokyo",
                status = AccountStatus.ACTIVE,
                createdAt = System.currentTimeMillis() - 86400000L * 110
            ),
            partnerUser,
            User(
                uid = "usr_partner_london1",
                fullName = "Arthur Pendelton",
                email = "arthur@foundryshoreditch.co.uk",
                phone = "+44 20 8123 4567",
                photoUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=300&auto=format&fit=crop&q=80",
                role = UserRole.GYM_OWNER,
                homeCity = "London",
                status = AccountStatus.ACTIVE,
                createdAt = System.currentTimeMillis() - 86400000L * 180
            ),
            User(
                uid = "usr_partner_ny1",
                fullName = "Frank Sullivan",
                email = "frank@manhattansteel.com",
                phone = "+1 (212) 555-8822",
                photoUrl = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=300&auto=format&fit=crop&q=80",
                role = UserRole.GYM_OWNER,
                homeCity = "New York",
                status = AccountStatus.ACTIVE,
                createdAt = System.currentTimeMillis() - 86400000L * 220
            ),
            User(
                uid = "usr_partner_new",
                fullName = "Taro Yamada",
                email = "taro@kyotozenbarbell.jp",
                phone = "+81 75 555 9012",
                photoUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=300&auto=format&fit=crop&q=80",
                role = UserRole.GYM_OWNER,
                homeCity = "Tokyo",
                status = AccountStatus.ACTIVE,
                createdAt = System.currentTimeMillis() - 86400000L * 10
            ),
            adminUser
        )
    )
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    // Current Session State
    private val _currentUser = MutableStateFlow<User>(memberUser)
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Membership Plans
    private val _plans = MutableStateFlow<List<MembershipPlan>>(
        listOf(
            MembershipPlan(
                id = "plan_global_unlimited",
                name = "Nomad Global All-Access",
                description = "Unlimited gym check-ins worldwide at all Standard & Premium partner locations.",
                price = 129.0,
                currency = "USD",
                billingCycle = "monthly",
                visitAllowance = -1, // unlimited
                eligibleGymTiers = listOf("standard", "premium"),
                citiesIncluded = listOf("all"),
                isActive = true
            ),
            MembershipPlan(
                id = "plan_city_flex",
                name = "Nomad Flex Pass",
                description = "10 monthly check-ins across any standard partner facilities globally.",
                price = 69.0,
                currency = "USD",
                billingCycle = "monthly",
                visitAllowance = 10,
                eligibleGymTiers = listOf("standard"),
                citiesIncluded = listOf("all"),
                isActive = true
            ),
            MembershipPlan(
                id = "plan_roamer",
                name = "Nomad Roamer Starter",
                description = "4 monthly drop-in passes ideal for occasional travelers.",
                price = 39.0,
                currency = "USD",
                billingCycle = "monthly",
                visitAllowance = 4,
                eligibleGymTiers = listOf("standard"),
                citiesIncluded = listOf("all"),
                isActive = true
            )
        )
    )
    val plans: StateFlow<List<MembershipPlan>> = _plans.asStateFlow()

    // Subscriptions
    private val _subscriptions = MutableStateFlow<List<Subscription>>(
        listOf(
            Subscription(
                id = "sub_alex_001",
                userId = "usr_member_alex",
                planId = "plan_global_unlimited",
                status = SubscriptionStatus.ACTIVE,
                startDate = System.currentTimeMillis() - 86400000L * 18,
                currentPeriodEnd = System.currentTimeMillis() + 86400000L * 12,
                renewalDate = System.currentTimeMillis() + 86400000L * 12,
                visitsUsedThisCycle = 7,
                visitsAllowance = -1,
                paymentMethodLast4 = "4242"
            ),
            Subscription(
                id = "sub_elena_002",
                userId = "usr_member_elena",
                planId = "plan_city_flex",
                status = SubscriptionStatus.ACTIVE,
                startDate = System.currentTimeMillis() - 86400000L * 40,
                currentPeriodEnd = System.currentTimeMillis() + 86400000L * 20,
                renewalDate = System.currentTimeMillis() + 86400000L * 20,
                visitsUsedThisCycle = 4,
                visitsAllowance = 10,
                paymentMethodLast4 = "9102"
            ),
            Subscription(
                id = "sub_david_003",
                userId = "usr_member_david",
                planId = "plan_global_unlimited",
                status = SubscriptionStatus.ACTIVE,
                startDate = System.currentTimeMillis() - 86400000L * 10,
                currentPeriodEnd = System.currentTimeMillis() + 86400000L * 20,
                renewalDate = System.currentTimeMillis() + 86400000L * 20,
                visitsUsedThisCycle = 3,
                visitsAllowance = -1,
                paymentMethodLast4 = "3319"
            ),
            Subscription(
                id = "sub_liam_004",
                userId = "usr_member_liam",
                planId = "plan_global_unlimited",
                status = SubscriptionStatus.ACTIVE,
                startDate = System.currentTimeMillis() - 86400000L * 5,
                currentPeriodEnd = System.currentTimeMillis() + 86400000L * 25,
                renewalDate = System.currentTimeMillis() + 86400000L * 25,
                visitsUsedThisCycle = 5,
                visitsAllowance = -1,
                paymentMethodLast4 = "1184"
            ),
            Subscription(
                id = "sub_chloe_005",
                userId = "usr_member_chloe",
                planId = "plan_roamer",
                status = SubscriptionStatus.CANCELLED,
                startDate = System.currentTimeMillis() - 86400000L * 60,
                currentPeriodEnd = System.currentTimeMillis() - 86400000L * 30,
                renewalDate = System.currentTimeMillis() - 86400000L * 30,
                visitsUsedThisCycle = 4,
                visitsAllowance = 4,
                paymentMethodLast4 = "7721"
            ),
            Subscription(
                id = "sub_kenji_006",
                userId = "usr_member_kenji",
                planId = "plan_city_flex",
                status = SubscriptionStatus.PAUSED,
                startDate = System.currentTimeMillis() - 86400000L * 90,
                currentPeriodEnd = System.currentTimeMillis() + 86400000L * 15,
                renewalDate = System.currentTimeMillis() + 86400000L * 15,
                visitsUsedThisCycle = 2,
                visitsAllowance = 10,
                paymentMethodLast4 = "5501"
            )
        )
    )
    val subscriptions: StateFlow<List<Subscription>> = _subscriptions.asStateFlow()

    // Gyms
    private val _gyms = MutableStateFlow<List<Gym>>(
        listOf(
            Gym(
                id = "gym_ironforge_tokyo",
                ownerId = "usr_partner_sarah",
                name = "IronForge Athletic Club",
                description = "High-performance strength conditioning, Olympic lifting platforms, recovery sauna & ice baths.",
                city = "Tokyo",
                address = "3-14-1 Shinjuku, Shinjuku City, Tokyo 160-0022",
                lat = 35.6917,
                lng = 139.7036,
                facilities = listOf("Olympic Lifting", "Sauna & Ice Bath", "Towel Service", "Free Weights", "Lockers", "Showers"),
                photos = listOf(
                    "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=800&auto=format&fit=crop&q=80",
                    "https://images.unsplash.com/photo-1540497077202-7c8a3999166f?w=800&auto=format&fit=crop&q=80"
                ),
                operatingHours = "Mon-Sun: 06:00 - 23:00",
                tier = GymTier.PREMIUM,
                status = GymStatus.ACTIVE,
                checkInCount = 142
            ),
            Gym(
                id = "gym_kanto_tokyo",
                ownerId = "usr_partner_tokyo2",
                name = "Kanto Powerhouse",
                description = "Raw barbell training, powerlifting racks, dumbbells up to 60kg, and turf track.",
                city = "Tokyo",
                address = "1-22-8 Shibuya, Shibuya City, Tokyo 150-0002",
                lat = 35.6595,
                lng = 139.7005,
                facilities = listOf("Power Racks", "Dumbbells", "Turf Track", "Showers", "Chalk Allowed"),
                photos = listOf(
                    "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?w=800&auto=format&fit=crop&q=80"
                ),
                operatingHours = "24/7 Access",
                tier = GymTier.STANDARD,
                status = GymStatus.ACTIVE,
                checkInCount = 98
            ),
            Gym(
                id = "gym_zenith_tokyo",
                ownerId = "usr_partner_tokyo3",
                name = "Zenith Roppongi Lab",
                description = "Modern boutique functional fitness, Eleiko bars, cardio theater & infrared recovery.",
                city = "Tokyo",
                address = "6-10-1 Roppongi, Minato City, Tokyo 106-0032",
                lat = 35.6628,
                lng = 139.7314,
                facilities = listOf("Eleiko Equipment", "Cardio Lab", "Infrared Sauna", "Smoothie Bar", "Lockers"),
                photos = listOf(
                    "https://images.unsplash.com/photo-1574680096145-d05b474e2155?w=800&auto=format&fit=crop&q=80"
                ),
                operatingHours = "Mon-Fri: 06:00 - 22:00, Sat-Sun: 08:00 - 20:00",
                tier = GymTier.PREMIUM,
                status = GymStatus.ACTIVE,
                checkInCount = 67
            ),
            Gym(
                id = "gym_foundry_london",
                ownerId = "usr_partner_london1",
                name = "The Foundry Shoreditch",
                description = "Industrial-grade community gym featuring strongman logs, ropes, sleds and recovery lounge.",
                city = "London",
                address = "22-26 Paul St, London EC2A 4JH, UK",
                lat = 51.5225,
                lng = -0.0862,
                facilities = listOf("Strongman Kit", "Sled Track", "Showers", "Specialty Bars", "Coffee Bar"),
                photos = listOf(
                    "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=800&auto=format&fit=crop&q=80"
                ),
                operatingHours = "Mon-Fri: 06:30 - 21:30, Sat: 08:00 - 18:00",
                tier = GymTier.PREMIUM,
                status = GymStatus.ACTIVE,
                checkInCount = 184
            ),
            Gym(
                id = "gym_canary_london",
                ownerId = "usr_partner_london2",
                name = "Canary Wharf Health Club",
                description = "Spacious multi-floor gym with cardio decks, pin-loaded machines, and 25m lap pool.",
                city = "London",
                address = "Cabot Square, London E14 4QS, UK",
                lat = 51.5055,
                lng = -0.0209,
                facilities = listOf("Lap Pool", "Cardio Zone", "Steam Room", "Free Weights", "Towels"),
                photos = listOf(
                    "https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=800&auto=format&fit=crop&q=80"
                ),
                operatingHours = "Mon-Sun: 06:00 - 22:00",
                tier = GymTier.STANDARD,
                status = GymStatus.ACTIVE,
                checkInCount = 112
            ),
            Gym(
                id = "gym_manhattan_ny",
                ownerId = "usr_partner_ny1",
                name = "Manhattan Steelworks",
                description = "Premier Midtown athletic sanctuary featuring custom platforms, rogue rigs & cold plunges.",
                city = "New York",
                address = "450 W 33rd St, New York, NY 10001",
                lat = 40.7538,
                lng = -73.9992,
                facilities = listOf("Cold Plunge", "Rogue Rigs", "Specialty Machines", "Towel Service", "Private Showers"),
                photos = listOf(
                    "https://images.unsplash.com/photo-1593079831268-3381b0db4a77?w=800&auto=format&fit=crop&q=80"
                ),
                operatingHours = "Mon-Fri: 05:30 - 23:00, Sat-Sun: 07:00 - 21:00",
                tier = GymTier.PREMIUM,
                status = GymStatus.ACTIVE,
                checkInCount = 230
            ),
            Gym(
                id = "gym_brooklyn_ny",
                ownerId = "usr_partner_ny2",
                name = "Brooklyn Barbell Club",
                description = "Williamsburg warehouse lifting facility with calibrate plates, chalk bowls, and open gym floor.",
                city = "New York",
                address = "182 N 10th St, Brooklyn, NY 11211",
                lat = 40.7188,
                lng = -73.9547,
                facilities = listOf("Calibrated Plates", "Bands & Chains", "Open 24h", "Lockers"),
                photos = listOf(
                    "https://images.unsplash.com/photo-1584824486509-112e4181ff6b?w=800&auto=format&fit=crop&q=80"
                ),
                operatingHours = "24/7 Access",
                tier = GymTier.STANDARD,
                status = GymStatus.ACTIVE,
                checkInCount = 89
            ),
            Gym(
                id = "gym_kreuzberg_berlin",
                ownerId = "usr_partner_berlin1",
                name = "Kreuzberg Strength Hub",
                description = "Raw Berlin industrial vibe, heavy iron, kettlebell deck, and spacious mobility zone.",
                city = "Berlin",
                address = "Oranienstraße 185, 10999 Berlin, Germany",
                lat = 52.5020,
                lng = 13.4180,
                facilities = listOf("Heavy Free Weights", "Kettlebells", "Mobility Area", "Showers"),
                photos = listOf(
                    "https://images.unsplash.com/photo-1517963879433-6ad2b056d712?w=800&auto=format&fit=crop&q=80"
                ),
                operatingHours = "Mon-Sun: 07:00 - 23:00",
                tier = GymTier.STANDARD,
                status = GymStatus.ACTIVE,
                checkInCount = 74
            ),
            Gym(
                id = "gym_barcelona_beach",
                ownerId = "usr_partner_bcn1",
                name = "Barceloneta Beach Fitness",
                description = "Oceanfront training facility with indoor gym floor and outdoor calisthenics rig.",
                city = "Barcelona",
                address = "Passeig Marítim de la Barceloneta 14, 08003 Barcelona",
                lat = 41.3809,
                lng = 2.1909,
                facilities = listOf("Outdoor Rig", "Indoor Free Weights", "Locker Room", "Beach Access"),
                photos = listOf(
                    "https://images.unsplash.com/photo-1540497077202-7c8a3999166f?w=800&auto=format&fit=crop&q=80"
                ),
                operatingHours = "Mon-Sun: 06:00 - 22:00",
                tier = GymTier.STANDARD,
                status = GymStatus.ACTIVE,
                checkInCount = 105
            ),
            Gym(
                id = "gym_marina_singapore",
                ownerId = "usr_partner_sg1",
                name = "Marina Bay Athletics",
                description = "Skyline view luxury fitness studio with Technogym Biostrength and recovery lounge.",
                city = "Singapore",
                address = "10 Marina Blvd, Marina Bay Financial Centre, Singapore 018983",
                lat = 1.2800,
                lng = 103.8540,
                facilities = listOf("Technogym Biostrength", "Hydrotherapy", "Towels", "City Views", "Private Lockers"),
                photos = listOf(
                    "https://images.unsplash.com/photo-1574680096145-d05b474e2155?w=800&auto=format&fit=crop&q=80"
                ),
                operatingHours = "Mon-Fri: 06:00 - 22:00, Sat-Sun: 07:00 - 20:00",
                tier = GymTier.PREMIUM,
                status = GymStatus.ACTIVE,
                checkInCount = 160
            )
        )
    )
    val gyms: StateFlow<List<Gym>> = _gyms.asStateFlow()

    // Gym Partners
    private val _partners = MutableStateFlow<List<GymPartner>>(
        listOf(
            GymPartner(
                id = "partner_sarah_01",
                userId = "usr_partner_sarah",
                businessName = "IronForge Athletics LLC",
                kycStatus = KycStatus.VERIFIED,
                payoutPerVisit = 14.50,
                currency = "USD",
                payoutMethod = "Stripe Connect (*8821)"
            ),
            GymPartner(
                id = "partner_london_01",
                userId = "usr_partner_london1",
                businessName = "Foundry Gym Holdings UK",
                kycStatus = KycStatus.VERIFIED,
                payoutPerVisit = 15.00,
                currency = "GBP",
                payoutMethod = "Barclays Direct (*3019)"
            ),
            GymPartner(
                id = "partner_ny_01",
                userId = "usr_partner_ny1",
                businessName = "Manhattan Steel Sports Inc",
                kycStatus = KycStatus.VERIFIED,
                payoutPerVisit = 16.00,
                currency = "USD",
                payoutMethod = "Chase Business (*4199)"
            ),
            GymPartner(
                id = "partner_pending_01",
                userId = "usr_partner_new",
                businessName = "Kyoto Zen Barbell Club",
                kycStatus = KycStatus.PENDING,
                payoutPerVisit = 13.50,
                currency = "USD",
                payoutMethod = "Bank of Japan (*0192)"
            )
        )
    )
    val partners: StateFlow<List<GymPartner>> = _partners.asStateFlow()

    // Access Rules
    private val _accessRules = MutableStateFlow<List<AccessRules>>(
        listOf(
            AccessRules(
                id = "rule_ironforge",
                gymId = "gym_ironforge_tokyo",
                eligiblePlanIds = listOf("plan_global_unlimited"),
                maxVisitsPerDay = 1,
                maxVisitsPerMonthPerMember = 16,
                bookingRequired = false
            ),
            AccessRules(
                id = "rule_kanto",
                gymId = "gym_kanto_tokyo",
                eligiblePlanIds = listOf("plan_global_unlimited", "plan_city_flex", "plan_roamer"),
                maxVisitsPerDay = 1,
                maxVisitsPerMonthPerMember = 20,
                bookingRequired = false
            )
        )
    )
    val accessRules: StateFlow<List<AccessRules>> = _accessRules.asStateFlow()

    // Visits (including recent 30-day network distribution and anomaly test scenarios)
    private val _visits = MutableStateFlow<List<Visit>>(
        listOf(
            Visit(
                id = "vis_001",
                userId = "usr_member_alex",
                userName = "Alex Vance",
                gymId = "gym_ironforge_tokyo",
                gymName = "IronForge Athletic Club",
                gymCity = "Tokyo",
                subscriptionId = "sub_alex_001",
                checkInTimestamp = System.currentTimeMillis() - 3600000L * 2,
                credentialCode = "NF-849-201",
                validationResult = ValidationResult.APPROVED,
                payoutAmount = 14.50,
                payoutStatus = SettlementStatus.PENDING
            ),
            Visit(
                id = "vis_002",
                userId = "usr_member_elena",
                userName = "Elena Rostova",
                gymId = "gym_foundry_london",
                gymName = "The Foundry Shoreditch",
                gymCity = "London",
                subscriptionId = "sub_elena_002",
                checkInTimestamp = System.currentTimeMillis() - 3600000L * 5,
                credentialCode = "NF-319-482",
                validationResult = ValidationResult.APPROVED,
                payoutAmount = 15.00,
                payoutStatus = SettlementStatus.PENDING
            ),
            Visit(
                id = "vis_003",
                userId = "usr_member_david",
                userName = "David Chen",
                gymId = "gym_marina_singapore",
                gymName = "Marina Bay Athletics",
                gymCity = "Singapore",
                subscriptionId = "sub_david_003",
                checkInTimestamp = System.currentTimeMillis() - 3600000L * 12,
                credentialCode = "NF-992-108",
                validationResult = ValidationResult.APPROVED,
                payoutAmount = 16.00,
                payoutStatus = SettlementStatus.PENDING
            ),
            // Anomaly Scenario 1: Impossible Travel window (Liam checked in Tokyo then London in 2 hours)
            Visit(
                id = "vis_anomaly_travel_1",
                userId = "usr_member_liam",
                userName = "Liam O'Connor",
                gymId = "gym_kanto_tokyo",
                gymName = "Kanto Powerhouse",
                gymCity = "Tokyo",
                subscriptionId = "sub_liam_004",
                checkInTimestamp = System.currentTimeMillis() - 3600000L * 3,
                credentialCode = "NF-441-209",
                validationResult = ValidationResult.APPROVED,
                payoutAmount = 14.50,
                payoutStatus = SettlementStatus.PENDING
            ),
            Visit(
                id = "vis_anomaly_travel_2",
                userId = "usr_member_liam",
                userName = "Liam O'Connor",
                gymId = "gym_foundry_london",
                gymName = "The Foundry Shoreditch",
                gymCity = "London",
                subscriptionId = "sub_liam_004",
                checkInTimestamp = System.currentTimeMillis() - 3600000L * 1,
                credentialCode = "NF-441-209",
                validationResult = ValidationResult.APPROVED,
                payoutAmount = 15.00,
                payoutStatus = SettlementStatus.PENDING
            ),
            // Anomaly Scenario 2: Repeated Terminal Denials (Chloe Bennett 3 attempts in 4 minutes)
            Visit(
                id = "vis_anomaly_fail_1",
                userId = "usr_member_chloe",
                userName = "Chloe Bennett",
                gymId = "gym_manhattan_ny",
                gymName = "Manhattan Steelworks",
                gymCity = "New York",
                subscriptionId = "sub_chloe_005",
                checkInTimestamp = System.currentTimeMillis() - 60000L * 14,
                credentialCode = "NF-000-111",
                validationResult = ValidationResult.DENIED,
                denialReason = "Invalid or expired credential code",
                payoutAmount = 0.0,
                payoutStatus = SettlementStatus.PENDING
            ),
            Visit(
                id = "vis_anomaly_fail_2",
                userId = "usr_member_chloe",
                userName = "Chloe Bennett",
                gymId = "gym_manhattan_ny",
                gymName = "Manhattan Steelworks",
                gymCity = "New York",
                subscriptionId = "sub_chloe_005",
                checkInTimestamp = System.currentTimeMillis() - 60000L * 12,
                credentialCode = "NF-000-111",
                validationResult = ValidationResult.DENIED,
                denialReason = "Invalid or expired credential code",
                payoutAmount = 0.0,
                payoutStatus = SettlementStatus.PENDING
            ),
            Visit(
                id = "vis_anomaly_fail_3",
                userId = "usr_member_chloe",
                userName = "Chloe Bennett",
                gymId = "gym_manhattan_ny",
                gymName = "Manhattan Steelworks",
                gymCity = "New York",
                subscriptionId = "sub_chloe_005",
                checkInTimestamp = System.currentTimeMillis() - 60000L * 10,
                credentialCode = "NF-000-111",
                validationResult = ValidationResult.DENIED,
                denialReason = "Invalid or expired credential code",
                payoutAmount = 0.0,
                payoutStatus = SettlementStatus.PENDING
            ),
            Visit(
                id = "vis_004",
                userId = "usr_member_kenji",
                userName = "Kenji Sato",
                gymId = "gym_zenith_tokyo",
                gymName = "Zenith Roppongi Lab",
                gymCity = "Tokyo",
                subscriptionId = "sub_kenji_006",
                checkInTimestamp = System.currentTimeMillis() - 86400000L * 2,
                credentialCode = "NF-712-409",
                validationResult = ValidationResult.APPROVED,
                payoutAmount = 16.00,
                payoutStatus = SettlementStatus.PAID
            ),
            Visit(
                id = "vis_005",
                userId = "usr_member_alex",
                userName = "Alex Vance",
                gymId = "gym_kanto_tokyo",
                gymName = "Kanto Powerhouse",
                gymCity = "Tokyo",
                subscriptionId = "sub_alex_001",
                checkInTimestamp = System.currentTimeMillis() - 86400000L * 4,
                credentialCode = "NF-849-201",
                validationResult = ValidationResult.APPROVED,
                payoutAmount = 14.50,
                payoutStatus = SettlementStatus.PAID
            ),
            Visit(
                id = "vis_006",
                userId = "usr_member_elena",
                userName = "Elena Rostova",
                gymId = "gym_canary_london",
                gymName = "Canary Wharf Health Club",
                gymCity = "London",
                subscriptionId = "sub_elena_002",
                checkInTimestamp = System.currentTimeMillis() - 86400000L * 6,
                credentialCode = "NF-319-482",
                validationResult = ValidationResult.APPROVED,
                payoutAmount = 15.00,
                payoutStatus = SettlementStatus.PAID
            ),
            Visit(
                id = "vis_007",
                userId = "usr_member_david",
                userName = "David Chen",
                gymId = "gym_kreuzberg_berlin",
                gymName = "Kreuzberg Strength Hub",
                gymCity = "Berlin",
                subscriptionId = "sub_david_003",
                checkInTimestamp = System.currentTimeMillis() - 86400000L * 9,
                credentialCode = "NF-992-108",
                validationResult = ValidationResult.APPROVED,
                payoutAmount = 14.00,
                payoutStatus = SettlementStatus.PAID
            ),
            Visit(
                id = "vis_008",
                userId = "usr_member_liam",
                userName = "Liam O'Connor",
                gymId = "gym_brooklyn_ny",
                gymName = "Brooklyn Barbell Club",
                gymCity = "New York",
                subscriptionId = "sub_liam_004",
                checkInTimestamp = System.currentTimeMillis() - 86400000L * 12,
                credentialCode = "NF-441-209",
                validationResult = ValidationResult.APPROVED,
                payoutAmount = 14.50,
                payoutStatus = SettlementStatus.PAID
            )
        )
    )
    val visits: StateFlow<List<Visit>> = _visits.asStateFlow()

    // Flagged Anomalies
    private val _anomalies = MutableStateFlow<List<CheckInAnomaly>>(
        listOf(
            CheckInAnomaly(
                visitId = "vis_anomaly_travel_2",
                type = AnomalyType.IMPOSSIBLE_TRAVEL,
                details = "User Liam O'Connor checked in at Kanto Powerhouse (Tokyo) and The Foundry (London) within 2.0 hours (implausible flight window > 11h).",
                detectedAt = System.currentTimeMillis() - 3600000L * 1,
                isResolved = false
            ),
            CheckInAnomaly(
                visitId = "vis_anomaly_fail_3",
                type = AnomalyType.REPEATED_FAILED_ACCESS,
                details = "User Chloe Bennett triggered 3 failed credential checks within 4 minutes at Manhattan Steelworks terminal.",
                detectedAt = System.currentTimeMillis() - 60000L * 10,
                isResolved = false
            )
        )
    )
    val anomalies: StateFlow<List<CheckInAnomaly>> = _anomalies.asStateFlow()

    // Payments
    private val _payments = MutableStateFlow<List<Payment>>(
        listOf(
            Payment(
                id = "pay_001",
                userId = "usr_member_alex",
                subscriptionId = "sub_alex_001",
                amount = 129.0,
                currency = "USD",
                status = PaymentStatus.SUCCEEDED,
                createdAt = System.currentTimeMillis() - 86400000L * 18,
                cardLast4 = "4242",
                description = "Nomad Global All-Access Monthly"
            ),
            Payment(
                id = "pay_002",
                userId = "usr_member_elena",
                subscriptionId = "sub_elena_002",
                amount = 69.0,
                currency = "USD",
                status = PaymentStatus.SUCCEEDED,
                createdAt = System.currentTimeMillis() - 86400000L * 10,
                cardLast4 = "9102",
                description = "Nomad Flex Pass Monthly"
            ),
            Payment(
                id = "pay_003",
                userId = "usr_member_david",
                subscriptionId = "sub_david_003",
                amount = 129.0,
                currency = "USD",
                status = PaymentStatus.SUCCEEDED,
                createdAt = System.currentTimeMillis() - 86400000L * 5,
                cardLast4 = "3319",
                description = "Nomad Global All-Access Monthly"
            ),
            Payment(
                id = "pay_004",
                userId = "usr_member_liam",
                subscriptionId = "sub_liam_004",
                amount = 129.0,
                currency = "USD",
                status = PaymentStatus.SUCCEEDED,
                createdAt = System.currentTimeMillis() - 86400000L * 3,
                cardLast4 = "1184",
                description = "Nomad Global All-Access Monthly"
            ),
            Payment(
                id = "pay_005",
                userId = "usr_member_chloe",
                subscriptionId = "sub_chloe_005",
                amount = 39.0,
                currency = "USD",
                status = PaymentStatus.REFUNDED,
                createdAt = System.currentTimeMillis() - 86400000L * 22,
                cardLast4 = "7721",
                description = "Nomad Roamer Starter Pass (Refunded on cancellation request)"
            ),
            Payment(
                id = "pay_006",
                userId = "usr_member_kenji",
                subscriptionId = "sub_kenji_006",
                amount = 69.0,
                currency = "USD",
                status = PaymentStatus.SUCCEEDED,
                createdAt = System.currentTimeMillis() - 86400000L * 35,
                cardLast4 = "5501",
                description = "Nomad Flex Pass Monthly"
            )
        )
    )
    val payments: StateFlow<List<Payment>> = _payments.asStateFlow()

    // Settlements
    private val _settlements = MutableStateFlow<List<Settlement>>(
        listOf(
            Settlement(
                id = "set_ironforge_aug",
                gymId = "gym_ironforge_tokyo",
                gymName = "IronForge Athletic Club",
                periodStart = System.currentTimeMillis() - 86400000L * 30,
                periodEnd = System.currentTimeMillis(),
                totalVisits = 142,
                totalAmount = 2059.0,
                status = SettlementStatus.PENDING,
                paidAt = null
            ),
            Settlement(
                id = "set_foundry_aug",
                gymId = "gym_foundry_london",
                gymName = "The Foundry Shoreditch",
                periodStart = System.currentTimeMillis() - 86400000L * 30,
                periodEnd = System.currentTimeMillis(),
                totalVisits = 184,
                totalAmount = 2760.0,
                status = SettlementStatus.PENDING,
                paidAt = null
            ),
            Settlement(
                id = "set_manhattan_aug",
                gymId = "gym_manhattan_ny",
                gymName = "Manhattan Steelworks",
                periodStart = System.currentTimeMillis() - 86400000L * 30,
                periodEnd = System.currentTimeMillis(),
                totalVisits = 230,
                totalAmount = 3680.0,
                status = SettlementStatus.PENDING,
                paidAt = null
            ),
            Settlement(
                id = "set_marina_aug",
                gymId = "gym_marina_singapore",
                gymName = "Marina Bay Athletics",
                periodStart = System.currentTimeMillis() - 86400000L * 30,
                periodEnd = System.currentTimeMillis(),
                totalVisits = 160,
                totalAmount = 2560.0,
                status = SettlementStatus.PENDING,
                paidAt = null
            ),
            Settlement(
                id = "set_ironforge_jul",
                gymId = "gym_ironforge_tokyo",
                gymName = "IronForge Athletic Club",
                periodStart = System.currentTimeMillis() - 86400000L * 60,
                periodEnd = System.currentTimeMillis() - 86400000L * 30,
                totalVisits = 128,
                totalAmount = 1856.0,
                status = SettlementStatus.PAID,
                paidAt = System.currentTimeMillis() - 86400000L * 28
            ),
            Settlement(
                id = "set_foundry_jul",
                gymId = "gym_foundry_london",
                gymName = "The Foundry Shoreditch",
                periodStart = System.currentTimeMillis() - 86400000L * 60,
                periodEnd = System.currentTimeMillis() - 86400000L * 30,
                totalVisits = 165,
                totalAmount = 2475.0,
                status = SettlementStatus.PAID,
                paidAt = System.currentTimeMillis() - 86400000L * 28
            )
        )
    )
    val settlements: StateFlow<List<Settlement>> = _settlements.asStateFlow()

    // Support Tickets & Disputes (including older than 48 hrs for "Needs Attention" queue)
    private val _supportTickets = MutableStateFlow<List<SupportTicket>>(
        listOf(
            SupportTicket(
                id = "tkt_001",
                raisedByUserId = "usr_member_alex",
                raisedByUserName = "Alex Vance",
                raisedByRole = UserRole.MEMBER,
                category = "Check-in Credential",
                subject = "Code sync issue at Kanto Powerhouse",
                description = "Front desk terminal took 2 attempts to validate my 6-digit credential code.",
                status = TicketStatus.RESOLVED,
                relatedVisitId = "vis_002",
                assignedTo = "Marcus Drake",
                resolutionNote = "Synced Redis cache layer for Shibuya gateway. Credential TTL updated to 60s.",
                resolvedAt = System.currentTimeMillis() - 86400000L * 1,
                createdAt = System.currentTimeMillis() - 86400000L * 3
            ),
            SupportTicket(
                id = "tkt_002",
                raisedByUserId = "usr_partner_sarah",
                raisedByUserName = "Sarah Connor (IronForge)",
                raisedByRole = UserRole.GYM_OWNER,
                category = "Settlement Payout",
                subject = "Direct deposit bank update request",
                description = "Updated Stripe routing code for August settlement transfer. Needs admin review and authorization.",
                status = TicketStatus.OPEN,
                relatedVisitId = null,
                assignedTo = "Marcus Drake",
                createdAt = System.currentTimeMillis() - 86400000L * 3 // >48h old dispute for Needs Attention!
            ),
            SupportTicket(
                id = "tkt_003",
                raisedByUserId = "usr_member_chloe",
                raisedByUserName = "Chloe Bennett",
                raisedByRole = UserRole.MEMBER,
                category = "Access Issue",
                subject = "Repeated check-in denial at Manhattan Steelworks",
                description = "Terminal denied access 3 times claiming inactive plan while traveling in NYC.",
                status = TicketStatus.OPEN,
                relatedVisitId = "vis_anomaly_fail_3",
                assignedTo = null,
                createdAt = System.currentTimeMillis() - 86400000L * 1
            ),
            SupportTicket(
                id = "tkt_004",
                raisedByUserId = "usr_partner_london1",
                raisedByUserName = "Arthur Pendelton (The Foundry)",
                raisedByRole = UserRole.GYM_OWNER,
                category = "Dispute",
                subject = "Disputed payout rate for premium sauna guest access",
                description = "Member Alex Vance visited during recovery sauna premium hours; requesting tier surcharge adjustment.",
                status = TicketStatus.OPEN,
                relatedVisitId = "vis_003",
                assignedTo = null,
                createdAt = System.currentTimeMillis() - 86400000L * 4 // >48h old dispute!
            )
        )
    )
    val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets.asStateFlow()

    // Promo Codes
    private val _promoCodes = MutableStateFlow<List<PromoCode>>(
        listOf(
            PromoCode(
                id = "promo_nomad2026",
                code = "NOMAD2026",
                discountType = "percent",
                value = 20.0,
                usageCount = 142,
                maxUsage = 500,
                expiryTimestamp = System.currentTimeMillis() + 86400000L * 120,
                isActive = true
            ),
            PromoCode(
                id = "promo_global30",
                code = "GLOBAL30",
                discountType = "fixed",
                value = 30.0,
                usageCount = 88,
                maxUsage = 200,
                expiryTimestamp = System.currentTimeMillis() + 86400000L * 60,
                isActive = true
            ),
            PromoCode(
                id = "promo_startpass",
                code = "STARTPASS",
                discountType = "fixed",
                value = 15.0,
                usageCount = 210,
                maxUsage = null,
                expiryTimestamp = System.currentTimeMillis() + 86400000L * 45,
                isActive = true
            ),
            PromoCode(
                id = "promo_founder50",
                code = "FOUNDER50",
                discountType = "percent",
                value = 50.0,
                usageCount = 25,
                maxUsage = 25,
                expiryTimestamp = System.currentTimeMillis() - 86400000L * 5,
                isActive = false
            )
        )
    )
    val promoCodes: StateFlow<List<PromoCode>> = _promoCodes.asStateFlow()

    // Audit Trail Log Entries
    private val _auditLogs = MutableStateFlow<List<AuditLogEntry>>(
        listOf(
            AuditLogEntry(
                id = "aud_001",
                actorAdminId = "usr_admin_marcus",
                actorAdminName = "Marcus Drake",
                targetType = "USER",
                targetId = "usr_member_chloe",
                targetName = "Chloe Bennett",
                action = "SUSPEND_USER",
                reason = "Suspended pending review for repeated terminal brute force credential attempts.",
                timestamp = System.currentTimeMillis() - 86400000L * 2
            ),
            AuditLogEntry(
                id = "aud_002",
                actorAdminId = "usr_admin_marcus",
                actorAdminName = "Marcus Drake",
                targetType = "PAYMENT",
                targetId = "pay_005",
                targetName = "Chloe Bennett ($39.00)",
                action = "REFUND",
                reason = "Authorized grace period subscription refund upon member support request.",
                timestamp = System.currentTimeMillis() - 86400000L * 22
            ),
            AuditLogEntry(
                id = "aud_003",
                actorAdminId = "usr_admin_marcus",
                actorAdminName = "Marcus Drake",
                targetType = "TICKET",
                targetId = "tkt_001",
                targetName = "Check-in Credential #tkt_001",
                action = "RESOLVE_TICKET",
                reason = "Resolved gateway latency with terminal Redis sync.",
                timestamp = System.currentTimeMillis() - 86400000L * 1
            )
        )
    )
    val auditLogs: StateFlow<List<AuditLogEntry>> = _auditLogs.asStateFlow()


    // Notifications
    private val _notifications = MutableStateFlow<List<NotificationItem>>(
        listOf(
            NotificationItem(
                id = "notif_001",
                userId = "usr_member_alex",
                type = "checkin",
                title = "Check-in Approved",
                body = "Welcome to IronForge Athletic Club! Enjoy your workout.",
                read = false,
                targetRoute = "activity",
                createdAt = System.currentTimeMillis() - 86400000L * 1
            ),
            NotificationItem(
                id = "notif_002",
                userId = "usr_member_alex",
                type = "billing",
                title = "Monthly Renewal Scheduled",
                body = "Your Nomad Global Pass will renew in 12 days for $129.00.",
                read = true,
                targetRoute = "membership",
                createdAt = System.currentTimeMillis() - 86400000L * 3
            )
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Onboarding State
    private val _hasCompletedOnboarding = MutableStateFlow(true)
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    fun completeOnboarding() {
        _hasCompletedOnboarding.value = true
    }

    fun resetOnboarding() {
        _hasCompletedOnboarding.value = false
    }

    // Notification Preferences
    private val _notificationPreferences = MutableStateFlow(NotificationPreferences())
    val notificationPreferences: StateFlow<NotificationPreferences> = _notificationPreferences.asStateFlow()

    fun updateNotificationPreferences(prefs: NotificationPreferences) {
        _notificationPreferences.value = prefs
    }

    // City Lead Captures (Expansion Request)
    private val _cityLeadCaptures = MutableStateFlow<List<CityLeadCapture>>(emptyList())
    val cityLeadCaptures: StateFlow<List<CityLeadCapture>> = _cityLeadCaptures.asStateFlow()

    fun submitCityLeadCapture(city: String, email: String): Boolean {
        if (email.isBlank() || !email.contains("@")) return false
        _cityLeadCaptures.value = _cityLeadCaptures.value + CityLeadCapture(city = city, email = email)
        return true
    }

    fun markNotificationRead(notifId: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == notifId) it.copy(read = true) else it
        }
    }

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(read = true) }
    }

    fun updateProfile(fullName: String, email: String, phone: String, homeCity: String) {
        val current = _currentUser.value
        _currentUser.value = current.copy(
            fullName = fullName.trim().ifEmpty { current.fullName },
            email = email.trim().ifEmpty { current.email },
            phone = phone.trim().ifEmpty { current.phone },
            homeCity = homeCity.trim().ifEmpty { current.homeCity }
        )
    }

    fun pauseSubscription(userId: String) {
        _subscriptions.value = _subscriptions.value.map {
            if (it.userId == userId) it.copy(status = SubscriptionStatus.PAUSED) else it
        }
        val notif = NotificationItem(
            id = "notif_${UUID.randomUUID().toString().take(8)}",
            userId = userId,
            type = "billing",
            title = "Membership Paused",
            body = "Your membership has been paused. Billing and access are on hold until resumed.",
            read = false,
            targetRoute = "membership"
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    fun resumeSubscription(userId: String) {
        _subscriptions.value = _subscriptions.value.map {
            if (it.userId == userId) it.copy(status = SubscriptionStatus.ACTIVE) else it
        }
        val notif = NotificationItem(
            id = "notif_${UUID.randomUUID().toString().take(8)}",
            userId = userId,
            type = "billing",
            title = "Membership Resumed",
            body = "Your membership is active again! You can check in at all network gyms.",
            read = false,
            targetRoute = "membership"
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    fun cancelSubscription(userId: String) {
        _subscriptions.value = _subscriptions.value.map {
            if (it.userId == userId) it.copy(status = SubscriptionStatus.CANCELLED) else it
        }
        val notif = NotificationItem(
            id = "notif_${UUID.randomUUID().toString().take(8)}",
            userId = userId,
            type = "billing",
            title = "Membership Cancelled",
            body = "Your subscription has been cancelled. Access continues through the end of the current cycle.",
            read = false,
            targetRoute = "membership"
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    // Current Member Active Credential Code
    private val _activeCredentialCode = MutableStateFlow("NF-849-201")
    val activeCredentialCode: StateFlow<String> = _activeCredentialCode.asStateFlow()

    // Rotate/Regenerate Credential Code
    fun refreshCredentialCode(): String {
        val rand = (100000..999999).random()
        val formatted = "NF-${rand.toString().substring(0, 3)}-${rand.toString().substring(3)}"
        _activeCredentialCode.value = formatted
        return formatted
    }

    // Role Switcher Actions
    fun switchRole(role: UserRole) {
        val newUser = when (role) {
            UserRole.MEMBER -> memberUser
            UserRole.GYM_OWNER -> partnerUser
            UserRole.ADMIN -> adminUser
        }
        _currentUser.value = newUser
    }

    fun loginAs(email: String, role: UserRole) {
        val user = User(
            uid = "usr_${UUID.randomUUID().toString().take(8)}",
            fullName = email.substringBefore("@").replace(".", " ").capitalizeWords(),
            email = email,
            phone = "+1 (555) 000-0000",
            photoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
            role = role,
            homeCity = "Tokyo",
            status = AccountStatus.ACTIVE,
            createdAt = System.currentTimeMillis()
        )
        _currentUser.value = user
    }

    fun logout() {
        _currentUser.value = memberUser
    }

    // Member Subscription Helpers
    fun getMemberSubscription(userId: String): Subscription? {
        return _subscriptions.value.find { it.userId == userId }
    }

    fun getPlan(planId: String): MembershipPlan? {
        return _plans.value.find { it.id == planId }
    }

    // Process Simulated Card Payment
    suspend fun processCardPayment(
        cardNumber: String,
        plan: MembershipPlan,
        userId: String
    ): Result<Payment> {
        delay(1200) // Realistic network delay simulation
        val cleanCard = cardNumber.replace(" ", "").replace("-", "")

        // Failure convention: cards starting with 4000 fail
        if (cleanCard.startsWith("4000")) {
            val failedPayment = Payment(
                id = "pay_${UUID.randomUUID().toString().take(8)}",
                userId = userId,
                subscriptionId = "sub_${UUID.randomUUID().toString().take(8)}",
                amount = plan.price,
                currency = plan.currency,
                status = PaymentStatus.FAILED,
                createdAt = System.currentTimeMillis(),
                cardLast4 = cleanCard.takeLast(4).ifEmpty { "4000" },
                description = "${plan.name} Subscription"
            )
            _payments.value = listOf(failedPayment) + _payments.value
            return Result.failure(Exception("Payment declined by issuing bank (Test card 4000 failure simulation). Please check card details or use a valid card."))
        }

        val last4 = cleanCard.takeLast(4).ifEmpty { "4242" }
        val subId = "sub_${UUID.randomUUID().toString().take(8)}"
        val newPayment = Payment(
            id = "pay_${UUID.randomUUID().toString().take(8)}",
            userId = userId,
            subscriptionId = subId,
            amount = plan.price,
            currency = plan.currency,
            status = PaymentStatus.SUCCEEDED,
            createdAt = System.currentTimeMillis(),
            cardLast4 = last4,
            description = "${plan.name} Subscription"
        )
        _payments.value = listOf(newPayment) + _payments.value

        // Create or update subscription
        val newSub = Subscription(
            id = subId,
            userId = userId,
            planId = plan.id,
            status = SubscriptionStatus.ACTIVE,
            startDate = System.currentTimeMillis(),
            currentPeriodEnd = System.currentTimeMillis() + 86400000L * 30,
            renewalDate = System.currentTimeMillis() + 86400000L * 30,
            visitsUsedThisCycle = 0,
            visitsAllowance = plan.visitAllowance,
            paymentMethodLast4 = last4
        )
        _subscriptions.value = _subscriptions.value.filterNot { it.userId == userId } + newSub

        // Add confirmation notification
        val notif = NotificationItem(
            id = "notif_${UUID.randomUUID().toString().take(8)}",
            userId = userId,
            type = "billing",
            title = "Subscription Activated",
            body = "Your ${plan.name} is now active. Access partner gyms immediately!",
            read = false
        )
        _notifications.value = listOf(notif) + _notifications.value

        return Result.success(newPayment)
    }

    // Gym Partner Validation Flow
    suspend fun validateCheckIn(
        rawCode: String,
        gymId: String
    ): Pair<ValidationResult, String> {
        delay(800) // Simulated terminal verification delay
        val cleanedCode = rawCode.trim().uppercase().replace(" ", "").replace("-", "")

        val targetGym = _gyms.value.find { it.id == gymId } ?: _gyms.value.first()
        val currentMember = memberUser
        val activeSub = _subscriptions.value.find { it.userId == currentMember.uid }

        // Code matching simulation:
        val cleanActiveCode = _activeCredentialCode.value.replace("-", "").uppercase()
        val isMatchingCode = cleanedCode == cleanActiveCode || cleanedCode.contains(cleanActiveCode.takeLast(6)) || cleanedCode.length == 6

        if (!isMatchingCode) {
            val deniedVisit = Visit(
                id = "vis_${UUID.randomUUID().toString().take(8)}",
                userId = currentMember.uid,
                userName = currentMember.fullName,
                gymId = targetGym.id,
                gymName = targetGym.name,
                gymCity = targetGym.city,
                subscriptionId = activeSub?.id ?: "unknown",
                credentialCode = rawCode,
                validationResult = ValidationResult.DENIED,
                denialReason = "Invalid or expired credential code",
                payoutAmount = 0.0,
                payoutStatus = SettlementStatus.PENDING
            )
            _visits.value = listOf(deniedVisit) + _visits.value
            return Pair(ValidationResult.DENIED, "Invalid or expired credential code.")
        }

        if (activeSub == null || activeSub.status != SubscriptionStatus.ACTIVE) {
            val reason = if (activeSub == null) "No active subscription found" else "Subscription status is ${activeSub.status.label}"
            val deniedVisit = Visit(
                id = "vis_${UUID.randomUUID().toString().take(8)}",
                userId = currentMember.uid,
                userName = currentMember.fullName,
                gymId = targetGym.id,
                gymName = targetGym.name,
                gymCity = targetGym.city,
                subscriptionId = activeSub?.id ?: "none",
                credentialCode = rawCode,
                validationResult = ValidationResult.DENIED,
                denialReason = reason,
                payoutAmount = 0.0,
                payoutStatus = SettlementStatus.PENDING
            )
            _visits.value = listOf(deniedVisit) + _visits.value
            return Pair(ValidationResult.DENIED, reason)
        }

        val plan = _plans.value.find { it.id == activeSub.planId }
        if (targetGym.tier == GymTier.PREMIUM && plan != null && !plan.eligibleGymTiers.contains("premium")) {
            val reason = "Plan '${plan.name}' is not eligible for Premium tier gyms."
            val deniedVisit = Visit(
                id = "vis_${UUID.randomUUID().toString().take(8)}",
                userId = currentMember.uid,
                userName = currentMember.fullName,
                gymId = targetGym.id,
                gymName = targetGym.name,
                gymCity = targetGym.city,
                subscriptionId = activeSub.id,
                credentialCode = rawCode,
                validationResult = ValidationResult.DENIED,
                denialReason = reason,
                payoutAmount = 0.0,
                payoutStatus = SettlementStatus.PENDING
            )
            _visits.value = listOf(deniedVisit) + _visits.value
            return Pair(ValidationResult.DENIED, reason)
        }

        if (!activeSub.isUnlimited && activeSub.visitsRemaining <= 0) {
            val reason = "Monthly cycle visit allowance reached (0 passes left)."
            val deniedVisit = Visit(
                id = "vis_${UUID.randomUUID().toString().take(8)}",
                userId = currentMember.uid,
                userName = currentMember.fullName,
                gymId = targetGym.id,
                gymName = targetGym.name,
                gymCity = targetGym.city,
                subscriptionId = activeSub.id,
                credentialCode = rawCode,
                validationResult = ValidationResult.DENIED,
                denialReason = reason,
                payoutAmount = 0.0,
                payoutStatus = SettlementStatus.PENDING
            )
            _visits.value = listOf(deniedVisit) + _visits.value
            return Pair(ValidationResult.DENIED, reason)
        }

        // Check-in Approved!
        val approvedVisit = Visit(
            id = "vis_${UUID.randomUUID().toString().take(8)}",
            userId = currentMember.uid,
            userName = currentMember.fullName,
            gymId = targetGym.id,
            gymName = targetGym.name,
            gymCity = targetGym.city,
            subscriptionId = activeSub.id,
            checkInTimestamp = System.currentTimeMillis(),
            credentialCode = _activeCredentialCode.value,
            validationResult = ValidationResult.APPROVED,
            denialReason = null,
            payoutAmount = 14.50,
            payoutStatus = SettlementStatus.PENDING
        )
        _visits.value = listOf(approvedVisit) + _visits.value

        // Update Gym check-in count
        _gyms.value = _gyms.value.map {
            if (it.id == targetGym.id) it.copy(checkInCount = it.checkInCount + 1) else it
        }

        // Update Member subscription visits
        _subscriptions.value = _subscriptions.value.map {
            if (it.id == activeSub.id) it.copy(visitsUsedThisCycle = it.visitsUsedThisCycle + 1) else it
        }

        // Add Member Notification
        val notif = NotificationItem(
            id = "notif_${UUID.randomUUID().toString().take(8)}",
            userId = currentMember.uid,
            type = "checkin",
            title = "Check-in Confirmed",
            body = "You are checked in at ${targetGym.name}.",
            read = false
        )
        _notifications.value = listOf(notif) + _notifications.value

        // Rotate credential code for next time
        refreshCredentialCode()

        return Pair(ValidationResult.APPROVED, "Check-in approved for ${currentMember.fullName} (${plan?.name ?: "All-Access"}).")
    }

    // Admin Actions
    fun updatePartnerKyc(partnerId: String, newStatus: KycStatus) {
        _partners.value = _partners.value.map {
            if (it.id == partnerId) it.copy(kycStatus = newStatus) else it
        }
    }

    fun toggleGymStatus(gymId: String, newStatus: GymStatus) {
        _gyms.value = _gyms.value.map {
            if (it.id == gymId) it.copy(status = newStatus) else it
        }
    }

    fun resolveTicket(ticketId: String) {
        _supportTickets.value = _supportTickets.value.map {
            if (it.id == ticketId) it.copy(status = TicketStatus.RESOLVED) else it
        }
    }

    fun settlePayout(settlementId: String) {
        _settlements.value = _settlements.value.map {
            if (it.id == settlementId) it.copy(status = SettlementStatus.PAID, paidAt = System.currentTimeMillis()) else it
        }
    }

    fun togglePlanActive(planId: String) {
        _plans.value = _plans.value.map {
            if (it.id == planId) it.copy(isActive = !it.isActive) else it
        }
    }

    fun updateGymProfile(
        gymId: String,
        name: String,
        description: String,
        address: String,
        lat: Double,
        lng: Double,
        facilities: List<String>,
        operatingHours: String,
        photos: List<String>
    ) {
        _gyms.value = _gyms.value.map {
            if (it.id == gymId) {
                it.copy(
                    name = name,
                    description = description,
                    address = address,
                    lat = lat,
                    lng = lng,
                    facilities = facilities,
                    operatingHours = operatingHours,
                    photos = photos
                )
            } else it
        }
    }

    fun setGymTemporarilyDisabled(gymId: String, disabled: Boolean) {
        _gyms.value = _gyms.value.map {
            if (it.id == gymId) {
                it.copy(status = if (disabled) GymStatus.TEMPORARILY_CLOSED else GymStatus.ACTIVE)
            } else it
        }
    }

    fun updateAccessRules(
        gymId: String,
        eligiblePlanIds: List<String>,
        maxVisitsPerDay: Int,
        maxVisitsPerMonth: Int,
        bookingRequired: Boolean
    ) {
        val existingIndex = _accessRules.value.indexOfFirst { it.gymId == gymId }
        if (existingIndex >= 0) {
            val updated = _accessRules.value[existingIndex].copy(
                eligiblePlanIds = eligiblePlanIds,
                maxVisitsPerDay = maxVisitsPerDay,
                maxVisitsPerMonthPerMember = maxVisitsPerMonth,
                bookingRequired = bookingRequired
            )
            _accessRules.value = _accessRules.value.toMutableList().also { it[existingIndex] = updated }
        } else {
            val newRule = AccessRules(
                id = "rule_${UUID.randomUUID().toString().take(8)}",
                gymId = gymId,
                eligiblePlanIds = eligiblePlanIds,
                maxVisitsPerDay = maxVisitsPerDay,
                maxVisitsPerMonthPerMember = maxVisitsPerMonth,
                bookingRequired = bookingRequired
            )
            _accessRules.value = _accessRules.value + newRule
        }
    }

    fun createSupportTicket(category: String, subject: String, description: String) {
        val ticket = SupportTicket(
            id = "tkt_${UUID.randomUUID().toString().take(8)}",
            raisedByUserId = _currentUser.value.uid,
            raisedByUserName = _currentUser.value.fullName,
            raisedByRole = _currentUser.value.role,
            category = category,
            subject = subject,
            description = description,
            status = TicketStatus.OPEN,
            createdAt = System.currentTimeMillis()
        )
        _supportTickets.value = listOf(ticket) + _supportTickets.value
    }

    // ==========================================
    // ADMIN OPERATIONS & AUDIT TRAIL
    // ==========================================

    private fun logAudit(targetType: String, targetId: String, targetName: String, action: String, reason: String) {
        val entry = AuditLogEntry(
            id = "aud_${UUID.randomUUID().toString().take(8)}",
            actorAdminId = _currentUser.value.uid,
            actorAdminName = _currentUser.value.fullName.ifEmpty { "Admin Operations" },
            targetType = targetType,
            targetId = targetId,
            targetName = targetName,
            action = action,
            reason = reason,
            timestamp = System.currentTimeMillis()
        )
        _auditLogs.value = listOf(entry) + _auditLogs.value
    }

    fun suspendUser(userId: String, reason: String) {
        val user = _allUsers.value.find { it.uid == userId }
        _allUsers.value = _allUsers.value.map {
            if (it.uid == userId) it.copy(status = AccountStatus.SUSPENDED) else it
        }
        logAudit(
            targetType = "USER",
            targetId = userId,
            targetName = user?.fullName ?: userId,
            action = "SUSPEND_USER",
            reason = reason.ifEmpty { "Administrative suspension" }
        )
    }

    fun reactivateUser(userId: String, reason: String) {
        val user = _allUsers.value.find { it.uid == userId }
        _allUsers.value = _allUsers.value.map {
            if (it.uid == userId) it.copy(status = AccountStatus.ACTIVE) else it
        }
        logAudit(
            targetType = "USER",
            targetId = userId,
            targetName = user?.fullName ?: userId,
            action = "REACTIVATE_USER",
            reason = reason.ifEmpty { "Account restored to good standing" }
        )
    }

    fun resetUserPassword(userId: String, reason: String): String {
        val user = _allUsers.value.find { it.uid == userId }
        val tempKey = "NF-RST-${(1000..9999).random()}"
        logAudit(
            targetType = "USER",
            targetId = userId,
            targetName = user?.fullName ?: userId,
            action = "RESET_PASSWORD",
            reason = reason.ifEmpty { "Temporary security access credential dispatched" }
        )
        return tempKey
    }

    fun approveGymPartnerKyc(partnerId: String) {
        val partner = _partners.value.find { it.id == partnerId }
        _partners.value = _partners.value.map {
            if (it.id == partnerId) it.copy(kycStatus = KycStatus.VERIFIED) else it
        }
        logAudit(
            targetType = "PARTNER",
            targetId = partnerId,
            targetName = partner?.businessName ?: partnerId,
            action = "KYC_APPROVE",
            reason = "Business registration, AML verification and banking KYC approved."
        )
    }

    fun rejectGymPartnerKyc(partnerId: String, reason: String) {
        val partner = _partners.value.find { it.id == partnerId }
        _partners.value = _partners.value.map {
            if (it.id == partnerId) it.copy(kycStatus = KycStatus.REJECTED) else it
        }
        logAudit(
            targetType = "PARTNER",
            targetId = partnerId,
            targetName = partner?.businessName ?: partnerId,
            action = "KYC_REJECT",
            reason = reason.ifEmpty { "Failed verification criteria" }
        )
    }

    fun suspendGym(gymId: String, reason: String) {
        val gym = _gyms.value.find { it.id == gymId }
        _gyms.value = _gyms.value.map {
            if (it.id == gymId) it.copy(status = GymStatus.SUSPENDED) else it
        }
        logAudit(
            targetType = "GYM",
            targetId = gymId,
            targetName = gym?.name ?: gymId,
            action = "SUSPEND_GYM",
            reason = reason.ifEmpty { "Partner facility suspended" }
        )
    }

    fun reactivateGym(gymId: String, reason: String) {
        val gym = _gyms.value.find { it.id == gymId }
        _gyms.value = _gyms.value.map {
            if (it.id == gymId) it.copy(status = GymStatus.ACTIVE) else it
        }
        logAudit(
            targetType = "GYM",
            targetId = gymId,
            targetName = gym?.name ?: gymId,
            action = "REACTIVATE_GYM",
            reason = reason.ifEmpty { "Partner facility restored to network" }
        )
    }

    fun saveMembershipPlan(plan: MembershipPlan, priceChangeGrandfatheredNote: String? = null) {
        val exists = _plans.value.any { it.id == plan.id }
        if (exists) {
            _plans.value = _plans.value.map { if (it.id == plan.id) plan else it }
            logAudit(
                targetType = "PLAN",
                targetId = plan.id,
                targetName = plan.name,
                action = "UPDATE_PLAN",
                reason = priceChangeGrandfatheredNote ?: "Updated plan configuration and tier allocations."
            )
        } else {
            _plans.value = _plans.value + plan
            logAudit(
                targetType = "PLAN",
                targetId = plan.id,
                targetName = plan.name,
                action = "CREATE_PLAN",
                reason = "Created new subscription product."
            )
        }
    }

    fun savePromoCode(promo: PromoCode) {
        val exists = _promoCodes.value.any { it.id == promo.id }
        if (exists) {
            _promoCodes.value = _promoCodes.value.map { if (it.id == promo.id) promo else it }
            logAudit(
                targetType = "PROMO",
                targetId = promo.id,
                targetName = promo.code,
                action = "UPDATE_PROMO",
                reason = "Updated promo code discount values."
            )
        } else {
            _promoCodes.value = listOf(promo) + _promoCodes.value
            logAudit(
                targetType = "PROMO",
                targetId = promo.id,
                targetName = promo.code,
                action = "CREATE_PROMO",
                reason = "Launched new discount promotional campaign."
            )
        }
    }

    fun togglePromoCode(promoId: String) {
        _promoCodes.value = _promoCodes.value.map {
            if (it.id == promoId) it.copy(isActive = !it.isActive) else it
        }
    }

    fun overrideCheckIn(visitId: String, reason: String) {
        val visit = _visits.value.find { it.id == visitId }
        _visits.value = _visits.value.map {
            if (it.id == visitId) it.copy(validationResult = ValidationResult.APPROVED, denialReason = null) else it
        }
        _anomalies.value = _anomalies.value.map {
            if (it.visitId == visitId) it.copy(isResolved = true, resolutionReason = reason) else it
        }
        logAudit(
            targetType = "CHECKIN",
            targetId = visitId,
            targetName = "${visit?.userName ?: "Member"} @ ${visit?.gymName ?: "Gym"}",
            action = "OVERRIDE_CHECKIN",
            reason = reason.ifEmpty { "Manual administrative override granted." }
        )
    }

    fun escalateAnomalyToTicket(anomaly: CheckInAnomaly) {
        val visit = _visits.value.find { it.id == anomaly.visitId }
        val ticket = SupportTicket(
            id = "tkt_${UUID.randomUUID().toString().take(8)}",
            raisedByUserId = visit?.userId ?: "system",
            raisedByUserName = visit?.userName ?: "Automated Sentry",
            raisedByRole = UserRole.MEMBER,
            category = "Security Anomaly",
            subject = "Escalated: ${anomaly.type.label}",
            description = anomaly.details,
            status = TicketStatus.OPEN,
            relatedVisitId = anomaly.visitId,
            assignedTo = _currentUser.value.fullName,
            createdAt = System.currentTimeMillis()
        )
        _supportTickets.value = listOf(ticket) + _supportTickets.value
        _anomalies.value = _anomalies.value.map {
            if (it.visitId == anomaly.visitId) it.copy(isResolved = true, resolutionReason = "Escalated to ticket #${ticket.id}") else it
        }
        logAudit(
            targetType = "CHECKIN",
            targetId = anomaly.visitId,
            targetName = anomaly.type.label,
            action = "ESCALATE_ANOMALY",
            reason = "Anomaly escalated to high-priority ticket #${ticket.id}."
        )
    }

    fun processRefund(paymentId: String, reason: String) {
        val payment = _payments.value.find { it.id == paymentId }
        _payments.value = _payments.value.map {
            if (it.id == paymentId) it.copy(status = PaymentStatus.REFUNDED) else it
        }
        // Update related subscription if active
        payment?.subscriptionId?.let { subId ->
            _subscriptions.value = _subscriptions.value.map {
                if (it.id == subId) it.copy(status = SubscriptionStatus.CANCELLED) else it
            }
        }
        logAudit(
            targetType = "PAYMENT",
            targetId = paymentId,
            targetName = "${payment?.userId} ($${payment?.amount})",
            action = "REFUND",
            reason = reason.ifEmpty { "Customer refund approved." }
        )
    }

    fun bulkSettlePayouts(settlementIds: List<String>) {
        val now = System.currentTimeMillis()
        var totalAmount = 0.0
        _settlements.value = _settlements.value.map {
            if (it.id in settlementIds && it.status == SettlementStatus.PENDING) {
                totalAmount += it.totalAmount
                it.copy(status = SettlementStatus.PAID, paidAt = now)
            } else it
        }
        logAudit(
            targetType = "SETTLEMENT",
            targetId = settlementIds.joinToString(),
            targetName = "Bulk Payout (${settlementIds.size} gyms)",
            action = "BULK_SETTLEMENT",
            reason = "Bulk Stripe Connect transfer dispatched for $${String.format("%.2f", totalAmount)} across ${settlementIds.size} partners."
        )
    }

    fun assignTicket(ticketId: String, adminName: String) {
        _supportTickets.value = _supportTickets.value.map {
            if (it.id == ticketId) it.copy(assignedTo = adminName) else it
        }
    }

    fun resolveTicketWithNote(ticketId: String, resolutionNote: String) {
        val ticket = _supportTickets.value.find { it.id == ticketId }
        _supportTickets.value = _supportTickets.value.map {
            if (it.id == ticketId) it.copy(
                status = TicketStatus.RESOLVED,
                resolutionNote = resolutionNote,
                resolvedAt = System.currentTimeMillis(),
                assignedTo = it.assignedTo ?: _currentUser.value.fullName
            ) else it
        }
        logAudit(
            targetType = "TICKET",
            targetId = ticketId,
            targetName = ticket?.subject ?: ticketId,
            action = "RESOLVE_TICKET",
            reason = resolutionNote.ifEmpty { "Ticket marked as resolved." }
        )
    }
}

private fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

