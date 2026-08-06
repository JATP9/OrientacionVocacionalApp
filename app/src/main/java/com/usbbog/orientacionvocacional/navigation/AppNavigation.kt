package com.usbbog.orientacionvocacional.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.usbbog.orientacionvocacional.screens.AdminWebAccessNoticeScreen
import com.usbbog.orientacionvocacional.screens.ForgotPasswordScreen
import com.usbbog.orientacionvocacional.screens.LandingWebScreenV2
import com.usbbog.orientacionvocacional.screens.LoginScreen
import com.usbbog.orientacionvocacional.screens.ProfileScreen
import com.usbbog.orientacionvocacional.screens.RegisterWebScreenV2
import com.usbbog.orientacionvocacional.screens.ResultsScreen
import com.usbbog.orientacionvocacional.screens.TestIntroWebScreenV2
import com.usbbog.orientacionvocacional.screens.TestQuestionWebScreen
import com.usbbog.orientacionvocacional.screens.TestReviewWebScreen
import com.usbbog.orientacionvocacional.ui.mobile.UserRole
import com.usbbog.orientacionvocacional.util.ResultsPdfExporter
import com.usbbog.orientacionvocacional.viewmodel.ForgotPasswordViewModel
import com.usbbog.orientacionvocacional.viewmodel.LoginViewModel
import com.usbbog.orientacionvocacional.viewmodel.ProfileViewModel
import com.usbbog.orientacionvocacional.viewmodel.RegisterViewModel
import com.usbbog.orientacionvocacional.viewmodel.ResultsViewModel
import com.usbbog.orientacionvocacional.viewmodel.TestViewModel

private object Routes {
    const val LANDING = "landing"
    const val LOGIN = "login"
    const val FORGOT_PASSWORD = "forgot_password"
    const val REGISTER = "register"
    const val BEFORE_TEST = "before_test"
    const val TEST = "test"
    const val TEST_REVIEW = "test_review"
    const val RESULTS = "results"
    const val PROFILE = "profile"
    const val ADMIN = "admin"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val loginViewModel: LoginViewModel = viewModel()
    val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
    val registerViewModel: RegisterViewModel = viewModel()
    val testViewModel: TestViewModel = viewModel()
    val resultsViewModel: ResultsViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()

    val loginState by loginViewModel.uiState.collectAsState()
    val forgotPasswordState by forgotPasswordViewModel.uiState.collectAsState()
    val registerState by registerViewModel.uiState.collectAsState()
    val testState by testViewModel.uiState.collectAsState()
    val resultsState by resultsViewModel.uiState.collectAsState()
    val profileState by profileViewModel.uiState.collectAsState()

    fun logout() {
        loginViewModel.clearSession()
        forgotPasswordViewModel.reset()
        registerViewModel.resetForm()
        testViewModel.resetTest()
        resultsViewModel.clearResults()
        profileViewModel.clearProfile()
        navController.navigate(Routes.LANDING) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LANDING,
    ) {
        composable(Routes.LANDING) {
            LandingWebScreenV2(
                onStartClick = { navController.navigate(Routes.LOGIN) { launchSingleTop = true } },
                onLoginClick = { navController.navigate(Routes.LOGIN) { launchSingleTop = true } },
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                email = loginState.email,
                password = loginState.password,
                rememberMe = loginState.rememberMe,
                isLoading = loginState.isLoading,
                errorMessage = loginState.errorMessage,
                onEmailChange = loginViewModel::onEmailChange,
                onPasswordChange = loginViewModel::onPasswordChange,
                onRememberChange = loginViewModel::onRememberChange,
                onLoginClick = {
                    if (loginViewModel.login()) {
                        val currentSession = loginViewModel.uiState.value
                        val role = currentSession.authenticatedRole ?: UserRole.Student
                        profileViewModel.loadFromLogin(currentSession.email, role)
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
                    forgotPasswordViewModel.prefillEmail(loginState.email)
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

        composable(Routes.REGISTER) {
            RegisterWebScreenV2(
                state = registerState,
                onFieldChange = registerViewModel::onFieldChange,
                onBirthDateChange = registerViewModel::onBirthDateChange,
                onBelongsToUniversityChange = registerViewModel::onBelongsToUniversityChange,
                onActiveStudentChange = registerViewModel::onActiveStudentChange,
                onAcceptTermsChange = registerViewModel::onAcceptTermsChange,
                onAuthorizeDataChange = registerViewModel::onAuthorizeDataChange,
                onRegisterClick = {
                    if (registerViewModel.register()) {
                        val registeredUser = registerViewModel.uiState.value
                        profileViewModel.loadFromRegistration(registeredUser)
                        loginViewModel.prefillEmail(registeredUser.email)
                        registerViewModel.resetForm()

                        if (!navController.popBackStack(Routes.LOGIN, inclusive = false)) {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.REGISTER) { inclusive = true }
                                launchSingleTop = true
                            }
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
                onStartClick = {
                    testViewModel.startAttempt(
                        audienceLabel = if (profileState.belongsToUniversity) {
                            "Usuario interno"
                        } else {
                            "Usuario externo"
                        },
                    )
                    navController.navigate(Routes.TEST) { launchSingleTop = true }
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
            var isSubmitting by rememberSaveable { mutableStateOf(false) }
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
                    isSubmitting = isSubmitting,
                    errorMessage = testState.errorMessage,
                    onEditQuestion = { index ->
                        testViewModel.jumpToQuestion(index)
                        if (!navController.popBackStack()) {
                            navController.navigate(Routes.TEST) { launchSingleTop = true }
                        }
                    },
                    onSubmitClick = {
                        if (testState.unansweredQuestionNumbers.isNotEmpty()) {
                            testViewModel.focusFirstUnanswered()
                            if (!navController.popBackStack()) {
                                navController.navigate(Routes.TEST) { launchSingleTop = true }
                            }
                        } else if (!isSubmitting) {
                            isSubmitting = true
                            testViewModel.stopAttempt()
                            resultsViewModel.generateResults(
                                answers = testState.answers,
                                questions = testState.questions,
                            )
                            navController.navigate(Routes.RESULTS) {
                                popUpTo(Routes.TEST) { inclusive = true }
                                launchSingleTop = true
                            }
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
            ResultsScreen(
                userName = profileState.fullName,
                mainArea = resultsState.mainArea,
                summary = resultsState.summary,
                scores = resultsState.scores,
                careers = resultsState.careers,
                generatedAt = resultsState.generatedAt,
                isDownloading = isDownloading,
                downloadStatus = downloadStatus,
                onDownloadClick = {
                    isDownloading = true
                    downloadStatus = runCatching {
                        val file = ResultsPdfExporter.createAndShare(
                            context = context,
                            userName = profileState.fullName,
                            result = resultsState,
                        )
                        "Informe creado: ${file.name}"
                    }.getOrElse {
                        "No fue posible crear el informe PDF."
                    }
                    isDownloading = false
                },
                onProfileClick = { navController.navigate(Routes.PROFILE) { launchSingleTop = true } },
            )
        }

        composable(Routes.PROFILE) {
            val isAdministrator = loginState.authenticatedRole == UserRole.Admin
            ProfileScreen(
                state = profileState,
                isAdministrator = isAdministrator,
                onSave = profileViewModel::updateProfile,
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