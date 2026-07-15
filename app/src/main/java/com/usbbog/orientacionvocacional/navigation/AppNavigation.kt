package com.usbbog.orientacionvocacional.navigation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.usbbog.orientacionvocacional.ui.mobile.LandingMobileScreen
import com.usbbog.orientacionvocacional.ui.mobile.LandingStep
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.usbbog.orientacionvocacional.ui.mobile.LoginMobileScreen
import com.usbbog.orientacionvocacional.ui.mobile.RegisterMobileScreen
import com.usbbog.orientacionvocacional.viewmodel.LoginViewModel
import com.usbbog.orientacionvocacional.viewmodel.RegisterViewModel
import com.usbbog.orientacionvocacional.ui.mobile.BeforeTestMobileScreen
import com.usbbog.orientacionvocacional.ui.mobile.TestSessionMobileScreen
import com.usbbog.orientacionvocacional.viewmodel.TestViewModel
import com.usbbog.orientacionvocacional.ui.mobile.ResultsMobileScreen
import com.usbbog.orientacionvocacional.viewmodel.ResultsViewModel
import com.usbbog.orientacionvocacional.ui.mobile.ProfileMobileScreen
import com.usbbog.orientacionvocacional.viewmodel.ProfileViewModel

private object Routes {
    const val LANDING = "landing"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val BEFORE_TEST = "before_test"
    const val TEST = "test"
    const val RESULTS = "results"
    const val PROFILE = "profile"
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val loginViewModel: LoginViewModel =
        viewModel()

    val registerViewModel: RegisterViewModel =
        viewModel()

    val testViewModel: TestViewModel =
        viewModel()

    val resultsViewModel: ResultsViewModel =
        viewModel()

    val profileViewModel: ProfileViewModel =
        viewModel()

    val loginState by
    loginViewModel.uiState.collectAsState()

    val registerState by
    registerViewModel.uiState.collectAsState()

    val testState by
    testViewModel.uiState.collectAsState()

    val resultsState by
    resultsViewModel.uiState.collectAsState()

    val profileState by
    profileViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.LANDING
    ) {
        composable(Routes.LANDING) {

            var showHelpDialog by rememberSaveable {
                mutableStateOf(false)
            }

            LandingMobileScreen(
                steps = listOf(
                    LandingStep(
                        number = 1,
                        title = "Crea tu cuenta",
                        description =
                            "Regístrate con tus datos personales para comenzar tu proceso de orientación."
                    ),
                    LandingStep(
                        number = 2,
                        title = "Realiza la prueba",
                        description =
                            "Responde las preguntas de acuerdo con tus intereses, habilidades y preferencias."
                    ),
                    LandingStep(
                        number = 3,
                        title = "Conoce tus resultados",
                        description =
                            "Consulta las áreas vocacionales y los programas académicos con mayor afinidad."
                    )
                ),

                onStartClick = {
                    /*
                     * El usuario debe autenticarse antes
                     * de realizar la prueba.
                     */
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
                }
            )

            if (showHelpDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showHelpDialog = false
                    },

                    title = {
                        Text(
                            text = "Ayuda"
                        )
                    },

                    text = {
                        Text(
                            text =
                                "Para realizar la prueba debes crear una cuenta o iniciar sesión. " +
                                        "Después podrás responder el cuestionario y consultar tus resultados vocacionales."
                        )
                    },

                    confirmButton = {
                        TextButton(
                            onClick = {
                                showHelpDialog = false
                            }
                        ) {
                            Text(
                                text = "Entendido"
                            )
                        }
                    }
                )
            }
        }

        composable(Routes.LOGIN) {

            LoginMobileScreen(
                email = loginState.email,
                password = loginState.password,
                rememberMe = loginState.rememberMe,
                isLoading = loginState.isLoading,
                errorMessage = loginState.errorMessage,

                onEmailChange = {
                    loginViewModel.onEmailChange(it)
                },

                onPasswordChange = {
                    loginViewModel.onPasswordChange(it)
                },

                onRememberChange = {
                    loginViewModel.onRememberChange(it)
                },

                onLoginClick = {

                    val credentialsAreValid =
                        loginViewModel.login()

                    if (credentialsAreValid) {

                        profileViewModel.loadFromLogin(
                            email = loginState.email
                        )

                        navController.navigate(
                            Routes.BEFORE_TEST
                        ) {
                            popUpTo(Routes.LOGIN) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    }
                },

                onRegisterClick = {
                    loginViewModel.clearError()

                    navController.navigate(
                        Routes.REGISTER
                    ) {
                        launchSingleTop = true
                    }
                },

                onForgotPasswordClick = {

                    /*
                     * Posteriormente:
                     *
                     * navController.navigate(
                     *     "forgot-password"
                     * )
                     */
                }
            )
        }

        composable(Routes.REGISTER) {

            RegisterMobileScreen(
                state = registerState,

                onFieldChange = {
                        field,
                        value ->

                    registerViewModel.onFieldChange(
                        field = field,
                        value = value
                    )
                },

                onAcceptTermsChange = {
                    registerViewModel
                        .onAcceptTermsChange(it)
                },

                onAuthorizeDataChange = {
                    registerViewModel
                        .onAuthorizeDataChange(it)
                },

                onRegisterClick = {

                    val registrationIsValid =
                        registerViewModel.register()

                    if (registrationIsValid) {

                        val registeredEmail =
                            registerState.email.trim()

                        profileViewModel.loadFromRegistration(
                            registerState = registerState
                        )

                        loginViewModel.prefillEmail(
                            registeredEmail
                        )

                        registerViewModel.resetForm()

                        navController.popBackStack(
                            route = Routes.LOGIN,
                            inclusive = false
                        )
                    }
                },

                onBackToLoginClick = {
                    registerViewModel.resetForm()

                    navController.popBackStack(
                        route = Routes.LOGIN,
                        inclusive = false
                    )
                }
            )
        }
        composable(Routes.BEFORE_TEST) {

            BeforeTestMobileScreen(
                title = "Prepárate para iniciar",
                description =
                    "Antes de comenzar, revisa estas recomendaciones para responder la prueba correctamente.",
                instructions = listOf(
                    "Busca un lugar tranquilo y sin interrupciones.",
                    "Lee cada pregunta cuidadosamente antes de responder.",
                    "Responde con sinceridad según tus intereses personales.",
                    "Evita elegir respuestas basadas en lo que otras personas esperan de ti."
                ),
                warning =
                    "Una vez iniciada la prueba, procura completarla sin cerrar la aplicación.",

                onStartClick = {
                    testViewModel.resetTest()

                    navController.navigate(Routes.TEST)
                },

                onBackClick = {
                    navController.navigate(Routes.LOGIN)
                },

                onProfileClick = {
                    navController.navigate(Routes.PROFILE) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.TEST) {

            val currentQuestion =
                testState.currentQuestion

            if (currentQuestion != null) {

                TestSessionMobileScreen(
                    question = currentQuestion,
                    questionIndex =
                        testState.currentQuestionIndex,
                    totalQuestions =
                        testState.questions.size,
                    selectedOptionId =
                        testState.selectedOptionId,
                    remainingTime =
                        testState.remainingTime,
                    answeredQuestionNumbers =
                        testState.answeredQuestionNumbers,
                    errorMessage = testState.errorMessage,

                    onSelectOption = { optionId ->
                        testViewModel.selectOption(optionId)
                    },

                    onPreviousClick = {
                        testViewModel.previousQuestion()
                    },

                    onNextClick = {
                        val testFinished =
                            testViewModel.nextQuestion()

                        if (testFinished) {

                            resultsViewModel.generateResults(
                                answers = testState.answers
                            )

                            navController.navigate(
                                Routes.RESULTS
                            ) {
                                popUpTo(Routes.TEST) {
                                    inclusive = true
                                }

                                launchSingleTop = true
                            }
                        }
                    },

                    onQuestionJump = { index ->
                        testViewModel.jumpToQuestion(index)
                    }
                )
            }
        }
        composable(Routes.RESULTS) {

            ResultsMobileScreen(
                userName = profileState.fullName,

                mainArea =
                    resultsState.mainArea,

                summary =
                    resultsState.summary,

                scores =
                    resultsState.scores,

                careers =
                    resultsState.careers,

                generatedAt =
                    resultsState.generatedAt,

                onDownloadClick = {
                    /*
                     * La generación del PDF
                     * se implementará después.
                     */
                },

                onProfileClick = {
                    navController.navigate(Routes.PROFILE) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.PROFILE) {

            ProfileMobileScreen(
                state = profileState,

                onEditClick = {
                    /*
                     * Después se agregará la edición
                     * de información del perfil.
                     */
                },

                onLogoutClick = {

                    loginViewModel.clearSession()
                    registerViewModel.resetForm()
                    testViewModel.resetTest()
                    resultsViewModel.clearResults()
                    profileViewModel.clearProfile()

                    navController.navigate(
                        Routes.LANDING
                    ) {
                        popUpTo(
                            navController.graph.startDestinationId
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },

                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}