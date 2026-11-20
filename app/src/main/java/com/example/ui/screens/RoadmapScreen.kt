package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LearningRule
import com.example.data.StudyCurriculum
import com.example.data.StudyModule
import com.example.data.StudyPhase
import com.example.data.local.ModuleProgressEntity
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.MathMonoFontFamily
import com.example.viewmodel.OverallProgressStats

@Composable
fun RoadmapScreen(
    learnerName: String,
    stats: OverallProgressStats,
    progressMap: Map<String, ModuleProgressEntity>,
    onOpenModule11: () -> Unit,
    onOpenLab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expandedPhases = remember {
        mutableStateMapOf(1 to true, 2 to true, 3 to false, 4 to false)
    }
    var expandedUpcomingModuleId by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 780.dp)
                .testTag("roadmap_lazy_column"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Hero Header + Progress Overview Card
            item {
                RoadmapHeroCard(
                    learnerName = learnerName,
                    stats = stats,
                    onOpenModule11 = onOpenModule11,
                    onOpenLab = onOpenLab
                )
            }

            // 2. BCS Micro-Class Philosophy Intro
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = "Daily study balance",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "দৈনিক ৩০–৪৫ মিনিটের মাইক্রো-ক্লাস যাত্রা",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = StudyCurriculum.INTRO_BANNER_TEXT,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // 3. Learning Protocol Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "আমাদের শেখার নিয়ম (Learning Protocol)",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    StudyCurriculum.learningRules.forEach { rule ->
                        LearningRuleCard(rule = rule)
                    }
                }
            }

            // 4. Master Study Map Header
            item {
                Column(modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        text = "১ বছরের মাস্টার স্টাডি ম্যাপ",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "1-Year Master Study Map • ৪টি প্রধান ধাপ ও ২১টি মডিউল",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 5. All 4 Phases & 21 Modules
            items(StudyCurriculum.phases, key = { it.index }) { phase ->
                val isExpanded = expandedPhases[phase.index] == true
                PhaseSectionCard(
                    phase = phase,
                    isExpanded = isExpanded,
                    progressMap = progressMap,
                    expandedUpcomingModuleId = expandedUpcomingModuleId,
                    onToggleExpand = { expandedPhases[phase.index] = !isExpanded },
                    onModuleClick = { module ->
                        if (module.isAvailable) {
                            onOpenModule11()
                        } else {
                            expandedUpcomingModuleId =
                                if (expandedUpcomingModuleId == module.id) null else module.id
                        }
                    },
                    onOpenLab = onOpenLab
                )
            }

            // 6. Closing Motivational Note
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = StudyCurriculum.FOOTER_NOTE_TEXT,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onOpenModule11,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("roadmap_footer_module_11_btn")
                        ) {
                            Text("মডিউল ১.১ দিয়ে শুরু করুন")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Start Module 1.1",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun RoadmapHeroCard(
    learnerName: String,
    stats: OverallProgressStats,
    onOpenModule11: () -> Unit,
    onOpenLab: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "স্বাগতম, $learnerName",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "GeoManifold Studio",
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CloudOff,
                                contentDescription = "Offline available",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "অফলাইন",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress bar across 21 modules
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "মাস্টার ম্যাপ অগ্রগতি",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${stats.completedModules} / ${stats.totalModules} মডিউল সম্পন্ন • মডিউল ১.১ সেকশন ${stats.module11SectionsRead}/4",
                        fontFamily = MathMonoFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = {
                        val fine = if (stats.completedModules > 0) {
                            stats.progressFraction
                        } else {
                            (stats.module11SectionsRead / 4f) * (1f / stats.totalModules.toFloat())
                        }
                        fine.coerceIn(0.03f, 1f)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onOpenModule11,
                        modifier = Modifier
                            .weight(1.25f)
                            .height(48.dp)
                            .testTag("hero_continue_module_11_btn"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Open Module 1.1",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("মডিউল ১.১", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onOpenLab,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("hero_open_lab_btn"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AutoGraph,
                            contentDescription = "Open Vector Lab",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ভেক্টর ল্যাব", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun LearningRuleCard(rule: LearningRule) {
    val icon: ImageVector = when (rule.id) {
        1 -> Icons.Outlined.Explore
        2 -> Icons.Outlined.Translate
        3 -> Icons.Outlined.Schedule
        else -> Icons.Outlined.Psychology
    }
    val accent: Color = when (rule.id) {
        1 -> MaterialTheme.colorScheme.primary
        2 -> MaterialTheme.colorScheme.secondary
        3 -> MaterialTheme.colorScheme.tertiary
        else -> Color(0xFF818CF8)
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = accent.copy(alpha = 0.14f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = rule.titleEn,
                        tint = accent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rule.titleBn,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = rule.titleEn,
                    fontFamily = MathMonoFontFamily,
                    fontSize = 11.sp,
                    color = accent
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = rule.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PhaseSectionCard(
    phase: StudyPhase,
    isExpanded: Boolean,
    progressMap: Map<String, ModuleProgressEntity>,
    expandedUpcomingModuleId: String?,
    onToggleExpand: () -> Unit,
    onModuleClick: (StudyModule) -> Unit,
    onOpenLab: () -> Unit
) {
    val phaseAccent = when (phase.index) {
        1 -> MaterialTheme.colorScheme.primary
        2 -> MaterialTheme.colorScheme.secondary
        3 -> Color(0xFF818CF8)
        else -> MaterialTheme.colorScheme.tertiary
    }
    val completedInPhase = phase.modules.count { progressMap[it.id]?.isCompleted == true }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, phaseAccent.copy(alpha = if (isExpanded) 0.45f else 0.22f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Phase Header (min 48dp touch target)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand)
                    .padding(16.dp)
                    .testTag("phase_header_${phase.index}"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = phaseAccent.copy(alpha = 0.16f)
                        ) {
                            Text(
                                text = phase.badgeBn,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = phaseAccent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$completedInPhase/${phase.modules.size}",
                            fontFamily = MathMonoFontFamily,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = phase.titleBn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = phase.titleEn,
                        fontFamily = DisplayFontFamily,
                        fontSize = 12.sp,
                        color = phaseAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = phase.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse phase" else "Expand phase",
                    tint = phaseAccent
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    phase.modules.forEach { module ->
                        val isCompleted = progressMap[module.id]?.isCompleted == true
                        val isUpcomingExpanded = expandedUpcomingModuleId == module.id
                        ModuleRoadmapTile(
                            module = module,
                            isCompleted = isCompleted,
                            isUpcomingExpanded = isUpcomingExpanded,
                            phaseAccent = phaseAccent,
                            onClick = { onModuleClick(module) },
                            onOpenLab = onOpenLab
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModuleRoadmapTile(
    module: StudyModule,
    isCompleted: Boolean,
    isUpcomingExpanded: Boolean,
    phaseAccent: Color,
    onClick: () -> Unit,
    onOpenLab: () -> Unit
) {
    val containerColor = when {
        isCompleted -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f)
        module.isAvailable -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.40f)
        else -> MaterialTheme.colorScheme.surfaceContainer
    }
    val borderColor = when {
        isCompleted -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
        module.isAvailable -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("roadmap_module_${module.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (module.isAvailable) phaseAccent else MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.size(width = 44.dp, height = 34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = module.codeBn,
                            fontFamily = MathMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (module.isAvailable) Color(0xFF021A27) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = module.titleBn,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = module.titleEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (module.isAvailable) phaseAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                when {
                    isCompleted -> Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Module completed",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(22.dp)
                    )
                    module.isAvailable -> Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "খুলুন",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    else -> Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    ) {
                        Text(
                            text = "পরবর্তী",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "জ্যামিতিক লক্ষ্য: ${module.geometricTakeaway}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AnimatedVisibility(visible = isUpcomingExpanded && !module.isAvailable) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Outlined.Verified,
                                contentDescription = "Upcoming module info",
                                tint = phaseAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "মডিউল ${module.codeBn} পরের সেশনে যুক্ত হবে। চাইলে এখনই ল্যাবে ম্যানিফোল্ড ও মেট্রিক দেখুন।",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = onOpenLab,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("ল্যাব", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
