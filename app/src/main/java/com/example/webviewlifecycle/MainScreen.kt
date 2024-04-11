package com.example.webviewlifecycle

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

private const val TAG = "MainScreen"

@Composable
fun WebviewTopBar(
    onAddressChange: (String) -> Unit,
    onLoadUrl: () -> Unit,
    url: String,
    favicon: Bitmap?,
) {
    Row(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .wrapContentSize()
            .background(Color.White)
    ) {
        Image(
            painter = if (favicon == null) {
                painterResource(id = R.drawable.kakao_logo)
            } else {
                BitmapPainter(favicon.asImageBitmap())
            },
            modifier = Modifier
                .padding(5.dp)
                .size(50.dp),
            contentDescription = "",
        )
        WebviewAddressBar(onAddressChange = onAddressChange, onLoadUrl = onLoadUrl, url = url)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WebviewAddressBar(onAddressChange: (String) -> Unit, onLoadUrl: () -> Unit, url: String) {
    var focusState by remember { mutableStateOf(false) }
    // TODO: 바꿔보기 - remember를 왜 쓰는지 생각해보기
    var tfValue by remember { mutableStateOf(TextFieldValue(url)) }

    tfValue = tfValue.copy(text = url)

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    TextField(
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent
        ),
        value = tfValue,
        onValueChange = {
            onAddressChange(it.text)
            tfValue = it
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onLoadUrl()
                keyboardController?.hide()
                focusManager.clearFocus()
                focusState = false
            }
        ),
        singleLine = true,
        trailingIcon = {
            AnimatedVisibility(visible = focusState) {
                Icon(
                    imageVector = Icons.Default.Clear, "",
                    modifier = Modifier
                        .clickable {
                            onAddressChange("")
                        }
                )
            }
        },
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            // works like onClick
                            tfValue = tfValue.copy(selection = TextRange(0, tfValue.text.length))
                        }
                    }
                }
            },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                focusState = it.isFocused
                if (it.isFocused) {
                    Log.e(TAG, "WebviewAddressBar focused: $tfValue")
                }
            }
    )
}

@Composable
fun LinearDeterminateIndicator(progress: Int) {
    if (progress != 100) {
        LinearProgressIndicator(
            progress = progress.toFloat() / 100,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
    }
}

@Composable
fun WebviewBottomBar(
    onHistoryBack: () -> Unit,
    onHistoryForward: () -> Unit,
    onRefreshPressed: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(color = Color.White)
    ) {
        val modifier = Modifier.weight(1f)
        BottomBarButton(
            onPressed = { onHistoryBack() },
            modifier = modifier,
            icon = Icons.Default.KeyboardArrowLeft
        )
        BottomBarButton(
            onPressed = { onHistoryForward() },
            modifier = modifier,
            icon = Icons.Default.KeyboardArrowRight
        )
        BottomBarButton(
            onPressed = { onRefreshPressed() },
            modifier = modifier,
            icon = Icons.Default.Refresh
        )
    }
}

@Composable
fun BottomBarButton(onPressed: () -> Unit, modifier: Modifier, icon: ImageVector) {
    IconButton(
        onClick = { onPressed() },
        modifier = modifier
    ) {
        Icon(icon, contentDescription = "")
    }
}
