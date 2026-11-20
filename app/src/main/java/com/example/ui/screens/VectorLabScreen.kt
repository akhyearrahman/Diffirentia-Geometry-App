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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AnimatedVectorOpsCanvas
import com.example.ui.components.FormulaBox
import com.example.ui.components.InteractiveVectorCoordinateCanvas
import com.example.ui.components.Manifold3DCanvas
import com.example.ui.components.MetricMatrixDisplay
import com.example.ui.components.MetricTensorCanvas
import com.example.ui.components.TelemetryStatTile
import com.example.ui.theme.LocalMathPalette
import com.example.ui.theme.MathMonoFontFamily
import com.example.viewmodel.ManifoldKind
import com.example.viewmodel.ManifoldState
import com.example.viewmodel.MetricTensor2D
import com.example.viewmodel.Vec2
import com.example.viewmodel.VectorAnalysis
import com.example.viewmodel.VectorAnimOp
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun VectorLabScreen(
    vecU: Vec2,
    vecV: Vec2,
    scalarFactor: Float,
    selectedAnimOp: VectorAnimOp,
    metric: MetricTensor2D,
    manifoldState: ManifoldState,
    onUpdateU: (Vec2) -> Unit,
    onUpdateV: (Vec2) -> Unit,
    onApplyPreset: (Vec2, Vec2) -> Unit,
    onDoubleV: () -> Unit,
    onUpdateScalar: (Float) -> Unit,
    onSelectAnimOp: (VectorAnimOp) -> Unit,
    onUpdateMetric: (Float, Float, Float) -> Unit,
    onSelectManifold: (ManifoldKind) -> Unit,
    onUpdateManifoldCurvature: (Float) -> Unit,
    onRotateManifold: (Float, Float) -> Unit,
    onUpdateManifoldPoint: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("ভেক্টর ড্যাশবোর্ড", "অ্যানিমেশন", "মেট্রিক ও ম্যানিফোল্ড")

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 780.dp)
        ) {
            // Segmented Lab Switcher
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                tabTitles.forEachIndexed { index, title ->
                    SegmentedButton(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = tabTitles.size),
                        modifier = Modifier.testTag("lab_tab_$index")
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1
                        )
                    }
                }
            }

            when (selectedTab) {
                0 -> VectorDashboardTab(
                    vecU = vecU,
                    vecV = vecV,
                    onUpdateU = onUpdateU,
                    onUpdateV = onUpdateV,
                    onApplyPreset = onApplyPreset,
                    onDoubleV = onDoubleV
                )
                1 -> VectorAnimationsTab(
                    vecU = vecU,
                    vecV = vecV,
                    scalarFactor = scalarFactor,
                    selectedOp = selectedAnimOp,
                    onSelectOp = onSelectAnimOp,
                    onUpdateScalar = onUpdateScalar
                )
                else -> MetricAndManifoldTab(
                    metric = metric,
                    vecU = vecU,
                    vecV = vecV,
                    manifoldState = manifoldState,
                    onUpdateMetric = onUpdateMetric,
                    onSelectManifold = onSelectManifold,
                    onUpdateManifoldCurvature = onUpdateManifoldCurvature,
                    onRotateManifold = onRotateManifold,
                    onUpdateManifoldPoint = onUpdateManifoldPoint
                )
            }
        }
    }
}

@Composable
private fun VectorDashboardTab(
    vecU: Vec2,
    vecV: Vec2,
    onUpdateU: (Vec2) -> Unit,
    onUpdateV: (Vec2) -> Unit,
    onApplyPreset: (Vec2, Vec2) -> Unit,
    onDoubleV: () -> Unit
) {
    val palette = LocalMathPalette.current
    val analysis = remember(vecU, vecV) { VectorAnalysis.compute(vecU, vecV) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("vector_dashboard_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "রিয়েল-টাইম ভেক্টর ও ডট গুণন ড্যাশবোর্ড",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "গ্রিডের ওপর u⃗ (নীল) বা v⃗ (কমলা) তীরের মাথা টেনে সরান—ছায়া, কোণ ও ডট গুণন সাথে সাথে বদলে যাবে।",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Draggable Coordinate Plot
        item {
            InteractiveVectorCoordinateCanvas(
                analysis = analysis,
                onUpdateU = onUpdateU,
                onUpdateV = onUpdateV,
                showSunbeams = true,
                height = 290.dp
            )
        }

        // Quick Geometric Experiment Presets (including Q1 double v!)
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedButton(
                        onClick = { onApplyPreset(Vec2(1.5f, 2.6f), Vec2(2.0f, 0.0f)) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("preset_60_deg")
                    ) {
                        Text("৬০° সেটআপ", style = MaterialTheme.typography.labelMedium)
                    }
                }
                item {
                    Button(
                        onClick = onDoubleV,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("preset_double_v")
                    ) {
                        Text("Q1 পরীক্ষা: ২×v⃗", style = MaterialTheme.typography.labelMedium)
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { onApplyPreset(Vec2(0.0f, 3.0f), Vec2(3.0f, 0.0f)) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("preset_orthogonal")
                    ) {
                        Text("লম্ব (৯০°)", style = MaterialTheme.typography.labelMedium)
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { onApplyPreset(Vec2(-2.2f, 2.2f), Vec2(3.0f, 0.0f)) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("preset_obtuse")
                    ) {
                        Text("স্থূলকোণ (১৩৫°)", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // Real-Time 6-Tile Geometric Telemetry Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    TelemetryStatTile(
                        label = "ভেক্টর u⃗",
                        value = String.format(Locale.US, "(%.1f, %.1f)", vecU.x, vecU.y),
                        sublabel = String.format(Locale.US, "‖u⃗‖ = %.2f", analysis.normU),
                        accentColor = palette.vectorU,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryStatTile(
                        label = "ভেক্টর v⃗",
                        value = String.format(Locale.US, "(%.1f, %.1f)", vecV.x, vecV.y),
                        sublabel = String.format(Locale.US, "‖v⃗‖ = %.2f", analysis.normV),
                        accentColor = palette.vectorV,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    TelemetryStatTile(
                        label = "ডট গুণন (u⃗ · v⃗)",
                        value = String.format(Locale.US, "%+.2f", analysis.dotProduct),
                        sublabel = "${analysis.alignment.labelBn} (${analysis.alignment.labelEn})",
                        accentColor = if (analysis.dotProduct >= 0f) palette.projectionShadow else palette.obtuseRose,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryStatTile(
                        label = "কোণ θ ও cos θ",
                        value = String.format(Locale.US, "%.1f°", analysis.angleDegrees),
                        sublabel = String.format(Locale.US, "cos θ = %+.3f", analysis.cosTheta),
                        accentColor = palette.metricViolet,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    TelemetryStatTile(
                        label = "ছায়ার দৈর্ঘ্য (‖u⃗‖cosθ)",
                        value = String.format(Locale.US, "%+.2f", analysis.shadowLengthSigned),
                        sublabel = "v⃗-এর দৈর্ঘ্যের ওপর নির্ভরশীল নয়!",
                        accentColor = palette.projectionShadow,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryStatTile(
                        label = "অভিক্ষেপ proj_v(u⃗)",
                        value = String.format(Locale.US, "(%.1f, %.1f)", analysis.projectionOnV.x, analysis.projectionOnV.y),
                        sublabel = "Orthogonal Projection",
                        accentColor = palette.vectorU,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Precision Coordinate Sliders
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "নিখুঁত স্থানাঙ্ক কন্ট্রোল (Precision Controls)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    CoordSliderRow("u₁ (x)", vecU.x, palette.vectorU) { onUpdateU(vecU.copy(x = it)) }
                    CoordSliderRow("u₂ (y)", vecU.y, palette.vectorU) { onUpdateU(vecU.copy(y = it)) }
                    CoordSliderRow("v₁ (x)", vecV.x, palette.vectorV) { onUpdateV(vecV.copy(x = it)) }
                    CoordSliderRow("v₂ (y)", vecV.y, palette.vectorV) { onUpdateV(vecV.copy(y = it)) }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun VectorAnimationsTab(
    vecU: Vec2,
    vecV: Vec2,
    scalarFactor: Float,
    selectedOp: VectorAnimOp,
    onSelectOp: (VectorAnimOp) -> Unit,
    onUpdateScalar: (Float) -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var isPlaying by remember { mutableStateOf(true) }

    LaunchedEffect(isPlaying, selectedOp) {
        while (isPlaying) {
            delay(16L)
            progress = if (progress >= 1f) 0f else (progress + 0.008f).coerceAtMost(1f)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("vector_animations_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "ভেক্টর অপারেশনের অ্যানিমেটেড ডায়াগ্রাম",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "প্রতিটি জ্যামিতিক অপারেশন ধাপে ধাপে কীভাবে স্পেস ও ভেক্টর বদলে দেয় তা দেখুন।",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(VectorAnimOp.entries) { op ->
                    FilterChip(
                        selected = selectedOp == op,
                        onClick = {
                            onSelectOp(op)
                            progress = 0f
                            isPlaying = true
                        },
                        label = { Text(op.titleBn, style = MaterialTheme.typography.labelMedium) },
                        modifier = Modifier.testTag("anim_op_${op.name}")
                    )
                }
            }
        }

        item {
            FormulaBox(
                label = "${selectedOp.titleBn} (${selectedOp.titleEn})",
                formula = selectedOp.formula,
                accentColor = MaterialTheme.colorScheme.primary
            )
        }

        item {
            AnimatedVectorOpsCanvas(
                op = selectedOp,
                progress = progress,
                u = vecU,
                v = vecV,
                scalarC = scalarFactor
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { isPlaying = !isPlaying },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("anim_play_pause_btn")
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause animation" else "Play animation",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isPlaying) "বিরতি" else "চালান")
                            }
                            OutlinedButton(
                                onClick = {
                                    progress = 0f
                                    isPlaying = true
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Replay,
                                    contentDescription = "Replay animation",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("আবার")
                            }
                        }
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontFamily = MathMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Slider(
                        value = progress,
                        onValueChange = {
                            isPlaying = false
                            progress = it
                        },
                        valueRange = 0f..1f
                    )

                    if (selectedOp == VectorAnimOp.SCALING) {
                        Spacer(modifier = Modifier.height(8.dp))
                        CoordSliderRow("স্কেলার গুণক c", scalarFactor, MaterialTheme.colorScheme.secondary, -2f..2.5f) {
                            onUpdateScalar(it)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun MetricAndManifoldTab(
    metric: MetricTensor2D,
    vecU: Vec2,
    vecV: Vec2,
    manifoldState: ManifoldState,
    onUpdateMetric: (Float, Float, Float) -> Unit,
    onSelectManifold: (ManifoldKind) -> Unit,
    onUpdateManifoldCurvature: (Float) -> Unit,
    onRotateManifold: (Float, Float) -> Unit,
    onUpdateManifoldPoint: (Float, Float) -> Unit
) {
    val palette = LocalMathPalette.current
    val curvature = remember(manifoldState) { manifoldState.computeTelemetry() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("metric_manifold_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // PART 1: Metric Tensor g_ij Interactive Explorer
        item {
            Text(
                text = "মেট্রিক টেন্সর (Metric Tensor gᵢⱼ) ল্যাব",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "g₁₁, g₂₂ ও g₁₂ বদলে দেখুন কীভাবে একক বৃত্ত (Unit Circle) একটি উপবৃত্তে (Ellipse) রূপান্তরিত হয়।",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            MetricMatrixDisplay(
                g11 = metric.g11,
                g12 = metric.g12,
                g22 = metric.g22,
                accentColor = palette.metricViolet,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            MetricTensorCanvas(metric = metric, height = 240.dp)
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { onUpdateMetric(1f, 0f, 1f) }, shape = RoundedCornerShape(10.dp)) {
                            Text("ইউক্লিডীয় I", style = MaterialTheme.typography.labelSmall)
                        }
                        Button(onClick = { onUpdateMetric(4f, 0f, 1f) }, shape = RoundedCornerShape(10.dp)) {
                            Text("G = diag(4,1)", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(onClick = { onUpdateMetric(2.2f, 0.9f, 1.4f) }, shape = RoundedCornerShape(10.dp)) {
                            Text("বাঁকা গ্রিড", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    CoordSliderRow("g₁₁", metric.g11, palette.metricViolet, 0.4f..4.8f) { onUpdateMetric(it, metric.g12, metric.g22) }
                    CoordSliderRow("g₂₂", metric.g22, palette.metricViolet, 0.4f..4.8f) { onUpdateMetric(metric.g11, metric.g12, it) }
                    CoordSliderRow("g₁₂ = g₂₁", metric.g12, palette.vectorV, -1.5f..1.5f) { onUpdateMetric(metric.g11, it, metric.g22) }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TelemetryStatTile(
                            label = "⟨u⃗, v⃗⟩_G",
                            value = String.format(Locale.US, "%+.2f", metric.innerProduct(vecU, vecV)),
                            accentColor = palette.metricViolet,
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryStatTile(
                            label = "G-কোণ θ_G",
                            value = String.format(Locale.US, "%.1f°", metric.angleDegrees(vecU, vecV)),
                            accentColor = palette.vectorU,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // PART 2: 3D Manifold & Curvature Tensor Visualizer
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "৩ডি ম্যানিফোল্ড ও বক্রতা টেন্সর (Manifolds & Curvature)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = manifoldState.kind.descriptionBn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ManifoldKind.entries) { kind ->
                    FilterChip(
                        selected = manifoldState.kind == kind,
                        onClick = { onSelectManifold(kind) },
                        label = { Text(kind.titleBn, style = MaterialTheme.typography.labelMedium) },
                        modifier = Modifier.testTag("manifold_kind_${kind.name}")
                    )
                }
            }
        }

        item {
            Manifold3DCanvas(
                state = manifoldState,
                onRotate = onRotateManifold,
                height = 280.dp
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                TelemetryStatTile(
                    label = "গাউসিয়ান বক্রতা K",
                    value = String.format(Locale.US, "%+.2f", curvature.gaussianK),
                    sublabel = "K = κ₁ · κ₂",
                    accentColor = if (curvature.gaussianK > 0.01f) palette.projectionShadow else if (curvature.gaussianK < -0.01f) palette.obtuseRose else palette.vectorU,
                    modifier = Modifier.weight(1f)
                )
                TelemetryStatTile(
                    label = "রিচি স্কেলার R",
                    value = String.format(Locale.US, "%+.2f", curvature.ricciScalarR),
                    sublabel = "R = 2K (2D সারফেস)",
                    accentColor = palette.vectorV,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "প্রধান বক্রতা: κ₁ = ${String.format(Locale.US, "%+.2f", curvature.k1)}, κ₂ = ${String.format(Locale.US, "%+.2f", curvature.k2)}",
                            fontFamily = MathMonoFontFamily,
                            fontSize = 12.sp,
                            color = palette.vectorU
                        )
                        Text(
                            text = "H = ${String.format(Locale.US, "%+.2f", curvature.meanH)}",
                            fontFamily = MathMonoFontFamily,
                            fontSize = 12.sp,
                            color = palette.projectionShadow
                        )
                    }
                    CoordSliderRow("বক্রতা মাত্রা (Curvature)", manifoldState.curvatureScale, palette.vectorV, 0.2f..1.3f) {
                        onUpdateManifoldCurvature(it)
                    }
                    CoordSliderRow("বিন্দু p-এর u অবস্থান", manifoldState.pointU, palette.vectorU, -0.8f..0.8f) {
                        onUpdateManifoldPoint(it, manifoldState.pointV)
                    }
                    CoordSliderRow("বিন্দু p-এর v অবস্থান", manifoldState.pointV, palette.projectionShadow, -0.8f..0.8f) {
                        onUpdateManifoldPoint(manifoldState.pointU, it)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
private fun CoordSliderRow(
    label: String,
    value: Float,
    accent: Color,
    range: ClosedFloatingPointRange<Float> = -4.5f..4.5f,
    onChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = MathMonoFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = accent,
            modifier = Modifier.width(126.dp)
        )
        Slider(
            value = value.coerceIn(range.start, range.endInclusive),
            onValueChange = onChange,
            valueRange = range,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = String.format(Locale.US, "%+.1f", value),
            fontFamily = MathMonoFontFamily,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(44.dp)
        )
    }
}
