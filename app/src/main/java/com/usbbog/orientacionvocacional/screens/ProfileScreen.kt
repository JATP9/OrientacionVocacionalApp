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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.usbbog.orientacionvocacional.ui.components.UsbAppFooter
import com.usbbog.orientacionvocacional.ui.components.UsbAppTopBar
import com.usbbog.orientacionvocacional.ui.mobile.ProfileUiState
import com.usbbog.orientacionvocacional.ui.theme.USBColors
import com.usbbog.orientacionvocacional.viewmodel.ChangePasswordUiState
import com.usbbog.orientacionvocacional.viewmodel.passwordRequirementText

private val profileSemesterOptions = (1..10).map(Int::toString)

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    passwordState: ChangePasswordUiState,
    isAdministrator: Boolean,
    onSave: (
        firstName: String,
        lastName: String,
        phone: String,
        city: String,
        department: String,
        currentCareer: String,
        currentSemester: String,
    ) -> Boolean,
    onDepartmentChange: (String) -> Unit,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onChangePasswordClick: () -> Unit,
    onClearPasswordForm: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onClearDeleteAccountError: () -> Unit,
    onAdminClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var editing by rememberSaveable { mutableStateOf(false) }
    var firstName by rememberSaveable(state.firstName) { mutableStateOf(state.firstName) }
    var lastName by rememberSaveable(state.lastName) { mutableStateOf(state.lastName) }
    var phone by rememberSaveable(state.phone) { mutableStateOf(state.phone) }
    var city by rememberSaveable(state.city) { mutableStateOf(state.city) }
    var department by rememberSaveable(state.department) { mutableStateOf(state.department) }
    var currentCareer by rememberSaveable(state.currentCareer) {
        mutableStateOf(state.currentCareer)
    }
    var currentSemester by rememberSaveable(state.currentSemester) {
        mutableStateOf(state.currentSemester)
    }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var changingPassword by rememberSaveable { mutableStateOf(false) }
    var deleteDialogOpen by rememberSaveable { mutableStateOf(false) }

    fun resetDraft() {
        firstName = state.firstName
        lastName = state.lastName
        phone = state.phone
        city = state.city
        department = state.department
        currentCareer = state.currentCareer
        currentSemester = state.currentSemester
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { UsbAppTopBar() },
        containerColor = USBColors.Cream,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
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

                        if (state.isLoading) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = USBColors.Orange,
                                    strokeWidth = 2.dp,
                                )
                                Text("Cargando perfil...", color = USBColors.TextMuted, fontSize = 13.sp)
                            }
                        }

                        state.errorMessage?.takeIf(String::isNotBlank)?.let { message ->
                            Text(message, color = USBColors.Danger, fontSize = 13.sp)
                        }

                        state.statusMessage?.takeIf(String::isNotBlank)?.let { message ->
                            Text(message, color = USBColors.Success, fontSize = 13.sp)
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(USBColors.SurfaceSoft, RoundedCornerShape(16.dp))
                                .padding(14.dp),
                        ) {
                            Text(
                                text = "Datos de cuenta",
                                color = USBColors.Blue,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "El documento, el correo y los consentimientos no se " +
                                    "modifican desde esta pantalla.",
                                color = USBColors.TextMuted,
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                            )
                        }

                        if (editing) {
                            ProfileField("Nombre", firstName, { firstName = it })
                            ProfileField("Apellido", lastName, { lastName = it })
                            ProfileField(
                                "Teléfono",
                                phone,
                                {
                                    phone = it
                                        .filter { character ->
                                            character.isDigit() || character == '+' || character == ' '
                                        }
                                },
                                KeyboardType.Phone,
                            )
                            ProfileDropdownField(
                                label = "Departamento",
                                value = department,
                                options = (state.departmentOptions.map { it.label } + department)
                                    .filter(String::isNotBlank)
                                    .distinct(),
                                enabled = !state.isSaving,
                                onSelect = { selected ->
                                    department = selected
                                    city = ""
                                    onDepartmentChange(selected)
                                },
                            )
                            ProfileDropdownField(
                                label = "Municipio",
                                value = city,
                                options = (state.cityOptions.map { it.label } + city)
                                    .filter(String::isNotBlank)
                                    .distinct(),
                                enabled = department.isNotBlank() && !state.isSaving,
                                onSelect = { city = it },
                            )

                            if (state.belongsToUniversity) {
                                Text(
                                    text = "Información académica",
                                    color = USBColors.Blue,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = "Puedes actualizar el programa y semestre registrados.",
                                    color = USBColors.TextMuted,
                                    fontSize = 12.5.sp,
                                    lineHeight = 18.sp,
                                )
                                ProfileDropdownField(
                                    label = "Programa",
                                    value = currentCareer,
                                    options = state.programOptions.map { it.label },
                                    enabled = state.programOptions.isNotEmpty() && !state.isSaving,
                                    onSelect = { currentCareer = it },
                                )
                                Text(
                                    text = "Solo se muestran programas con enlace institucional.",
                                    color = USBColors.TextMuted,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp,
                                )
                                ProfileDropdownField(
                                    label = "Semestre",
                                    value = currentSemester,
                                    options = profileSemesterOptions,
                                    enabled = !state.isSaving,
                                    onSelect = { currentSemester = it },
                                )
                            }

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
                                        resetDraft()
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
                                            firstName,
                                            lastName,
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
                                            errorMessage =
                                                "Revisa el nombre, el teléfono, la ubicación y " +
                                                    "los datos académicos."
                                        }
                                    },
                                    enabled = !state.isSaving,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = USBColors.Orange,
                                    ),
                                ) {
                                    if (state.isSaving) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = USBColors.White,
                                            strokeWidth = 2.dp,
                                        )
                                    } else {
                                        Icon(Icons.Outlined.Save, contentDescription = null)
                                    }
                                    Spacer(Modifier.width(6.dp))
                                    Text(if (state.isSaving) "Guardando..." else "Guardar")
                                }
                            }
                        } else {
                            ProfileDataRow(
                                "Nombre de usuario",
                                state.username.ifBlank { "No registrado" },
                            )
                            ProfileDataRow("Correo", state.email.ifBlank { "No registrado" })
                            ProfileDataRow(
                                "Documento",
                                state.documentNumber.ifBlank { "No registrado" },
                            )
                            ProfileDataRow("Teléfono", state.phone.ifBlank { "No registrado" })
                            ProfileDataRow("Municipio", state.city.ifBlank { "No registrado" })
                            ProfileDataRow(
                                "Departamento",
                                state.department.ifBlank { "No registrado" },
                            )
                            if (state.belongsToUniversity) {
                                ProfileDataRow(
                                    "Programa",
                                    state.currentCareer.ifBlank { "No registrado" },
                                )
                                ProfileDataRow(
                                    "Semestre",
                                    state.currentSemester.ifBlank { "No registrado" },
                                )
                            }

                            Button(
                                onClick = {
                                    resetDraft()
                                    errorMessage = null
                                    editing = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                enabled = !state.isLoading,
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

            item {
                PasswordSecurityCard(
                    state = passwordState,
                    expanded = changingPassword && !passwordState.isSuccess,
                    onExpand = {
                        onClearPasswordForm()
                        changingPassword = true
                    },
                    onCancel = {
                        onClearPasswordForm()
                        changingPassword = false
                    },
                    onCurrentPasswordChange = onCurrentPasswordChange,
                    onNewPasswordChange = onNewPasswordChange,
                    onConfirmPasswordChange = onConfirmPasswordChange,
                    onChangePasswordClick = onChangePasswordClick,
                    modifier = Modifier.padding(horizontal = 14.dp),
                )
            }

            item {
                AccountManagementCard(
                    canDeleteAccount = state.canDeleteAccount,
                    errorMessage = state.deleteAccountError,
                    onDeleteClick = {
                        onClearDeleteAccountError()
                        deleteDialogOpen = true
                    },
                    modifier = Modifier.padding(horizontal = 14.dp),
                )
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

    if (deleteDialogOpen) {
        DeleteAccountConfirmationDialog(
            isDeleting = state.isDeletingAccount,
            errorMessage = state.deleteAccountError,
            onDismiss = {
                if (!state.isDeletingAccount) {
                    onClearDeleteAccountError()
                    deleteDialogOpen = false
                }
            },
            onConfirm = onDeleteAccountClick,
        )
    }
}

@Composable
private fun AccountManagementCard(
    canDeleteAccount: Boolean,
    errorMessage: String?,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = USBColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Gestión de cuenta",
                color = USBColors.Black,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = if (canDeleteAccount) {
                    "Puedes desactivar tu perfil. No podrás iniciar sesión hasta que " +
                        "un administrador reactive tu cuenta."
                } else {
                    "La cuenta ROOT está protegida y no puede eliminar su propio perfil."
                },
                color = USBColors.TextMuted,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
            errorMessage?.takeIf(String::isNotBlank)?.let { message ->
                Text(
                    text = message,
                    color = USBColors.Danger,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )
            }
            Button(
                onClick = onDeleteClick,
                enabled = canDeleteAccount,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = USBColors.Danger,
                    contentColor = USBColors.White,
                    disabledContainerColor = USBColors.TextMuted.copy(alpha = 0.35f),
                ),
            ) {
                Icon(Icons.Outlined.DeleteForever, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Eliminar perfil", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DeleteAccountConfirmationDialog(
    isDeleting: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(
        onDismissRequest = {
            if (!isDeleting) onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = !isDeleting,
            dismissOnClickOutside = !isDeleting,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 360.dp),
            shape = RoundedCornerShape(18.dp),
            color = USBColors.White,
            shadowElevation = 18.dp,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .background(USBColors.Orange),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(USBColors.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "!",
                            color = USBColors.Orange,
                            fontSize = 30.sp,
                            lineHeight = 32.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 22.dp,
                            top = 20.dp,
                            end = 22.dp,
                            bottom = 14.dp,
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "¿Eliminar tu perfil?",
                        color = USBColors.Black,
                        fontSize = 20.sp,
                        lineHeight = 25.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Tu cuenta será desactivada y se cerrará la sesión. No podrás " +
                            "volver a ingresar hasta que un administrador la reactive.",
                        color = USBColors.TextMuted,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Tus pruebas, resultados y registros se conservarán.",
                        color = USBColors.TextMuted,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        textAlign = TextAlign.Center,
                    )

                    errorMessage?.takeIf(String::isNotBlank)?.let { message ->
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = message,
                            color = USBColors.Danger,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = onConfirm,
                        enabled = !isDeleting,
                        modifier = Modifier
                            .widthIn(min = 166.dp)
                            .height(46.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = USBColors.Orange,
                            contentColor = USBColors.White,
                            disabledContainerColor = USBColors.Orange.copy(alpha = 0.55f),
                            disabledContentColor = USBColors.White,
                        ),
                        contentPadding = PaddingValues(horizontal = 26.dp),
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = USBColors.White,
                                strokeWidth = 2.dp,
                            )
                            Spacer(Modifier.width(6.dp))
                        }
                        Text(
                            text = if (isDeleting) "Desactivando..." else "Sí, desactivar",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    TextButton(
                        onClick = onDismiss,
                        enabled = !isDeleting,
                    ) {
                        Text(
                            text = "Cancelar",
                            color = if (isDeleting) USBColors.TextMuted else USBColors.Orange,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordSecurityCard(
    state: ChangePasswordUiState,
    expanded: Boolean,
    onExpand: () -> Unit,
    onCancel: () -> Unit,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onChangePasswordClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = USBColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = USBColors.Blue,
                )
                Text(
                    text = "Seguridad",
                    color = USBColors.Black,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }

            Text(
                text = "Actualiza la contraseña de acceso a tu cuenta.",
                color = USBColors.TextMuted,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )

            state.statusMessage?.takeIf(String::isNotBlank)?.let { message ->
                Text(
                    text = message,
                    color = if (state.isSuccess) USBColors.Success else USBColors.Danger,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            if (expanded) {
                ProfilePasswordField(
                    value = state.currentPassword,
                    onValueChange = onCurrentPasswordChange,
                    label = "Contraseña actual",
                    error = state.currentPasswordError,
                    enabled = !state.isSubmitting,
                )
                ProfilePasswordField(
                    value = state.newPassword,
                    onValueChange = onNewPasswordChange,
                    label = "Nueva contraseña",
                    error = state.newPasswordError,
                    enabled = !state.isSubmitting,
                )
                Text(
                    text = passwordRequirementText(),
                    color = USBColors.TextMuted,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                )
                ProfilePasswordField(
                    value = state.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = "Confirmar nueva contraseña",
                    error = state.confirmPasswordError,
                    enabled = !state.isSubmitting,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onCancel,
                        enabled = !state.isSubmitting,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = onChangePasswordClick,
                        enabled = !state.isSubmitting,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = USBColors.Orange,
                        ),
                    ) {
                        if (state.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = USBColors.White,
                                strokeWidth = 2.dp,
                            )
                            Spacer(Modifier.width(6.dp))
                        }
                        Text(
                            text = if (state.isSubmitting) "Actualizando..." else "Actualizar",
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            } else {
                Button(
                    onClick = onExpand,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = USBColors.Orange,
                        contentColor = USBColors.White,
                    ),
                ) {
                    Icon(Icons.Outlined.Lock, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (state.isSuccess) {
                            "Cambiar nuevamente"
                        } else {
                            "Cambiar contraseña"
                        },
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfilePasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    enabled: Boolean,
) {
    var visible by rememberSaveable { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (visible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { visible = !visible }, enabled = enabled) {
                    Icon(
                        imageVector = if (visible) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = if (visible) {
                            "Ocultar contraseña"
                        } else {
                            "Mostrar contraseña"
                        },
                    )
                }
            },
            isError = error != null,
            shape = RoundedCornerShape(15.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = USBColors.Orange,
                cursorColor = USBColors.Orange,
            ),
        )
        error?.let {
            Text(
                text = it,
                color = USBColors.Danger,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
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

@Composable
private fun ProfileDropdownField(
    label: String,
    value: String,
    options: List<String>,
    enabled: Boolean,
    onSelect: (String) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = label,
            color = USBColors.Text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(15.dp),
            ) {
                Text(
                    text = value.ifBlank { "Selecciona $label" },
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                    color = if (value.isBlank()) USBColors.TextMuted else USBColors.Black,
                )
                Text("▾", color = USBColors.TextMuted)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f),
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}
