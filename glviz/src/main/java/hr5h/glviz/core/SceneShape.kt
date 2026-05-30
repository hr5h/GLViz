package hr5h.glviz.core

import android.opengl.Matrix
import hr5h.glviz.shapes.ShapeData

/**
 * Сущность сцены: геометрия (ShapeData) + матрица модели (трансформация).
 * Не зависит от OpenGL API — только от Android Matrix.
 */
internal class SceneShape(
    val shapeData: ShapeData,
    val modelMatrix: FloatArray = FloatArray(16).apply { Matrix.setIdentityM(this, 0) }
)
