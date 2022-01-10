package com.example.rally.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rally.R
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun LoadingErrorContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    error: Boolean,
    errorContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    when {
        empty -> emptyContent()
        error -> SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = loading),
            onRefresh = onRefresh
        ) {
            errorContent()
        }
        else -> SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = loading),
            onRefresh = onRefresh
        ) {
            content()
        }
    }
}

@Composable
internal fun FullScreenLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}

@Composable
internal fun FullScreenError(
    @DrawableRes drawableRes: Int = R.drawable.error,
    heading: String = "Oops! Ett fel har inträffat",
    body: String = "Ett oväntat fel har inträffat. Klicka på knappen för att försöka igen",
    buttonText: String = "Försök igen",
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = null,
            modifier = Modifier.padding(12.dp)
        )
        Text(text = heading, style = MaterialTheme.typography.h5, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = body, style = MaterialTheme.typography.body1, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(36.dp))
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = buttonText)
        }
    }
}