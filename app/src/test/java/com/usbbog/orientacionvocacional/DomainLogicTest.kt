package com.usbbog.orientacionvocacional

import com.usbbog.orientacionvocacional.ui.mobile.QuestionOptionUi
import com.usbbog.orientacionvocacional.ui.mobile.QuestionUi
import com.usbbog.orientacionvocacional.ui.mobile.RegisterUiState
import com.usbbog.orientacionvocacional.ui.mobile.TestUiState
import com.usbbog.orientacionvocacional.ui.mobile.UserRole
import com.usbbog.orientacionvocacional.ui.mobile.formatTestDuration
import com.usbbog.orientacionvocacional.viewmodel.TestViewModel
import com.usbbog.orientacionvocacional.viewmodel.birthDateValidationError
import com.usbbog.orientacionvocacional.viewmodel.isValidEmail
import com.usbbog.orientacionvocacional.viewmodel.isValidPhone
import com.usbbog.orientacionvocacional.viewmodel.passwordValidationError
import com.usbbog.orientacionvocacional.viewmodel.toUserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

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
        val now = utcDate(2026, Calendar.AUGUST, 15)
        val minor = utcDate(2010, Calendar.AUGUST, 15)
        val adult = utcDate(2000, Calendar.AUGUST, 15)

        assertNotNull(birthDateValidationError(minor, now))
        assertNull(birthDateValidationError(adult, now))
    }

    private fun utcDate(year: Int, month: Int, day: Int): Long =
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(year, month, day)
        }.timeInMillis
}
