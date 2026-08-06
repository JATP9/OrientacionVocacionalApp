package com.usbbog.orientacionvocacional.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usbbog.orientacionvocacional.R
import com.usbbog.orientacionvocacional.ui.theme.USBColors

@Composable
fun UsbHeaderLogo(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(R.drawable.usb_header_logo),
        contentDescription = "Universidad de San Buenaventura",
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}

@Composable
fun UsbFooterLogo(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(R.drawable.usb_footer_brand),
        contentDescription = "Universidad de San Buenaventura, sede Bogotá",
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}

@Composable
fun UsbAppTopBar(
    userLabel: String? = null,
    onUserClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .heightIn(min = 68.dp)
            .shadow(elevation = 4.dp)
            .background(USBColors.White)
            .padding(
                horizontal = 12.dp,
                vertical = 10.dp,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            UsbHeaderLogo(
                modifier = Modifier
                    .width(143.dp)
                    .height(44.dp),
            )
        }

        if (userLabel != null) {
            Spacer(
                modifier = Modifier.width(6.dp),
            )

            Box(
                modifier = Modifier
                    .height(42.dp)
                    .widthIn(
                        min = 76.dp,
                        max = 118.dp,
                    )
                    .clip(RoundedCornerShape(100.dp))
                    .background(USBColors.Orange)
                    .then(
                        if (onUserClick != null) {
                            Modifier.clickable(
                                onClick = onUserClick,
                            )
                        } else {
                            Modifier
                        },
                    )
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = userLabel
                        .substringBefore(" ")
                        .ifBlank { "Usuario" },
                    color = USBColors.White,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun UsbAppFooter(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(USBColors.Blue)
            .navigationBarsPadding()
            .padding(
                horizontal = 18.dp,
                vertical = 14.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        UsbFooterLogo(
            modifier = Modifier
                .width(230.dp)
                .height(46.dp),
        )

        Spacer(
            modifier = Modifier.height(7.dp),
        )

        Text(
            text =
                "Somos una institución educativa de la Comunidad " +
                        "Franciscana Provincia de la Santa Fe de educación " +
                        "superior con personería jurídica reconocida por el " +
                        "Ministerio de Educación en Resolución 1326 del 25 " +
                        "de marzo de 1975.",
            color = USBColors.White.copy(alpha = 0.88f),
            fontSize = 8.5.sp,
            lineHeight = 12.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(
            modifier = Modifier.height(3.dp),
        )

        Text(
            text =
                "Copyright © 2026 Universidad de San Buenaventura, " +
                        "Sede Bogotá | Políticas de uso y privacidad | " +
                        "Términos y Condiciones",
            color = USBColors.White.copy(alpha = 0.78f),
            fontSize = 8.5.sp,
            lineHeight = 12.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(
            modifier = Modifier.height(2.dp),
        )

        Text(
            text =
                "Institución de educación superior sujeta a la " +
                        "inspección y vigilancia del Ministerio de " +
                        "Educación Nacional.",
            color = USBColors.White.copy(alpha = 0.78f),
            fontSize = 8.5.sp,
            lineHeight = 12.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun UsbGuideImage(
    @DrawableRes drawableRes: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(drawableRes),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}