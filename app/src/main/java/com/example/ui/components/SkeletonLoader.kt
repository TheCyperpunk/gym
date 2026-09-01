package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NomadConcrete
import com.example.ui.theme.NomadLine

@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
    width: Dp? = null,
    shapeRadius: Dp = 4.dp
) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeletonAlpha"
    )

    val boxModifier = if (width != null) {
        modifier.width(width).height(height)
    } else {
        modifier.fillMaxWidth().height(height)
    }

    Box(
        modifier = boxModifier
            .clip(RoundedCornerShape(shapeRadius))
            .background(NomadLine.copy(alpha = alpha))
    )
}

@Composable
fun GymCardSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(NomadConcrete)
            .padding(12.dp)
    ) {
        SkeletonBox(height = 140.dp, shapeRadius = 4.dp)
        Spacer(modifier = Modifier.height(10.dp))
        SkeletonBox(height = 18.dp, width = 160.dp)
        Spacer(modifier = Modifier.height(6.dp))
        SkeletonBox(height = 12.dp, width = 220.dp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            SkeletonBox(height = 20.dp, width = 60.dp)
            SkeletonBox(height = 20.dp, width = 80.dp)
        }
    }
}
