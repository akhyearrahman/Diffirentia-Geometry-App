package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalMathPalette
import com.example.viewmodel.ManifoldKind
import com.example.viewmodel.ManifoldState
import com.example.viewmodel.MetricTensor2D
import com.example.viewmodel.Vec2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Interactive Metric Tensor G = [g_ij] Canvas:
 * Plots the Euclidean unit circle (dashed) vs the Riemannian G-unit ellipse {v : v^T G v = 1}
 * plus basis vectors e1=(1,0) and e2=(0,1) so users see how G warps rulers and protractors!
 */
@Composable
fun MetricTensorCanvas(
    metric: MetricTensor2D,
    sampleU: Vec2 = Vec2(1f, 0f),
    sampleV: Vec2 = Vec2(0f, 1f),
    height: Dp = 250.dp,
    modifier: Modifier = Modifier
) {
    val palette = LocalMathPalette.current
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            .testTag("metric_tensor_canvas")
    ) {
        drawRect(palette.canvasBackground)
        val origin = Offset(size.width / 2f, size.height / 2f)
        val scale = min(size.width, size.height) / 5.2f
        fun toPx(p: Vec2) = Offset(origin.x + p.x * scale, origin.y - p.y * scale)

        drawCoordinateGrid(origin, scale, palette.gridMinor, palette.gridMajor, palette.axisLine, textMeasurer, palette.labelText)

        // Standard Euclidean unit circle (dashed reference)
        drawCircle(
            color = palette.axisLine.copy(alpha = 0.65f),
            radius = scale,
            center = origin,
            style = Stroke(
                width = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
            )
        )

        // Concentric G-norm ellipses (r_G = 1 and r_G = 2)
        val unitPts = metric.unitBallPoints(96)
        if (unitPts.size > 2) {
            // Outer r_G = 2 faint contour
            val outerPath = Path().apply {
                val first = toPx(unitPts.first() * 2f)
                moveTo(first.x, first.y)
                for (i in 1 until unitPts.size) {
                    val pt = toPx(unitPts[i] * 2f)
                    lineTo(pt.x, pt.y)
                }
                close()
            }
            drawPath(
                path = outerPath,
                color = palette.metricViolet.copy(alpha = 0.25f),
                style = Stroke(width = 1.6f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
            )

            // Primary unit ball {v : <v,v>_G = 1}
            val unitPath = Path().apply {
                val first = toPx(unitPts.first())
                moveTo(first.x, first.y)
                for (i in 1 until unitPts.size) {
                    val pt = toPx(unitPts[i])
                    lineTo(pt.x, pt.y)
                }
                close()
            }
            drawPath(path = unitPath, color = palette.metricViolet.copy(alpha = 0.16f))
            drawPath(path = unitPath, color = palette.metricViolet, style = Stroke(width = 4f))
        }

        // Draw basis/sample vectors
        drawVectorArrow(origin, toPx(sampleU), palette.vectorU, 5f, 15f)
        drawVectorArrow(origin, toPx(sampleV), palette.vectorV, 5f, 15f)

        val normU = String.format(java.util.Locale.US, "‖(1,0)‖_G=%.2f", metric.norm(sampleU))
        val normV = String.format(java.util.Locale.US, "‖(0,1)‖_G=%.2f", metric.norm(sampleV))
        drawMathBadge(textMeasurer, normU, toPx(sampleU) + Offset(8f, 8f), palette.vectorU)
        drawMathBadge(textMeasurer, normV, toPx(sampleV) + Offset(8f, -20f), palette.vectorV)
        drawMathBadge(textMeasurer, "⟨v⃗,v⃗⟩_G = 1", Offset(12f, 12f), palette.metricViolet)
    }
}

/**
 * Interactive 3D Projected Manifold & Curvature Tensor Canvas:
 * Supports Sphere (K>0), Euclidean Plane (K=0), and Hyperbolic Saddle (K<0).
 * Users can drag to rotate the 3D manifold and inspect Tangent Plane T_pM,
 * Surface Normal n⃗, and Principal Curvature Curves κ₁, κ₂!
 */
@Composable
fun Manifold3DCanvas(
    state: ManifoldState,
    onRotate: (Float, Float) -> Unit,
    height: Dp = 280.dp,
    modifier: Modifier = Modifier
) {
    val palette = LocalMathPalette.current
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            .testTag("manifold_3d_canvas")
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onRotate(dragAmount.x * 0.45f, -dragAmount.y * 0.35f)
                }
            }
    ) {
        drawRect(palette.canvasBackground)

        val yaw = Math.toRadians(state.yawDeg.toDouble())
        val pitch = Math.toRadians(state.pitchDeg.toDouble())
        val cosY = cos(yaw).toFloat()
        val sinY = sin(yaw).toFloat()
        val cosP = cos(pitch).toFloat()
        val sinP = sin(pitch).toFloat()

        val scale = min(size.width, size.height) * 0.31f
        val cx = size.width * 0.5f
        val cy = size.height * 0.54f
        val a = state.curvatureScale

        fun surfaceZ(u: Float, v: Float): Float = when (state.kind) {
            ManifoldKind.PLANE -> 0f
            ManifoldKind.SPHERE -> 0.55f - 0.5f * a * (u * u + v * v)
            ManifoldKind.SADDLE -> 0.5f * a * (u * u - v * v)
        }

        fun project(x: Float, y: Float, z: Float): Offset {
            // Rotate around Z (yaw), then tilt around X (pitch)
            val rx = x * cosY - y * sinY
            val ry = x * sinY + y * cosY
            val rz = z
            val py = ry * cosP - rz * sinP
            val pz = ry * sinP + rz * cosP
            val persp = 4.2f / (4.2f - py * 0.55f)
            return Offset(cx + rx * scale * persp, cy - pz * scale * persp)
        }

        val steps = 12
        val range = 1.25f

        // Wireframe u-curves (Cyan family)
        for (i in 0..steps) {
            val vVal = -range + (2f * range * i / steps)
            val path = Path()
            for (j in 0..24) {
                val uVal = -range + (2f * range * j / 24)
                val pt = project(uVal, vVal, surfaceZ(uVal, vVal))
                if (j == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
            }
            val isCenter = i == steps / 2
            drawPath(
                path = path,
                color = if (isCenter) palette.vectorU.copy(alpha = 0.85f) else palette.vectorU.copy(alpha = 0.30f),
                style = Stroke(width = if (isCenter) 2.8f else 1.4f)
            )
        }

        // Wireframe v-curves (Amber family)
        for (i in 0..steps) {
            val uVal = -range + (2f * range * i / steps)
            val path = Path()
            for (j in 0..24) {
                val vVal = -range + (2f * range * j / 24)
                val pt = project(uVal, vVal, surfaceZ(uVal, vVal))
                if (j == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
            }
            val isCenter = i == steps / 2
            drawPath(
                path = path,
                color = if (isCenter) palette.vectorV.copy(alpha = 0.85f) else palette.vectorV.copy(alpha = 0.28f),
                style = Stroke(width = if (isCenter) 2.8f else 1.4f)
            )
        }

        // Tangent plane T_pM & Surface Normal n⃗ at point p = (pointU, pointV)
        val pu = state.pointU
        val pv = state.pointV
        val pz = surfaceZ(pu, pv)
        val (dzdu, dzdv) = when (state.kind) {
            ManifoldKind.PLANE -> 0f to 0f
            ManifoldKind.SPHERE -> (-a * pu) to (-a * pv)
            ManifoldKind.SADDLE -> (a * pu) to (-a * pv)
        }

        // Tangent plane patch corners
        val h = 0.42f
        val c1 = project(pu - h, pv - h, pz - h * dzdu - h * dzdv)
        val c2 = project(pu + h, pv - h, pz + h * dzdu - h * dzdv)
        val c3 = project(pu + h, pv + h, pz + h * dzdu + h * dzdv)
        val c4 = project(pu - h, pv + h, pz - h * dzdu + h * dzdv)
        val planePath = Path().apply {
            moveTo(c1.x, c1.y)
            lineTo(c2.x, c2.y)
            lineTo(c3.x, c3.y)
            lineTo(c4.x, c4.y)
            close()
        }
        drawPath(planePath, color = palette.tangentPlaneFill)
        drawPath(planePath, color = palette.vectorU, style = Stroke(width = 2.4f))

        // Surface normal vector (-dzdu, -dzdv, 1) normalized
        val nLen = sqrt(dzdu * dzdu + dzdv * dzdv + 1f)
        val nx = -dzdu / nLen
        val ny = -dzdv / nLen
        val nz = 1f / nLen
        val pScreen = project(pu, pv, pz)
        val normalTip = project(pu + nx * 0.72f, pv + ny * 0.72f, pz + nz * 0.72f)
        drawVectorArrow(pScreen, normalTip, palette.projectionShadow, 5f, 15f)

        // Point p glowing dot
        drawCircle(color = Color.White.copy(alpha = 0.28f), radius = 12f, center = pScreen)
        drawCircle(color = Color.White, radius = 5.5f, center = pScreen)

        drawMathBadge(textMeasurer, "n⃗", normalTip + Offset(8f, -14f), palette.projectionShadow)
        drawMathBadge(textMeasurer, "T_pM", c2 + Offset(6f, -10f), palette.vectorU)
        drawMathBadge(textMeasurer, "ড্র্যাগ করে 3D ঘোরান", Offset(12f, 12f), palette.labelText.copy(alpha = 0.72f))
    }
}
