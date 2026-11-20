package com.example.viewmodel

import androidx.compose.runtime.Immutable
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.sqrt

@Immutable
data class Vec2(val x: Float, val y: Float) {
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun times(scalar: Float) = Vec2(x * scalar, y * scalar)
    fun dot(other: Vec2): Float = x * other.x + y * other.y
    fun norm(): Float = hypot(x, y)
}

enum class AlignmentKind(val labelBn: String, val labelEn: String) {
    ACUTE("সূক্ষ্মকোণ", "Acute"),
    ORTHOGONAL("লম্ব", "Orthogonal"),
    OBTUSE("স্থূলকোণ", "Obtuse")
}

@Immutable
data class VectorAnalysis(
    val u: Vec2,
    val v: Vec2,
    val normU: Float,
    val normV: Float,
    val dotProduct: Float,
    val cosTheta: Float,
    val angleDegrees: Float,
    val shadowLengthSigned: Float,
    val projectionOnV: Vec2,
    val alignment: AlignmentKind
) {
    companion object {
        fun compute(u: Vec2, v: Vec2): VectorAnalysis {
            val nU = u.norm()
            val nV = v.norm()
            val dot = u.dot(v)
            val cosT = if (nU > 1e-4f && nV > 1e-4f) {
                (dot / (nU * nV)).coerceIn(-1f, 1f)
            } else 0f
            val angleDeg = (acos(cosT.toDouble()) * 180.0 / PI).toFloat()
            val signedShadow = if (nV > 1e-4f) dot / nV else 0f
            val projScale = if (nV > 1e-4f) dot / (nV * nV) else 0f
            val proj = v * projScale
            val align = when {
                abs(dot) < 0.12f -> AlignmentKind.ORTHOGONAL
                dot > 0f -> AlignmentKind.ACUTE
                else -> AlignmentKind.OBTUSE
            }
            return VectorAnalysis(
                u = u,
                v = v,
                normU = nU,
                normV = nV,
                dotProduct = dot,
                cosTheta = cosT,
                angleDegrees = angleDeg,
                shadowLengthSigned = signedShadow,
                projectionOnV = proj,
                alignment = align
            )
        }
    }
}

enum class VectorAnimOp(val titleBn: String, val titleEn: String, val formula: String) {
    ADDITION("ভেক্টর যোগ", "Vector Addition", "w⃗ = u⃗ + v⃗"),
    SCALING("স্কেলার গুণন", "Scalar Scaling", "w⃗ = c · v⃗"),
    PROJECTION("লম্ব অভিক্ষেপ", "Orthogonal Shadow", "proj_v(u⃗) = ((u⃗·v⃗)/‖v⃗‖²) v⃗"),
    LINEAR_TRANSFORM("লিনিয়ার রূপান্তর", "Matrix Grid Warp", "T(v⃗) = A · v⃗")
}

@Immutable
data class MetricTensor2D(
    val g11: Float = 1f,
    val g12: Float = 0f,
    val g22: Float = 1f
) {
    val determinant: Float get() = g11 * g22 - g12 * g12
    val isPositiveDefinite: Boolean get() = g11 > 0.05f && determinant > 0.05f

    fun innerProduct(u: Vec2, v: Vec2): Float =
        g11 * u.x * v.x + g12 * (u.x * v.y + u.y * v.x) + g22 * u.y * v.y

    fun norm(v: Vec2): Float {
        val q = innerProduct(v, v)
        return if (q > 0f) sqrt(q) else 0f
    }

    fun angleDegrees(u: Vec2, v: Vec2): Float {
        val nu = norm(u)
        val nv = norm(v)
        if (nu < 1e-4f || nv < 1e-4f) return 0f
        val c = (innerProduct(u, v) / (nu * nv)).coerceIn(-1f, 1f)
        return (acos(c.toDouble()) * 180.0 / PI).toFloat()
    }

    /**
     * Generates points along the unit circle {v : v^T G v = 1} in coordinate space.
     */
    fun unitBallPoints(samples: Int = 96): List<Vec2> {
        if (!isPositiveDefinite) return emptyList()
        val pts = ArrayList<Vec2>(samples + 1)
        for (i in 0..samples) {
            val theta = (2.0 * PI * i / samples).toFloat()
            val dir = Vec2(cos(theta), sin(theta))
            val q = innerProduct(dir, dir)
            val r = if (q > 1e-5f) 1f / sqrt(q) else 0f
            pts.add(Vec2(dir.x * r, dir.y * r))
        }
        return pts
    }
}

enum class ManifoldKind(
    val titleBn: String,
    val titleEn: String,
    val curvatureBadge: String,
    val descriptionBn: String
) {
    SPHERE(
        titleBn = "গোলক ম্যানিফোল্ড",
        titleEn = "Elliptic / Sphere",
        curvatureBadge = "K > 0 (ধনাত্মক বক্রতা)",
        descriptionBn = "পৃথিবীর পৃষ্ঠের মতো ধনাত্মক গাউসিয়ান বক্রতা—সমান্তরাল জিওডেসিক পরস্পরের কাছে এসে মিলিত হয়।"
    ),
    PLANE(
        titleBn = "ইউক্লিডীয় সমতল",
        titleEn = "Flat Euclidean Plane",
        curvatureBadge = "K = 0 (শূন্য বক্রতা)",
        descriptionBn = "কোনো অন্তর্নিহিত বক্রতা নেই—প্যারালাল ট্রান্সপোর্টে ভেক্টরের দিক অপরিবর্তিত থাকে।"
    ),
    SADDLE(
        titleBn = "হাইপারবোলিক স্যাডল",
        titleEn = "Hyperbolic Saddle",
        curvatureBadge = "K < 0 (ঋণাত্মক বক্রতা)",
        descriptionBn = "ঘোড়ার জিনের মতো বিপরীত দিকে বাঁকানো প্রধান বক্রতা (κ₁ > 0, κ₂ < 0)—জিওডেসিকগুলো দ্রুত দূরে সরে যায়।"
    )
}

@Immutable
data class ManifoldState(
    val kind: ManifoldKind = ManifoldKind.SADDLE,
    val curvatureScale: Float = 0.72f,
    val yawDeg: Float = 36f,
    val pitchDeg: Float = 26f,
    val pointU: Float = 0.25f,
    val pointV: Float = 0.2f
) {
    /**
     * Computes local principal curvatures (k1, k2), Gaussian curvature K = k1*k2,
     * Mean curvature H = (k1 + k2)/2, and scalar curvature R = 2K at point (pointU, pointV).
     */
    fun computeTelemetry(): CurvatureTelemetry {
        val a = curvatureScale.coerceIn(0.15f, 1.35f)
        return when (kind) {
            ManifoldKind.PLANE -> CurvatureTelemetry(
                k1 = 0f,
                k2 = 0f,
                gaussianK = 0f,
                meanH = 0f,
                ricciScalarR = 0f
            )
            ManifoldKind.SPHERE -> {
                // Paraboloid dome z = -0.5 * a * (u^2 + v^2)
                val fx = -a * pointU
                val fy = -a * pointV
                val denom = 1f + fx * fx + fy * fy
                val kGauss = (a * a) / (denom * denom)
                val k1 = sqrt(kGauss)
                val k2 = sqrt(kGauss)
                CurvatureTelemetry(
                    k1 = k1,
                    k2 = k2,
                    gaussianK = kGauss,
                    meanH = (k1 + k2) / 2f,
                    ricciScalarR = 2f * kGauss
                )
            }
            ManifoldKind.SADDLE -> {
                // Hyperbolic paraboloid z = 0.5 * a * (u^2 - v^2)
                val fx = a * pointU
                val fy = -a * pointV
                val denom = 1f + fx * fx + fy * fy
                val kGauss = -(a * a) / (denom * denom)
                val k1 = a / sqrt(denom)
                val k2 = -a / sqrt(denom)
                CurvatureTelemetry(
                    k1 = k1,
                    k2 = k2,
                    gaussianK = kGauss,
                    meanH = (k1 + k2) / 2f,
                    ricciScalarR = 2f * kGauss
                )
            }
        }
    }
}

@Immutable
data class CurvatureTelemetry(
    val k1: Float,
    val k2: Float,
    val gaussianK: Float,
    val meanH: Float,
    val ricciScalarR: Float
)
