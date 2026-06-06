package hr5h.glviz.core

import android.content.res.AssetManager
import hr5h.glviz.models.ObjLoader
import hr5h.glviz.shapes.CubeShape
import hr5h.glviz.shapes.PyramidShape
import hr5h.glviz.shapes.ShapeData
import hr5h.glviz.shapes.ShapeDefinition
import hr5h.glviz.shapes.ShapeType
import hr5h.glviz.shapes.SquareShape
import hr5h.glviz.shapes.TriangleShape

@DslMarker
internal annotation class SceneDsl

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

@SceneDsl
class OpenGLSceneScope internal constructor(
    private val assetManager: AssetManager,
) {
    internal val shapes = mutableListOf<SceneShape>()

    fun Triangle(
        color: FloatArray? = null,
        block: (ShapeTransformScope.() -> Unit)? = null,
    ) {
        addShape(ShapeType.TRIANGLE, color, block)
    }

    fun Square(
        color: FloatArray? = null,
        block: (ShapeTransformScope.() -> Unit)? = null,
    ) {
        addShape(ShapeType.SQUARE, color, block)
    }

    fun Cube(
        color: FloatArray? = null,
        block: (ShapeTransformScope.() -> Unit)? = null,
    ) {
        addShape(ShapeType.CUBE, color, block)
    }

    fun Pyramid(
        color: FloatArray? = null,
        block: (ShapeTransformScope.() -> Unit)? = null,
    ) {
        addShape(ShapeType.PYRAMID, color, block)
    }

    fun Model(
        modelPath: String,
        texturePath: String? = null,
        color: FloatArray = floatArrayOf(0.7f, 0.5f, 0.3f, 1f),
        block: (ShapeTransformScope.() -> Unit)? = null,
    ) {
        val mesh = ObjLoader.load(assetManager, modelPath) ?: return
        if (mesh.vertices.size < 9) return

        val scope = ShapeTransformScope().also { transformScope ->
            block?.let { transformScope.it() }
        }
        val matrix = scope.transformState.buildModelMatrix()
        val hasTexture = texturePath != null && mesh.texCoords != null
        val modelColor = when {
            hasTexture && mesh.vertexColors == null -> floatArrayOf(1f, 1f, 1f, 1f)
            else -> color
        }
        val shapeData = ShapeData.createShapeData(
            vertices = mesh.vertices,
            color = modelColor,
            vertexColors = mesh.vertexColors,
            texCoords = mesh.texCoords,
            textureAssetPath = texturePath,
        )
        shapes.add(SceneShape(shapeData, modelMatrix = matrix))
    }

    private fun addShape(
        type: ShapeType,
        color: FloatArray?,
        block: (ShapeTransformScope.() -> Unit)?,
    ) {
        val scope = ShapeTransformScope().also { transformScope ->
            block?.let { transformScope.it() }
        }
        val matrix = scope.transformState.buildModelMatrix()
        shapeDefinitions[type]?.createShapeDataList(color)?.forEach { shapeData ->
            shapes.add(SceneShape(shapeData, modelMatrix = matrix.copyOf()))
        }
    }

    private companion object {
        private val shapeDefinitions: Map<ShapeType, ShapeDefinition> = mapOf(
            ShapeType.TRIANGLE to TriangleShape,
            ShapeType.SQUARE to SquareShape,
            ShapeType.CUBE to CubeShape,
            ShapeType.PYRAMID to PyramidShape,
        )
    }
}
