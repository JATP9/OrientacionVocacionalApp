package com.usbbog.orientacionvocacional.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usbbog.orientacionvocacional.ui.components.UsbAppFooter
import com.usbbog.orientacionvocacional.ui.components.UsbAppTopBar
import com.usbbog.orientacionvocacional.ui.mobile.AdminMetricUi
import com.usbbog.orientacionvocacional.ui.mobile.AdminResultUi
import com.usbbog.orientacionvocacional.ui.mobile.AdminUiState
import com.usbbog.orientacionvocacional.ui.mobile.AdminUserUi
import com.usbbog.orientacionvocacional.ui.mobile.GeographicDistributionUi
import com.usbbog.orientacionvocacional.ui.theme.USBColors

private enum class AdminView(
    val label: String,
    val icon: ImageVector,
) {
    Overview("Resumen", Icons.Outlined.Home),
    Users("Usuarios", Icons.Outlined.Groups),
    Results("Resultados", Icons.Outlined.Assessment),
    Reports("Reportes", Icons.Outlined.Description),
    Settings("Configuración", Icons.Outlined.Settings),
}

@Composable
fun AdminDashboardScreen(
    administratorName: String,
    state: AdminUiState,
    onRoleChange: (userId: String, role: String) -> Unit,
    onStatusChange: (userId: String, status: String) -> Unit,
    onExport: (format: String) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var activeView by rememberSaveable { mutableStateOf(AdminView.Overview) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            UsbAppTopBar(
                userLabel = administratorName,
                onUserClick = onProfileClick,
            )
        },
        containerColor = USBColors.Sand,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFF5EBDD), Color(0xFFFCF8F2)),
                    ),
                ),
            contentPadding = PaddingValues(top = 14.dp, bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                AdminNavigation(
                    selected = activeView,
                    onSelected = { activeView = it },
                )
            }

            if (!state.statusMessage.isNullOrBlank()) {
                item {
                    Text(
                        text = state.statusMessage.orEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                            .background(Color(0xFFEAF6F0), RoundedCornerShape(15.dp))
                            .padding(13.dp),
                        color = USBColors.Success,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            when (activeView) {
                AdminView.Overview -> overviewContent(state, onExport)
                AdminView.Users -> usersContent(state.users, onRoleChange, onStatusChange)
                AdminView.Results -> resultsContent(state.recentResults)
                AdminView.Reports -> reportsContent(onExport)
                AdminView.Settings -> settingsContent()
            }

            item { UsbAppFooter() }
        }
    }
}

@Composable
private fun AdminNavigation(
    selected: AdminView,
    onSelected: (AdminView) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 14.dp)) {
        Text(
            text = "Panel de seguimiento USB Vocacional",
            color = USBColors.Black,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Usuarios, resultados y distribución institucional",
            color = USBColors.TextMuted,
            fontSize = 13.sp,
        )
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AdminView.entries.forEach { view ->
                val active = view == selected
                Row(
                    modifier = Modifier
                        .background(
                            if (active) USBColors.Orange else USBColors.White,
                            RoundedCornerShape(100.dp),
                        )
                        .clickable { onSelected(view) }
                        .padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = view.icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (active) USBColors.White else USBColors.TextMuted,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = view.label,
                        color = if (active) USBColors.White else USBColors.Text,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.overviewContent(
    state: AdminUiState,
    onExport: (String) -> Unit,
) {
    item {
        AdminSectionHeader(
            eyebrow = "ADMINISTRACIÓN",
            title = "Resumen general",
            subtitle = "Indicadores consolidados de la plataforma.",
        )
    }

    items(state.metrics) { metric ->
        MetricCard(metric, Modifier.padding(horizontal = 14.dp))
    }

    item {
        DistributionCard(
            internalUsers = state.internalUsers,
            externalUsers = state.externalUsers,
            modifier = Modifier.padding(horizontal = 14.dp),
        )
    }

    item {
        GeographicCard(
            data = state.geographicDistribution,
            modifier = Modifier.padding(horizontal = 14.dp),
        )
    }

    item {
        AdminCard(Modifier.padding(horizontal = 14.dp)) {
            Text(
                text = "Resultados recientes",
                color = USBColors.Black,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(Modifier.height(12.dp))
            state.recentResults.forEach { result ->
                RecentResultRow(result)
                Spacer(Modifier.height(10.dp))
            }
        }
    }

    item {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("PDF", "CSV", "Excel").forEach { format ->
                OutlinedButton(
                    onClick = { onExport(format) },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                ) {
                    Text(format, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.usersContent(
    users: List<AdminUserUi>,
    onRoleChange: (String, String) -> Unit,
    onStatusChange: (String, String) -> Unit,
) {
    item {
        AdminSectionHeader(
            eyebrow = "USUARIOS",
            title = "Gestión de usuarios",
            subtitle = "Consulta datos y administra roles o estados de la demostración.",
        )
    }
    items(users, key = AdminUserUi::id) { user ->
        UserManagementCard(
            user = user,
            onRoleChange = { onRoleChange(user.id, it) },
            onStatusChange = { onStatusChange(user.id, it) },
            modifier = Modifier.padding(horizontal = 14.dp),
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.resultsContent(
    results: List<AdminResultUi>,
) {
    item {
        AdminSectionHeader(
            eyebrow = "RESULTADOS",
            title = "Resultados por individuo",
            subtitle = "Últimas pruebas finalizadas y recomendaciones principales.",
        )
    }
    items(results, key = AdminResultUi::id) { result ->
        AdminCard(Modifier.padding(horizontal = 14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.studentName,
                        color = USBColors.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(result.city, color = USBColors.TextMuted, fontSize = 13.sp)
                }
                Text(
                    text = "${result.affinity}%",
                    color = USBColors.Orange,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = result.primaryArea,
                color = USBColors.Blue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Programa sugerido: ${result.topCareer}",
                color = USBColors.Text,
                fontSize = 14.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Finalizada: ${result.completedAt}",
                color = USBColors.TextMuted,
                fontSize = 11.sp,
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.reportsContent(
    onExport: (String) -> Unit,
) {
    item {
        AdminSectionHeader(
            eyebrow = "REPORTES",
            title = "Generación de reportes",
            subtitle = "Exportaciones simuladas listas para conectar con el backend.",
        )
    }
    val reports = listOf(
        Triple("Reporte por usuario", "Historial y resumen individual de resultados.", "PDF"),
        Triple("Reporte por programa", "Usuarios agrupados por programa sugerido.", "CSV"),
        Triple("Reporte por departamento", "Distribución según procedencia registrada.", "Excel"),
        Triple("Reporte por periodo", "Corte administrativo para un rango de fechas.", "PDF"),
    )
    items(reports) { report ->
        AdminCard(Modifier.padding(horizontal = 14.dp)) {
            Text(
                text = report.first,
                color = USBColors.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = report.second,
                color = USBColors.TextMuted,
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = { onExport(report.third) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = USBColors.Orange),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text("Generar ${report.third}", fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.settingsContent() {
    item {
        AdminSectionHeader(
            eyebrow = "CONFIGURACIÓN",
            title = "Configuración general",
            subtitle = "Parámetros institucionales previstos para la integración backend.",
        )
    }
    val settings = listOf(
        "Duración y versión de la prueba" to "Configurada desde el servicio de intentos.",
        "Catálogo de programas" to "Pendiente de sincronización con la oferta académica.",
        "Políticas de privacidad" to "Textos institucionales visibles en registro y reportes.",
    )
    items(settings) { setting ->
        AdminCard(Modifier.padding(horizontal = 14.dp)) {
            Text(
                text = setting.first,
                color = USBColors.Black,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = setting.second,
                color = USBColors.TextMuted,
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )
        }
    }
}

@Composable
private fun AdminSectionHeader(
    eyebrow: String,
    title: String,
    subtitle: String,
) {
    Column(modifier = Modifier.padding(horizontal = 14.dp)) {
        Text(
            text = eyebrow,
            color = USBColors.Orange,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = title,
            color = USBColors.Black,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(Modifier.height(4.dp))
        Text(text = subtitle, color = USBColors.TextMuted, fontSize = 13.sp)
    }
}

@Composable
private fun MetricCard(
    metric: AdminMetricUi,
    modifier: Modifier = Modifier,
) {
    AdminCard(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = metric.label.uppercase(),
                color = USBColors.Orange,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp,
            )
            if (metric.change.isNotBlank()) {
                Text(
                    text = metric.change,
                    color = USBColors.Success,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = metric.value,
            color = USBColors.Black,
            fontSize = if (metric.value.length > 12) 22.sp else 34.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(4.dp))
        Text(metric.supportingText, color = USBColors.TextMuted, fontSize = 13.sp)
    }
}

@Composable
private fun DistributionCard(
    internalUsers: Int,
    externalUsers: Int,
    modifier: Modifier = Modifier,
) {
    val total = (internalUsers + externalUsers).coerceAtLeast(1)
    val internalSweep = internalUsers * 360f / total

    AdminCard(modifier) {
        Text(
            text = "Distribución de usuarios",
            color = USBColors.Black,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = USBColors.Orange,
                        startAngle = -90f,
                        sweepAngle = internalSweep,
                        useCenter = false,
                        style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Butt),
                    )
                    drawArc(
                        color = USBColors.Blue,
                        startAngle = -90f + internalSweep,
                        sweepAngle = 360f - internalSweep,
                        useCenter = false,
                        style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Butt),
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(internalUsers * 100 / total)}%",
                        color = USBColors.Black,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text("Internos", color = USBColors.TextMuted, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.width(18.dp))
            Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                DistributionLegend("Internos", internalUsers, USBColors.Orange)
                DistributionLegend("Externos", externalUsers, USBColors.Blue)
            }
        }
    }
}

@Composable
private fun DistributionLegend(label: String, value: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(11.dp).background(color, CircleShape))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, color = USBColors.TextMuted, fontSize = 11.sp)
            Text(value.toString(), color = USBColors.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun GeographicCard(
    data: List<GeographicDistributionUi>,
    modifier: Modifier = Modifier,
) {
    val maximum = data.maxOfOrNull(GeographicDistributionUi::users)?.coerceAtLeast(1) ?: 1
    AdminCard(modifier) {
        Text(
            text = "Distribución geográfica",
            color = USBColors.Black,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(Modifier.height(14.dp))
        data.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.region,
                    modifier = Modifier.width(105.dp),
                    color = USBColors.TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .background(USBColors.Sand, RoundedCornerShape(100.dp)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(item.users.toFloat() / maximum)
                            .height(10.dp)
                            .background(USBColors.Orange, RoundedCornerShape(100.dp)),
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = item.users.toString(),
                    modifier = Modifier.width(32.dp),
                    color = USBColors.Black,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                )
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun RecentResultRow(result: AdminResultUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(USBColors.SurfaceSoft, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(USBColors.Orange, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                result.studentName.take(1),
                color = USBColors.White,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(result.studentName, color = USBColors.Black, fontWeight = FontWeight.Bold)
            Text(result.topCareer, color = USBColors.TextMuted, fontSize = 11.sp)
        }
        Text(
            "${result.affinity}%",
            color = USBColors.Orange,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun UserManagementCard(
    user: AdminUserUi,
    onRoleChange: (String) -> Unit,
    onStatusChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var roleMenuOpen by remember { mutableStateOf(false) }
    var statusMenuOpen by remember { mutableStateOf(false) }

    AdminCard(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(46.dp).background(USBColors.Orange, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = user.fullName.take(1),
                    color = USBColors.White,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(11.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.fullName,
                    color = USBColors.Black,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(user.email, color = USBColors.TextMuted, fontSize = 12.sp)
                Text(
                    text = "${user.documentNumber} · ${user.city}",
                    color = USBColors.TextSecondary,
                    fontSize = 11.sp,
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { roleMenuOpen = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(user.role, fontSize = 11.sp, maxLines = 1)
                }
                DropdownMenu(
                    expanded = roleMenuOpen,
                    onDismissRequest = { roleMenuOpen = false },
                ) {
                    listOf("Estudiante", "Administrador").forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role) },
                            onClick = {
                                roleMenuOpen = false
                                onRoleChange(role)
                            },
                        )
                    }
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { statusMenuOpen = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(user.status, fontSize = 11.sp)
                }
                DropdownMenu(
                    expanded = statusMenuOpen,
                    onDismissRequest = { statusMenuOpen = false },
                ) {
                    listOf("Activo", "Inactivo").forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status) },
                            onClick = {
                                statusMenuOpen = false
                                onStatusChange(status)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = USBColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(17.dp),
            content = content,
        )
    }
}
