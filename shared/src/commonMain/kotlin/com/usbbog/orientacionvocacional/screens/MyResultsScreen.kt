package com.usbbog.orientacionvocacional.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usbbog.orientacionvocacional.ui.components.UsbAppFooter
import com.usbbog.orientacionvocacional.ui.components.UsbAppTopBar
import com.usbbog.orientacionvocacional.ui.mobile.ResultHistoryItemUi
import com.usbbog.orientacionvocacional.ui.mobile.ResultsHistoryUiState
import com.usbbog.orientacionvocacional.ui.theme.USBColors

private val HistoryPanel = Color(0xFFFFFDFB)
private val HistoryCardBorder = Color(0xFFE4DDD3)
private val HistoryCardSoft = Color(0xFFFCFAF7)
private val HistoryGradientMiddle = Color(0xFF8B3E38)

@Composable
fun MyResultsScreen(
    userName: String,
    state: ResultsHistoryUiState,
    onResultClick: (Long) -> Unit,
    onRetryClick: () -> Unit,
    onTakeTestClick: () -> Unit,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            UsbAppTopBar(
                userLabel = userName.ifBlank { "Usuario" },
                onUserClick = onProfileClick,
            )
        },
        containerColor = USBColors.Orange,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        0f to USBColors.Orange,
                        0.42f to HistoryGradientMiddle,
                        1f to USBColors.Blue,
                    ),
                ),
            contentPadding = PaddingValues(top = 14.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            item {
                BackToHomeAction(
                    onClick = onBackClick,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            }

            item {
                ResultsHistoryHeader(
                    resultCount = state.items.size,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            }

            state.errorMessage?.takeIf(String::isNotBlank)?.let { message ->
                item {
                    HistoryErrorCard(
                        message = message,
                        onRetryClick = onRetryClick,
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                }
            }

            when {
                state.isLoading -> item {
                    LoadingHistoryCard(
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                }

                state.items.isEmpty() && state.errorMessage.isNullOrBlank() -> item {
                    EmptyHistoryCard(
                        onTakeTestClick = onTakeTestClick,
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                }

                state.items.isNotEmpty() -> {
                    item {
                        ResultsListHeading(
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                    }

                    itemsIndexed(
                        items = state.items,
                        key = { _, item -> item.id },
                    ) { index, item ->
                        ResultHistoryCard(
                            item = item,
                            isLatest = index == 0,
                            isOpening = state.openingResultId == item.id,
                            enabled = state.openingResultId == null,
                            onClick = { onResultClick(item.id) },
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                    }
                }

                else -> Unit
            }

            item {
                Spacer(Modifier.height(9.dp))
                UsbAppFooter()
            }
        }
    }
}

@Composable
private fun BackToHomeAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(USBColors.White.copy(alpha = 0.96f))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.ArrowBack,
            contentDescription = null,
            tint = USBColors.Orange,
            modifier = Modifier.size(19.dp),
        )
        Text(
            text = "Volver al inicio",
            color = USBColors.Orange,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ResultsHistoryHeader(
    resultCount: Int,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = HistoryPanel),
        border = BorderStroke(1.dp, HistoryCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(17.dp),
                colors = CardDefaults.cardColors(containerColor = USBColors.White),
                border = BorderStroke(1.dp, HistoryCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            ) {
                Text(
                    text = "Resultados de tus pruebas vocacionales",
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 16.dp),
                    color = USBColors.Orange,
                    fontSize = 22.sp,
                    lineHeight = 27.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(17.dp))
                    .background(USBColors.White)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(USBColors.OrangeSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Assessment,
                        contentDescription = null,
                        tint = USBColors.Orange,
                        modifier = Modifier.size(26.dp),
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mis resultados",
                        color = USBColors.Black,
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = "Revisa cada prueba y abre nuevamente su informe de afinidad y carreras.",
                        color = USBColors.TextMuted,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(USBColors.OrangeSoft)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = null,
                            tint = USBColors.Orange,
                            modifier = Modifier.size(15.dp),
                        )
                        Text(
                            text = when (resultCount) {
                                1 -> "1 prueba guardada"
                                else -> "$resultCount pruebas guardadas"
                            },
                            color = USBColors.Orange,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultsListHeading(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = USBColors.White),
        border = BorderStroke(1.dp, HistoryCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(
                text = "Pruebas realizadas",
                color = USBColors.Orange,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Selecciona una prueba para consultar su resultado completo.",
                color = USBColors.TextMuted,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
        }
    }
}

@Composable
private fun LoadingHistoryCard(
    modifier: Modifier = Modifier,
) {
    BrandedStateCard(modifier = modifier) {
        CircularProgressIndicator(
            modifier = Modifier.size(36.dp),
            color = USBColors.Orange,
            strokeWidth = 3.dp,
        )
        Spacer(Modifier.height(13.dp))
        Text(
            text = "Consultando tus resultados...",
            color = USBColors.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = "Estamos recuperando las pruebas asociadas a tu cuenta.",
            color = USBColors.TextMuted,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun EmptyHistoryCard(
    onTakeTestClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BrandedStateCard(modifier = modifier) {
        StateIcon(icon = Icons.Outlined.Quiz)
        Spacer(Modifier.height(14.dp))
        Text(
            text = "Aún no tienes resultados",
            color = USBColors.Black,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = "Completa tu primera prueba vocacional para descubrir tus áreas de afinidad y programas recomendados.",
            color = USBColors.TextMuted,
            fontSize = 13.5.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(17.dp))
        HistoryPrimaryButton(
            text = "Realizar mi primera prueba",
            onClick = onTakeTestClick,
        )
    }
}

@Composable
private fun HistoryErrorCard(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BrandedStateCard(modifier = modifier) {
        StateIcon(
            icon = Icons.Outlined.Refresh,
            backgroundColor = USBColors.Danger.copy(alpha = 0.10f),
            iconColor = USBColors.Danger,
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = "No pudimos cargar tus resultados",
            color = USBColors.Black,
            fontSize = 18.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = message,
            color = USBColors.TextMuted,
            fontSize = 13.5.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(17.dp))
        OutlinedButton(
            onClick = onRetryClick,
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            border = BorderStroke(1.5.dp, USBColors.Orange),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = USBColors.Orange),
            contentPadding = PaddingValues(vertical = 12.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text("Intentar nuevamente", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BrandedStateCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = USBColors.White),
        border = BorderStroke(1.dp, HistoryCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )
    }
}

@Composable
private fun StateIcon(
    icon: ImageVector,
    backgroundColor: Color = USBColors.OrangeSoft,
    iconColor: Color = USBColors.Orange,
) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(27.dp),
        )
    }
}

@Composable
private fun ResultHistoryCard(
    item: ResultHistoryItemUi,
    isLatest: Boolean,
    isOpening: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = USBColors.White),
        border = BorderStroke(1.5.dp, USBColors.Orange.copy(alpha = 0.75f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(11.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(USBColors.Orange),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item.attemptNumber.toString(),
                        color = USBColors.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Prueba vocacional",
                        color = USBColors.Black,
                        fontSize = 17.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = USBColors.Success,
                            modifier = Modifier.size(15.dp),
                        )
                        Text(
                            text = "Completada",
                            color = USBColors.Success,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                if (isLatest) {
                    Text(
                        text = "RECIENTE",
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(USBColors.OrangeSoft)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        color = USBColors.Orange,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.35.sp,
                    )
                }
            }

            Spacer(Modifier.height(13.dp))

            HistoryMetadataTile(
                icon = Icons.Outlined.CalendarMonth,
                label = "Fecha de finalización",
                value = item.completedAt,
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HistoryMetadataTile(
                    icon = Icons.Outlined.Schedule,
                    label = "Duración",
                    value = item.duration,
                    modifier = Modifier.weight(1f),
                )
                HistoryMetadataTile(
                    icon = Icons.Outlined.History,
                    label = "Versión",
                    value = item.version,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(13.dp))

            Button(
                onClick = onClick,
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = USBColors.Orange,
                    contentColor = USBColors.White,
                    disabledContainerColor = USBColors.Orange.copy(alpha = 0.55f),
                    disabledContentColor = USBColors.White,
                ),
                contentPadding = PaddingValues(horizontal = 14.dp),
            ) {
                if (isOpening) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = USBColors.White,
                        strokeWidth = 2.dp,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Abriendo resultado...", fontWeight = FontWeight.Bold)
                } else {
                    Text("Ver resultado", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(7.dp))
                    Icon(
                        imageVector = Icons.Outlined.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryMetadataTile(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(HistoryCardSoft)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = USBColors.Orange,
            modifier = Modifier
                .padding(top = 1.dp)
                .size(17.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = USBColors.TextMuted,
                fontSize = 10.5.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = value,
                color = USBColors.Text,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun HistoryPrimaryButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = USBColors.Orange,
            contentColor = USBColors.White,
        ),
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}
