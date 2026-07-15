package com.usbbog.orientacionvocacional.ui.mobile

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usbbog.orientacionvocacional.ui.theme.USBColors

private val CardShape = RoundedCornerShape(24.dp)
private val InputShape = RoundedCornerShape(16.dp)
private val PillShape = RoundedCornerShape(100.dp)

@Composable
fun UsbGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(
                colors = listOf(
                    USBColors.Orange,
                    Color(0xFFE9851E),
                    Color(0xFF9F6842),
                    USBColors.Blue,
                ),
            ),
        ),
    ) {
        content()
    }
}

@Composable
fun UsbBrandBar(
    userName: String? = null,
    actionLabel: String? = null,
    onHelpClick: () -> Unit = {},
    onUserClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(USBColors.White)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "USB",
                style = MaterialTheme.typography.titleLarge,
                color = USBColors.Orange,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Orientación vocacional",
                style = MaterialTheme.typography.bodyMedium,
                color = USBColors.TextMuted,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            UsbCompactAction(text = "Ayuda", onClick = onHelpClick)
            val mainAction = userName?.takeIf { it.isNotBlank() } ?: actionLabel?.takeIf { it.isNotBlank() }
            if (mainAction != null) {
                UsbCompactAction(
                    text = mainAction,
                    filled = true,
                    onClick = onUserClick,
                )
            }
        }
    }
}

@Composable
private fun UsbCompactAction(
    text: String,
    filled: Boolean = false,
    onClick: () -> Unit,
) {
    val background = if (filled) USBColors.Orange else USBColors.White
    val content = if (filled) USBColors.White else USBColors.TextMuted
    val border = if (filled) null else BorderStroke(1.dp, Color(0xFFBFBFBF))

    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = PillShape,
        colors = CardDefaults.cardColors(containerColor = background),
        border = border,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            color = content,
            maxLines = 1,
        )
    }
}

@Composable
fun UsbCard(
    modifier: Modifier = Modifier,
    containerColor: Color = USBColors.White,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = CardShape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f),
            ),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, USBColors.Border),
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            content = content,
        )
    }
}

@Composable
fun UsbPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 58.dp),
        enabled = enabled && !loading,
        shape = PillShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = USBColors.Orange,
            contentColor = USBColors.White,
            disabledContainerColor = USBColors.Orange.copy(alpha = 0.45f),
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = USBColors.White,
            )
        } else {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun UsbSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        enabled = enabled,
        shape = PillShape,
        border = BorderStroke(1.dp, Color(0xFFD7D5D0)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = USBColors.Black),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun UsbTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    errorText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    enabled: Boolean = true,
    singleLine: Boolean = true,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = USBColors.Black,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                if (placeholder.isNotBlank()) {
                    Text(placeholder, color = Color(0xFF989898))
                }
            },
            enabled = enabled,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            singleLine = singleLine,
            shape = InputShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = USBColors.Orange,
                unfocusedBorderColor = Color(0xFFD7D5D0),
                focusedContainerColor = USBColors.White,
                unfocusedContainerColor = USBColors.White,
                errorBorderColor = USBColors.Danger,
            ),
        )
        if (!errorText.isNullOrBlank()) {
            Text(
                text = errorText,
                color = USBColors.Danger,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun UsbCheckboxLine(
    checked: Boolean,
    text: String,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(InputShape)
            .background(USBColors.Cream)
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = USBColors.Orange,
                uncheckedColor = USBColors.TextMuted,
            ),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            modifier = Modifier.padding(top = 11.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = USBColors.Black,
        )
    }
}

@Composable
fun UsbProgress(
    progress: Float,
    label: String,
    trailingText: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(
                trailingText,
                style = MaterialTheme.typography.bodyMedium,
                color = USBColors.TextMuted,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(PillShape)
                .background(Color(0xFFF0E7DB)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .height(14.dp)
                    .clip(PillShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(USBColors.Orange, Color(0xFFF6B05D)),
                        ),
                    ),
            )
        }
    }
}

@Composable
fun UsbAnswerOption(
    option: QuestionOptionUi,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (selected) USBColors.Orange else Color(0xFFE7DDCF)
    val container = if (selected) Color(0xFFFFF4E7) else USBColors.White

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        border = BorderStroke(2.dp, borderColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = USBColors.Orange,
                    unselectedColor = Color(0xFFD8C8B5),
                ),
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = USBColors.Black,
                )
                if (option.description.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = option.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = USBColors.TextMuted,
                    )
                }
            }
        }
    }
}

@Composable
fun UsbSectionTitle(
    eyebrow: String? = null,
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (!eyebrow.isNullOrBlank()) {
            Text(
                text = eyebrow.uppercase(),
                color = USBColors.Orange,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 13.sp,
                    letterSpacing = 1.4.sp,
                ),
            )
            Spacer(Modifier.height(8.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = USBColors.Black,
        )
        if (!description.isNullOrBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = USBColors.TextMuted,
            )
        }
    }
}

@Composable
fun UsbErrorBanner(
    message: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = message,
        modifier = modifier
            .fillMaxWidth()
            .clip(InputShape)
            .background(USBColors.Danger.copy(alpha = 0.08f))
            .padding(14.dp),
        color = USBColors.Danger,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
fun UsbStatusPill(
    text: String,
    modifier: Modifier = Modifier,
    danger: Boolean = false,
) {
    val background = if (danger) USBColors.Danger.copy(alpha = 0.08f) else USBColors.OrangeSoft
    val content = if (danger) USBColors.Danger else USBColors.Orange

    Text(
        text = text,
        modifier = modifier
            .clip(PillShape)
            .background(background)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        color = content,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 1,
    )
}

@Composable
fun UsbDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = USBColors.Border,
    )
}
