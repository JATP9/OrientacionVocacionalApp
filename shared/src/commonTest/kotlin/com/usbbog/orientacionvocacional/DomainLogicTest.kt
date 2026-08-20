package com.usbbog.orientacionvocacional

import com.usbbog.orientacionvocacional.ui.mobile.QuestionOptionUi
import com.usbbog.orientacionvocacional.ui.mobile.QuestionUi
import com.usbbog.orientacionvocacional.ui.mobile.RegisterUiState
import com.usbbog.orientacionvocacional.ui.mobile.TestUiState
import com.usbbog.orientacionvocacional.ui.mobile.UserRole
import com.usbbog.orientacionvocacional.ui.mobile.formatTestDuration
import com.usbbog.orientacionvocacional.platform.CivilDate
import com.usbbog.orientacionvocacional.platform.civilDateFromEpochMillis
import com.usbbog.orientacionvocacional.platform.toEpochMillis
import com.usbbog.orientacionvocacional.viewmodel.TestViewModel
import com.usbbog.orientacionvocacional.viewmodel.birthDateValidationError
import com.usbbog.orientacionvocacional.viewmodel.isValidEmail
import com.usbbog.orientacionvocacional.viewmodel.isValidPhone
import com.usbbog.orientacionvocacional.viewmodel.passwordValidationError
import com.usbbog.orientacionvocacional.viewmodel.toUserRole
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DomainLogicTest {

    @Test
    fun backendRolesMapToMobileAccess() {
        assertEquals(UserRole.Admin, "ROOT".toUserRole())
        assertEquals(UserRole.Admin, "ADMINISTRADOR".toUserRole())
        assertEquals(UserRole.Student, "ESTUDIANTE".toUserRole())
    }

    @Test
    fun testStateTracksTheRequiredQuestionBank() {
        val questions = (1..TestViewModel.EXPECTED_QUESTION_COUNT).map { id ->
            QuestionUi(
                id = id.toString(),
                backendId = id.toLong(),
                code = "P$id",
                statement = "Pregunta $id",
                options = listOf(QuestionOptionUi("1", "Rara vez")),
            )
        }
        val answers = questions.associate { it.id to "1" }
        val state = TestUiState(questions = questions, answers = answers)

        assertEquals(180, state.questions.size)
        assertEquals(100, state.progressPercent)
        assertTrue(state.unansweredQuestionNumbers.isEmpty())
    }

    @Test
    fun elapsedTimeUsesHoursMinutesAndSeconds() {
        assertEquals("01:01:01", formatTestDuration(3_661))
    }

    @Test
    fun authenticationRulesMatchTheWebFrontend() {
        assertTrue(isValidEmail("usuario@example.com"))
        assertFalse(isValidEmail("usuario"))
        assertTrue(isValidPhone("+57 3001234567"))
        assertFalse(isValidPhone("123"))
        assertNull(passwordValidationError("ClaveSegura1!"))
        assertNotNull(passwordValidationError("sin-mayuscula1!"))
        assertNotNull(passwordValidationError("SinNumero!"))
    }

    @Test
    fun registrationOnlyRequestsAcademicDataForCurrentStudents() {
        assertFalse(RegisterUiState().requiresAcademicData)
        assertFalse(
            RegisterUiState(
                institutionLinkedChoice = "Sí",
                institutionRelationship = "Inscrito",
            ).requiresAcademicData,
        )
        assertTrue(
            RegisterUiState(
                institutionLinkedChoice = "Sí",
                institutionRelationship = "Estudiante",
            ).requiresAcademicData,
        )
    }

    @Test
    fun birthDateRejectsMinorsWithAStableClock() {
        val now = CivilDate(2026, 8, 15).toEpochMillis()
        val minor = CivilDate(2010, 8, 15).toEpochMillis()
        val adult = CivilDate(2000, 8, 15).toEpochMillis()

        assertNotNull(birthDateValidationError(minor, now))
        assertNull(birthDateValidationError(adult, now))
    }

    @Test
    fun civilDatesRoundTripAcrossLeapDays() {
        val leapDay = CivilDate(2024, 2, 29)
        assertEquals(leapDay, civilDateFromEpochMillis(leapDay.toEpochMillis()))
        assertEquals(CivilDate(2023, 2, 28), leapDay.minusYears(1))
    }
}
