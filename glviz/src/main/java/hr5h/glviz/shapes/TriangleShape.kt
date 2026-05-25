package hr5h.glviz.shapes

object TriangleShape : ShapeDefinition {

    fun newInstance(): Pair<FloatArray, FloatArray> {
        val vertices = floatArrayOf(
            0.0f, 0.6f, 0.0f,
            -0.5f, -0.3f, 0.0f,
            0.5f, -0.3f, 0.0f
        )
        val color = floatArrayOf(0.6f, 0.8f, 0.2f, 1.0f)
        return Pair(vertices, color)
    }

    override fun createShapeDataList(): List<ShapeData> {
        val (vertices, color) = newInstance()
        return listOf(ShapeData.createShapeData(vertices, color))
    }
}