package hr5h.glviz.core

import hr5h.glviz.shapes.ShapeData

/**
 * Сцена — коллекция сущностей (SceneShape) для отрисовки.
 * Управление списком фигур отделено от рендерера.
 */
internal class Scene {

    private val _shapes = mutableListOf<SceneShape>()
    val shapes: List<SceneShape> get() = _shapes

    fun addShape(shapeData: ShapeData, modelMatrix: FloatArray? = null) {
        if (modelMatrix != null) {
            _shapes.add(SceneShape(shapeData, modelMatrix = modelMatrix))
        } else {
            _shapes.add(SceneShape(shapeData))
        }
    }

    fun clearShapes() {
        _shapes.clear()
    }

    fun replaceShapes(shapes: List<SceneShape>) {
        _shapes.clear()
        _shapes.addAll(shapes)
    }
}
