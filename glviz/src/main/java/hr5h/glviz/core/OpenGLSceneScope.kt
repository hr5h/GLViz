package hr5h.glviz.core

import android.opengl.Matrix
import hr5h.glviz.shapes.ShapeType

@DslMarker
annotation class SceneDsl

@SceneDsl
class ShapeTransformScope internal constructor() {
    internal val transforms = mutableListOf<Transform>()

    fun translate(dx: Float, dy: Float, dz: Float) {
        transforms.add(Transform.Translate(dx, dy, dz))
    }

    fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        transforms.add(Transform.Rotate(angle, x, y, z))
    }

    fun scale(sx: Float, sy: Float, sz: Float) {
        transforms.add(Transform.Scale(sx, sy, sz))
    }

    fun applyAll(source: List<Transform>) {
        transforms.addAll(source)
    }
}

class ShapeDescription internal constructor(
    val type: ShapeType,
    val displayName: String,
    val transforms: List<Transform> = emptyList(),
    val customVertices: FloatArray? = null,
    val customColor: FloatArray? = null,
    val customVertexColors: FloatArray? = null,
    val customTexCoords: FloatArray? = null,
    val customTexturePath: String? = null,
) {
    fun buildModelMatrix(): FloatArray {
        val matrix = FloatArray(16)
        Matrix.setIdentityM(matrix, 0)
        for (t in transforms) {
            when (t) {
                is Transform.Translate -> Matrix.translateM(matrix, 0, t.dx, t.dy, t.dz)
                is Transform.Rotate -> Matrix.rotateM(matrix, 0, t.angle, t.x, t.y, t.z)
                is Transform.Scale -> Matrix.scaleM(matrix, 0, t.sx, t.sy, t.sz)
            }
        }
        return matrix
    }
}

@SceneDsl
class OpenGLSceneScope internal constructor() {
    internal val shapes = mutableListOf<ShapeDescription>()

    fun Triangle(name: String = "Треугольник", block: ShapeTransformScope.() -> Unit = {}) {
        addShape(ShapeType.TRIANGLE, name, block)
    }

    fun Square(name: String = "Квадрат", block: ShapeTransformScope.() -> Unit = {}) {
        addShape(ShapeType.SQUARE, name, block)
    }

    fun Cube(name: String = "Куб", block: ShapeTransformScope.() -> Unit = {}) {
        addShape(ShapeType.CUBE, name, block)
    }

    fun Pyramid(name: String = "Пирамида", block: ShapeTransformScope.() -> Unit = {}) {
        addShape(ShapeType.PYRAMID, name, block)
    }

    fun Model(
        vertices: FloatArray,
        name: String = "Модель",
        color: FloatArray = floatArrayOf(0.7f, 0.5f, 0.3f, 1f),
        vertexColors: FloatArray? = null,
        texCoords: FloatArray? = null,
        texturePath: String? = null,
        block: ShapeTransformScope.() -> Unit = {}
    ) {
        val scope = ShapeTransformScope().apply(block)
        shapes.add(
            ShapeDescription(
                type = ShapeType.MODEL,
                displayName = name,
                transforms = scope.transforms.toList(),
                customVertices = vertices,
                customColor = color,
                customVertexColors = vertexColors,
                customTexCoords = texCoords,
                customTexturePath = texturePath,
            )
        )
    }

    private fun addShape(type: ShapeType, name: String, block: ShapeTransformScope.() -> Unit) {
        val scope = ShapeTransformScope().apply(block)
        shapes.add(
            ShapeDescription(
                type = type,
                displayName = name,
                transforms = scope.transforms.toList(),
            )
        )
    }
}
