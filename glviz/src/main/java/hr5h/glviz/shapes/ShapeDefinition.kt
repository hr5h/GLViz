package hr5h.glviz.shapes

/**
 * Единый контракт для всех фигур: создание списка сегментов (ShapeData) для рендера.
 * Рендерер и сцена работают через этот интерфейс, не зная конкретные классы фигур.
 */
sealed interface ShapeDefinition {

    /** Возвращает список сегментов геометрии (вершины + цвет) для отрисовки. */
    fun createShapeDataList(color: FloatArray? = null): List<ShapeData>
}
