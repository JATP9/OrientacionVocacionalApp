package com.usbbog.orientacionvocacional.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.usbbog.orientacionvocacional.screens.AdminWebAccessNoticeScreen
import com.usbbog.orientacionvocacional.screens.ForgotPasswordScreen
import com.usbbog.orientacionvocacional.screens.LandingWebScreenV2
import com.usbbog.orientacionvocacional.screens.LoginScreen
import com.usbbog.orientacionvocacional.screens.MyResultsScreen
import com.usbbog.orientacionvocacional.screens.ProfileScreen
import com.usbbog.orientacionvocacional.screens.RegisterWebScreenV2
import com.usbbog.orientacionvocacional.screens.ResetPasswordScreen
import com.usbbog.orientacionvocacional.screens.ResultsScreen
import com.usbbog.orientacionvocacional.screens.TestIntroWebScreenV2
import com.usbbog.orientacionvocacional.screens.TestQuestionWebScreen
import com.usbbog.orientacionvocacional.screens.TestReviewWebScreen
import com.usbbog.orientacionvocacional.ui.mobile.UserRole
import com.usbbog.orientacionvocacional.platform.AppEnvironment
import com.usbbog.orientacionvocacional.viewmodel.ChangePasswordViewModel
import com.usbbog.orientacionvocacional.viewmodel.ForgotPasswordViewModel
import com.usbbog.orientacionvocacional.viewmodel.LoginViewModel
import com.usbbog.orientacionvocacional.viewmodel.ProfileViewModel
import com.usbbog.orientacionvocacional.viewmodel.RegisterViewModel
import com.usbbog.orientacionvocacional.viewmodel.ResetPasswordViewModel
import com.usbbog.orientacionvocacional.viewmodel.ResultsViewModel
import com.usbbog.orientacionvocacional.viewmodel.TestViewModel
import com.usbbog.orientacionvocacional.viewmodel.isValidEmail

private object Routes {
    const val LANDING = "landing"
    const val LOGIN = "login"
    const val FORGOT_PASSWORD = "forgot_password"
    const val RESET_PASSWORD = "reset_password"
    const val REGISTER = "register"
    const val BEFORE_TEST = "before_test"
    const val TEST = "test"
    const val TEST_REVIEW = "test_review"
    const val MY_RESULTS = "my_results"
    const val RESULTS = "results"
    const val PROFILE = "profile"
    const val ADMIN = "admin"
}

@Composable
fun AppNavigation(
    initialResetToken: String? = null,
    resetLinkVersion: Int = 0,
) {
    val navController = rememberNavController()
    // La factory explícita es necesaria en Kotlin/Native, donde no existe la
    // reflexión JVM usada por el constructor predeterminado de viewModel().
    val loginViewModel: LoginViewModel = viewModel { LoginViewModel() }
    val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel { ForgotPasswordViewModel() }
    val registerViewModel: RegisterViewModel = viewModel { RegisterViewModel() }
    val resetPasswordViewModel: ResetPasswordViewModel = viewModel { ResetPasswordViewModel() }
    val testViewModel: TestViewModel = viewModel { TestViewModel() }
    val resultsViewModel: ResultsViewModel = viewModel { ResultsViewModel() }
    val profileViewModel: ProfileViewModel = viewModel { ProfileViewModel() }
    val changePasswordViewModel: ChangePasswordViewModel = viewModel {
        ChangePasswordViewModel()
    }

    val loginState by loginViewModel.uiState.collectAsState()
    val forgotPasswordState by forgotPasswordViewModel.uiState.collectAsState()
    val registerState by registerViewModel.uiState.collectAsState()
    val resetPasswordState by resetPasswordViewModel.uiState.collectAsState()
    val testState by testViewModel.uiState.collectAsState()
    val resultsState by resultsViewModel.uiState.collectAsState()
    val resultsHistoryState by resultsViewModel.historyState.collectAsState()
    val profileState by profileViewModel.uiState.collectAsState()
    val changePasswordState by changePasswordViewModel.uiState.collectAsState()

    fun logout() {
        loginViewModel.clearSession()
        forgotPasswordViewModel.reset()
        registerViewModel.resetForm()
        resetPasswordViewModel.clear()
        testViewModel.resetTest()
        resultsViewModel.clearResults()
        profileViewModel.clearProfile()
        changePasswordViewModel.clear()
        navController.navigate(Routes.LANDING) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun sessionExpired() {
        loginViewModel.clearSession()
        testViewModel.resetTest()
        resultsViewModel.clearResults()
        profileViewModel.clearProfile()
        changePasswordViewModel.clear()
        loginViewModel.showError("Tu sesión expiró. Inicia sesión nuevamente.")
        navController.navigate(Routes.LOGIN) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
        }
    }

    LaunchedEffect(initialResetToken, resetLinkVersion) {
        if (!initialResetToken.isNullOrBlank()) {
            resetPasswordViewModel.setToken(initialResetToken)
            navController.navigate(Routes.RESET_PASSWORD) {
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (initialResetToken.isNullOrBlank()) {
            Routes.LANDING
        } else {
            Routes.RESET_PASSWORD
        },
    ) {
        composable(Routes.LANDING) {
            LaunchedEffect(loginState.authenticatedRole) {
                loginState.authenticatedRole?.let { role ->
                    profileViewModel.loadFromLogin(
                        identifier = loginState.identifier,
                        role = role,
                        onSessionExpired = ::sessionExpired,
                    )
                    navController.navigate(
                        if (role == UserRole.Admin) Routes.ADMIN else Routes.BEFORE_TEST,
                    ) {
                        popUpTo(Routes.LANDING) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            LandingWebScreenV2(
                onStartClick = { navController.navigate(Routes.LOGIN) { launchSingleTop = true } },
                onLoginClick = { navController.navigate(Routes.LOGIN) { launchSingleTop = true } },
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                identifier = loginState.identifier,
                password = loginState.password,
                rememberMe = loginState.rememberMe,
                isLoading = loginState.isLoading,
                errorMessage = loginState.errorMessage,
                onIdentifierChange = loginViewModel::onIdentifierChange,
                onPasswordChange = loginViewModel::onPasswordChange,
                onRememberChange = loginViewModel::onRememberChange,
                onLoginClick = {
                    loginViewModel.login { role ->
                        val currentSession = loginViewModel.uiState.value
                        profileViewModel.loadFromLogin(
                            identifier = currentSession.identifier,
                            role = role,
                            onSessionExpired = ::sessionExpired,
                        )
                        navController.navigate(
                            if (role == UserRole.Admin) Routes.ADMIN else Routes.BEFORE_TEST,
                        ) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onRegisterClick = {
                    loginViewModel.clearError()
                    navController.navigate(Routes.REGISTER) { launchSingleTop = true }
                },
                onForgotPasswordClick = {
                    loginViewModel.clearError()
                    forgotPasswordViewModel.prefillEmail(
                        loginState.identifier.takeIf(::isValidEmail).orEmpty(),
                    )
                    navController.navigate(Routes.FORGOT_PASSWORD) { launchSingleTop = true }
                },
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                email = forgotPasswordState.email,
                document = forgotPasswordState.document,
                isSubmitting = forgotPasswordState.isSubmitting,
                emailError = forgotPasswordState.emailError,
                documentError = forgotPasswordState.documentError,
                statusMessage = forgotPasswordState.statusMessage,
                isSuccess = forgotPasswordState.isSuccess,
                onEmailChange = forgotPasswordViewModel::onEmailChange,
                onDocumentChange = forgotPasswordViewModel::onDocumentChange,
                onRecoverClick = forgotPasswordViewModel::recoverPassword,
                onBackToLoginClick = {
                    forgotPasswordViewModel.reset()
                    if (!navController.popBackStack()) {
                        navController.navigate(Routes.LOGIN) { launchSingleTop = true }
                    }
                },
            )
        }

        composable(Routes.RESET_PASSWORD) {
            ResetPasswordScreen(
                state = resetPasswordState,
                onPasswordChange = resetPasswordViewModel::onPasswordChange,
                onConfirmPasswordChange = resetPasswordViewModel::onConfirmPasswordChange,
                onResetClick = resetPasswordViewModel::resetPassword,
                onRequestAgainClick = {
                    resetPasswordViewModel.clear()
                    navController.navigate(Routes.FORGOT_PASSWORD) {
                        popUpTo(Routes.RESET_PASSWORD) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackToLoginClick = {
                    resetPasswordViewModel.clear()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.RESET_PASSWORD) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(Routes.REGISTER) {
            LaunchedEffect(Unit) {
                registerViewModel.loadCatalogs()
            }
            RegisterWebScreenV2(
                state = registerState,
                onFieldChange = registerViewModel::onFieldChange,
                onBirthDateChange = registerViewModel::onBirthDateChange,
                onInstitutionLinkedChoiceChange =
                    registerViewModel::onInstitutionLinkedChoiceChange,
                onInstitutionRelationshipChange =
                    registerViewModel::onInstitutionRelationshipChange,
                onPersonalDataConsentChange =
                    registerViewModel::onPersonalDataConsentChange,
                onPrivacyPolicyChange = registerViewModel::onPrivacyPolicyChange,
                onTermsChange = registerViewModel::onTermsChange,
                onAdultConfirmedChange = registerViewModel::onAdultConfirmedChange,
                onRegisterClick = {
                    registerViewModel.register { identifier, message ->
                        loginViewModel.prefillIdentifier(identifier)
                        loginViewModel.showError(message)
                        registerViewModel.resetForm()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onBackToLoginClick = {
                    registerViewModel.resetForm()
                    if (!navController.popBackStack()) {
                        navController.navigate(Routes.LOGIN) { launchSingleTop = true }
                    }
                },
            )
        }

        composable(Routes.BEFORE_TEST) {
            TestIntroWebScreenV2(
                userName = profileState.fullName,
                errorMessage = testState.errorMessage,
                isLoading = testState.isLoadingQuestions,
                onStartClick = {
                    testViewModel.startAttempt(
                        audienceLabel = if (profileState.belongsToUniversity) {
                            "Usuario interno"
                        } else {
                            "Usuario externo"
                        },
                        onReady = {
                            navController.navigate(Routes.TEST) { launchSingleTop = true }
                        },
                        onSessionExpired = ::sessionExpired,
                    )
                },
                onResultsClick = {
                    navController.navigate(Routes.MY_RESULTS) { launchSingleTop = true }
                },
                onProfileClick = { navController.navigate(Routes.PROFILE) { launchSingleTop = true } },
            )
        }

        composable(Routes.TEST) {
            val currentQuestion = testState.currentQuestion
            if (currentQuestion == null || testState.attemptId == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.BEFORE_TEST) {
                        popUpTo(Routes.TEST) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            } else {
                TestQuestionWebScreen(
                    question = currentQuestion,
                    questionIndex = testState.currentQuestionIndex,
                    totalQuestions = testState.questions.size,
                    selectedOptionId = testState.selectedOptionId,
                    remainingTime = testState.remainingTime,
                    answeredQuestionNumbers = testState.answeredQuestionNumbers,
                    errorMessage = testState.errorMessage,
                    onSelectOption = testViewModel::selectOption,
                    onPreviousClick = testViewModel::previousQuestion,
                    onNextClick = {
                        if (testViewModel.nextQuestion()) {
                            navController.navigate(Routes.TEST_REVIEW) { launchSingleTop = true }
                        }
                    },
                    onReviewClick = {
                        navController.navigate(Routes.TEST_REVIEW) { launchSingleTop = true }
                    },
                    onQuestionJump = testViewModel::jumpToQuestion,
                    onExitTest = {
                        testViewModel.resetTest()
                        navController.navigate(Routes.BEFORE_TEST) {
                            popUpTo(Routes.TEST) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    audienceLabel = testState.audienceLabel,
                    versionLabel = testState.versionLabel,
                    attemptLabel = testState.attemptLabel,
                )
            }
        }

        composable(Routes.TEST_REVIEW) {
            if (testState.questions.isEmpty() || testState.attemptId == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.BEFORE_TEST) {
                        popUpTo(Routes.TEST_REVIEW) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            } else {
                TestReviewWebScreen(
                    questions = testState.questions,
                    answeredQuestionNumbers = testState.answeredQuestionNumbers,
                    isSubmitting = resultsState.isLoading,
                    errorMessage = resultsState.errorMessage ?: testState.errorMessage,
                    onEditQuestion = { index ->
                        testViewModel.jumpToQuestion(index)
                        if (!navController.popBackStack()) {
                            navController.navigate(Routes.TEST) { launchSingleTop = true }
                        }
                    },
                    onSubmitClick = { satisfaction ->
                        if (testState.unansweredQuestionNumbers.isNotEmpty()) {
                            testViewModel.focusFirstUnanswered()
                            if (!navController.popBackStack()) {
                                navController.navigate(Routes.TEST) { launchSingleTop = true }
                            }
                        } else if (satisfaction != null && !resultsState.isLoading) {
                            resultsViewModel.submitAttempt(
                                answers = testState.answers,
                                questions = testState.questions,
                                elapsedSeconds = testState.elapsedSeconds,
                                satisfaction = satisfaction,
                                onSuccess = {
                                    testViewModel.stopAttempt()
                                    navController.navigate(Routes.RESULTS) {
                                        popUpTo(Routes.TEST) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                onSessionExpired = ::sessionExpired,
                            )
                        }
                    },
                    onBackToQuestionsClick = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Routes.TEST) { launchSingleTop = true }
                        }
                    },
                )
            }
        }

        composable(Routes.RESULTS) {
            var isDownloading by rememberSaveable { mutableStateOf(false) }
            var downloadStatus by rememberSaveable { mutableStateOf<String?>(null) }
            val openedFromHistory =
                navController.previousBackStackEntry?.destination?.route == Routes.MY_RESULTS
            val resultsReturnRoute = if (openedFromHistory) {
                Routes.MY_RESULTS
            } else {
                Routes.BEFORE_TEST
            }

            ResultsScreen(
                userName = profileState.fullName,
                mainAreaId = resultsState.mainAreaId,
                mainArea = resultsState.mainArea,
                summary = resultsState.summary,
                scores = resultsState.scores,
                careers = resultsState.careers,
                generatedAt = resultsState.generatedAt,
                isDownloading = isDownloading,
                downloadStatus = downloadStatus,
                backLabel = if (openedFromHistory) {
                    "Volver a mis resultados"
                } else {
                    "Volver al inicio de la prueba"
                },
                onBackClick = {
                    if (!navController.popBackStack(resultsReturnRoute, false)) {
                        navController.navigate(resultsReturnRoute) {
                            popUpTo(Routes.RESULTS) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onDownloadClick = {
                    isDownloading = true
                    downloadStatus = runCatching {
                        val exportedReport = AppEnvironment.reportExporter.createAndShare(
                            userName = profileState.fullName,
                            result = resultsState,
                        )
                        "Informe generado: ${exportedReport.fileName}"
                    }.getOrElse {
                        "No fue posible generar el PDF."
                    }
                    isDownloading = false
                },
                onProfileClick = { navController.navigate(Routes.PROFILE) { launchSingleTop = true } },
            )
        }

        composable(Routes.MY_RESULTS) {
            LaunchedEffect(Unit) {
                resultsViewModel.loadHistory(::sessionExpired)
            }

            MyResultsScreen(
                userName = profileState.fullName,
                state = resultsHistoryState,
                onResultClick = { testId ->
                    resultsViewModel.openResult(
                        testId = testId,
                        onSuccess = {
                            navController.navigate(Routes.RESULTS) {
                                launchSingleTop = true
                            }
                        },
                        onSessionExpired = ::sessionExpired,
                    )
                },
                onRetryClick = {
                    resultsViewModel.loadHistory(::sessionExpired)
                },
                onTakeTestClick = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Routes.BEFORE_TEST) { launchSingleTop = true }
                    }
                },
                onBackClick = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Routes.BEFORE_TEST) { launchSingleTop = true }
                    }
                },
                onProfileClick = {
                    navController.navigate(Routes.PROFILE) { launchSingleTop = true }
                },
            )
        }

        composable(Routes.PROFILE) {
            val isAdministrator = loginState.authenticatedRole == UserRole.Admin
            ProfileScreen(
                state = profileState,
                passwordState = changePasswordState,
                isAdministrator = isAdministrator,
                onSave = profileViewModel::updateProfile,
                onDepartmentChange = profileViewModel::onDepartmentSelectionChanged,
                onCurrentPasswordChange = changePasswordViewModel::onCurrentPasswordChange,
                onNewPasswordChange = changePasswordViewModel::onNewPasswordChange,
                onConfirmPasswordChange = changePasswordViewModel::onConfirmPasswordChange,
                onChangePasswordClick = {
                    changePasswordViewModel.changePassword(::sessionExpired)
                },
                onClearPasswordForm = changePasswordViewModel::clear,
                onDeleteAccountClick = {
                    profileViewModel.deleteAccount(
                        onDeleted = ::logout,
                        onSessionExpired = ::sessionExpired,
                    )
                },
                onClearDeleteAccountError = profileViewModel::clearDeleteAccountError,
                onAdminClick = { navController.navigate(Routes.ADMIN) { launchSingleTop = true } },
                onLogoutClick = ::logout,
                onBackClick = { navController.popBackStack() },
            )
        }

        composable(Routes.ADMIN) {
            if (loginState.authenticatedRole != UserRole.Admin) {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ADMIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            } else {
                AdminWebAccessNoticeScreen(
                    administratorName = profileState.fullName,
                    onAcknowledge = ::logout,
                )
            }
        }
    }
}
