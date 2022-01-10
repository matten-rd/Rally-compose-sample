package com.example.rally.ui.components.linechart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat
import kotlin.math.*

interface XAxisDrawer {
    fun requiredHeight(drawScope: DrawScope): Float

    fun drawAxisLine(
        drawScope: DrawScope,
        canvas: Canvas,
        drawableArea: Rect
    )

    fun drawAxisLabels(
        drawScope: DrawScope,
        canvas: Canvas,
        drawableArea: Rect,
        labels: List<String>
    )
}

// FIXME: I think the whole labelRatio thing is inherently flawed
class SimpleXAxisDrawer(
    private val labelTextSize: TextUnit = 12.sp,
    private val labelTextColor: Color = Color.White,
    /** 1 means we draw everything. 2 means we draw every other, and so on. */
    private val labelRatio: Int = 1,
    private val axisLineThickness: Dp = 1.dp,
    private val axisLineColor: Color = Color.DarkGray
) : XAxisDrawer {
    private val axisLinePaint = Paint().apply {
        isAntiAlias = true
        color = axisLineColor
        style = PaintingStyle.Stroke
    }

    private val textPaint = android.graphics.Paint().apply {
        isAntiAlias = true
        color = labelTextColor.toLegacyInt()
    }

    override fun requiredHeight(drawScope: DrawScope): Float {
        return with(drawScope) {
            (3f / 2f) * (labelTextSize.toPx() + axisLineThickness.toPx())
        }
    }

    override fun drawAxisLine(
        drawScope: DrawScope,
        canvas: Canvas,
        drawableArea: Rect
    ) {
        with(drawScope) {
            val lineThickness = axisLineThickness.toPx()
            val y = drawableArea.top + (lineThickness / 2f)

            canvas.drawLine(
                p1 = Offset(
                    x = drawableArea.left,
                    y = y
                ),
                p2 = Offset(
                    x = drawableArea.right,
                    y = y
                ),
                paint = axisLinePaint.apply {
                    strokeWidth = lineThickness
                }
            )
        }
    }

    override fun drawAxisLabels(
        drawScope: DrawScope,
        canvas: Canvas,
        drawableArea: Rect,
        labels: List<String>
    ) {
        with(drawScope) {
            val labelPaint = textPaint.apply {
                textSize = labelTextSize.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
            }

            val labelIncrements = drawableArea.width / (labels.size - 1)
            labels.forEachIndexed { index, label ->
                if (index.rem(labelRatio) == 0) {
                    val x = drawableArea.left + (labelIncrements * (index))
                    val y = drawableArea.bottom

                    canvas.nativeCanvas.drawText(label, x, y, labelPaint)
                }
            }
        }
    }
}


interface YAxisDrawer {
    fun drawAxisLine(
        drawScope: DrawScope,
        canvas: Canvas,
        drawableArea: Rect
    )

    fun drawAxisLabels(
        drawScope: DrawScope,
        canvas: Canvas,
        drawableArea: Rect,
        minValue: Float,
        maxValue: Float
    )
}

typealias LabelFormatter = (value: Float) -> String

private fun prettyCount(number: Float): String {
    val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
    val numValue = number.toLong()
    val value = floor(log10(numValue.toDouble())).toInt()
    val base = value / 3
    return if (value >= 3 && base < suffix.size) {
        DecimalFormat("#0.0").format(
            numValue / 10.0.pow((base * 3).toDouble())
        ) + suffix[base]
    } else {
        DecimalFormat("#,##0").format(numValue)
    }
}

// FIXME: I think the whole labelRatio thing is inherently flawed
class SimpleYAxisDrawer(
    private val labelTextSize: TextUnit = 12.sp,
    private val labelTextColor: Color = Color.White,
    private val labelRatio: Int = 3,
    private val labelValueFormatter: LabelFormatter = { value -> prettyCount(value) },
    private val axisLineThickness: Dp = 1.dp,
    private val axisLineColor: Color = Color.DarkGray
) : YAxisDrawer {
    private val axisLinePaint = Paint().apply {
        isAntiAlias = true
        color = axisLineColor
        style = PaintingStyle.Stroke
    }
    private val textPaint = android.graphics.Paint().apply {
        isAntiAlias = true
        color = labelTextColor.toLegacyInt()
    }
    private val textBounds = android.graphics.Rect()

    override fun drawAxisLine(
        drawScope: DrawScope,
        canvas: Canvas,
        drawableArea: Rect
    ) = with(drawScope) {
        val lineThickness = axisLineThickness.toPx()
        val x = drawableArea.right - (lineThickness / 2f)

        canvas.drawLine(
            p1 = Offset(
                x = x,
                y = drawableArea.top
            ),
            p2 = Offset(
                x = x,
                y = drawableArea.bottom
            ),
            paint = axisLinePaint.apply {
                strokeWidth = lineThickness
            }
        )
    }

    override fun drawAxisLabels(
        drawScope: DrawScope,
        canvas: Canvas,
        drawableArea: Rect,
        minValue: Float,
        maxValue: Float
    ) = with(drawScope) {
        val labelPaint = textPaint.apply {
            textSize = labelTextSize.toPx()
            textAlign = android.graphics.Paint.Align.RIGHT
        }
        val minLabelHeight = (labelTextSize.toPx() * labelRatio.toFloat())
        val totalHeight = drawableArea.height
        val labelCount = max((drawableArea.height / minLabelHeight).roundToInt(), 2)

        for (i in 0..labelCount) {
            val value = minValue + (i * ((maxValue - minValue) / labelCount))

            val label = labelValueFormatter(value)
            val x =
                drawableArea.right - axisLineThickness.toPx() - (labelTextSize.toPx() / 2f)

            labelPaint.getTextBounds(label, 0, label.length, textBounds)

            val y =
                drawableArea.bottom - (i * (totalHeight / labelCount)) + (textBounds.height() / 2f)

            canvas.nativeCanvas.drawText(label, x, y, labelPaint)
        }
    }
}



fun Color.toLegacyInt(): Int {
    return android.graphics.Color.argb(
        (alpha * 255.0f + 0.5f).toInt(),
        (red * 255.0f + 0.5f).toInt(),
        (green * 255.0f + 0.5f).toInt(),
        (blue * 255.0f + 0.5f).toInt()
    )
}