package hr5h.glviz.shapes

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Чистое описание геометрии фигуры для рендера: вершины, цвет и опциональная текстура.
 * Трансформации (model matrix) хранятся в SceneShape.
 */
data class ShapeData(
    val vertexBuffer: FloatBuffer,
    val color: FloatArray,
    val colorBuffer: FloatBuffer? = null,
    val texCoordBuffer: FloatBuffer? = null,
    val textureAssetPath: String? = null,
    val vertexCount: Int
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShapeData

        if (vertexCount != other.vertexCount) return false
        if (vertexBuffer != other.vertexBuffer) return false
        if (!color.contentEquals(other.color)) return false
        if (colorBuffer != other.colorBuffer) return false
        if (texCoordBuffer != other.texCoordBuffer) return false
        if (textureAssetPath != other.textureAssetPath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vertexCount
        result = 31 * result + vertexBuffer.hashCode()
        result = 31 * result + color.contentHashCode()
        result = 31 * result + (colorBuffer?.hashCode() ?: 0)
        result = 31 * result + (texCoordBuffer?.hashCode() ?: 0)
        result = 31 * result + (textureAssetPath?.hashCode() ?: 0)
        return result
    }

    companion object {
        fun createShapeData(
            vertices: FloatArray,
            color: FloatArray,
            vertexColors: FloatArray? = null,
            texCoords: FloatArray? = null,
            textureAssetPath: String? = null
        ): ShapeData {
            val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .apply {
                    put(vertices)
                    position(0)
                }
            val expectedColorSize = (vertices.size / 3) * 4
            val colorBuffer = vertexColors
                ?.takeIf { it.size == expectedColorSize }
                ?.let { colors ->
                    ByteBuffer.allocateDirect(colors.size * 4)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer()
                        .apply {
                            put(colors)
                            position(0)
                        }
                }
            val expectedTexCoordSize = (vertices.size / 3) * 2
            val texCoordBuffer = texCoords
                ?.takeIf { it.size == expectedTexCoordSize }
                ?.let { uvs ->
                    ByteBuffer.allocateDirect(uvs.size * 4)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer()
                        .apply {
                            put(uvs)
                            position(0)
                        }
                }
            return ShapeData(
                vertexBuffer = vertexBuffer,
                color = color,
                colorBuffer = colorBuffer,
                texCoordBuffer = texCoordBuffer,
                textureAssetPath = textureAssetPath,
                vertexCount = vertices.size / 3
            )
        }
    }
}
