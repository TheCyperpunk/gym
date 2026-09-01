package com.example.ui.member

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

data class WorkoutExercise(
    val id: String,
    val name: String,
    val durationSeconds: Int,
    val icon: ImageVector,
    val badgeBg: Color,
    val badgeTint: Color
)

data class PresetCircuit(
    val id: String,
    val title: String,
    val exerciseCount: Int,
    val durationFormatted: String,
    val icon: ImageVector,
    val badgeBg: Color,
    val badgeTint: Color,
    val exercises: List<WorkoutExercise>
)

@Composable
fun MemberWorkoutCircuitScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToCheckIn: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Preset circuits
    val presets = remember {
        listOf(
            PresetCircuit(
                id = "hiit",
                title = "HIIT Starter",
                exerciseCount = 4,
                durationFormatted = "12m",
                icon = Icons.Outlined.ElectricBolt,
                badgeBg = NomadAmberLight,
                badgeTint = NomadAmber,
                exercises = listOf(
                    WorkoutExercise("1", "Jumping Jacks", 45, Icons.Outlined.ElectricBolt, NomadAmberLight, NomadAmber),
                    WorkoutExercise("2", "High Knees", 30, Icons.Outlined.ElectricBolt, NomadAmberLight, NomadAmber),
                    WorkoutExercise("3", "Mountain Climbers", 40, Icons.Outlined.ElectricBolt, NomadAmberLight, NomadAmber),
                    WorkoutExercise("4", "Burpees", 30, Icons.Outlined.ElectricBolt, NomadAmberLight, NomadAmber)
                )
            ),
            PresetCircuit(
                id = "mobility",
                title = "Mobility Flow",
                exerciseCount = 3,
                durationFormatted = "11m",
                icon = Icons.Outlined.Eco,
                badgeBg = NomadMossLight,
                badgeTint = NomadMoss,
                exercises = listOf(
                    WorkoutExercise("1", "Cat-Cow Stretch", 45, Icons.Outlined.Eco, NomadMossLight, NomadMoss),
                    WorkoutExercise("2", "Hip Openers", 60, Icons.Outlined.Eco, NomadMossLight, NomadMoss),
                    WorkoutExercise("3", "Thoracic Rotations", 40, Icons.Outlined.Eco, NomadMossLight, NomadMoss)
                )
            ),
            PresetCircuit(
                id = "strength",
                title = "Strength Circuit",
                exerciseCount = 3,
                durationFormatted = "3m 50s",
                icon = Icons.Outlined.FitnessCenter,
                badgeBg = NomadPurpleLight,
                badgeTint = NomadPurple,
                exercises = listOf(
                    WorkoutExercise("1", "Push-up", 30, Icons.Outlined.ElectricBolt, NomadAmberLight, NomadAmber),
                    WorkoutExercise("2", "Sit-up", 25, Icons.Outlined.Eco, NomadMossLight, NomadMoss),
                    WorkoutExercise("3", "Squat", 40, Icons.Outlined.FitnessCenter, NomadPurpleLight, NomadPurple)
                )
            )
        )
    }

    var selectedPresetId by remember { mutableStateOf("strength") }
    var currentExercises by remember {
        mutableStateOf(presets.first { it.id == "strength" }.exercises)
    }

    var isRestEnabled by remember { mutableStateOf(true) }
    var restMode by remember { mutableStateOf("Smart defaults") } // "Smart defaults" vs "Uniform rest"
    var restDurationSeconds by remember { mutableIntStateOf(90) }
    var roundsCount by remember { mutableIntStateOf(1) }
    var showAddDialog by remember { mutableStateOf(false) }
    var isWorkoutActive by remember { mutableStateOf(false) }

    // Calculate total time
    val totalTimeSeconds = remember(currentExercises, isRestEnabled, restDurationSeconds, roundsCount) {
        val exerciseTime = currentExercises.sumOf { it.durationSeconds }
        val restTime = if (isRestEnabled && currentExercises.size > 1) (currentExercises.size - 1) * restDurationSeconds else 0
        (exerciseTime + restTime) * roundsCount
    }

    val totalTimeFormatted = remember(totalTimeSeconds) {
        val mins = totalTimeSeconds / 60
        val secs = totalTimeSeconds % 60
        if (mins > 0 && secs > 0) "${mins}m ${secs}s"
        else if (mins > 0) "${mins}m"
        else "${secs}s"
    }

    Scaffold(
        containerColor = NomadConcrete,
        topBar = {
            // Sunny yellow top decorative header accent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(FitLoopYellow)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Full Body Circuit",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NomadInk,
                    letterSpacing = (-0.5).sp
                )
            }

            // Stat pills row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Exercise count pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FitnessCenter,
                            contentDescription = null,
                            tint = NomadInk,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${currentExercises.size} exercises",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadInk
                        )
                    }
                }

                // Total duration pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = NomadMist,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = NomadInk,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = totalTimeFormatted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadInk
                        )
                    }
                }

                // Smart rests pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = NomadMossLight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NomadMoss.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = NomadMoss,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Smart rests",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadMoss
                        )
                    }
                }
            }

            // Horizontal Presets Carousel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                presets.forEach { preset ->
                    val isSelected = selectedPresetId == preset.id
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = NomadMist,
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) NomadInk else NomadLine
                        ),
                        modifier = Modifier
                            .width(135.dp)
                            .clickable {
                                selectedPresetId = preset.id
                                currentExercises = preset.exercises
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Circular icon badge
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(preset.badgeBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = preset.icon,
                                    contentDescription = null,
                                    tint = preset.badgeTint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = preset.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NomadInk
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${preset.exerciseCount} exercises",
                                    fontSize = 11.sp,
                                    color = NomadSteel
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Schedule,
                                        contentDescription = null,
                                        tint = NomadSteel,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = preset.durationFormatted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = NomadSteel
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Exercise List Card Container
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = NomadMist,
                border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    currentExercises.forEachIndexed { index, exercise ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Circular icon badge
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(exercise.badgeBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = exercise.icon,
                                        contentDescription = null,
                                        tint = exercise.badgeTint,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = exercise.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NomadInk
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${exercise.durationSeconds}s",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NomadInk
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Icon(
                                    imageVector = Icons.Outlined.DragHandle,
                                    contentDescription = "Reorder",
                                    tint = NomadFog,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        if (index < currentExercises.size - 1) {
                            HorizontalDivider(
                                color = NomadLine,
                                thickness = 0.8.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = NomadLine, thickness = 0.8.dp)

                    // Add Exercise Action
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAddDialog = true }
                            .padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null,
                            tint = NomadMoss,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add exercise",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadMoss
                        )
                    }
                }
            }

            // Rest Settings Card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = NomadMist,
                border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header with Green Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rest Settings",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadInk
                        )
                        Switch(
                            checked = isRestEnabled,
                            onCheckedChange = { isRestEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NomadMoss,
                                uncheckedThumbColor = NomadFog,
                                uncheckedTrackColor = NomadLine
                            )
                        )
                    }

                    AnimatedVisibility(visible = isRestEnabled) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Segmented pill control: "Smart defaults" | "Uniform rest"
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = NomadConcrete,
                                border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // Smart defaults option
                                    val isSmartSelected = restMode == "Smart defaults"
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isSmartSelected) NomadMossLight else Color.Transparent,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { restMode = "Smart defaults" }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (isSmartSelected) {
                                                Icon(
                                                    imageVector = Icons.Outlined.AutoAwesome,
                                                    contentDescription = null,
                                                    tint = NomadMoss,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            Text(
                                                text = "Smart defaults",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSmartSelected) NomadMoss else NomadSteel
                                            )
                                        }
                                    }

                                    // Uniform rest option
                                    val isUniformSelected = restMode == "Uniform rest"
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isUniformSelected) NomadMist else Color.Transparent,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { restMode = "Uniform rest" }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Uniform rest",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isUniformSelected) NomadInk else NomadSteel
                                            )
                                        }
                                    }
                                }
                            }

                            // Between exercises duration row
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = NomadMist,
                                border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        restDurationSeconds = when (restDurationSeconds) {
                                            30 -> 60
                                            60 -> 90
                                            90 -> 120
                                            else -> 30
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.Schedule,
                                            contentDescription = null,
                                            tint = NomadInk,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Between exercises",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NomadInk
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${restDurationSeconds / 60}m ${restDurationSeconds % 60}s",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NomadMoss
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                                            contentDescription = null,
                                            tint = NomadFog,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Rounds Row
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = NomadMist,
                border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        roundsCount = if (roundsCount < 5) roundsCount + 1 else 1
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Loop,
                            contentDescription = null,
                            tint = NomadInk,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Rounds: $roundsCount",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadInk
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                        contentDescription = null,
                        tint = NomadFog,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // Total Workout Time Card (Mint Curvy Container)
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = NomadMossLight,
                border = androidx.compose.foundation.BorderStroke(1.dp, NomadMoss.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = null,
                            tint = NomadMoss,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Total workout time",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = NomadMoss
                        )
                        Text(
                            text = totalTimeFormatted,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NomadInk
                        )
                    }
                }
            }

            // Primary Start Workout Pill CTA Button (Coral/Red-Orange)
            Button(
                onClick = { isWorkoutActive = true },
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start Workout",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Save Template Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = null,
                    tint = NomadSteel,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Save template",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomadInk
                )
            }
        }
    }

    // Add Exercise Dialog
    if (showAddDialog) {
        var newName by remember { mutableStateOf("") }
        var newDuration by remember { mutableStateOf("30") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = NomadMist,
            title = {
                Text("Add Custom Exercise", fontWeight = FontWeight.Bold, color = NomadInk)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Exercise Name") },
                        placeholder = { Text("e.g. Plank, Lunges") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newDuration,
                        onValueChange = { newDuration = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Duration (seconds)") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val dur = newDuration.toIntOrNull() ?: 30
                        val name = newName.ifBlank { "Custom Exercise" }
                        currentExercises = currentExercises + WorkoutExercise(
                            id = System.currentTimeMillis().toString(),
                            name = name,
                            durationSeconds = dur,
                            icon = Icons.Outlined.FitnessCenter,
                            badgeBg = NomadBlueLight,
                            badgeTint = NomadBlue
                        )
                        showAddDialog = false
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NomadSignal)
                ) {
                    Text("Add", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = NomadSteel)
                }
            }
        )
    }

    // Live Workout Timer Active Modal / Screen
    if (isWorkoutActive) {
        LiveWorkoutTimerDialog(
            exercises = currentExercises,
            rounds = roundsCount,
            restSeconds = if (isRestEnabled) restDurationSeconds else 0,
            onDismiss = { isWorkoutActive = false }
        )
    }
}

@Composable
private fun LiveWorkoutTimerDialog(
    exercises: List<WorkoutExercise>,
    rounds: Int,
    restSeconds: Int,
    onDismiss: () -> Unit
) {
    var currentExerciseIndex by remember { mutableIntStateOf(0) }
    var currentRound by remember { mutableIntStateOf(1) }
    var isResting by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }

    val currentExercise = exercises.getOrNull(currentExerciseIndex) ?: exercises.first()
    var secondsRemaining by remember { mutableIntStateOf(currentExercise.durationSeconds) }

    LaunchedEffect(currentExerciseIndex, isResting, isPaused, isCompleted) {
        if (isCompleted || isPaused) return@LaunchedEffect

        secondsRemaining = if (isResting) restSeconds else currentExercise.durationSeconds
        while (secondsRemaining > 0) {
            delay(1000)
            if (!isPaused) {
                secondsRemaining--
            }
        }

        // When timer reaches 0
        if (!isResting && restSeconds > 0 && (currentExerciseIndex < exercises.size - 1 || currentRound < rounds)) {
            isResting = true
        } else {
            isResting = false
            if (currentExerciseIndex < exercises.size - 1) {
                currentExerciseIndex++
            } else if (currentRound < rounds) {
                currentRound++
                currentExerciseIndex = 0
            } else {
                isCompleted = true
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = NomadMist,
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(NomadMossLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = NomadMoss,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Text(
                        text = "Circuit Completed!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NomadInk
                    )
                    Text(
                        text = "Great job finishing all $rounds rounds of exercises.",
                        fontSize = 13.sp,
                        color = NomadSteel,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                } else {
                    // Status Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isResting) NomadMossLight else NomadAmberLight
                    ) {
                        Text(
                            text = if (isResting) "REST PERIOD" else "ROUND $currentRound OF $rounds",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isResting) NomadMoss else NomadAmber,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }

                    // Circular Countdown Display
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(if (isResting) NomadMossLight else NomadSurfaceVariant)
                            .border(3.dp, if (isResting) NomadMoss else NomadSignal, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$secondsRemaining",
                                fontSize = 52.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isResting) NomadMoss else NomadSignal
                            )
                            Text(
                                text = "seconds",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NomadSteel
                            )
                        }
                    }

                    // Current Exercise Name
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isResting) "Get Ready for Next Exercise" else currentExercise.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadInk
                        )
                        if (!isResting) {
                            Text(
                                text = "Exercise ${currentExerciseIndex + 1} of ${exercises.size}",
                                fontSize = 12.sp,
                                color = NomadSteel
                            )
                        }
                    }

                    // Controls Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { isPaused = !isPaused },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NomadConcrete)
                        ) {
                            Icon(
                                imageVector = if (isPaused) Icons.Outlined.PlayArrow else Icons.Outlined.Pause,
                                contentDescription = "Play/Pause",
                                tint = NomadInk
                            )
                        }

                        IconButton(
                            onClick = {
                                if (currentExerciseIndex < exercises.size - 1) {
                                    currentExerciseIndex++
                                    isResting = false
                                } else if (currentRound < rounds) {
                                    currentRound++
                                    currentExerciseIndex = 0
                                    isResting = false
                                } else {
                                    isCompleted = true
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NomadConcrete)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SkipNext,
                                contentDescription = "Skip",
                                tint = NomadInk
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NomadInk),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isCompleted) "Done" else "End Workout",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}
