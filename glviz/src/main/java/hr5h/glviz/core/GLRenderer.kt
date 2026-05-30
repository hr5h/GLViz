package hr5h.glviz.core

import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Рендерер низкого уровня: получает готовые сущности сцены и рисует их через OpenGL ES.
 */
internal class GLRenderer(
    private val assetManager: AssetManager
) : GLSurfaceView.Renderer {

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        attribute vec4 aColor;
        attribute vec2 aTexCoord;
        varying vec4 fColor;
        varying vec2 fTexCoord;
        void main() {
            fColor = aColor;
            fTexCoord = aTexCoord;
            gl_Position = uMVPMatrix * vPosition;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform sampler2D uTexture;
        uniform bool uUseTexture;
        varying vec4 fColor;
        varying vec2 fTexCoord;
        void main() {
            vec4 baseColor = fColor;
            if (uUseTexture) {
                baseColor *= texture2D(uTexture, fTexCoord);
            }
            gl_FragColor = baseColor;
        }
    """.trimIndent()

    private val scene = Scene()
    private val textureCache = mutableMapOf<String, Int>()

    @Volatile
    private var pendingSync: List<SceneShape>? = null

    fun syncScene(shapes: List<SceneShape>) {
        pendingSync = ArrayList(shapes)
    }

    private var program: Int = 0
    private var mvpMatrixHandle: Int = 0
    private var textureHandle: Int = 0
    private var useTextureHandle: Int = 0
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        textureHandle = GLES20.glGetUniformLocation(program, "uTexture")
        useTextureHandle = GLES20.glGetUniformLocation(program, "uUseTexture")
        Matrix.setLookAtM(viewMatrix, 0,
            0f, 0f, 3f,
            0f, 0f, 0f,
            0f, 1f, 0f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 10f)
    }

    override fun onDrawFrame(gl: GL10?) {
        pendingSync?.let { shapes ->
            pendingSync = null
            scene.replaceShapes(shapes)
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(program)

        val shapesToDraw = scene.shapes.toList()
        for (entity in shapesToDraw) {
            val shapeData = entity.shapeData
            val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
            val colorHandle = GLES20.glGetAttribLocation(program, "aColor")
            val texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")

            GLES20.glEnableVertexAttribArray(positionHandle)
            GLES20.glVertexAttribPointer(
                positionHandle,
                3,
                GLES20.GL_FLOAT,
                false,
                0,
                shapeData.vertexBuffer
            )

            if (shapeData.colorBuffer != null) {
                GLES20.glEnableVertexAttribArray(colorHandle)
                GLES20.glVertexAttribPointer(
                    colorHandle,
                    4,
                    GLES20.GL_FLOAT,
                    false,
                    0,
                    shapeData.colorBuffer
                )
            } else {
                GLES20.glDisableVertexAttribArray(colorHandle)
                GLES20.glVertexAttrib4fv(colorHandle, shapeData.color, 0)
            }

            val useTexture = shapeData.texCoordBuffer != null && shapeData.textureAssetPath != null
            if (useTexture) {
                GLES20.glEnableVertexAttribArray(texCoordHandle)
                GLES20.glVertexAttribPointer(
                    texCoordHandle,
                    2,
                    GLES20.GL_FLOAT,
                    false,
                    0,
                    shapeData.texCoordBuffer
                )
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, getTextureId(shapeData.textureAssetPath))
                GLES20.glUniform1i(textureHandle, 0)
                GLES20.glUniform1i(useTextureHandle, 1)
            } else {
                GLES20.glDisableVertexAttribArray(texCoordHandle)
                GLES20.glVertexAttrib2f(texCoordHandle, 0f, 0f)
                GLES20.glUniform1i(useTextureHandle, 0)
            }

            val mvpMatrix = FloatArray(16)
            val mvMatrix = FloatArray(16)
            Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, entity.modelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0)
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, shapeData.vertexCount)

            GLES20.glDisableVertexAttribArray(positionHandle)
            if (shapeData.colorBuffer != null) {
                GLES20.glDisableVertexAttribArray(colorHandle)
            }
            if (useTexture) {
                GLES20.glDisableVertexAttribArray(texCoordHandle)
            }
        }
    }

    private fun getTextureId(assetPath: String): Int {
        return textureCache.getOrPut(assetPath) {
            loadTextureFromAssets(assetPath)
        }
    }

    private fun loadTextureFromAssets(assetPath: String): Int {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        val textureId = textureIds[0]

        assetManager.open(assetPath).use { stream ->
            val bitmap = BitmapFactory.decodeStream(stream)
                ?: throw IllegalArgumentException("Failed to decode texture: $assetPath")
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle()
        }

        return textureId
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}
