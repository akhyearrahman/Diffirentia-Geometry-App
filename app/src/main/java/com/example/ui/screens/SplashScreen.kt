package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.MathMonoFontFamily
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.WarmAmber
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
    onEnterStudio: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash_manifold")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "geodesic_phase"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
    ) {
        // Generated dark Riemannian manifold hero artwork
        Image(
            painter = painterResource(id = R.drawable.img_manifold_hero_1795175609808),
            contentDescription = "Riemannian manifold dark geometric backdrop",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.68f)
        )

        // Atmospheric obsidian radial/vertical scrim for razor-sharp typography legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ObsidianBackground.copy(alpha = 0.55f),
                            ObsidianBackground.copy(alpha = 0.78f),
                            ObsidianBackground.copy(alpha = 0.96f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .widthIn(max = 560.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top micro-badge
            Surface(
                shape = CircleShape,
                color = ElectricCyan.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.35f))
            ) {
                Text(
                    text = "DIFFERENTIAL GEOMETRY STUDIO • OFFLINE READY",
                    fontFamily = MathMonoFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElectricCyan,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            // Centerpiece animated live vector geodesic emblem + brand identity
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.size(164.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val r = size.minDimension * 0.42f

                        // Outer rotating geodesic ellipse rings
                        for (ring in 0 until 3) {
                            val tilt = phase * (if (ring % 2 == 0) 1f else -0.7f) + ring * 1.05f
                            val path = Path()
                            for (i in 0..72) {
                                val a = (2.0 * PI * i / 72).toFloat()
                                val lx = cos(a) * r
                                val ly = sin(a) * r * (0.42f + ring * 0.14f)
                                val rx = lx * cos(tilt) - ly * sin(tilt)
                                val ry = lx * sin(tilt) + ly * cos(tilt)
                                val px = cx + rx
                                val py = cy + ry
                                if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                            }
                            path.close()
                            val ringColor = when (ring) {
                                0 -> ElectricCyan.copy(alpha = 0.85f)
                                1 -> WarmAmber.copy(alpha = 0.72f)
                                else -> EmeraldMint.copy(alpha = 0.55f)
                            }
                            drawPath(path, color = ringColor, style = Stroke(width = 2.4f))
                        }

                        // Orbiting tangent particle on primary geodesic
                        val px = cx + cos(phase) * r
                        val py = cy + sin(phase) * r * 0.42f
                        drawCircle(color = WarmAmber.copy(alpha = 0.30f), radius = 14f, center = Offset(px, py))
                        drawCircle(color = WarmAmber, radius = 5f, center = Offset(px, py))
                    }

                    // Center math symbol g_ij
                    Surface(
                        shape = CircleShape,
                        color = ObsidianBackground.copy(alpha = 0.85f),
                        border = BorderStroke(1.5.dp, ElectricCyan.copy(alpha = 0.5f)),
                        modifier = Modifier.size(58.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "gᵢⱼ",
                                fontFamily = MathMonoFontFamily,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = stringResource(id = R.string.app_name),
                    fontFamily = DisplayFontFamily,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.splash_tagline),
                    style = MaterialTheme.typography.titleMedium,
                    color = ElectricCyan,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(id = R.string.splash_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFB6C6DF),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Core concept pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SplashConceptPill(Icons.Outlined.Public, "Manifolds", ElectricCyan)
                    SplashConceptPill(Icons.Outlined.GridOn, "Tensors", WarmAmber)
                    SplashConceptPill(Icons.Outlined.AutoGraph, "Curvature", EmeraldMint)
                }
            }

            // Bottom Action
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onEnterStudio,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("splash_enter_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricCyan,
                        contentColor = Color(0xFF021A27)
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.splash_enter_cta),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Enter GeoManifold studio",
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "৪ ধাপ • ২১ মডিউল • ইন্টারেক্টিভ ভেক্টর ও মেট্রিক ল্যাব",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8CA2C4)
                )
            }
        }
    }
}

@Composable
private fun SplashConceptPill(
    icon: ImageVector,
    label: String,
    accent: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = accent.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.30f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accent,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                fontFamily = DisplayFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}
