package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalMathPalette
import com.example.ui.theme.MathMonoFontFamily
import com.example.viewmodel.AlignmentKind
import com.example.viewmodel.Vec2
import com.example.viewmodel.VectorAnalysis
import com.example.viewmodel.VectorAnimOp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Section 1 Visualization: Point vs Vector on a curved Earth manifold.
 * Shows Dhaka and London as distinct points with separate tilted Tangent Planes.
 */
@Composable
fun PointVsVectorEarthCanvas(
    dhakaAngleDeg: Float,
    modifier: Modifier = Modifier
) {
    val palette = LocalMathPalette.current
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp))
            .testTag("point_vs_vector_canvas")
    ) {
        drawRect(palette.canvasBackground)

        val center = Offset(size.width * 0.5f, size.height * 1.18f)
        val radius = min(size.width * 0.56f, size.height * 0.94f)

        // Subtle latitude/longitude sphere arcs
        drawCircle(
            color = palette.vectorU.copy(alpha = 0.08f),
            radius = radius,
            center = center
        )
        drawCircle(
            color = palette.vectorU.copy(alpha = 0.45f),
            radius = radius,
            center = center,
            style = Stroke(width = 2.4f)
        )
        drawCircle(
            color = palette.gridMajor,
            radius = radius * 0.82f,
            center = center,
            style = Stroke(
                width = 1.2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
            )
        )

        // Draw Dhaka point + its tangent plane + velocity vector
        drawTangentPointOnCircle(
            center = center,
            radius = radius,
            angleDegFromTop = dhakaAngleDeg,
            pointColor = palette.vectorU,
            vectorColor = palette.projectionShadow,
            label = "ঢাকা TₚM",
            textMeasurer = textMeasurer,
            textColor = palette.labelText
        )

        // Draw London point on opposite side + its distinct tangent plane
        drawTangentPointOnCircle(
            center = center,
            radius = radius,
            angleDegFromTop = -34f,
            pointColor = palette.vectorV,
            vectorColor = palette.vectorV,
            label = "লন্ডন T_qM",
            textMeasurer = textMeasurer,
            textColor = palette.labelText
        )
    }
}

private fun DrawScope.drawTangentPointOnCircle(
    center: Offset,
    radius: Float,
    angleDegFromTop: Float,
    pointColor: Color,
    vectorColor: Color,
    label: String,
    textMeasurer: TextMeasurer,
    textColor: Color
) {
    val rad = Math.toRadians((-90.0 + angleDegFromTop))
    val nx = cos(rad).toFloat()
    val ny = sin(rad).toFloat()
    val point = Offset(center.x + radius * nx, center.y + radius * ny)

    // Tangent unit direction (perpendicular to normal)
    val tx = -ny
    val ty = nx
    val planeHalfLen = 78f

    // Tangent plane line segment
    drawLine(
        color = pointColor.copy(alpha = 0.65f),
        start = Offset(point.x - tx * planeHalfLen, point.y - ty * planeHalfLen),
        end = Offset(point.x + tx * planeHalfLen, point.y + ty * planeHalfLen),
        strokeWidth = 3f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))
    )

    // Velocity tangent vector arrow starting at point
    val arrowEnd = Offset(point.x + tx * 62f, point.y + ty * 62f)
    drawVectorArrow(
        start = point,
        end = arrowEnd,
        color = vectorColor,
        strokeWidth = 5f,
        headSize = 15f
    )

    // Location Point dot
    drawCircle(color = pointColor.copy(alpha = 0.25f), radius = 14f, center = point)
    drawCircle(color = pointColor, radius = 6.5f, center = point)

    // Label
    val layout = textMeasurer.measure(
        text = label,
        style = TextStyle(
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = MathMonoFontFamily
        )
    )
    drawText(
        textLayoutResult = layout,
        topLeft = Offset(
            (point.x - layout.size.width / 2f).coerceIn(8f, size.width - layout.size.width - 8f),
            (point.y - 36f).coerceIn(8f, size.height - layout.size.height - 8f)
        )
    )
}

/**
 * Interactive 2D Coordinate Grid where users can directly drag the tips of u⃗ and v⃗
 * to inspect orthogonal projection (shadow), sunbeams, angle θ, and dot product in real time.
 */
@Composable
fun InteractiveVectorCoordinateCanvas(
    analysis: VectorAnalysis,
    onUpdateU: (Vec2) -> Unit,
    onUpdateV: (Vec2) -> Unit,
    showSunbeams: Boolean = true,
    height: Dp = 290.dp,
    modifier: Modifier = Modifier
) {
    val palette = LocalMathPalette.current
    val textMeasurer = rememberTextMeasurer()
    var activeHandle by remember { mutableStateOf<String?>(null) }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            .testTag("interactive_vector_canvas")
            .pointerInput(analysis.u, analysis.v) {
                detectDragGestures(
                    onDragStart = { touch ->
                        val origin = Offset(size.width / 2f, size.height / 2f)
                        val scale = min(size.width, size.height) / 10f
                        val uPx = Offset(origin.x + analysis.u.x * scale, origin.y - analysis.u.y * scale)
                        val vPx = Offset(origin.x + analysis.v.x * scale, origin.y - analysis.v.y * scale)
                        val du = hypot(touch.x - uPx.x, touch.y - uPx.y)
                        val dv = hypot(touch.x - vPx.x, touch.y - vPx.y)
                        activeHandle = if (du <= dv) "u" else "v"
                    },
                    onDragEnd = { activeHandle = null },
                    onDragCancel = { activeHandle = null },
                    onDrag = { change, _ ->
                        change.consume()
                        val origin = Offset(size.width / 2f, size.height / 2f)
                        val scale = min(size.width, size.height) / 10f
                        val mathX = ((change.position.x - origin.x) / scale * 10f).roundToInt() / 10f
                        val mathY = ((origin.y - change.position.y) / scale * 10f).roundToInt() / 10f
                        val updated = Vec2(mathX.coerceIn(-4.5f, 4.5f), mathY.coerceIn(-4.5f, 4.5f))
                        if (activeHandle == "u") onUpdateU(updated) else onUpdateV(updated)
                    }
                )
            }
    ) {
        drawRect(palette.canvasBackground)
        val origin = Offset(size.width / 2f, size.height / 2f)
        val scale = min(size.width, size.height) / 10f

        drawCoordinateGrid(origin, scale, palette.gridMinor, palette.gridMajor, palette.axisLine, textMeasurer, palette.labelText)

        fun toPx(v: Vec2) = Offset(origin.x + v.x * scale, origin.y - v.y * scale)

        val uEnd = toPx(analysis.u)
        val vEnd = toPx(analysis.v)
        val projEnd = toPx(analysis.projectionOnV)

        // Extended line along v⃗ to visualize full shadow axis
        if (analysis.normV > 0.05f) {
            val dirX = analysis.v.x / analysis.normV
            val dirY = analysis.v.y / analysis.normV
            drawLine(
                color = palette.vectorV.copy(alpha = 0.22f),
                start = toPx(Vec2(-dirX * 6f, -dirY * 6f)),
                end = toPx(Vec2(dirX * 6f, dirY * 6f)),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
            )
        }

        // Perpendicular sunbeam drop line from u⃗ tip to proj_v(u⃗)
        if (showSunbeams && analysis.normV > 0.05f) {
            drawLine(
                color = palette.sunbeam,
                start = uEnd,
                end = projEnd,
                strokeWidth = 3f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
            )
            // Tiny sunbeam indicator dot at projection foot
            drawCircle(
                color = if (analysis.dotProduct >= 0f) palette.projectionShadow else palette.obtuseRose,
                radius = 5.5f,
                center = projEnd
            )
        }

        // Angle arc θ between v⃗ and u⃗
        if (analysis.normU > 0.1f && analysis.normV > 0.1f) {
            val angleU = atan2(-analysis.u.y, analysis.u.x) * 180f / PI.toFloat()
            val angleV = atan2(-analysis.v.y, analysis.v.x) * 180f / PI.toFloat()
            var sweep = angleU - angleV
            while (sweep > 180f) sweep -= 360f
            while (sweep < -180f) sweep += 360f
            val arcRadius = scale * 0.85f
            val arcColor = when (analysis.alignment) {
                AlignmentKind.ACUTE -> palette.projectionShadow
                AlignmentKind.ORTHOGONAL -> palette.vectorU
                AlignmentKind.OBTUSE -> palette.obtuseRose
            }
            drawArc(
                color = arcColor.copy(alpha = 0.20f),
                startAngle = angleV,
                sweepAngle = sweep,
                useCenter = true,
                topLeft = Offset(origin.x - arcRadius, origin.y - arcRadius),
                size = Size(arcRadius * 2f, arcRadius * 2f)
            )
            drawArc(
                color = arcColor,
                startAngle = angleV,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(origin.x - arcRadius, origin.y - arcRadius),
                size = Size(arcRadius * 2f, arcRadius * 2f),
                style = Stroke(width = 2.6f)
            )
        }

        // Orthogonal Projection shadow vector along v⃗
        val shadowColor = if (analysis.dotProduct >= -0.05f) palette.projectionShadow else palette.obtuseRose
        if (analysis.projectionOnV.norm() > 0.08f) {
            drawLine(
                color = shadowColor.copy(alpha = 0.35f),
                start = origin,
                end = projEnd,
                strokeWidth = 13f,
                cap = StrokeCap.Round
            )
            drawVectorArrow(
                start = origin,
                end = projEnd,
                color = shadowColor,
                strokeWidth = 5.5f,
                headSize = 14f
            )
        }

        // Draw vector v⃗ (Amber)
        drawVectorArrow(
            start = origin,
            end = vEnd,
            color = palette.vectorV,
            strokeWidth = 5.5f,
            headSize = 17f
        )
        // Draw vector u⃗ (Cyan)
        drawVectorArrow(
            start = origin,
            end = uEnd,
            color = palette.vectorU,
            strokeWidth = 5.5f,
            headSize = 17f
        )

        // Draggable handle halos
        drawCircle(color = palette.vectorU.copy(alpha = 0.24f), radius = 20f, center = uEnd)
        drawCircle(color = palette.vectorV.copy(alpha = 0.24f), radius = 20f, center = vEnd)

        // Crisp vector labels
        drawMathBadge(textMeasurer, "u⃗", uEnd + Offset(12f, -22f), palette.vectorU)
        drawMathBadge(textMeasurer, "v⃗", vEnd + Offset(12f, 8f), palette.vectorV)
        if (analysis.projectionOnV.norm() > 0.3f) {
            drawMathBadge(textMeasurer, "ছায়া", projEnd + Offset(-12f, 14f), shadowColor)
        }
    }
}

/**
 * Animated Vector Operations Canvas supporting 4 core operations with smooth 0f..1f interpolation.
 */
@Composable
fun AnimatedVectorOpsCanvas(
    op: VectorAnimOp,
    progress: Float,
    u: Vec2,
    v: Vec2,
    scalarC: Float,
    modifier: Modifier = Modifier
) {
    val palette = LocalMathPalette.current
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(275.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            .testTag("animated_vector_ops_canvas")
    ) {
        drawRect(palette.canvasBackground)
        val origin = Offset(size.width / 2f, size.height / 2f)
        val scale = min(size.width, size.height) / 10f
        fun toPx(p: Vec2) = Offset(origin.x + p.x * scale, origin.y - p.y * scale)

        val t = progress.coerceIn(0f, 1f)

        if (op == VectorAnimOp.LINEAR_TRANSFORM) {
            // Interpolate 2x2 Matrix I -> A = [[1.35, 0.55], [-0.25, 1.15]]
            val a11 = 1f + (1.35f - 1f) * t
            val a12 = 0.55f * t
            val a21 = -0.25f * t
            val a22 = 1f + (1.15f - 1f) * t
            fun transform(p: Vec2) = Vec2(a11 * p.x + a12 * p.y, a21 * p.x + a22 * p.y)

            // Draw faint static reference grid
            drawCoordinateGrid(origin, scale, palette.gridMinor.copy(alpha = 0.45f), palette.gridMajor.copy(alpha = 0.45f), palette.axisLine.copy(alpha = 0.5f), textMeasurer, palette.labelText.copy(alpha = 0.4f))

            // Draw warped grid lines
            for (k in -4..4) {
                val color = if (k == 0) palette.metricViolet else palette.metricViolet.copy(alpha = 0.35f)
                val sw = if (k == 0) 2.5f else 1.4f
                drawLine(color, toPx(transform(Vec2(k.toFloat(), -4.5f))), toPx(transform(Vec2(k.toFloat(), 4.5f))), strokeWidth = sw)
                drawLine(color, toPx(transform(Vec2(-4.5f, k.toFloat()))), toPx(transform(Vec2(4.5f, k.toFloat()))), strokeWidth = sw)
            }

            // Basis vectors î and ĵ after warp
            val iHat = transform(Vec2(1f, 0f))
            val jHat = transform(Vec2(0f, 1f))
            val uWarped = transform(u)
            drawVectorArrow(origin, toPx(iHat), palette.projectionShadow, 5f, 15f)
            drawVectorArrow(origin, toPx(jHat), palette.vectorV, 5f, 15f)
            drawVectorArrow(origin, toPx(uWarped), palette.vectorU, 5.5f, 16f)
            drawMathBadge(textMeasurer, "A·î", toPx(iHat) + Offset(8f, -18f), palette.projectionShadow)
            drawMathBadge(textMeasurer, "A·ĵ", toPx(jHat) + Offset(8f, -18f), palette.vectorV)
            drawMathBadge(textMeasurer, "A·u⃗", toPx(uWarped) + Offset(10f, -18f), palette.vectorU)
        } else {
            drawCoordinateGrid(origin, scale, palette.gridMinor, palette.gridMajor, palette.axisLine, textMeasurer, palette.labelText)

            when (op) {
                VectorAnimOp.ADDITION -> {
                    val sum = u + v
                    // Sliding v⃗ from origin to tip of u⃗
                    val slideStart = Vec2(u.x * t, u.y * t)
                    val slideEnd = slideStart + v

                    drawVectorArrow(origin, toPx(u), palette.vectorU, 5f, 16f)
                    drawVectorArrow(origin, toPx(v), palette.vectorV.copy(alpha = 0.45f), 4f, 14f)
                    drawVectorArrow(toPx(slideStart), toPx(slideEnd), palette.vectorV, 5f, 15f)

                    // Growing resultant u⃗ + v⃗
                    if (t > 0.2f) {
                        val grow = ((t - 0.2f) / 0.8f).coerceIn(0f, 1f)
                        val partialSum = sum * grow
                        drawVectorArrow(origin, toPx(partialSum), palette.projectionShadow, 6f, 18f)
                        if (grow > 0.7f) {
                            drawMathBadge(textMeasurer, "u⃗+v⃗", toPx(sum) + Offset(8f, -20f), palette.projectionShadow)
                        }
                    }
                    drawMathBadge(textMeasurer, "u⃗", toPx(u) + Offset(-22f, -20f), palette.vectorU)
                }

                VectorAnimOp.SCALING -> {
                    val currentScale = 1f + (scalarC - 1f) * t
                    val scaledV = v * currentScale
                    drawVectorArrow(origin, toPx(v), palette.vectorV.copy(alpha = 0.35f), 4f, 14f)
                    val scaledColor = if (currentScale >= 0f) palette.vectorV else palette.obtuseRose
                    drawVectorArrow(origin, toPx(scaledV), scaledColor, 6f, 18f)
                    drawMathBadge(
                        textMeasurer,
                        String.format(java.util.Locale.US, "%.2fv⃗", currentScale),
                        toPx(scaledV) + Offset(10f, -20f),
                        scaledColor
                    )
                }

                VectorAnimOp.PROJECTION -> {
                    val analysis = VectorAnalysis.compute(u, v)
                    val proj = analysis.projectionOnV
                    val animatedDrop = Vec2(
                        u.x + (proj.x - u.x) * t,
                        u.y + (proj.y - u.y) * t
                    )
                    val shadowColor = if (analysis.dotProduct >= 0f) palette.projectionShadow else palette.obtuseRose

                    drawVectorArrow(origin, toPx(v), palette.vectorV, 5f, 16f)
                    drawVectorArrow(origin, toPx(u), palette.vectorU, 5f, 16f)

                    // Sweeping sunbeam line
                    drawLine(
                        color = palette.sunbeam,
                        start = toPx(u),
                        end = toPx(animatedDrop),
                        strokeWidth = 3.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
                    )
                    drawCircle(color = palette.sunbeam, radius = 6f, center = toPx(animatedDrop))

                    // Growing shadow on v⃗
                    val growingProj = proj * t
                    if (growingProj.norm() > 0.05f) {
                        drawVectorArrow(origin, toPx(growingProj), shadowColor, 6f, 16f)
                    }
                    drawMathBadge(textMeasurer, "u⃗", toPx(u) + Offset(8f, -20f), palette.vectorU)
                    drawMathBadge(textMeasurer, "v⃗", toPx(v) + Offset(8f, 8f), palette.vectorV)
                }

                VectorAnimOp.LINEAR_TRANSFORM -> Unit
            }
        }
    }
}

internal fun DrawScope.drawCoordinateGrid(
    origin: Offset,
    scale: Float,
    gridMinor: Color,
    gridMajor: Color,
    axisLine: Color,
    textMeasurer: TextMeasurer,
    labelColor: Color
) {
    val maxUnitsX = (size.width / (2f * scale)).toInt() + 1
    val maxUnitsY = (size.height / (2f * scale)).toInt() + 1

    for (i in -maxUnitsX..maxUnitsX) {
        val x = origin.x + i * scale
        drawLine(
            color = if (i == 0) axisLine else if (i % 2 == 0) gridMajor else gridMinor,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = if (i == 0) 2.2f else 1f
        )
        if (i != 0 && i % 2 == 0 && x in 14f..(size.width - 20f)) {
            val l = textMeasurer.measure(
                text = i.toString(),
                style = TextStyle(color = labelColor.copy(alpha = 0.5f), fontSize = 10.sp, fontFamily = MathMonoFontFamily)
            )
            drawText(l, topLeft = Offset(x + 3f, origin.y + 4f))
        }
    }
    for (j in -maxUnitsY..maxUnitsY) {
        val y = origin.y - j * scale
        drawLine(
            color = if (j == 0) axisLine else if (j % 2 == 0) gridMajor else gridMinor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = if (j == 0) 2.2f else 1f
        )
        if (j != 0 && j % 2 == 0 && y in 12f..(size.height - 16f)) {
            val l = textMeasurer.measure(
                text = j.toString(),
                style = TextStyle(color = labelColor.copy(alpha = 0.5f), fontSize = 10.sp, fontFamily = MathMonoFontFamily)
            )
            drawText(l, topLeft = Offset(origin.x + 5f, y - 14f))
        }
    }
}

internal fun DrawScope.drawVectorArrow(
    start: Offset,
    end: Offset,
    color: Color,
    strokeWidth: Float = 5f,
    headSize: Float = 16f
) {
    val dx = end.x - start.x
    val dy = end.y - start.y
    val len = hypot(dx, dy)
    if (len < 2f) return

    val ux = dx / len
    val uy = dy / len
    val shaftEnd = Offset(end.x - ux * (headSize * 0.65f), end.y - uy * (headSize * 0.65f))

    drawLine(
        color = color,
        start = start,
        end = shaftEnd,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )

    val perpX = -uy
    val perpY = ux
    val wing1 = Offset(
        end.x - ux * headSize + perpX * (headSize * 0.45f),
        end.y - uy * headSize + perpY * (headSize * 0.45f)
    )
    val wing2 = Offset(
        end.x - ux * headSize - perpX * (headSize * 0.45f),
        end.y - uy * headSize - perpY * (headSize * 0.45f)
    )

    val headPath = Path().apply {
        moveTo(end.x, end.y)
        lineTo(wing1.x, wing1.y)
        lineTo(wing2.x, wing2.y)
        close()
    }
    drawPath(headPath, color = color)
}

internal fun DrawScope.drawMathBadge(
    textMeasurer: TextMeasurer,
    text: String,
    pos: Offset,
    color: Color
) {
    val result = textMeasurer.measure(
        text = text,
        style = TextStyle(
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = MathMonoFontFamily
        )
    )
    val safeX = pos.x.coerceIn(6f, (size.width - result.size.width - 6f).coerceAtLeast(6f))
    val safeY = pos.y.coerceIn(6f, (size.height - result.size.height - 6f).coerceAtLeast(6f))
    drawText(result, topLeft = Offset(safeX, safeY))
}
