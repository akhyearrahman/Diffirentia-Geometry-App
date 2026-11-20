package com.example.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.R
import com.example.ui.screens.Module11Screen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.RoadmapScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VectorLabScreen
import com.example.ui.theme.DisplayFontFamily
import com.example.viewmodel.GeoManifoldViewModel

const val ROUTE_SPLASH = "splash"
const val ROUTE_MAIN = "main"

private data class NavTabItem(
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val MAIN_TABS = listOf(
    NavTabItem(R.string.nav_roadmap, Icons.Filled.Explore, Icons.Outlined.Explore, "nav_tab_roadmap"),
    NavTabItem(R.string.nav_module_1_1, Icons.Filled.MenuBook, Icons.Outlined.MenuBook, "nav_tab_module_11"),
    NavTabItem(R.string.nav_lab, Icons.Filled.AutoGraph, Icons.Outlined.AutoGraph, "nav_tab_lab"),
    NavTabItem(R.string.nav_progress, Icons.Filled.TrendingUp, Icons.Outlined.TrendingUp, "nav_tab_progress")
)

@Composable
fun GeoManifoldApp(viewModel: GeoManifoldViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ROUTE_SPLASH
    ) {
        composable(ROUTE_SPLASH) {
            SplashScreen(
                onEnterStudio = {
                    navController.navigate(ROUTE_MAIN) {
                        popUpTo(ROUTE_SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(ROUTE_MAIN) {
            MainStudioShell(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainStudioShell(viewModel: GeoManifoldViewModel) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val stats by viewModel.overallStats.collectAsStateWithLifecycle()
    val allProgress by viewModel.allProgress.collectAsStateWithLifecycle()
    val module11Progress by viewModel.module11Progress.collectAsStateWithLifecycle()
    val sessions by viewModel.recentSessions.collectAsStateWithLifecycle()
    val timerState by viewModel.timerState.collectAsStateWithLifecycle()

    val vecU by viewModel.vecU.collectAsStateWithLifecycle()
    val vecV by viewModel.vecV.collectAsStateWithLifecycle()
    val scalarFactor by viewModel.scalarFactor.collectAsStateWithLifecycle()
    val selectedAnimOp by viewModel.selectedAnimOp.collectAsStateWithLifecycle()
    val metric by viewModel.metricTensor.collectAsStateWithLifecycle()
    val manifoldState by viewModel.manifoldState.collectAsStateWithLifecycle()

    val screenTitle = when (selectedTabIndex) {
        0 -> "GeoManifold • স্টাডি ম্যাপ"
        1 -> "মডিউল ১.১ • ভেক্টর ও ডট গুণন"
        2 -> "ইন্টারেক্টিভ জ্যামিতি ল্যাব"
        else -> "অগ্রগতি ও সেটিংস"
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 700.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = screenTitle,
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.toggleDarkMode(!settings.darkModeEnabled) },
                            modifier = Modifier.testTag("top_bar_theme_toggle")
                        ) {
                            Icon(
                                imageVector = if (settings.darkModeEnabled) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                contentDescription = if (settings.darkModeEnabled) "Switch to light mode" else "Switch to dark mode",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            bottomBar = {
                if (!useNavRail) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp
                    ) {
                        MAIN_TABS.forEachIndexed { idx, item ->
                            val selected = selectedTabIndex == idx
                            NavigationBarItem(
                                selected = selected,
                                onClick = { selectedTabIndex = idx },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = stringResource(id = item.labelRes)
                                    )
                                },
                                label = { Text(stringResource(id = item.labelRes)) },
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        MAIN_TABS.forEachIndexed { idx, item ->
                            val selected = selectedTabIndex == idx
                            NavigationRailItem(
                                selected = selected,
                                onClick = { selectedTabIndex = idx },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = stringResource(id = item.labelRes)
                                    )
                                },
                                label = { Text(stringResource(id = item.labelRes)) },
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }

                when (selectedTabIndex) {
                    0 -> RoadmapScreen(
                        learnerName = settings.learnerName,
                        stats = stats,
                        progressMap = allProgress,
                        onOpenModule11 = { selectedTabIndex = 1 },
                        onOpenLab = { selectedTabIndex = 2 },
                        modifier = Modifier.weight(1f)
                    )
                    1 -> Module11Screen(
                        progress = module11Progress,
                        vecU = vecU,
                        vecV = vecV,
                        metric = metric,
                        onUpdateU = viewModel::updateVecU,
                        onUpdateV = viewModel::updateVecV,
                        onUpdateMetric = viewModel::updateMetricTensor,
                        onMarkSectionRead = { section -> viewModel.markSectionRead("1.1", section) },
                        onSubmitIntuition = { qNum, opt, note, correct ->
                            viewModel.submitIntuitionAnswer("1.1", qNum, opt, note, correct)
                        },
                        onSetCompleted = { done -> viewModel.setModuleCompleted("1.1", done) },
                        onOpenFullLab = { selectedTabIndex = 2 },
                        modifier = Modifier.weight(1f)
                    )
                    2 -> VectorLabScreen(
                        vecU = vecU,
                        vecV = vecV,
                        scalarFactor = scalarFactor,
                        selectedAnimOp = selectedAnimOp,
                        metric = metric,
                        manifoldState = manifoldState,
                        onUpdateU = viewModel::updateVecU,
                        onUpdateV = viewModel::updateVecV,
                        onApplyPreset = viewModel::applyVectorPreset,
                        onDoubleV = viewModel::doubleVectorV,
                        onUpdateScalar = viewModel::updateScalarFactor,
                        onSelectAnimOp = viewModel::selectAnimOp,
                        onUpdateMetric = viewModel::updateMetricTensor,
                        onSelectManifold = viewModel::selectManifoldKind,
                        onUpdateManifoldCurvature = viewModel::updateManifoldCurvature,
                        onRotateManifold = viewModel::rotateManifold,
                        onUpdateManifoldPoint = viewModel::updateManifoldPoint,
                        modifier = Modifier.weight(1f)
                    )
                    else -> ProgressScreen(
                        settings = settings,
                        stats = stats,
                        module11Progress = module11Progress,
                        sessions = sessions,
                        timerState = timerState,
                        onToggleDarkMode = viewModel::toggleDarkMode,
                        onSaveProfile = viewModel::updateLearnerProfile,
                        onSetTimerMinutes = viewModel::setTimerDuration,
                        onToggleTimer = viewModel::toggleTimer,
                        onLogQuickSession = viewModel::logQuickSession,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
