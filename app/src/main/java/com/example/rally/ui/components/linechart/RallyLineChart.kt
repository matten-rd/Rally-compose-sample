package com.example.rally.ui.components.linechart

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.rally.ui.components.linechart.LineChartUtils.calculateDrawableArea
import com.example.rally.ui.components.linechart.LineChartUtils.calculateFillPath
import com.example.rally.ui.components.linechart.LineChartUtils.calculateLinePath
import com.example.rally.ui.components.linechart.LineChartUtils.calculateXAxisDrawableArea
import com.example.rally.ui.components.linechart.LineChartUtils.calculateXAxisLabelsDrawableArea
import com.example.rally.ui.components.linechart.LineChartUtils.calculateYAxisDrawableArea
import com.example.rally.ui.components.linechart.LineChartUtils.calculateYAxisLabelsDrawableArea
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset


data class LineChartData(
    val timeStamp: Long,
    val amount: Float
)

@Composable
internal fun RallyLineChart(
    modifier: Modifier = Modifier,
    color: Color,
    lineChartData: List<LineChartData>,
    lineDrawer: LineDrawer = SolidLineDrawer(color = color),
    lineShader: LineShader = NoLineShader,
    xAxisDrawer: XAxisDrawer = SimpleXAxisDrawer(
        labelRatio = 4, axisLineColor = MaterialTheme.colors.onSurface.copy(0.1f)),
    yAxisDrawer: YAxisDrawer = SimpleYAxisDrawer(
        axisLineColor = MaterialTheme.colors.onSurface.copy(0.1f)
    ),
) {
    if (lineChartData.size <= 1) {
        return Box(modifier, contentAlignment = Alignment.Center) {
            Text(text = "Inte tillräckligt med data ännu")
        }
    }

    val transitionAnimation = remember(lineChartData) { Animatable(initialValue = 0f) }

    LaunchedEffect(lineChartData) {
        transitionAnimation.snapTo(0f)
        transitionAnimation.animateTo(1f,
            animationSpec = TweenSpec<Float>(durationMillis = 900, delay = 300, easing = LinearOutSlowInEasing)
        )
    }
    val maxYCoordinate = lineChartData.maxOf { it.amount }
    val minYCoordinate = lineChartData.minOf { it.amount }

    // TODO: Implement axis labels etc
    Canvas(modifier = modifier) {

        drawIntoCanvas { canvas: Canvas ->
            // Y - axis and labels
            val yAxisDrawableArea = calculateYAxisDrawableArea(
                xAxisLabelSize = xAxisDrawer.requiredHeight(this),
                size = size
            )
            val yAxisLabelsDrawableArea = calculateYAxisLabelsDrawableArea(
                yAxisDrawableArea = yAxisDrawableArea,
                offset = 10f
            )
            // X - axis and labels
            val xAxisDrawableArea = calculateXAxisDrawableArea(
                yAxisWidth = yAxisDrawableArea.width,
                labelHeight = xAxisDrawer.requiredHeight(this),
                size = size
            )
            val xAxisLabelsDrawableArea = calculateXAxisLabelsDrawableArea(
                xAxisDrawableArea = xAxisDrawableArea,
                offset = 10f
            )
            // Chart
            val chartDrawableArea = calculateDrawableArea(
                xAxisDrawableArea = xAxisDrawableArea,
                yAxisDrawableArea = yAxisDrawableArea,
                size = size,
                offset = 0f
            )

            // Draw the line(s)
            lineDrawer.drawLine(
                drawScope = this,
                canvas = canvas,
                linePath = calculateLinePath(
                    drawableArea = chartDrawableArea,
                    lineChartData = lineChartData,
                    transitionProgress = transitionAnimation.value
                )
            )

            // Only add lineShader if it's not NoLineShader
            if (lineShader !is NoLineShader) {
                lineShader.fillLine(
                    drawScope = this,
                    canvas = canvas,
                    fillPath = calculateFillPath(
                        drawableArea = chartDrawableArea,
                        lineChartData = lineChartData,
                        transitionProgress = transitionAnimation.value
                    )
                )
            }

            // Draw the X Axis line and labels.
            xAxisDrawer.drawAxisLine(
                drawScope = this,
                drawableArea = xAxisDrawableArea,
                canvas = canvas
            )
            // TODO: change the labels according to the TimeInterval selected
            xAxisDrawer.drawAxisLabels(
                drawScope = this,
                canvas = canvas,
                drawableArea = xAxisLabelsDrawableArea,
                labels = lineChartData.map {
                    OffsetDateTime
                        .ofInstant(Instant.ofEpochMilli(it.timeStamp), ZoneOffset.UTC)
                        .month.name.take(3)
                }
            )

            // Draw the Y Axis line and labels.
            yAxisDrawer.drawAxisLine(
                drawScope = this,
                canvas = canvas,
                drawableArea = yAxisDrawableArea
            )
            yAxisDrawer.drawAxisLabels(
                drawScope = this,
                canvas = canvas,
                drawableArea = yAxisLabelsDrawableArea,
                minValue = minYCoordinate,
                maxValue = maxYCoordinate
            )
        }

    }
}

/**
 * LineDrawer is the Interface that handles drawing the line
 */
interface LineDrawer {
    fun drawLine(
        drawScope: DrawScope,
        canvas: Canvas,
        linePath: Path
    )
}

data class SolidLineDrawer(
    val thickness: Dp = 3.dp,
    val color: Color = Color.Green
) : LineDrawer {
    private val paint = Paint().apply {
        this.color = this@SolidLineDrawer.color
        this.style = PaintingStyle.Stroke
        this.isAntiAlias = true
    }

    override fun drawLine(
        drawScope: DrawScope,
        canvas: Canvas,
        linePath: Path
    ) {
        val lineThickness = with(drawScope) {
            thickness.toPx()
        }

        canvas.drawPath(
            path = linePath,
            paint = paint.apply {
                strokeWidth = lineThickness
            }
        )
    }
}

/**
 * LineShaders are used to draw under the actual line
 */
interface LineShader {
    fun fillLine(
        drawScope: DrawScope,
        canvas: Canvas,
        fillPath: Path
    )
}

object NoLineShader : LineShader {
    override fun fillLine(drawScope: DrawScope, canvas: Canvas, fillPath: Path) {
        // Do nothing
    }
}

class SolidLineShader(val color: Color = Color.Blue) : LineShader {
    override fun fillLine(drawScope: DrawScope, canvas: Canvas, fillPath: Path) {
        drawScope.drawPath(
            path = fillPath,
            color = color
        )
    }
}
