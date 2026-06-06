package hr5h.glviz.shapes

object SquareShape : ShapeDefinition {

    fun newInstance(): Pair<FloatArray, FloatArray> {
        val vertices = floatArrayOf(
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f
        )
        val color = floatArrayOf(0.2f, 0.6f, 0.9f, 1.0f)
        return Pair(vertices, color)
    }

    override fun createShapeDataList(color: FloatArray?): List<ShapeData> {
        val (vertices, defaultColor) = newInstance()
        return listOf(ShapeData.createShapeData(vertices, color ?: defaultColor))
    }
}