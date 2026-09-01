package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NomadInk
import com.example.ui.theme.NomadLine
import com.example.ui.theme.NomadMist
import kotlin.random.Random

@Composable
fun QrCodeVisual(
    code: String,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    qrColor: Color = NomadInk,
    backgroundColor: Color = NomadMist
) {
    // Generate deterministic grid pattern based on the credential code
    val matrixSize = 17
    val grid = remember(code) {
        val seed = code.hashCode().toLong()
        val random = Random(seed)
        Array(matrixSize) { r ->
            BooleanArray(matrixSize) { c ->
                // Keep the 3 finder corner squares standard QR shape
                val isTopLeftCorner = (r < 5 && c < 5)
                val isTopRightCorner = (r < 5 && c >= matrixSize - 5)
                val isBottomLeftCorner = (r >= matrixSize - 5 && c < 5)

                if (isTopLeftCorner || isTopRightCorner || isBottomLeftCorner) {
                    false
                } else {
                    random.nextBoolean()
                }
            }
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .border(1.dp, NomadLine, RoundedCornerShape(6.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().aspectRatio(1f)) {
            val canvasWidth = this.size.width
            val cellSize = canvasWidth / matrixSize

            // Draw Finder Patterns (3 corners)
            fun drawFinder(x: Float, y: Float, finderCells: Float = 4.5f) {
                val finderWidth = finderCells * cellSize
                // Outer square outline
                drawRoundRect(
                    color = qrColor,
                    topLeft = Offset(x, y),
                    size = Size(finderWidth, finderWidth),
                    cornerRadius = CornerRadius(3.dp.toPx()),
                    style = Stroke(width = cellSize * 0.9f)
                )
                // Inner solid square
                val innerOffset = finderWidth * 0.28f
                val innerSize = finderWidth * 0.44f
                drawRoundRect(
                    color = qrColor,
                    topLeft = Offset(x + innerOffset, y + innerOffset),
                    size = Size(innerSize, innerSize),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
            }

            // Top-Left
            drawFinder(0f, 0f)
            // Top-Right
            drawFinder(canvasWidth - 4.5f * cellSize, 0f)
            // Bottom-Left
            drawFinder(0f, canvasWidth - 4.5f * cellSize)

            // Draw data modules
            for (r in 0 until matrixSize) {
                for (c in 0 until matrixSize) {
                    val isTopLeftCorner = (r < 5 && c < 5)
                    val isTopRightCorner = (r < 5 && c >= matrixSize - 5)
                    val isBottomLeftCorner = (r >= matrixSize - 5 && c < 5)

                    if (!isTopLeftCorner && !isTopRightCorner && !isBottomLeftCorner && grid[r][c]) {
                        drawRoundRect(
                            color = qrColor,
                            topLeft = Offset(c * cellSize + cellSize * 0.1f, r * cellSize + cellSize * 0.1f),
                            size = Size(cellSize * 0.8f, cellSize * 0.8f),
                            cornerRadius = CornerRadius(1.5.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}
