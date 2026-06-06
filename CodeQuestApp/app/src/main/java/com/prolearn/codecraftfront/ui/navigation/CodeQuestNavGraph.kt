package com.prolearn.codecraftfront.ui.navigation

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.prolearn.codecraftfront.ui.auth.AuthViewModel
import com.prolearn.codecraftfront.ui.profile.ProfileViewModel
import com.prolearn.codecraftfront.ui.screens.*
import com.prolearn.codecraftfront.ui.war.WarScreen
import java.net.URLDecoder

@Composable
fun CodeQuestNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val profileViewModel: ProfileViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AuthRoutes.Splash,
        modifier = modifier,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
    ) {
        composable(AuthRoutes.Splash) {
            SplashScreen(onStart = {
                authViewModel.refreshAuthState()
                val destination = if (authState.isAuthenticated) CodeQuestDestination.Home.route else AuthRoutes.Login
                navController.navigate(destination) {
                    popUpTo(AuthRoutes.Splash) { inclusive = true }
                }
            })
        }
        
        composable(AuthRoutes.Login) {
            val navigateHome: () -> Unit = {
                navController.navigate(CodeQuestDestination.Home.route) {
                    popUpTo(AuthRoutes.Login) { inclusive = true }
                }
            }
            LoginScreen(
                state = authState,
                onLogin = { e, p ->
                    authViewModel.login(e, p, navigateHome)
                },
                onSignInAnonymously = {
                    authViewModel.signInAnonymously(navigateHome)
                },
                onGoogleToken = { token ->
                    authViewModel.loginWithGoogle(token, navigateHome)
                },
                onGoogleError = { msg -> authViewModel.setError(msg) },
                onGithubError = { msg -> authViewModel.setError(msg) },
                onForgotPassword = { email -> authViewModel.sendPasswordReset(email) },
                onDismissMessage = authViewModel::clearMessages,
                onOpenRegister = { navController.navigate(AuthRoutes.Register) },
                onPhoneSignIn = { phone, activity ->
                    authViewModel.startPhoneNumberVerification(phone, activity)
                },
                onVerifySmsCode = { code ->
                    authViewModel.verifySmsCode(code, navigateHome)
                },
                onSignInWithGitHub = { activity ->
                    authViewModel.loginWithGitHub(
                        activity = activity,
                        onSuccess = navigateHome,
                        onError = { msg -> authViewModel.setError(msg) },
                    )
                },
                onSendSignInLink = { email -> authViewModel.sendSignInLink(email) },
                onResetPhoneState = authViewModel::resetPhoneAuthState,
            )
        }

        composable(AuthRoutes.Register) {
            RegisterScreen(
                state = authState,
                onRegister = { email, password, confirmPassword ->
                    authViewModel.register(email, password, confirmPassword) {
                        navController.navigate(CodeQuestDestination.Home.route) {
                            popUpTo(AuthRoutes.Login) { inclusive = true }
                        }
                    }
                },
                onDismissMessage = authViewModel::clearMessages,
                onOpenLogin = { navController.popBackStack() },
            )
        }

        composable(CodeQuestDestination.Home.route) {
            HomeScreen(
                onStartDailyChallenge = { navController.navigate(CodeQuestDestination.Mission.createRoute("Python", "daily", 1)) },
                onQuickStart = { lang, track, id -> 
                    profileViewModel.refresh()
                    navController.navigate(CodeQuestDestination.Mission.createRoute(lang, track, id)) 
                }
            )
        }

        composable(CodeQuestDestination.Languages.route) {
            LanguagesScreen(onOpenLevels = { lang ->
                navController.navigate(CodeQuestDestination.Levels.createRoute(lang))
            })
        }

        composable(
            route = CodeQuestDestination.Levels.route, 
            arguments = listOf(navArgument("language") { type = NavType.StringType })
        ) { backStackEntry ->
            val lang = URLDecoder.decode(backStackEntry.arguments?.getString("language") ?: "", "UTF-8")
            LevelsScreen(language = lang, onOpenPlay = { l, t, id -> 
                navController.navigate(CodeQuestDestination.Mission.createRoute(l, t, id)) 
            })
        }

        composable(CodeQuestDestination.Play.route) {
            PlayHubScreen(
                onOpenLanguages = { navController.navigate(CodeQuestDestination.Languages.route) },
                onQuickPlay = { navController.navigate(CodeQuestDestination.Mission.createRoute("Python", "beginner", 1)) },
                onDailyChallenge = { navController.navigate(CodeQuestDestination.Mission.createRoute("Python", "daily", 1)) },
                onContinueLast = { navController.navigate(CodeQuestDestination.Mission.createRoute("Python", "beginner", 1)) },
                onOpenCodingWar = { navController.navigate("war_selection") }
            )
        }

        composable("war_selection") {
            WarSelectionScreen(
                onSelectLanguage = { lang -> navController.navigate("war_mode/$lang") },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            "war_mode/{language}",
            arguments = listOf(navArgument("language") { type = NavType.StringType })
        ) { backStackEntry ->
            val lang = backStackEntry.arguments?.getString("language") ?: "Python"
            WarModeSelectionScreen(
                language = lang,
                onStartWar = { mode -> navController.navigate("war_battle/$lang/$mode") },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            "war_battle/{language}/{mode}",
            arguments = listOf(
                navArgument("language") { type = NavType.StringType },
                navArgument("mode") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val lang = backStackEntry.arguments?.getString("language") ?: "Python"
            val mode = backStackEntry.arguments?.getString("mode") ?: "online"
            WarScreen(
                language = lang,
                mode = mode,
                onBack = {
                    profileViewModel.refresh()
                    navController.popBackStack()
                },
            )
        }

        composable(
            "war_battle/{language}/friend/{challengeId}",
            arguments = listOf(
                navArgument("language") { type = NavType.StringType },
                navArgument("challengeId") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val lang = backStackEntry.arguments?.getString("language") ?: "Python"
            val challengeId = backStackEntry.arguments?.getString("challengeId") ?: ""
            WarScreen(
                language = lang,
                mode = "friend",
                challengeId = challengeId,
                onBack = {
                    profileViewModel.refresh()
                    navController.popBackStack()
                },
            )
        }

        composable(
            route = CodeQuestDestination.Mission.route,
            arguments = listOf(
                navArgument("language") { type = NavType.StringType },
                navArgument("track") { type = NavType.StringType },
                navArgument("levelId") { type = NavType.IntType },
            )
        ) { backStackEntry ->
            val lang = URLDecoder.decode(backStackEntry.arguments?.getString("language") ?: "", "UTF-8")
            PlayScreen(
                language = lang,
                track = backStackEntry.arguments?.getString("track") ?: "",
                levelId = backStackEntry.arguments?.getInt("levelId") ?: 1
            )
        }

        composable(CodeQuestDestination.Stats.route) { 
            StatsScreen(onOpenLeaderboard = { navController.navigate(CodeQuestDestination.Leaderboard.route) }) 
        }
        
        composable(CodeQuestDestination.Leaderboard.route) { LeaderboardScreen() }
        
        composable(CodeQuestDestination.Profile.route) {
            ProfileScreen(
                email = authState.email,
                onStartFriendDuel = { language, challengeId ->
                    navController.navigate("war_battle/$language/friend/$challengeId")
                },
                onSignOut = {
                    authViewModel.signOut { 
                        navController.navigate(AuthRoutes.Login) { 
                            popUpTo(0) { inclusive = true } 
                        } 
                    } 
                }
            )
        }
    }
}
