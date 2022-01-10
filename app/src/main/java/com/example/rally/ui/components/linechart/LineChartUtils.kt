package com.example.rally.ui.components.linechart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

object LineChartUtils {
    /**
     * The actual graphs drawable area
     * @return a Rect of the allowed drawable area
     */
    fun calculateDrawableArea(
        xAxisDrawableArea: Rect,
        yAxisDrawableArea: Rect,
        size: Size,
        offset: Float
    ): Rect {
        val horizontalOffset = xAxisDrawableArea.width * offset / 100f

        return Rect(
            left = yAxisDrawableArea.right + horizontalOffset,
            top = 0f,
            bottom = xAxisDrawableArea.top,
            right = size.width - horizontalOffset
        )
    }

    /**
     * The drawable area of the line that marks the X axis
     * @return a Rect of the allowed drawable area
     */
    fun calculateXAxisDrawableArea(
        yAxisWidth: Float,
        labelHeight: Float,
        size: Size
    ): Rect {
        return Rect(
            left = yAxisWidth,
            top = size.height - labelHeight,
            bottom = size.height,
            right = size.width
        )
    }

    /**
     * The drawable area of the labels on the X axis
     * @param offset specify padding from the edges in the X direction
     * @return a Rect of the allowed drawable area
     */
    fun calculateXAxisLabelsDrawableArea(
        xAxisDrawableArea: Rect,
        offset: Float
    ): Rect {
        val horizontalOffset = xAxisDrawableArea.width * offset / 100f

        return Rect(
            left = xAxisDrawableArea.left + horizontalOffset,
            top = xAxisDrawableArea.top,
            bottom = xAxisDrawableArea.bottom,
            right = xAxisDrawableArea.right - horizontalOffset
        )
    }

    /**
     * The drawable area of the line that marks the Y axis
     * @return a Rect of the allowed drawable area
     */
    fun Density.calculateYAxisDrawableArea(
        xAxisLabelSize: Float,
        size: Size,
    ): Rect {
        // Either 50dp or 15% of the chart width.
        val right = minOf(50.dp.toPx(), size.width * 15f / 100f)

        return Rect(
            left = 0f,
            top = 0f,
            bottom = size.height - xAxisLabelSize,
            right = right
        )
    }

    /**
     * The drawable area of the labels on the Y axis
     * @param offset specify padding from the edges in the Y direction
     * @return a Rect of the allowed drawable area
     */
    fun calculateYAxisLabelsDrawableArea(
        yAxisDrawableArea: Rect,
        offset: Float
    ): Rect {
        val verticalOffset = yAxisDrawableArea.width * offset / 100f

        return Rect(
            left = yAxisDrawableArea.left,
            top = yAxisDrawableArea.top + verticalOffset,
            bottom = yAxisDrawableArea.bottom - verticalOffset,
            right = yAxisDrawableArea.right
        )
    }

    /**
     * Handle the animation of the lines drawn
     */
    private fun withProgress(
        index: Int,
        lineChartData: List<LineChartData>,
        transitionProgress: Float,
        showWithProgress: (progress: Float) -> Unit
    ) {
        val size = lineChartData.size
        // Calculate the index (point) we are moving to by the transitionProgress
        val toIndex = (size * transitionProgress).toInt() + 1

        // If we are on the index we are moving toward then only draw the part of the line
        // corresponding to the progress between the two points
        if (index == toIndex) {
            // Get the left over.
            val perIndex = (1f / size.toFloat())
            val down = (index - 1) * perIndex

            showWithProgress((transitionProgress - down) / perIndex)
        } else if (index < toIndex) {
            // If we have already passed the index we are moving toward then display the full line
            showWithProgress(1f)
        }
    }

    fun calculateLinePath(
        drawableArea: Rect,
        lineChartData: List<LineChartData>,
        transitionProgress: Float
    ): Path = Path().apply {
        var prevPointLocation: Offset? = null
        val maxYCoordinate = lineChartData.maxOf { it.amount }
        val minYCoordinate = lineChartData.minOf { it.amount }
        val maxXCoordinate = lineChartData.maxOf { it.timeStamp }
        val minXCoordinate = lineChartData.minOf { it.timeStamp }
        lineChartData.forEachIndexed { index, point ->
            withProgress(
                index = index,
                transitionProgress = transitionProgress,
                lineChartData = lineChartData
            ) { progress ->
                val pointLocation = calculatePointLocation(
                    drawableArea = drawableArea,
                    point = point,
                    maxXCoordinate = maxXCoordinate,
                    minXCoordinate = minXCoordinate,
                    maxYCoordinate = maxYCoordinate,
                    minYCoordinate = minYCoordinate
                )

                if (index == 0) {
                    // Start by moving to the first pointLocation
                    moveTo(pointLocation.x, pointLocation.y)
                } else {
                    if (progress <= 1f) {
                        // Animate the line to the next point
                        // We have to change the `dy` based on the progress
                        val prevX = prevPointLocation!!.x
                        val prevY = prevPointLocation!!.y
                        // Calculate how "far" we have come based on progress between the two points
                        val x = (pointLocation.x - prevX) * progress + prevX
                        val y = (pointLocation.y - prevY) * progress + prevY

                        lineTo(x, y)
                    } else {
                        // Draw full line to the specified point
                        lineTo(pointLocation.x, pointLocation.y)
                    }
                }

                prevPointLocation = pointLocation
            }
        }
    }

    private fun calculatePointLocation(
        drawableArea: Rect,
        point: LineChartData,
        maxXCoordinate: Long,
        minXCoordinate: Long,
        maxYCoordinate: Float,
        minYCoordinate: Float,
    ): Offset {
        val x = calculateXCoordinate(
            maxXCoordinate = maxXCoordinate,
            minXCoordinate = minXCoordinate,
            currentXCoordinate = point.timeStamp,
            canvasWidth = drawableArea.width
        )
        val y = calculateYCoordinate(
            maxYCoordinate = maxYCoordinate,
            minYCoordinate = minYCoordinate,
            currentYCoordinate = point.amount,
            canvasHeight = drawableArea.height
        )

        return Offset(x = x + drawableArea.left, y = y)
    }

    private fun calculateXCoordinate(
        maxXCoordinate: Long,
        minXCoordinate: Long,
        currentXCoordinate: Long,
        canvasWidth: Float
    ): Float {
        val shiftedMaxValue = maxXCoordinate - minXCoordinate
        val shiftedCurrentValue = currentXCoordinate - minXCoordinate
        val relativePercentageOfScreen = canvasWidth / shiftedMaxValue
        return shiftedCurrentValue.toFloat() * relativePercentageOfScreen
    }


    private fun calculateYCoordinate(
        maxYCoordinate: Float,
        minYCoordinate: Float,
        currentYCoordinate: Float,
        canvasHeight: Float
    ): Float {
        val minAndMaxValueDifference = maxOf(maxYCoordinate - minYCoordinate, 1f)
        val maxAndCurrentValueDifference = maxYCoordinate - currentYCoordinate
        val relativePercentageOfScreen = canvasHeight / minAndMaxValueDifference
        return maxAndCurrentValueDifference * relativePercentageOfScreen
    }


    fun calculateFillPath(
        drawableArea: Rect,
        lineChartData: List<LineChartData>,
        transitionProgress: Float
    ): Path = Path().apply {

        // we start from the bottom left
        moveTo(drawableArea.left, drawableArea.bottom)
        var prevPointX : Float? = null
        var prevPointLocation: Offset? = null
        val maxYCoordinate = lineChartData.maxOf { it.amount }
        val minYCoordinate = lineChartData.minOf { it.amount }
        val maxXCoordinate = lineChartData.maxOf { it.timeStamp }
        val minXCoordinate = lineChartData.minOf { it.timeStamp }
        lineChartData.forEachIndexed { index, point ->
            withProgress(
                index = index,
                transitionProgress = transitionProgress,
                lineChartData = lineChartData
            ) { progress ->
                val pointLocation = calculatePointLocation(
                    drawableArea = drawableArea,
                    point = point,
                    maxXCoordinate = maxXCoordinate,
                    minXCoordinate = minXCoordinate,
                    maxYCoordinate = maxYCoordinate,
                    minYCoordinate = minYCoordinate
                )


                if (index == 0) {
                    lineTo(drawableArea.left, pointLocation.y)
                    lineTo(pointLocation.x, pointLocation.y)
                } else {
                    if (progress <= 1f) {
                        // We have to change the `dy` based on the progress
                        val prevX = prevPointLocation!!.x
                        val prevY = prevPointLocation!!.y

                        val x = (pointLocation.x - prevX) * progress + prevX
                        val y = (pointLocation.y - prevY) * progress + prevY

                        lineTo(x, y)

                        prevPointX = x
                    } else {
                        lineTo(pointLocation.x, pointLocation.y)
                        prevPointX = pointLocation.x
                    }
                }

                prevPointLocation = pointLocation
            }
        }
        // We need to connect the line to the end of the drawable area
        prevPointX?.let { x->
            lineTo(x, drawableArea.bottom)
            lineTo(drawableArea.right, drawableArea.bottom)
        } ?: lineTo(drawableArea.left, drawableArea.bottom)
    }
}