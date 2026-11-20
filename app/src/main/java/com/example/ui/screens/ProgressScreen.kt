package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ModuleProgressEntity
import com.example.data.local.StudySessionEntity
import com.example.data.local.UserSettingsEntity
import com.example.ui.components.TelemetryStatTile
import com.example.ui.theme.LocalMathPalette
import com.example.ui.theme.MathMonoFontFamily
import com.example.viewmodel.OverallProgressStats
import com.example.viewmodel.StudyTimerState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressScreen(
    settings: UserSettingsEntity,
    stats: OverallProgressStats,
    module11Progress: ModuleProgressEntity,
    sessions: List<StudySessionEntity>,
    timerState: StudyTimerState,
    onToggleDarkMode: (Boolean) -> Unit,
    onSaveProfile: (String, Int) -> Unit,
    onSetTimerMinutes: (Int) -> Unit,
    onToggleTimer: () -> Unit,
    onLogQuickSession: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalMathPalette.current
    var nameInput by remember(settings.learnerName) { mutableStateOf(settings.learnerName) }
    val dateFormatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 780.dp)
                .testTag("progress_screen_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "অগ্রগতি ও স্টাডি স্টুডিও",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "দৈনিক ৩০–৪৫ মিনিটের মাইক্রো-ক্লাস ট্র্যাকার ও অফলাইন সেটিংস",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Stats 2x2 Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        TelemetryStatTile(
                            label = "সম্পন্ন মডিউল",
                            value = "${stats.completedModules} / ${stats.totalModules}",
                            sublabel = "১ বছরের ম্যাপ",
                            accentColor = palette.vectorU,
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryStatTile(
                            label = "মডিউল ১.১ সেকশন",
                            value = "${stats.module11SectionsRead} / 4",
                            sublabel = "যাচাই: ${stats.intuitionChecksVerified}/2",
                            accentColor = palette.projectionShadow,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        TelemetryStatTile(
                            label = "মোট অধ্যয়ন সময়",
                            value = "${stats.totalStudyMinutes} মি.",
                            sublabel = "${stats.totalSessions}টি সেশন",
                            accentColor = palette.vectorV,
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryStatTile(
                            label = "ডাটা স্টোরেজ",
                            value = "Offline SQLite",
                            sublabel = "রুম ডাটাবেস সক্রিয়",
                            accentColor = palette.metricViolet,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // BCS Micro-Class 30/45 Min Timer
            item {
                val mins = timerState.remainingSeconds / 60
                val secs = timerState.remainingSeconds % 60
                val timeDisplay = String.format(Locale.US, "%02d:%02d", mins, secs)

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Timer,
                                contentDescription = "Micro-class study timer",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "মাইক্রো-ক্লাস ফোকাস টাইমার",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = timeDisplay,
                            fontFamily = MathMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 42.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { onSetTimerMinutes(30) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("timer_preset_30")
                            ) {
                                Text("৩০ মিনিট")
                            }
                            OutlinedButton(
                                onClick = { onSetTimerMinutes(45) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("timer_preset_45")
                            ) {
                                Text("৪৫ মিনিট")
                            }
                            Button(
                                onClick = onToggleTimer,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("timer_toggle_btn")
                            ) {
                                Icon(
                                    imageVector = if (timerState.isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = "Start or pause timer",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (timerState.isRunning) "বিরতি" else "শুরু")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { onLogQuickSession(30, "মডিউল ১.১ স্ব-অধ্যয়ন সেশন") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("log_quick_session_btn")
                        ) {
                            Text("+ ৩০ মিনিটের সেশন লগ করুন")
                        }
                    }
                }
            }

            // Saved Module 1.1 Intuition Notes
            if (module11Progress.q1Note.isNotBlank() || module11Progress.q2Note.isNotBlank()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "মডিউল ১.১ জ্যামিতিক নোটসমূহ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (module11Progress.q1Note.isNotBlank()) {
                                Text(
                                    text = "প্রশ্ন ১: ${module11Progress.q1Note}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (module11Progress.q2Note.isNotBlank()) {
                                Text(
                                    text = "প্রশ্ন ২: ${module11Progress.q2Note}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Appearance & Offline Settings
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.DarkMode,
                                    contentDescription = "Dark mode switch",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "ডার্ক মোড (Dark Mode)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "দীর্ঘক্ষণ পড়ার জন্য চোখের আরামদায়ক থিম",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = settings.darkModeEnabled,
                                onCheckedChange = onToggleDarkMode,
                                modifier = Modifier.testTag("dark_mode_switch")
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("শিক্ষার্থীর নাম") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Person, contentDescription = "Learner name")
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("learner_name_input")
                            )
                            Button(
                                onClick = { onSaveProfile(nameInput, settings.dailyGoalMinutes) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("save_profile_btn")
                            ) {
                                Text("সংরক্ষণ")
                            }
                        }

                        // Offline Mode Notice
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CloudOff,
                                    contentDescription = "Offline local database mode",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "অফলাইন লোকাল স্টোরেজ সক্রিয়: তোমার সকল মডিউল অগ্রগতি, জ্যামিতিক যাচাই ও স্টাডি সেশন এই ডিভাইসের Room ডাটাবেসে নিরাপদে সংরক্ষিত হচ্ছে। পরবর্তীতে Firebase সক্রিয় করলে মাল্টি-ডিভাইস ক্লাউড সিঙ্ক যুক্ত করা যাবে।",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Recent Study Sessions Log
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = "Session history",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "সাম্প্রতিক মাইক্রো-ক্লাস সেশন",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (sessions.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "এখনো কোনো সেশন লগ হয়নি। উপরের ৩০/৪৫ মিনিটের ফোকাস টাইমার শুরু করে তোমার প্রথম মাইক্রো-ক্লাস রেকর্ড করো!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(sessions, key = { it.id }) { session ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "মডিউল ${session.moduleId} • ${session.note.ifBlank { "অধ্যয়ন সেশন" }}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = dateFormatter.format(Date(session.completedAt)),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "${session.durationMinutes} মি.",
                                    fontFamily = MathMonoFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}
