package com.svdroid.paybacktest.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.svdroid.paybacktest.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun NoInternetMessage(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(id = R.string.no_internet_message),
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun InfoTextRecord(title: String, content: String) {
    Text(text = buildAnnotatedString {
        withStyle(style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)) { append(title) }
        append(" ")
        withStyle(style = SpanStyle(fontSize = 16.sp, fontStyle = FontStyle.Italic)) { append(content) }
    })
}

@Composable
fun ZoomableImage(url: String?) {
    val scale = remember { mutableStateOf(1f) }
    val rotationState = remember { mutableStateOf(1f) }

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current).data(url).error(R.drawable.pic_error_placeholder_200)
            .crossfade(true).build(),
    )

    Box(modifier = Modifier
        .clip(RectangleShape)
        .fillMaxSize()
        .background(Color.Black)
        .pointerInput(Unit) {
            detectTransformGestures { _, _, zoom, rotation ->
                scale.value *= zoom
                rotationState.value += rotation
            }
        }) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer(
                    scaleX = maxOf(.5f, minOf(3f, scale.value)),
                    scaleY = maxOf(.5f, minOf(3f, scale.value)),
                    rotationZ = rotationState.value
                ),
            contentDescription = null,
            painter = painter,
        )
    }
}

@Composable
fun PixabayIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageVector: ImageVector,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        content = { Icon(imageVector = imageVector, contentDescription = imageVector.name, tint = tint) },
    )
}

@Composable
fun PixabayIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    painter: Painter,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        content = { Icon(painter = painter, contentDescription = "Action button", tint = tint) },
    )
}

@Composable
fun SystemStatusBarPainter(color: Color) {
    val systemUiController = rememberSystemUiController()
    DisposableEffect(systemUiController) {
        systemUiController.setSystemBarsColor(color = color)
        onDispose {}
    }
}

inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier =
    composed {
        clickable(indication = null,
            interactionSource = remember { MutableInteractionSource() }) {
            onClick()
        }
    }