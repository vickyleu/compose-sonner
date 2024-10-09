package com.dokar.sonner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import platform.CoreFoundation.CFAbsoluteTimeGetCurrent

internal actual fun currentNanoTime(): Long = (CFAbsoluteTimeGetCurrent() * 1000000000).toLong()

internal actual val CloseButtonSize: Dp = 24.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun ToasterPopup(
    alignment: Alignment,
    modifier: Modifier,
    offset: IntOffset,
    content: @Composable () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var isVisible by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            isVisible = event == Lifecycle.Event.ON_RESUME
        }
        // 监听生命周期
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    if (isVisible) {
        Popup(alignment = alignment,
            properties = PopupProperties(
                dismissOnClickOutside = false,
                usePlatformInsets = false,
                usePlatformDefaultWidth = false
            ),
            offset = offset) {
            content()
        }
    }

}