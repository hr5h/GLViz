package hr5h.glviz.shapes

object PyramidShape : ShapeDefinition {

    fun newInstance(): List<Pair<FloatArray, FloatArray>> {
        val list = mutableListOf<Pair<FloatArray, FloatArray>>()

        val verticesFront = floatArrayOf(
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
        )
        val colorFront = floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f)
        list.add(Pair(verticesFront, colorFront))

        val verticesRight = floatArrayOf(
            0.0f, 0.5f, 0.0f,
            0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, -0.5f,
        )
        val colorRight = floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f)
        list.add(Pair(verticesRight, colorRight))

        val verticesBack = floatArrayOf(
            0.0f, 0.5f, 0.0f,
            0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
        )
        val colorBack = floatArrayOf(0.0f, 0.0f, 1.0f, 1.0f)
        list.add(Pair(verticesBack, colorBack))

        val verticesLeft = floatArrayOf(
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
        )
        val colorLeft = floatArrayOf(1.0f, 1.0f, 0.0f, 1.0f)
        list.add(Pair(verticesLeft, colorLeft))

        val verticesBase = floatArrayOf(
            -0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f,
        )
        val colorBase = floatArrayOf(1.0f, 0.0f, 1.0f, 1.0f)
        list.add(Pair(verticesBase, colorBase))

        return list
    }

    override fun createShapeDataList(): List<ShapeData> =
        newInstance().map { (vertices, color) -> ShapeData.Companion.createShapeData(vertices, color) }
}
