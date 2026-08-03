package com.usbbog.orientacionvocacional.navigation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.usbbog.orientacionvocacional.screens.ForgotPasswordScreen
import com.usbbog.orientacionvocacional.screens.LandingWebScreenV2
import com.usbbog.orientacionvocacional.screens.LoginScreen
import com.usbbog.orientacionvocacional.screens.RegisterWebScreenV2
import com.usbbog.orientacionvocacional.screens.TestIntroWebScreenV2
import com.usbbog.orientacionvocacional.screens.TestQuestionWebScreen
import com.usbbog.orientacionvocacional.screens.TestReviewWebScreen
import com.usbbog.orientacionvocacional.ui.mobile.ProfileMobileScreen
import com.usbbog.orientacionvocacional.ui.mobile.ResultsMobileScreen
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
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

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

    NavHost(
        navController = navController,
        startDestination = Routes.LANDING,
    ) {
        composable(Routes.LANDING) {
            var showHelpDialog by rememberSaveable { mutableStateOf(false) }

            LandingWebScreenV2(
                onStartClick = {
                    navController.navigate(Routes.LOGIN) {
                        launchSingleTop = true
                    }
                },
                onLoginClick = {
                    navController.navigate(Routes.LOGIN) {
                        launchSingleTop = true
                    }
                },
                onHelpClick = {
                    showHelpDialog = true
                },
            )

            if (showHelpDialog) {
                AlertDialog(
                    onDismissRequest = { showHelpDialog = false },
                    title = {
                        Text(text = "Ayuda")
                    },
                    text = {
                        Text(
                            text = "Para realizar la prueba debes crear una cuenta o iniciar sesión. " +
                                    "Después podrás responder el cuestionario y consultar tus resultados vocacionales.",
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { showHelpDialog = false }) {
                            Text(text = "Entendido")
                        }
                    },
                )
            }
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
                    val credentialsAreValid = loginViewModel.login()

                    if (credentialsAreValid) {
                        profileViewModel.loadFromLogin(email = loginState.email)

                        navController.navigate(Routes.BEFORE_TEST) {
                            popUpTo(Routes.LOGIN) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                },
                onRegisterClick = {
                    loginViewModel.clearError()
                    navController.navigate(Routes.REGISTER) {
                        launchSingleTop = true
                    }
                },
                onForgotPasswordClick = {
                    loginViewModel.clearError()
                    forgotPasswordViewModel.prefillEmail(loginState.email)

                    navController.navigate(Routes.FORGOT_PASSWORD) {
                        launchSingleTop = true
                    }
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
                        navController.navigate(Routes.LOGIN) {
                            launchSingleTop = true
                        }
                    }
                },
            )
        }

        composable(Routes.REGISTER) {
            RegisterWebScreenV2(
                state = registerState,
                onFieldChange = { field, value ->
                    registerViewModel.onFieldChange(
                        field = field,
                        value = value,
                    )
                },
                onAcceptTermsChange = registerViewModel::onAcceptTermsChange,
                onAuthorizeDataChange = registerViewModel::onAuthorizeDataChange,
                onRegisterClick = {
                    val registrationIsValid = registerViewModel.register()

                    if (registrationIsValid) {
                        val registeredEmail = registerState.email.trim()

                        profileViewModel.loadFromRegistration(registerState = registerState)
                        loginViewModel.prefillEmail(registeredEmail)
                        registerViewModel.resetForm()

                        navController.popBackStack(
                            route = Routes.LOGIN,
                            inclusive = false,
                        )
                    }
                },
                onBackToLoginClick = {
                    registerViewModel.resetForm()
                    navController.popBackStack(
                        route = Routes.LOGIN,
                        inclusive = false,
                    )
                },
            )
        }

        composable(Routes.BEFORE_TEST) {
            TestIntroWebScreenV2(
                userName = profileState.fullName,
                errorMessage = testState.errorMessage,
                onStartClick = {
                    testViewModel.resetTest()
                    navController.navigate(Routes.TEST) {
                        launchSingleTop = true
                    }
                },
                onProfileClick = {
                    navController.navigate(Routes.PROFILE) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(Routes.TEST) {
            val currentQuestion = testState.currentQuestion

            if (currentQuestion == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.BEFORE_TEST) {
                        popUpTo(Routes.TEST) {
                            inclusive = true
                        }
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
                        val testFinished = testViewModel.nextQuestion()

                        if (testFinished) {
                            navController.navigate(Routes.TEST_REVIEW) {
                                launchSingleTop = true
                            }
                        }
                    },
                    onReviewClick = {
                        navController.navigate(Routes.TEST_REVIEW) {
                            launchSingleTop = true
                        }
                    },
                    onQuestionJump = testViewModel::jumpToQuestion,
                    onExitTest = {
                        testViewModel.resetTest()
                        navController.navigate(Routes.BEFORE_TEST) {
                            popUpTo(Routes.TEST) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                )
            }
        }

        composable(Routes.TEST_REVIEW) {
            var isSubmitting by rememberSaveable { mutableStateOf(false) }

            if (testState.questions.isEmpty()) {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.BEFORE_TEST) {
                        popUpTo(Routes.TEST_REVIEW) {
                            inclusive = true
                        }
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
                            navController.navigate(Routes.TEST) {
                                launchSingleTop = true
                            }
                        }
                    },
                    onSubmitClick = {
                        val allQuestionsAnswered = testState.questions.indices.all { index ->
                            index + 1 in testState.answeredQuestionNumbers
                        }

                        if (allQuestionsAnswered && !isSubmitting) {
                            isSubmitting = true

                            /*
                             * Cuando exista el endpoint movil, la llamada submitAttempt
                             * debe ejecutarse aqui antes de generar y abrir resultados.
                             */
                            resultsViewModel.generateResults(
                                answers = testState.answers,
                            )

                            navController.navigate(Routes.RESULTS) {
                                popUpTo(Routes.TEST) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    },
                    onBackToQuestionsClick = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Routes.TEST) {
                                launchSingleTop = true
                            }
                        }
                    },
                )
            }
        }

        composable(Routes.RESULTS) {
            ResultsMobileScreen(
                userName = profileState.fullName,
                mainArea = resultsState.mainArea,
                summary = resultsState.summary,
                scores = resultsState.scores,
                careers = resultsState.careers,
                generatedAt = resultsState.generatedAt,
                onDownloadClick = {
                    // La generacion del PDF se implementara despues.
                },
                onProfileClick = {
                    navController.navigate(Routes.PROFILE) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(Routes.PROFILE) {
            ProfileMobileScreen(
                state = profileState,
                onEditClick = {
                    // La edicion del perfil se implementara despues.
                },
                onLogoutClick = {
                    loginViewModel.clearSession()
                    forgotPasswordViewModel.reset()
                    registerViewModel.resetForm()
                    testViewModel.resetTest()
                    resultsViewModel.clearResults()
                    profileViewModel.clearProfile()

                    navController.navigate(Routes.LANDING) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }
    }
}