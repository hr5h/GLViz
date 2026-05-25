package hr5h.glviz.models

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Загрузчик OBJ-файлов из assets. Поддерживает вершины (v), UV (vt), грани (f)
 * и опциональные цвета вершин в формате `v x y z r g b [a]`.
 * Текстура задаётся отдельно при использовании модели, не через MTL.
 */
data class ObjMeshData(
    val vertices: FloatArray,
    val vertexColors: FloatArray? = null,
    val texCoords: FloatArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ObjMeshData

        if (!vertices.contentEquals(other.vertices)) return false
        if (!vertexColors.contentEquals(other.vertexColors)) return false
        if (!texCoords.contentEquals(other.texCoords)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vertices.contentHashCode()
        result = 31 * result + (vertexColors?.contentHashCode() ?: 0)
        result = 31 * result + (texCoords?.contentHashCode() ?: 0)
        return result
    }
}

object ObjLoader {

    fun load(context: Context, assetPath: String): ObjMeshData? {
        return try {
            context.assets.open(assetPath).use { input ->
                BufferedReader(InputStreamReader(input)).use { reader ->
                    parseObj(reader)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private data class FaceCorner(val v: Int, val vt: Int?, val vn: Int?)

    private fun parseObj(reader: BufferedReader): ObjMeshData {
        val vertices = mutableListOf<Float>()
        val vertexColors = mutableListOf<Float>()
        val texCoords = mutableListOf<Float>()
        val faceCorners = mutableListOf<FaceCorner>()
        var hasVertexColors = false

        reader.forEachLine { line ->
            val trimmed = line.substringBefore("#").trim()
            when {
                trimmed.startsWith("v ") -> {
                    val parts = trimmed.split(Regex("\\s+"))
                    if (parts.size >= 4) {
                        vertices.add(parts[1].toFloat())
                        vertices.add(parts[2].toFloat())
                        vertices.add(parts[3].toFloat())

                        if (parts.size >= 7) {
                            hasVertexColors = true
                            vertexColors.add(parts[4].toFloat())
                            vertexColors.add(parts[5].toFloat())
                            vertexColors.add(parts[6].toFloat())
                            vertexColors.add(parts.getOrNull(7)?.toFloatOrNull() ?: 1f)
                        } else {
                            vertexColors.add(1f)
                            vertexColors.add(1f)
                            vertexColors.add(1f)
                            vertexColors.add(1f)
                        }
                    }
                }
                trimmed.startsWith("vt ") -> {
                    val parts = trimmed.split(Regex("\\s+"))
                    if (parts.size >= 3) {
                        texCoords.add(parts[1].toFloat())
                        texCoords.add(parts[2].toFloat())
                    }
                }
                trimmed.startsWith("f ") -> {
                    val corners = trimmed.substring(2).trim().split(Regex("\\s+"))
                        .mapNotNull { parseFaceCorner(it) }
                    triangulateFace(corners).forEach { faceCorners.add(it) }
                }
            }
        }

        val expandedVertices = FloatArray(faceCorners.size * 3) { i ->
            val corner = faceCorners[i / 3]
            val component = i % 3
            vertices.getOrElse(corner.v * 3 + component) { 0f }
        }
        val expandedColors = if (hasVertexColors) {
            FloatArray(faceCorners.size * 4) { i ->
                val corner = faceCorners[i / 4]
                val component = i % 4
                vertexColors.getOrElse(corner.v * 4 + component) { 1f }
            }
        } else {
            null
        }
        val hasTexCoords = texCoords.isNotEmpty() && faceCorners.any { it.vt != null }
        val expandedTexCoords = if (hasTexCoords) {
            FloatArray(faceCorners.size * 2) { i ->
                val corner = faceCorners[i / 2]
                val component = i % 2
                if (corner.vt != null) {
                    val u = texCoords.getOrElse(corner.vt * 2) { 0f }
                    val v = texCoords.getOrElse(corner.vt * 2 + 1) { 0f }
                    if (component == 0) u else 1f - v
                } else {
                    0f
                }
            }
        } else {
            null
        }

        return ObjMeshData(
            vertices = normalizeVertices(expandedVertices),
            vertexColors = expandedColors,
            texCoords = expandedTexCoords
        )
    }

    private fun parseFaceCorner(part: String): FaceCorner? {
        val tokens = part.split("/")
        if (tokens.isEmpty()) return null
        val v = tokens[0].toIntOrNull()?.minus(1) ?: return null
        val vt = tokens.getOrNull(1)?.takeIf { it.isNotEmpty() }?.toIntOrNull()?.minus(1)
        val vn = tokens.getOrNull(2)?.takeIf { it.isNotEmpty() }?.toIntOrNull()?.minus(1)
        return FaceCorner(v, vt, vn)
    }

    private fun triangulateFace(corners: List<FaceCorner>): List<FaceCorner> {
        return when (corners.size) {
            3 -> corners
            4 -> listOf(
                corners[0], corners[1], corners[2],
                corners[0], corners[2], corners[3]
            )
            else -> corners.chunked(3).filter { it.size == 3 }.flatten()
        }
    }

    /** Центрирует геометрию в начало координат и масштабирует в бокс [-1, 1] по максимальной оси. */
    private fun normalizeVertices(vertices: FloatArray): FloatArray {
        if (vertices.size < 3) return vertices
        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var minZ = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var maxY = Float.MIN_VALUE
        var maxZ = Float.MIN_VALUE
        var i = 0
        while (i < vertices.size) {
            val x = vertices[i++]
            val y = vertices[i++]
            val z = vertices[i++]
            minX = minOf(minX, x)
            minY = minOf(minY, y)
            minZ = minOf(minZ, z)
            maxX = maxOf(maxX, x)
            maxY = maxOf(maxY, y)
            maxZ = maxOf(maxZ, z)
        }
        val cx = (minX + maxX) / 2f
        val cy = (minY + maxY) / 2f
        val cz = (minZ + maxZ) / 2f
        val scale = 2f / maxOf(
            (maxX - minX).coerceAtLeast(1e-6f),
            (maxY - minY).coerceAtLeast(1e-6f),
            (maxZ - minZ).coerceAtLeast(1e-6f)
        )
        return FloatArray(vertices.size) { j ->
            when (j % 3) {
                0 -> (vertices[j] - cx) * scale
                1 -> (vertices[j] - cy) * scale
                else -> (vertices[j] - cz) * scale
            }
        }
    }
}
