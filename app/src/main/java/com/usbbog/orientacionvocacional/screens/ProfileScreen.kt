package com.usbbog.orientacionvocacional.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usbbog.orientacionvocacional.ui.components.UsbAppFooter
import com.usbbog.orientacionvocacional.ui.components.UsbAppTopBar
import com.usbbog.orientacionvocacional.ui.mobile.ProfileUiState
import com.usbbog.orientacionvocacional.ui.theme.USBColors

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    isAdministrator: Boolean,
    onSave: (
        fullName: String,
        phone: String,
        city: String,
        department: String,
        currentCareer: String,
        currentSemester: String,
    ) -> Boolean,
    onAdminClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var editing by rememberSaveable { mutableStateOf(false) }
    var fullName by rememberSaveable(state.fullName) { mutableStateOf(state.fullName) }
    var phone by rememberSaveable(state.phone) { mutableStateOf(state.phone) }
    var city by rememberSaveable(state.city) { mutableStateOf(state.city) }
    var department by rememberSaveable(state.department) { mutableStateOf(state.department) }
    var currentCareer by rememberSaveable(state.currentCareer) { mutableStateOf(state.currentCareer) }
    var currentSemester by rememberSaveable(state.currentSemester) { mutableStateOf(state.currentSemester) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { UsbAppTopBar() },
        containerColor = USBColors.Cream,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(USBColors.OrangeSoft, USBColors.Cream, USBColors.Sand),
                    ),
                ),
            contentPadding = PaddingValues(top = 18.dp, bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                ProfileHero(
                    state = state,
                    modifier = Modifier.padding(horizontal = 14.dp),
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = USBColors.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Text(
                            text = if (editing) "Editar información" else "Información personal",
                            color = USBColors.Black,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        )

                        if (editing) {
                            ProfileField("Nombre completo", fullName, { fullName = it })
                            ProfileField(
                                "Teléfono",
                                phone,
                                { phone = it.filter(Char::isDigit).take(15) },
                                KeyboardType.Phone,
                            )
                            ProfileField("Departamento", department, { department = it })
                            ProfileField("Ciudad", city, { city = it })
                            ProfileField("Carrera actual", currentCareer, { currentCareer = it })
                            ProfileField("Semestre actual", currentSemester, { currentSemester = it })

                            if (!errorMessage.isNullOrBlank()) {
                                Text(
                                    text = errorMessage.orEmpty(),
                                    color = USBColors.Danger,
                                    fontSize = 13.sp,
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        editing = false
                                        errorMessage = null
                                    },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text("Cancelar")
                                }
                                Button(
                                    onClick = {
                                        val saved = onSave(
                                            fullName,
                                            phone,
                                            city,
                                            department,
                                            currentCareer,
                                            currentSemester,
                                        )
                                        if (saved) {
                                            editing = false
                                            errorMessage = null
                                        } else {
                                            errorMessage = "Revisa el nombre y el número de teléfono."
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = USBColors.Orange,
                                    ),
                                ) {
                                    Icon(Icons.Outlined.Save, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Guardar")
                                }
                            }
                        } else {
                            ProfileDataRow("Correo", state.email.ifBlank { "No registrado" })
                            ProfileDataRow(
                                "Documento",
                                state.documentNumber.ifBlank { "No registrado" },
                            )
                            ProfileDataRow("Edad", state.age?.let { "$it años" } ?: "No registrada")
                            ProfileDataRow("Teléfono", state.phone.ifBlank { "No registrado" })
                            ProfileDataRow(
                                "Ubicación",
                                listOf(state.city, state.department)
                                    .filter(String::isNotBlank)
                                    .distinct()
                                    .joinToString(", ")
                                    .ifBlank { "No registrada" },
                            )
                            ProfileDataRow("Género", state.gender.ifBlank { "No registrado" })
                            if (state.belongsToUniversity || state.currentCareer.isNotBlank()) {
                                ProfileDataRow(
                                    "Programa académico",
                                    state.currentCareer.ifBlank { "No registrado" },
                                )
                                ProfileDataRow(
                                    "Semestre",
                                    state.currentSemester.ifBlank { "No registrado" },
                                )
                            }

                            Button(
                                onClick = { editing = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = USBColors.Orange,
                                ),
                                shape = RoundedCornerShape(15.dp),
                            ) {
                                Icon(Icons.Outlined.Edit, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Editar perfil", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            if (isAdministrator) {
                item {
                    OutlinedButton(
                        onClick = onAdminClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                            .height(54.dp),
                        shape = RoundedCornerShape(15.dp),
                    ) {
                        Icon(Icons.Outlined.AdminPanelSettings, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Abrir administración", fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 14.dp)) {
                    OutlinedButton(
                        onClick = onLogoutClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(15.dp),
                    ) {
                        Icon(Icons.Outlined.Logout, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Cerrar sesión", fontWeight = FontWeight.Bold)
                    }
                    TextButton(
                        onClick = onBackClick,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Volver", color = USBColors.Orange)
                    }
                }
            }

            item { UsbAppFooter() }
        }
    }
}

@Composable
private fun ProfileHero(
    state: ProfileUiState,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = USBColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(78.dp)
                    .background(
                        Brush.linearGradient(listOf(USBColors.Orange, USBColors.Blue)),
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = state.initials,
                    color = USBColors.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Spacer(Modifier.width(15.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.role.uppercase(),
                    color = USBColors.Orange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = state.fullName,
                    color = USBColors.Black,
                    fontSize = 23.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = state.email,
                    color = USBColors.TextMuted,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
private fun ProfileDataRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(USBColors.SurfaceSoft, RoundedCornerShape(16.dp))
            .padding(14.dp),
    ) {
        Text(
            text = label.uppercase(),
            color = USBColors.Blue,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.7.sp,
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = value,
            color = USBColors.Black,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(15.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = USBColors.Orange,
            cursorColor = USBColors.Orange,
        ),
    )
}
