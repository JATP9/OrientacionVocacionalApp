package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import com.usbbog.orientacionvocacional.ui.mobile.AdminMetricUi
import com.usbbog.orientacionvocacional.ui.mobile.AdminResultUi
import com.usbbog.orientacionvocacional.ui.mobile.AdminUiState
import com.usbbog.orientacionvocacional.ui.mobile.AdminUserUi
import com.usbbog.orientacionvocacional.ui.mobile.GeographicDistributionUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdminViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(initialState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun updateRole(userId: String, role: String) {
        _uiState.value = _uiState.value.copy(
            users = _uiState.value.users.map { user ->
                if (user.id == userId) user.copy(role = role) else user
            },
            statusMessage = "Cambio de rol aplicado en la demostración.",
        )
    }

    fun updateStatus(userId: String, status: String) {
        _uiState.value = _uiState.value.copy(
            users = _uiState.value.users.map { user ->
                if (user.id == userId) user.copy(status = status) else user
            },
            statusMessage = "Estado del usuario actualizado en la demostración.",
        )
    }

    fun reportGenerated(format: String) {
        _uiState.value = _uiState.value.copy(
            statusMessage = "Reporte ${format.uppercase()} preparado para conectar con el backend.",
        )
    }

    fun clearStatus() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }

    private fun initialState() = AdminUiState(
        metrics = listOf(
            AdminMetricUi("Total de usuarios", "1.284", "Personas registradas en la plataforma", "+8,4%"),
            AdminMetricUi("Pruebas completadas", "1.105", "Resultados disponibles para consulta", "+6,1%"),
            AdminMetricUi("Usuarios activos", "1.118", "Cuentas habilitadas actualmente", "87%"),
            AdminMetricUi("Área más seleccionada", "Ingeniería y tecnología", "Mayor afinidad del periodo"),
        ),
        users = listOf(
            AdminUserUi("usr-001", "Laura Martínez", "laura.martinez@example.com", "Estudiante", "Activo", "1023456789", "Bogotá D.C."),
            AdminUserUi("usr-002", "Carlos Rodríguez", "carlos.rodriguez@example.com", "Estudiante", "Activo", "1098765432", "Medellín"),
            AdminUserUi("usr-003", "Mariana Gómez", "mariana.gomez@example.com", "Estudiante", "Activo", "1144556677", "Cali"),
            AdminUserUi("usr-004", "Coordinación USB", "admin@usb.edu.co", "Administrador", "Activo", "900000001", "Bogotá D.C."),
        ),
        recentResults = listOf(
            AdminResultUi("res-001", "Laura Martínez", "Bogotá D.C.", "Ingeniería y tecnología", "Ingeniería de Sistemas", 92, "01/08/2026"),
            AdminResultUi("res-002", "Carlos Rodríguez", "Medellín", "Negocios y gestión", "Administración de Empresas", 86, "31/07/2026"),
            AdminResultUi("res-003", "Mariana Gómez", "Cali", "Arte y comunicación", "Diseño Gráfico", 89, "30/07/2026"),
        ),
        geographicDistribution = listOf(
            GeographicDistributionUi("Bogotá D.C.", 387, 342),
            GeographicDistributionUi("Antioquia", 246, 210),
            GeographicDistributionUi("Valle del Cauca", 198, 166),
            GeographicDistributionUi("Cundinamarca", 142, 119),
            GeographicDistributionUi("Santander", 109, 91),
            GeographicDistributionUi("Otros", 202, 177),
        ),
        internalUsers = 796,
        externalUsers = 488,
    )
}
