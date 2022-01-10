package com.example.rally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.DecimalFormat
import kotlin.math.absoluteValue


@Composable
internal fun <T> StatementBody(
    items: List<T>,
    colors: (T) -> Color,
    amounts: (T) -> Float,
    totalAmount: Float,
    circleLabel: String,
    cardLabel: String,
    buttonText: String,
    onClick: () -> Unit,
    rows: @Composable (T) -> Unit
) {

    Column {
        // Animating circle and balance box
        Box(Modifier.padding(12.dp)) {
            val accountsProportion = items.extractProportions { amounts(it).absoluteValue }
            val circleColors = items.map { colors(it) }

            // TODO: Check if floatProportions is empty first or if any element is 0f
            AnimatedCircle(
                accountsProportion,
                circleColors,
                Modifier
                    .height(275.dp)
                    .align(Alignment.Center)
                    .fillMaxWidth()
            )
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = circleLabel,
                        style = MaterialTheme.typography.body2
                    )
                }
                Text(
                    text = formatAmount(totalAmount),
                    style = MaterialTheme.typography.h3
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        RallyCard(
            items = items,
            colors = colors,
            amounts = amounts,
            cardLabel = cardLabel,
            button = {
                TextButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = buttonText)
                }
            },
            rows = rows,
        )
    }
}

@Composable
internal fun <T> RallyCard(
    items: List<T>? = null,
    colors: ((T) -> Color)? = null,
    amounts: ((T) -> Float)? = null,
    cardLabel: String,
    currentAmount: Float? = null,
    showAll: Boolean = true,
    button: @Composable () -> Unit = {},
    alt: @Composable () -> Unit = { Text(text = "Ingen data att visa") },
    rows: @Composable (T) -> Unit = {},
) {
    Card(shape = MaterialTheme.shapes.small) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 2.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = cardLabel, style = MaterialTheme.typography.h6)
                    button()
                }
                if (currentAmount != null) {
                    Text(
                        text = formatAmount(currentAmount) + " kr",
                        style = MaterialTheme.typography.h3
                    )
                }
            }

            ProportionsDivider(data = items, values = amounts, colors = colors)
            if (items.isNullOrEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 250.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    alt()
                }
            } else {
                if (showAll) {
                    LazyColumn {
                        items(items) { item ->
                            rows(item)
                        }
                        item { Spacer(modifier = Modifier.height(12.dp)) }
                    }
                } else {
                    Column {
                        items.take(3).forEach { rows(it) }
                    }
                }
            }
        }
    }
}


@Composable
internal fun <T> ProportionsDivider(
    data: List<T>?,
    values: ((T) -> Float)?,
    colors: ((T) -> Color)?
) {
    if (data.isNullOrEmpty() || colors == null || values == null) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colors.background)
        )
    } else {
        val proportions = data.extractProportions { values(it).absoluteValue }
        val notUnColors = data.map { colors(it) }
        val unColors = notUnColors.distinct()

        // FIXME: This can be done in some better way maybe in a use case and then store in uistate
        // FIXME: This makes the Spacer below crash if the proportion is 0f
        val pair = notUnColors zip proportions
        val group = pair.groupBy { it.first }
        val hell = group.mapKeys { it1 ->
            it1.value.sumOf {
                it.second.toDouble()
            }
        }
        val floatProportions = hell.keys.toList().map { it.toFloat() }

        if (proportions.all { it.absoluteValue == 0f }) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colors.background)
            )
        } else {
            Row(Modifier.fillMaxWidth()) {
                proportions.forEachIndexed { index, proportion ->
                    if (proportion.absoluteValue > 0f) {
                        // Spacer requires weight greater than 0
                        Spacer(
                            modifier = Modifier
                                .weight(proportion.absoluteValue)
                                .height(1.dp)
                                .background(notUnColors[index])
                        )
                    }
                }
            }
        }
    }
}


@Composable
internal fun BaseRow(
    color: Color,
    title: String,
    subtitle: String,
    amount: Float,
    onClick: () -> Unit
) {
    val formattedAmount = remember(amount) { formatAmount(amount) }
    Row(
        modifier = Modifier
            .height(75.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(12.dp))
        RallyIndicator(color = color)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.subtitle1)
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = subtitle, style = MaterialTheme.typography.body2)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        
        Text(text = "$formattedAmount kr", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.width(12.dp))
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
        Spacer(modifier = Modifier.width(12.dp))
        Spacer(modifier = Modifier.width(12.dp))
    }
    RallyDivider(Modifier.padding(horizontal = 12.dp))
}

@Composable
private fun RallyIndicator(color: Color, modifier: Modifier = Modifier) {
    Spacer(modifier = modifier
        .size(4.dp, 36.dp)
        .background(color))
}

@Composable
fun RallyDivider(modifier: Modifier = Modifier) {
    Divider(color = MaterialTheme.colors.background, thickness = 1.dp, modifier = modifier)
}

internal fun formatAmount(amount: Float): String {
    return AmountDecimalFormat.format(amount)
}
private val AmountDecimalFormat = DecimalFormat("#,###")

internal fun <E> List<E>.extractProportions(selector: (E) -> Float): List<Float> {
    val total = this.sumOf { selector(it).toDouble() }
    return this.map { (selector(it)/total).toFloat() }
}

internal inline fun <T> Iterable<T>.sumOfFloat(selector: (T) -> Float): Float {
    var sum: Double = 0.toDouble()
    for (element in this) {
        sum += selector(element)
    }
    return sum.toFloat()
}