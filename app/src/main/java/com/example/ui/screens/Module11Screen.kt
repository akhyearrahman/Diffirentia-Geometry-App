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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AlignmentRow
import com.example.data.IntuitionQuestion
import com.example.data.StudyCurriculum
import com.example.data.local.ModuleProgressEntity
import com.example.ui.components.FormulaBox
import com.example.ui.components.GeometricInsightCallout
import com.example.ui.components.InteractiveVectorCoordinateCanvas
import com.example.ui.components.MetricMatrixDisplay
import com.example.ui.components.MetricTensorCanvas
import com.example.ui.components.PointVsVectorEarthCanvas
import com.example.ui.components.TelemetryStatTile
import com.example.ui.theme.LocalMathPalette
import com.example.ui.theme.MathMonoFontFamily
import com.example.viewmodel.MetricTensor2D
import com.example.viewmodel.Vec2
import com.example.viewmodel.VectorAnalysis
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun Module11Screen(
    progress: ModuleProgressEntity,
    vecU: Vec2,
    vecV: Vec2,
    metric: MetricTensor2D,
    onUpdateU: (Vec2) -> Unit,
    onUpdateV: (Vec2) -> Unit,
    onUpdateMetric: (Float, Float, Float) -> Unit,
    onMarkSectionRead: (Int) -> Unit,
    onSubmitIntuition: (Int, Int, String, Boolean) -> Unit,
    onSetCompleted: (Boolean) -> Unit,
    onOpenFullLab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalMathPalette.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var dhakaAngleDeg by remember { mutableFloatStateOf(24f) }
    val analysis = remember(vecU, vecV) { VectorAnalysis.compute(vecU, vecV) }

    val sectionLabels = listOf(
        "১. বিন্দু ও ভেক্টর",
        "২. ডট গুণনের ছায়া",
        "৩. স্কেল ও চাঁদা",
        "৪. মেট্রিক টেন্সর",
        "জ্যামিতিক যাচাই"
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 780.dp)
                .testTag("module_11_lazy_column"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Module 1.1 Header Card + Section Jump Bar
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "ধাপ ১ • মডিউল ১.১",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                if (progress.isCompleted) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = "Completed",
                                            tint = MaterialTheme.colorScheme.tertiary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "সম্পন্ন",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "ভেক্টর, ডট গুণন ও ইনার প্রোডাক্ট",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Vectors, Dot Product & Inner Product",
                                fontFamily = MathMonoFontFamily,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "আজ আমরা একটি পরিচিত বীজগাণিতিক সূত্রকে ভেঙে তার ভেতর থেকে ডিফারেনশিয়াল জিওমেট্রির (Differential Geometry) সবচেয়ে বড় যন্ত্র মেট্রিক টেন্সরের (Metric Tensor) বীজটা বের করে আনব।",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Section Quick-Jump Chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(sectionLabels.size) { idx ->
                            val isRead = idx < 4 && ((progress.sectionsReadMask and (1 shl idx)) != 0)
                            FilterChip(
                                selected = isRead,
                                onClick = {
                                    scope.launch { listState.animateScrollToItem(idx + 1) }
                                },
                                label = { Text(sectionLabels[idx], style = MaterialTheme.typography.labelMedium) },
                                leadingIcon = if (isRead) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = "Section read",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else null
                            )
                        }
                    }
                }
            }

            // SECTION 1: Point vs Vector
            item {
                LessonSectionCard(
                    sectionNumber = "১",
                    titleBn = "বিন্দু বনাম ভেক্টর: প্রথম জ্যামিতিক পার্থক্য",
                    titleEn = "Point vs. Vector & Tangent Plane",
                    isRead = (progress.sectionsReadMask and 1) != 0,
                    onMarkRead = { onMarkSectionRead(0) }
                ) {
                    Text(
                        text = "প্রাথমিক লিনিয়ার অ্যালজেব্রায় (Linear Algebra) আমরা (3, 4) বলতে একই সাথে একটি বিন্দু এবং একটি ভেক্টর—দুটোই বুঝে অভ্যস্ত। ডিফারেনশিয়াল জিওমেট্রিতে এই দুই ধারণাকে আলাদা করা বাধ্যতামূলক।",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    BulletHighlight(
                        label = "বিন্দু (Point):",
                        text = "এটি স্পেসের (Space) কেবল একটি অবস্থান (Location)—যেমন পৃথিবীর মানচিত্রে ঢাকা শহর। দুটি শহরকে যোগ করার কোনো অর্থ হয় না।",
                        accent = palette.vectorU
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BulletHighlight(
                        label = "ভেক্টর (Vector):",
                        text = "এটি কোনো নির্দিষ্ট বিন্দু থেকে শুরু হওয়া একটি গতিবেগ (Velocity) বা দিকনির্দেশক তীর (Tangent Arrow)—যেমন ঢাকা থেকে ঘণ্টায় ৬০ কিমি বেগে উত্তর-পূর্ব দিকে যাওয়ার নির্দেশ।",
                        accent = palette.projectionShadow
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Interactive Curved Earth Manifold visualization
                    PointVsVectorEarthCanvas(dhakaAngleDeg = dhakaAngleDeg)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ঢাকার অবস্থান গোলকের ওপর সরান:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = String.format(Locale.US, "%+.0f°", dhakaAngleDeg),
                            fontFamily = MathMonoFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.vectorU
                        )
                    }
                    Slider(
                        value = dhakaAngleDeg,
                        onValueChange = { dhakaAngleDeg = it },
                        valueRange = -12f..48f,
                        modifier = Modifier.testTag("dhaka_tangent_slider")
                    )

                    GeometricInsightCallout(
                        title = "ট্যানজেন্ট স্পেস (Tangent Space)-এর বীজ",
                        body = "মনে রাখবেন: একটি বাঁকা তলে (যেমন পৃথিবীর গোলকে) ঢাকার একটা ভেক্টর আর লন্ডনের একটা ভেক্টর আলাদা আলাদা স্পর্শক সমতলে (Tangent Plane) বাস করে। এখনই এই ছবিটা মাথায় গেঁথে রাখুন—মাস কয়েক পর এটিই হয়ে উঠবে ট্যানজেন্ট স্পেস (TₚM)।"
                    )
                }
            }

            // SECTION 2: Geometry of Dot Product
            item {
                LessonSectionCard(
                    sectionNumber = "২",
                    titleBn = "ডট গুণনের জ্যামিতি: ছায়া ও স্কেলিং",
                    titleEn = "Geometry of Dot Product (Orthogonal Projection)",
                    isRead = (progress.sectionsReadMask and (1 shl 1)) != 0,
                    onMarkRead = { onMarkSectionRead(1) }
                ) {
                    Text(
                        text = "আমরা সবাই দুটি সূত্র মুখস্থ জানি—কিন্তু দুটি ভেক্টরকে ডট গুণ করলে যে সাধারণ সংখ্যা (Scalar) পাওয়া যায়, জ্যামিতিকভাবে সেটি আসলে কী নির্দেশ করে?",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FormulaBox(
                        label = "১. বীজগাণিতিক রূপ (Algebraic Form)",
                        formula = "u⃗ · v⃗ = u₁v₁ + u₂v₂ + u₃v₃",
                        accentColor = palette.vectorU
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FormulaBox(
                        label = "২. জ্যামিতিক রূপ (Geometric Form)",
                        formula = "u⃗ · v⃗ = ‖u⃗‖ ‖v⃗‖ cos θ",
                        accentColor = palette.vectorV
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "ছায়ার ছবি (The Shadow / Orthogonal Projection)",
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.projectionShadow
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "কল্পনা করুন, v⃗ ভেক্টরটি মাটিতে শুয়ে আছে এবং তার ঠিক ওপর থেকে 90° কোণে (লম্বভাবে) সূর্যের আলো পড়ছে। এবার u⃗ ভেক্টরটিকে v⃗-এর ওপরে ধরলে v⃗-এর রেখা বরাবর u⃗-এর একটি ছায়া পড়বে।\n• এই ছায়াটির দৈর্ঘ্যই হলো ‖u⃗‖ cos θ — লম্ব অভিক্ষেপ (Orthogonal Projection)।\n• এবার এই ছায়ার দৈর্ঘ্যকে মূল ভেক্টর v⃗-এর নিজের দৈর্ঘ্য (‖v⃗‖) দিয়ে গুণ করলেই পাওয়া যায় ডট গুণন (Dot Product)।",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Interactive draggable shadow canvas right inside the lesson!
                    Text(
                        text = "ইন্টারেক্টিভ ছায়া ডায়াগ্রাম (u⃗ বা v⃗-এর মাথা আঙুল দিয়ে টেনে দেখুন):",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    InteractiveVectorCoordinateCanvas(
                        analysis = analysis,
                        onUpdateU = onUpdateU,
                        onUpdateV = onUpdateV,
                        showSunbeams = true,
                        height = 250.dp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TelemetryStatTile(
                            label = "ছায়ার দৈর্ঘ্য (‖u⃗‖cosθ)",
                            value = String.format(Locale.US, "%+.2f", analysis.shadowLengthSigned),
                            accentColor = palette.projectionShadow,
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryStatTile(
                            label = "ডট গুণন (u⃗ · v⃗)",
                            value = String.format(Locale.US, "%+.2f", analysis.dotProduct),
                            accentColor = palette.vectorU,
                            sublabel = "${analysis.alignment.labelBn} (${String.format(Locale.US, "%.1f°", analysis.angleDegrees)})",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "সহযোগিতার পরিমাপ (Measure of Alignment):",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StudyCurriculum.module11AlignmentRows.forEach { row ->
                        AlignmentTableRowCard(row = row)
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    GeometricInsightCallout(
                        title = "গভীর অন্তর্দৃষ্টি: u₁v₁ + u₂v₂ আর ‖u⃗‖‖v⃗‖cosθ সমান হয় কেন?",
                        body = "v⃗ = (v₁, v₂) ভেক্টরটিকে স্থির রাখুন এবং ভাবুন, \"v⃗-এর সাথে ডট গুণ করা\" মানে হলো একটি লিনিয়ার ফাংশনাল মেশিন (Linear Functional)—যেকোনো ভেক্টর u⃗ ইনপুট দিলে সে v⃗-এর ওপর তার ছায়া মেপে ‖v⃗‖ দিয়ে গুণ করে একটি সংখ্যা ফেরত দেয়।\n১. ছায়া ফেলা একটি লিনিয়ার প্রক্রিয়া (Linear Operation)।\n২. ভিত্তি ভেক্টর î = (1, 0) ও ĵ = (0, 1)-কে এই মেশিনে দিলে জ্যামিতিক প্রতিসাম্যের কারণে যথাক্রমে v₁ ও v₂ পাওয়া যায়।\n৩. যেহেতু u⃗ = u₁î + u₂ĵ, তাই লিনিয়ারিটির কারণে আউটপুট অবশ্যই u₁v₁ + u₂v₂ হতে হবে—যা পরে কো-ভেক্টর (Covector) বুঝতে সরাসরি কাজে লাগবে!",
                        accentColor = palette.vectorU
                    )
                }
            }

            // SECTION 3: Ruler & Protractor
            item {
                LessonSectionCard(
                    sectionNumber = "৩",
                    titleBn = "ডট গুণনই জ্যামিতির \"স্কেল ও চাঁদা\"",
                    titleEn = "Ruler (Norm) & Protractor (Angle)",
                    isRead = (progress.sectionsReadMask and (1 shl 2)) != 0,
                    onMarkRead = { onMarkSectionRead(2) }
                ) {
                    Text(
                        text = "একটি সাধারণ ভেক্টর স্পেস (Vector Space) শুধু ভেক্টর যোগ করতে আর টেনে বড়-ছোট করতে জানে—সেখানে \"দৈর্ঘ্য\" (Length) বা \"কোণ\" (Angle) বলতে কিছু নেই। ডট গুণন যোগ করার সাথে সাথেই স্পেসটি জ্যামিতি ফিরে পায়:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FormulaBox(
                        label = "স্কেল (Ruler / Norm) — নিজের সাথে ডট গুণন (θ = 0°)",
                        formula = "‖v⃗‖ = √(v⃗ · v⃗) = √(v₁² + v₂²)",
                        accentColor = palette.projectionShadow
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FormulaBox(
                        label = "চাঁদা (Protractor / Angle) — দৈর্ঘ্য দিয়ে ভাগ করলে শুধু কোণ",
                        formula = "cos θ = (u⃗ · v⃗) / (‖u⃗‖ ‖v⃗‖)",
                        accentColor = palette.vectorV
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TelemetryStatTile(
                            label = "বর্তমান ‖u⃗‖",
                            value = String.format(Locale.US, "%.2f", analysis.normU),
                            accentColor = palette.vectorU,
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryStatTile(
                            label = "বর্তমান ‖v⃗‖",
                            value = String.format(Locale.US, "%.2f", analysis.normV),
                            accentColor = palette.vectorV,
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryStatTile(
                            label = "কোণ θ",
                            value = String.format(Locale.US, "%.1f°", analysis.angleDegrees),
                            accentColor = palette.projectionShadow,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // SECTION 4: Inner Product & Birth of Metric Tensor
            item {
                LessonSectionCard(
                    sectionNumber = "৪",
                    titleBn = "ইনার প্রোডাক্ট ও মেট্রিক টেন্সরের জন্ম",
                    titleEn = "Inner Product & Riemannian Metric Tensor gᵢⱼ",
                    isRead = (progress.sectionsReadMask and (1 shl 3)) != 0,
                    onMarkRead = { onMarkSectionRead(3) }
                ) {
                    Text(
                        text = "এবার আসি সেই জায়গায়, যেখান থেকে আসল ডিফারেনশিয়াল জিওমেট্রি শুরু হয়। সাধারণ ডট গুণনকে ম্যাট্রিক্স আকারে এভাবে লেখা যায়:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FormulaBox(
                        label = "কার্তেসীয় গ্রিডে অভেদ ম্যাট্রিক্স (Identity Matrix I)",
                        formula = "u⃗ · v⃗ = u⃗ᵀ I v⃗ = [u₁ u₂] [[1, 0], [0, 1]] [v₁ v₂]ᵀ",
                        accentColor = palette.vectorU
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "কিন্তু জগতটা যদি বাঁকা বা টান-খাওয়া হয়? পৃথিবীর মানচিত্রের অক্ষাংশ-দ্রাঘিমাংশ গ্রিডের কথা ভাবুন। বিষুবরেখায় ১ ডিগ্রি দ্রাঘিমাংশ মানে প্রায় ১১১ কিমি, কিন্তু উত্তর মেরুর কাছে সেই একই ১ ডিগ্রির বাস্তব দূরত্ব মাত্র কয়েক কিলোমিটার! তাই সঠিক দূরত্ব ও কোণ মাপতে মাঝখানের I ম্যাট্রিক্স পাল্টে একটি সাধারণ সিমেট্রিক ম্যাট্রিক্স G বসাতে হয়—যাকে বলে ইনার প্রোডাক্ট (Inner Product):",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FormulaBox(
                        label = "সাধারণীকৃত ইনার প্রোডাক্ট (Generalized Inner Product)",
                        formula = "⟨u⃗, v⃗⟩ = u⃗ᵀ G v⃗ = g₁₁u₁v₁ + g₁₂(u₁v₂+u₂v₁) + g₂₂u₂v₂",
                        accentColor = palette.metricViolet
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // 3 Axioms
                    BulletHighlight("১. প্রতিসাম্য (Symmetry):", "⟨u⃗, v⃗⟩ = ⟨v⃗, u⃗⟩ — অর্থাৎ G একটি সিমেট্রিক ম্যাট্রিক্স (g₁₂ = g₂₁)।", palette.vectorU)
                    Spacer(modifier = Modifier.height(6.dp))
                    BulletHighlight("২. বাইলিনিয়ারিটি (Bilinearity):", "উভয় ইনপুটেই যোগ ও স্কেলার গুণের নিয়ম মানা—যাতে ছায়ার জ্যামিতি অটুট থাকে।", palette.vectorV)
                    Spacer(modifier = Modifier.height(6.dp))
                    BulletHighlight("৩. ধনাত্মক-নির্দিষ্টতা (Positive-Definiteness):", "অশূন্য ভেক্টর v⃗-এর জন্য ⟨v⃗, v⃗⟩ > 0 — কোনো বাস্তব তীরের দৈর্ঘ্য ঋণাত্মক বা শূন্য হতে পারে না।", palette.projectionShadow)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live interactive Metric Tensor Unit Ball Explorer!
                    MetricMatrixDisplay(
                        g11 = metric.g11,
                        g12 = metric.g12,
                        g22 = metric.g22,
                        accentColor = palette.metricViolet,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { onUpdateMetric(1f, 0f, 1f) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("preset_metric_identity")
                        ) {
                            Text("ইউক্লিডীয় I", style = MaterialTheme.typography.labelSmall)
                        }
                        Button(
                            onClick = { onUpdateMetric(4f, 0f, 1f) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = palette.metricViolet),
                            modifier = Modifier.testTag("preset_metric_diag41")
                        ) {
                            Text("প্রশ্ন ২: G = diag(4, 1)", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    MetricTensorCanvas(metric = metric, height = 225.dp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "g₁₁ = ${String.format(Locale.US, "%.2f", metric.g11)} • g₂₂ = ${String.format(Locale.US, "%.2f", metric.g22)} • g₁₂ = ${String.format(Locale.US, "%.2f", metric.g12)}",
                        fontFamily = MathMonoFontFamily,
                        fontSize = 12.sp,
                        color = palette.metricViolet
                    )
                    Slider(
                        value = metric.g11,
                        onValueChange = { onUpdateMetric(it, metric.g12, metric.g22) },
                        valueRange = 0.4f..4.5f
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    GeometricInsightCallout(
                        title = "ডিফারেনশিয়াল জিওমেট্রির সংযোগ: রিম্যানিয়ান মেট্রিক টেন্সর (gᵢⱼ)",
                        body = "যখন এই ম্যাট্রিক্স G = [gᵢⱼ] কোনো বাঁকা তলের প্রতিটি বিন্দুতে বিন্দুতে পরিবর্তিত হয় (কারণ বক্রতার জন্য এক বিন্দু থেকে আরেক বিন্দুতে গ্রিডের টান ও কোণ বদলে যায়), তখন এই gᵢⱼ-কেই বলা হয় রিম্যানিয়ান মেট্রিক টেন্সর (Riemannian Metric Tensor)—যা পুরো আধুনিক জ্যামিতি ও আইনস্টাইনের সাধারণ আপেক্ষিকতার (General Relativity) মূল ভিত্তি!",
                        accentColor = palette.metricViolet
                    )
                }
            }

            // SECTION 5: Intuition Check (Both Exact Questions)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "আজকের জ্যামিতিক যাচাই (Intuition Check)",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "পরের মডিউলে (মডিউল ১.২) যাওয়ার আগে নিজের ভাষায় ছোট্ট দুটি প্রশ্নের উত্তর দাও—ভুল হলেও কোনো সমস্যা নেই, এতে জ্যামিতিক অনুভূতিটা পাকা হবে:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    StudyCurriculum.module11Questions.forEach { q ->
                        val savedOption = if (q.number == 1) progress.q1SelectedOption else progress.q2SelectedOption
                        val savedNote = if (q.number == 1) progress.q1Note else progress.q2Note
                        val isVerified = if (q.number == 1) progress.q1Verified else progress.q2Verified

                        IntuitionCheckCard(
                            question = q,
                            initialOption = savedOption,
                            initialNote = savedNote,
                            isVerified = isVerified,
                            onSubmit = { opt, note, correct ->
                                onSubmitIntuition(q.number, opt, note, correct)
                            },
                            onOpenLab = onOpenFullLab
                        )
                    }

                    // Module Completion Banner
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (progress.isCompleted) {
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        border = BorderStroke(
                            1.dp,
                            if (progress.isCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = if (progress.isCompleted) "মডিউল ১.১ সম্পন্ন হয়েছে!" else "মডিউল ১.১ সমাপ্তি",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (progress.isCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "তোমার অগ্রগতি ও জ্যামিতিক যাচাইয়ের নোট অফলাইন ডাটাবেসে নিরাপদে সংরক্ষিত থাকে।",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { onSetCompleted(!progress.isCompleted) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("complete_module_11_btn"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (progress.isCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (progress.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.TaskAlt,
                                        contentDescription = "Toggle completion",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (progress.isCompleted) "সম্পন্ন চিহ্নিত" else "সম্পন্ন করুন",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                OutlinedButton(
                                    onClick = onOpenFullLab,
                                    modifier = Modifier.height(48.dp),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.AutoGraph,
                                        contentDescription = "Open Interactive Lab",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ফুল ল্যাব")
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun LessonSectionCard(
    sectionNumber: String,
    titleBn: String,
    titleEn: String,
    isRead: Boolean,
    onMarkRead: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = sectionNumber,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = titleBn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = titleEn,
                        fontFamily = MathMonoFontFamily,
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            content()
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onMarkRead,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isRead) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.testTag("mark_section_read_$sectionNumber")
                ) {
                    Icon(
                        imageVector = if (isRead) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                        contentDescription = "Mark section understood",
                        tint = if (isRead) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRead) "ধারণা পরিষ্কার ✓" else "পড়া সম্পন্ন চিহ্নিত করুন",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isRead) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun BulletHighlight(label: String, text: String, accent: Color) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(accent)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = accent
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun AlignmentTableRowCard(row: AlignmentRow) {
    val palette = LocalMathPalette.current
    val accent = when (row.accentType) {
        "positive" -> palette.projectionShadow
        "zero" -> palette.vectorU
        else -> palette.obtuseRose
    }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = row.condition,
                    fontFamily = MathMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = accent
                )
                Text(
                    text = row.geometry,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ছায়ার আচরণ: ${row.shadowBehavior}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun IntuitionCheckCard(
    question: IntuitionQuestion,
    initialOption: Int,
    initialNote: String,
    isVerified: Boolean,
    onSubmit: (Int, String, Boolean) -> Unit,
    onOpenLab: () -> Unit
) {
    var selectedOption by remember(initialOption) { mutableIntStateOf(initialOption) }
    var noteText by remember(initialNote) { mutableStateOf(initialNote) }
    var showHint by remember { mutableStateOf(false) }
    var showFeedback by remember(isVerified) { mutableStateOf(isVerified) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            if (isVerified) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.55f) else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "প্রশ্ন ${question.number}: ${question.titleBn}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
                if (isVerified) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Question verified",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = question.promptBn,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Quick geometric choices
            question.quickOptions.forEachIndexed { idx, optLabel ->
                val isSelected = selectedOption == idx
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f) else MaterialTheme.colorScheme.surfaceContainer,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedOption = idx }
                        .testTag("q${question.number}_option_$idx")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                            contentDescription = "Select option",
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = optLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("নিজের ভাষায় জ্যামিতিক যুক্তি (ঐচ্ছিক)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("q${question.number}_note_input"),
                minLines = 2,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showHint = !showHint },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = "Toggle hint",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("হিন্ট")
                }
                Button(
                    onClick = {
                        showFeedback = true
                        val correct = selectedOption == question.correctOptionIndex
                        onSubmit(selectedOption, noteText, correct)
                    },
                    enabled = selectedOption >= 0,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("q${question.number}_verify_btn")
                ) {
                    Text("যাচাই ও সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
                }
            }

            AnimatedVisibility(visible = showHint) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Text(
                        text = "হিন্ট: ${question.hintBn}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            AnimatedVisibility(visible = showFeedback) {
                val isCorrect = selectedOption == question.correctOptionIndex
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isCorrect) MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f),
                    border = BorderStroke(
                        1.dp,
                        if (isCorrect) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isCorrect) "সঠিক জ্যামিতিক অনুভূতি!" else "আরেকবার ছায়া ও স্কেলটি ভেবে দেখো:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = question.verifiedExplanationBn,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = onOpenLab,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("ল্যাবে সরাসরি পরীক্ষা করুন", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
