package hr5h.glviz.core

import android.opengl.GLSurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Декларативная версия OpenGLScene с DSL-scope для описания фигур и трансформаций.
 *
 * ```
 * OpenGLScene(modifier = Modifier.fillMaxSize()) {
 *     Triangle {
 *         translate(1f, 0f, 0f)
 *         rotate(45f, 0f, 1f, 0f)
 *     }
 *     Cube {
 *         scale(0.5f, 0.5f, 0.5f)
 *     }
 * }
 * ```
 */
@Composable
fun OpenGLScene(
    modifier: Modifier = Modifier,
    content: OpenGLSceneScope.() -> Unit
) {
    val context = LocalContext.current
    val renderer = remember(context) { GLRenderer(context.assets) }
    val currentContent by rememberUpdatedState(content)

    SideEffect {
        val scope = OpenGLSceneScope().apply(currentContent)
        renderer.syncScene(scope.shapes)
    }

    AndroidView(
        factory = { context ->
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(2)
                setRenderer(renderer)
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            }
        },
        modifier = modifier
    )
}