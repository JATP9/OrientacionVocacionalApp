package com.usbbog.orientacionvocacional

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.usbbog.orientacionvocacional.navigation.AppNavigation
import com.usbbog.orientacionvocacional.ui.theme.OrientacionVocacionalAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContent {
            OrientacionVocacionalAppTheme {
                AppNavigation()
            }
        }
    }
}