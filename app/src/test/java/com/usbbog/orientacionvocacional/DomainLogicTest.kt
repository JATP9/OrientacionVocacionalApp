package com.usbbog.orientacionvocacional

import com.usbbog.orientacionvocacional.ui.mobile.QuestionOptionUi
import com.usbbog.orientacionvocacional.ui.mobile.QuestionUi
import com.usbbog.orientacionvocacional.ui.mobile.UserRole
import com.usbbog.orientacionvocacional.ui.mobile.VocationalArea
import com.usbbog.orientacionvocacional.viewmodel.AdminViewModel
import com.usbbog.orientacionvocacional.viewmodel.LoginViewModel
import com.usbbog.orientacionvocacional.viewmodel.ResultsViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainLogicTest {

    @Test
    fun administrativeEmailOpensAdministrativeRole() {
        val viewModel = LoginViewModel()
        viewModel.onEmailChange("admin@usb.edu.co")
        viewModel.onPasswordChange("secreto")

        assertTrue(viewModel.login())
        assertEquals(UserRole.Admin, viewModel.uiState.value.authenticatedRole)
    }

    @Test
    fun resultsContainFiveAreasAndThreeRecommendations() {
        val questions = VocationalArea.entries.mapIndexed { index, area ->
            QuestionUi(
                id = "question_${index + 1}",
                statement = "Pregunta de $area",
                area = area,
                options = listOf(QuestionOptionUi("5", "Totalmente de acuerdo")),
            )
        }
        val answers = questions.associate { it.id to "5" }
        val viewModel = ResultsViewModel()

        viewModel.generateResults(answers, questions)

        assertTrue(viewModel.uiState.value.isReady)
        assertEquals(5, viewModel.uiState.value.scores.size)
        assertEquals(3, viewModel.uiState.value.careers.size)
    }

    @Test
    fun administrativeMetricsUseConsistentTotals() {
        val state = AdminViewModel().uiState.value

        assertEquals(1284, state.internalUsers + state.externalUsers)
        assertEquals(1284, state.geographicDistribution.sumOf { it.users })
        assertEquals(1105, state.geographicDistribution.sumOf { it.completedTests })
    }
}
