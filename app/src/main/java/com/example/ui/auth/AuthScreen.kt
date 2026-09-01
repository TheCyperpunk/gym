package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onLoginSuccess: (String, UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    // 0: Sign up, 1: Log in
    var activeTab by remember { mutableIntStateOf(1) } // Default to Log in

    // Form fields
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("alex.nomad@example.com") }
    var password by remember { mutableStateOf("password123") }
    var homeCity by remember { mutableStateOf("Tokyo") }
    var cityDropdownExpanded by remember { mutableStateOf(false) }

    // Inline Field Errors
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailAlreadyRegistered by remember { mutableStateOf(false) }

    val cities = listOf("Tokyo", "London", "New York", "Berlin", "Barcelona", "Singapore", "Paris", "Sydney")

    Scaffold(
        containerColor = NomadConcrete,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Brand Logo & Credential Header
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = NomadInk,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383C45)),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.FitnessCenter,
                        contentDescription = "Fit loop",
                        tint = NomadSignal,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Fit loop",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = NomadInk
            )
            Text(
                text = "Global Gym Network • Single Pass",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = NomadSteel
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Main Auth Form Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = NomadMist,
                border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Single screen Tab Switcher: Sign up vs Log in
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = NomadConcrete,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(3.dp)) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (activeTab == 0) NomadInk else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        activeTab = 0
                                        emailError = null
                                        passwordError = null
                                        nameError = null
                                        emailAlreadyRegistered = false
                                    }
                            ) {
                                Text(
                                    text = "Sign up",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeTab == 0) Color.White else NomadSteel,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (activeTab == 1) NomadInk else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        activeTab = 1
                                        emailError = null
                                        passwordError = null
                                        nameError = null
                                        emailAlreadyRegistered = false
                                    }
                            ) {
                                Text(
                                    text = "Log in",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeTab == 1) Color.White else NomadSteel,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Sign Up Specific Fields
                    if (activeTab == 0) {
                        // Full Name
                        Text(
                            text = "FULL NAME",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadSteel
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = {
                                fullName = it
                                if (nameError != null) nameError = null
                            },
                            placeholder = { Text("Asha Patel") },
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
                            Text(
                                text = nameError.orEmpty(),
                                color = NomadBrick,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Home City Dropdown
                        Text(
                            text = "HOME CITY",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomadSteel
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ExposedDropdownMenuBox(
                            expanded = cityDropdownExpanded,
                            onExpandedChange = { cityDropdownExpanded = it },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = homeCity,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityDropdownExpanded) },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NomadSignal,
                                    unfocusedBorderColor = NomadLine,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = cityDropdownExpanded,
                                onDismissRequest = { cityDropdownExpanded = false }
                            ) {
                                cities.forEach { city ->
                                    DropdownMenuItem(
                                        text = { Text(city, fontSize = 13.sp) },
                                        onClick = {
                                            homeCity = city
                                            cityDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Email Field
                    Text(
                        text = "EMAIL ADDRESS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadSteel
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (emailError != null) emailError = null
                            emailAlreadyRegistered = false
                        },
                        placeholder = { Text("member@nomadfit.io") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        isError = emailError != null || emailAlreadyRegistered,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NomadSignal,
                            unfocusedBorderColor = NomadLine,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    // Inline plain language email error with tab flipping link
                    if (emailAlreadyRegistered) {
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "This email's already registered — ",
                                color = NomadBrick,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Text(
                                text = "log in instead?",
                                color = NomadSignal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    activeTab = 1
                                    emailAlreadyRegistered = false
                                }
                            )
                        }
                    } else if (emailError != null) {
                        Text(
                            text = emailError.orEmpty(),
                            color = NomadBrick,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Field
                    Text(
                        text = "PASSWORD",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomadSteel
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (passwordError != null) passwordError = null
                        },
                        placeholder = { Text("••••••••") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        isError = passwordError != null,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NomadSignal,
                            unfocusedBorderColor = NomadLine,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    if (passwordError != null) {
                        Text(
                            text = passwordError.orEmpty(),
                            color = NomadBrick,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Submit Action Button
                    Button(
                        onClick = {
                            var hasError = false
                            if (activeTab == 0 && fullName.isBlank()) {
                                nameError = "Please enter your full name."
                                hasError = true
                            }
                            if (email.isBlank() || !email.contains("@")) {
                                emailError = "Please enter a valid email address."
                                hasError = true
                            }
                            if (password.length < 4) {
                                passwordError = "Password must be at least 4 characters."
                                hasError = true
                            }
                            if (activeTab == 0 && email.equals("alex.nomad@example.com", ignoreCase = true)) {
                                emailAlreadyRegistered = true
                                return@Button
                            }
                            if (hasError) return@Button

                            val inferredRole = when {
                                email.contains("partner") || email.contains("gym") -> UserRole.GYM_OWNER
                                email.contains("admin") -> UserRole.ADMIN
                                else -> UserRole.MEMBER
                            }
                            onLoginSuccess(email, inferredRole)
                        },
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NomadSignal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = if (activeTab == 0) "Create Account" else "Log in",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Thin divider labeled "or continue with"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(NomadLine)
                        )
                        Text(
                            text = "or continue with",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = NomadFog,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(NomadLine)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Google Sign In Button
                    OutlinedButton(
                        onClick = {
                            onLoginSuccess("alex.nomad@example.com", UserRole.MEMBER)
                        },
                        shape = RoundedCornerShape(22.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = NomadInk,
                                modifier = Modifier.size(18.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("G", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Continue with Google",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NomadInk
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Demo Personas
            Text(
                text = "QUICK DEMO PERSONAS",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = NomadFog
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DemoLoginButton(
                    label = "Member",
                    sub = "Alex Vance",
                    modifier = Modifier.weight(1f),
                    onClick = { onLoginSuccess("alex.nomad@example.com", UserRole.MEMBER) }
                )
                DemoLoginButton(
                    label = "Gym Partner",
                    sub = "Sarah Connor",
                    modifier = Modifier.weight(1f),
                    onClick = { onLoginSuccess("sarah.partner@ironforge.com", UserRole.GYM_OWNER) }
                )
                DemoLoginButton(
                    label = "Admin",
                    sub = "Marcus Drake",
                    modifier = Modifier.weight(1f),
                    onClick = { onLoginSuccess("admin@nomadfit.io", UserRole.ADMIN) }
                )
            }
        }
    }
}

@Composable
private fun DemoLoginButton(
    label: String,
    sub: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = NomadMist,
        border = androidx.compose.foundation.BorderStroke(1.dp, NomadLine),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NomadInk)
            Text(text = sub, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = NomadFog)
        }
    }
}
