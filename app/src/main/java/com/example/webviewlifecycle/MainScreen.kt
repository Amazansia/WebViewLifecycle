package com.example.webviewlifecycle

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.webviewlifecycle.R.drawable.kakao_logo

private const val TAG = "MainScreen"

@Composable
fun WebviewTopBar(onAddressChange: (String) -> Unit, onLoadUrl: () -> Unit, url: String) {
    Row(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .wrapContentSize()
            .background(Color.Transparent)
    ) {
        Icon(
            modifier = Modifier
                .padding(5.dp)
                .size(50.dp),
            painter = painterResource(kakao_logo),
            contentDescription = "",
            tint = Color(R.color.kakao_brand)
        )
        WebviewAddressBar(onAddressChange = onAddressChange, onLoadUrl = onLoadUrl, url = url)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WebviewAddressBar(onAddressChange: (String) -> Unit, onLoadUrl: () -> Unit, url: String) {
    var focusState by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                focusState = it.isFocused
            },
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent
        ),
        value = url,
        onValueChange = {
            onAddressChange(it)
            Log.d(TAG, "WebviewAddressBar: asdasdads $it")
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
                        })
            }
        }
    )
}

@Composable
fun LinearDeterminateIndicator(viewModel: MainActivityViewModel) {
    val progress = viewModel.progress.observeAsState().value ?: 0
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
fun WebviewBottomBar(viewModel: MainActivityViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(color = Color.White)
    ) {
        val modifier = Modifier.weight(1f)
        //TODO: viewModel 직접 참조하지 말고 호이스팅으로 변경
        BottomBarButton(
            onPressed = { viewModel.uiAction.invoke(WebViewUiAction.HistoryBack) },
            modifier = modifier,
            icon = Icons.Default.KeyboardArrowLeft
        )
        BottomBarButton(
            onPressed = { viewModel.uiAction.invoke(WebViewUiAction.HistoryForward) },
            modifier = modifier,
            icon = Icons.Default.KeyboardArrowRight
        )
        BottomBarButton(
            onPressed = { viewModel.uiAction.invoke(WebViewUiAction.RefreshPressed) },
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

@Preview(showBackground = true)
@Composable
fun PreviewTopBar() {
    var addressBarUrl by remember {
        mutableStateOf("init value")
    }
    TextField(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent
        ),
        value = addressBarUrl,
        onValueChange = { changedValue ->
            addressBarUrl = changedValue
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                Log.e(TAG, "WebviewAddressBar: onSearch: $addressBarUrl")
            }
        ),
        singleLine = true,
    )
}
