package com.dokar.sonner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.dokar.sonner.icons.CheckCircle
import com.dokar.sonner.icons.Info
import com.dokar.sonner.icons.Warning
import com.dokar.sonner.theme.colorsOf
import kotlin.math.max
import kotlin.time.Duration.Companion.milliseconds

/**
 * Provides default values used by the toaster and toasts.
 */
object ToasterDefaults {
    /**
     * Short toast duration, which is 2000ms.
     */
    val DurationShort = 2000.milliseconds

    /**
     * Short toast duration, which is 4000ms.
     */
    val DurationDefault = 4000.milliseconds

    /**
     * Long toast duration, which is 8000ms.
     */
    val DurationLong = 8000.milliseconds

    /**
     * The default toast shape.
     */
    val Shape = RoundedCornerShape(8.dp)

    internal val Elevation = 12.dp
    internal val ShadowAmbientColor = Color.Gray
    internal val DarkShadowAmbientColor = Color.Black
    internal val ShadowSpotColor = Color.Gray
    internal val DarkShadowSpotColor = Color.Black

    internal const val ENTER_TRANSITION_DURATION = 300
    internal const val EXIT_TRANSITION_DURATION = 255

    private val IconSize = 20.dp

    private val ActionButtonShape = RoundedCornerShape(4.dp)

    /**
     * The icon slot that displays an icon for toasts that have a different type than [ToastType.Normal].
     */
    @Composable
    fun iconSlot(toast: Toast) {
        val contentColor = LocalToastContentColor.current
        val leftSize = LocalToastLeftContentSize.current
        when (toast.type) {
            ToastType.Normal -> {
                leftSize.value = Size.Zero
            }
            ToastType.Toast -> {
                leftSize.value = Size.Zero
            }
            ToastType.Success -> {
                Box(modifier = Modifier
                    .onGloballyPositioned {
                        leftSize.value = it.size.toSize()
                    }
                    .padding(end = 16.dp)) {
                    Image(
                        imageVector = CheckCircle,
                        contentDescription = "Success",
                        modifier = Modifier.size(IconSize),
                        colorFilter = ColorFilter.tint(contentColor),
                    )
                }
            }

            ToastType.Info -> {
                Box(modifier = Modifier
                    .onGloballyPositioned {
                        leftSize.value = it.size.toSize()
                    }
                    .padding(end = 16.dp)) {
                    Image(
                        imageVector = Info,
                        contentDescription = "Info",
                        modifier = Modifier.size(IconSize),
                        colorFilter = ColorFilter.tint(contentColor),
                    )
                }
            }

            ToastType.Warning -> {
                Box(modifier = Modifier
                    .onGloballyPositioned {
                        leftSize.value = it.size.toSize()
                    }
                    .padding(end = 16.dp)) {
                    Image(
                        imageVector = Warning,
                        contentDescription = "Warning",
                        modifier = Modifier.size(IconSize),
                        colorFilter = ColorFilter.tint(contentColor),
                    )
                }
            }

            ToastType.Error -> {
                Box(modifier = Modifier
                    .onGloballyPositioned {
                        leftSize.value = it.size.toSize()
                    }
                    .padding(end = 16.dp)) {
                    Image(
                        imageVector = Info,
                        contentDescription = "Error",
                        modifier = Modifier.size(IconSize),
                        colorFilter = ColorFilter.tint(contentColor),
                    )
                }
            }
            else->{
                leftSize.value = Size.Zero
            }
        }
    }

    /**
     * The message slot that calls [toString] on the toast message and displays the result.
     */
    @Composable
    fun messageSlot(toast: Toast) {
        val contentColor = LocalToastContentColor.current
        val leftSize = LocalToastLeftContentSize.current
        with(LocalDensity.current){
            BoxWithConstraints(modifier = Modifier
                .let {
                    if(toast.type in listOf(ToastType.Normal,ToastType.Toast)) it else it .padding(end = leftSize.value.width.toDp())
                }
                .let {
                    if(toast.type == ToastType.Toast) it.wrapContentWidth() else it.fillMaxWidth()
                }
                .wrapContentHeight()
            ){
                BasicText(
                    toast.message.toString(),
                    maxLines = 15,
                    minLines = 1,
                    style = TextStyle.Default.copy(
                        textAlign = TextAlign.Center
                    ),
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .requiredWidthIn(max = constraints.maxWidth.toDp())
                        .wrapContentSize(),
                    color = { contentColor })
            }
        }

    }

    /**
     * The action slot that supports [TextToastAction].
     */
    @Composable
    fun actionSlot(toast: Toast) {
        when (val action = toast.action) {
            null -> {}

            is TextToastAction -> {
                ActionButton(
                    onClick = { action.onClick(toast) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    with(LocalDensity.current){
                        BasicText(
                            action.text,
                            maxLines = 3,
                            minLines = 1,
                            style = TextStyle.Default.copy(
                                textAlign = TextAlign.Center
                            ),
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .requiredWidthIn(max = 150.dp)
                                .wrapContentSize(),
                            color = { Color.White })
                    }
                }
            }

            else -> throw IllegalStateException(
                "Please provide a custom action slot to " +
                        "display this type: ${action::class.simpleName}"
            )
        }
    }

    @Composable
    internal fun contentColor(toast: Toast, richColors: Boolean, darkTheme: Boolean): Color {
        val type = if (richColors) toast.type else ToastType.Normal
        return colorsOf(type, darkTheme).content
    }

    @Composable
    internal fun border(toast: Toast, richColors: Boolean, darkTheme: Boolean): BorderStroke {
        val type = if (richColors) toast.type else ToastType.Normal
        val color = colorsOf(type, darkTheme).border
        return BorderStroke(
            width = 0.8.dp,
            brush = SolidColor(color),
        )
    }

    @Composable
    internal fun background(toast: Toast, richColors: Boolean, darkTheme: Boolean): Brush {
        val type = if (richColors) toast.type else ToastType.Normal
        return SolidColor(colorsOf(type, darkTheme).background)
    }

    @Composable
    private fun ActionButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        contentPadding: PaddingValues = PaddingValues(0.dp),
        content: @Composable RowScope.() -> Unit,
    ) {
        Row(
            modifier
                .defaultMinSize(
                    minWidth = 64.dp,
                )
                .background(color = Color.Black, shape = ActionButtonShape)
                .clickable { onClick() }
                .padding(contentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}
