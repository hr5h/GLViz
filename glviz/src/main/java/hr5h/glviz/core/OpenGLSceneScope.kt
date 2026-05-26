package hr5h.glviz.core

import android.content.res.AssetManager
import hr5h.glviz.models.ObjLoader
import hr5h.glviz.shapes.ShapeType

@DslMarker
annotation class SceneDsl

@SceneDsl
class ShapeTransformScope internal constructor() {
    internal var transformState: TransformState = TransformState()
        private set

    fun translate(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
        transformState = transformState.translate(x, y, z)
    }

    fun rotate(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
        transformState = transformState.rotate(x, y, z)
    }

    fun scale(x: Float = 1f, y: Float = 1f, z: Float = 1f) {
        transformState = transformState.scale(x, y, z)
    }

    fun applyTransformState(state: TransformState) {
        transformState = state
    }
}

class ShapeDescription internal constructor(
    val type: ShapeType,
    val transformState: TransformState = TransformState(),
    val customVertices: FloatArray? = null,
    val customColor: FloatArray? = null,
    val customVertexColors: FloatArray? = null,
    val customTexCoords: FloatArray? = null,
    val customTexturePath: String? = null,
) {
    fun buildModelMatrix(): FloatArray = transformState.buildModelMatrix()
}

@SceneDsl
class OpenGLSceneScope internal constructor(
    private val assetManager: AssetManager,
) {
    internal val shapes = mutableListOf<ShapeDescription>()

    fun Triangle(block: ShapeTransformScope.() -> Unit = {}) {
        addShape(ShapeType.TRIANGLE, block)
    }

    fun Square(block: ShapeTransformScope.() -> Unit = {}) {
        addShape(ShapeType.SQUARE, block)
    }

    fun Cube(block: ShapeTransformScope.() -> Unit = {}) {
        addShape(ShapeType.CUBE, block)
    }

    fun Pyramid(block: ShapeTransformScope.() -> Unit = {}) {
        addShape(ShapeType.PYRAMID, block)
    }

    fun Model(
        modelPath: String,
        texturePath: String? = null,
        color: FloatArray = floatArrayOf(0.7f, 0.5f, 0.3f, 1f),
        block: ShapeTransformScope.() -> Unit = {},
    ) {
        val mesh = ObjLoader.load(assetManager, modelPath) ?: return
        if (mesh.vertices.size < 9) return

        val scope = ShapeTransformScope().apply(block)
        shapes.add(
            ShapeDescription(
                type = ShapeType.MODEL,
                transformState = scope.transformState,
                customVertices = mesh.vertices,
                customColor = color,
                customVertexColors = mesh.vertexColors,
                customTexCoords = mesh.texCoords,
                customTexturePath = texturePath,
            )
        )
    }

    private fun addShape(type: ShapeType, block: ShapeTransformScope.() -> Unit) {
        val scope = ShapeTransformScope().apply(block)
        shapes.add(
            ShapeDescription(
                type = type,
                transformState = scope.transformState,
            )
        )
    }
}
