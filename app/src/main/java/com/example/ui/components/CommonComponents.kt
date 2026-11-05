package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyanMuted
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberGreenSuccess
import com.example.ui.theme.CyberOrangeWarning
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberTextTertiary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    borderColor: Color = CyberBorder,
    backgroundColor: Color = CyberSurfaceCard,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor.copy(alpha = 0.85f), RoundedCornerShape(22.dp))
            .padding(20.dp)
    ) {
        content()
    }
}

@Composable
fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun HeroVaultHeader(
    score: Int,
    gradeTitle: String,
    gradeColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, CyberCyanPrimary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
    ) {
        // Hero Image
        Image(
            painter = painterResource(id = R.drawable.img_cyber_vault_1793022363754),
            contentDescription = "Cyber Vault Art",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient scrim overlays for high contrast readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        0.4f to CyberDarkBg.copy(alpha = 0.75f),
                        1.0f to CyberDarkBg.copy(alpha = 0.95f)
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(
                    text = "HARDWARE INTEGRITY ENGINE",
                    color = CyberCyanPrimary
                )

                StatusBadge(
                    text = gradeTitle,
                    color = gradeColor
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "System Security Score",
                        fontSize = 12.sp,
                        color = CyberTextSecondary
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$score",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = CyberTextPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "/100",
                            fontSize = 14.sp,
                            color = CyberTextTertiary,
                            modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                        )
                    }
                }

                CyberRadarVisualizer(
                    modifier = Modifier.size(60.dp),
                    pulseColor = gradeColor
                )
            }
        }
    }
}

@Composable
fun CyberRadarVisualizer(
    modifier: Modifier = Modifier,
    pulseColor: Color = CyberCyanPrimary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAngle"
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        // Outer & inner concentric rings
        drawCircle(
            color = CyberBorder,
            radius = radius,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
        drawCircle(
            color = CyberBorder.copy(alpha = 0.6f),
            radius = radius * 0.65f,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
        drawCircle(
            color = CyberBorder.copy(alpha = 0.3f),
            radius = radius * 0.3f,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )

        // Crosshairs
        drawLine(
            color = CyberBorder.copy(alpha = 0.4f),
            start = Offset(center.x, 0f),
            end = Offset(center.x, size.height),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = CyberBorder.copy(alpha = 0.4f),
            start = Offset(0f, center.y),
            end = Offset(size.width, center.y),
            strokeWidth = 1.dp.toPx()
        )

        // Sweeping radar beam
        val rad = Math.toRadians(sweepAngle.toDouble())
        val beamEnd = Offset(
            (center.x + radius * cos(rad)).toFloat(),
            (center.y + radius * sin(rad)).toFloat()
        )
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(pulseColor.copy(alpha = 0.1f), pulseColor),
                start = center,
                end = beamEnd
            ),
            start = center,
            end = beamEnd,
            strokeWidth = 2.dp.toPx()
        )

        // Center Blip
        drawCircle(
            color = pulseColor,
            radius = 3.dp.toPx(),
            center = center
        )
    }
}

@Composable
fun SecurityScoreGauge(
    score: Int,
    gradeTitle: String,
    gradeColor: Color,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "scoreGauge"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(150.dp)
        ) {
            Canvas(modifier = Modifier.size(140.dp)) {
                val strokeWidth = 12.dp.toPx()
                // Track arc
                drawArc(
                    color = Color(0xFF1E293B),
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                // Score arc
                drawArc(
                    brush = Brush.sweepGradient(
                        0.0f to CyberCyanPrimary,
                        0.5f to gradeColor,
                        1.0f to gradeColor
                    ),
                    startAngle = 135f,
                    sweepAngle = 270f * animatedProgress,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$score",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = CyberTextPrimary,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "/100",
                    fontSize = 12.sp,
                    color = CyberTextTertiary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        StatusBadge(
            text = gradeTitle,
            color = gradeColor
        )
    }
}

@Composable
fun PropertyRow(
    title: String,
    value: String,
    isSafe: Boolean? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val clickableModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    Row(
        modifier = clickableModifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                color = CyberTextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = CyberTextPrimary,
                fontFamily = FontFamily.Monospace
            )
        }

        if (isSafe != null) {
            val statusColor = if (isSafe) CyberGreenSuccess else CyberOrangeWarning
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        if (onClick != null) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Details for $title",
                tint = CyberCyanPrimary.copy(alpha = 0.8f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
