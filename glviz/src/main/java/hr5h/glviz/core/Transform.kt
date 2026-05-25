package hr5h.glviz.core

import android.opengl.Matrix

data class Vector3(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
) {
    operator fun plus(other: Vector3): Vector3 =
        Vector3(x + other.x, y + other.y, z + other.z)

    operator fun times(other: Vector3): Vector3 =
        Vector3(x * other.x, y * other.y, z * other.z)

    companion object {
        val Zero = Vector3()
        val One = Vector3(1f, 1f, 1f)
    }
}

data class TransformState(
    val translation: Vector3 = Vector3.Zero,
    val rotation: Vector3 = Vector3.Zero,
    val scale: Vector3 = Vector3.One,
) {
    fun translate(x: Float = 0f, y: Float = 0f, z: Float = 0f): TransformState =
        copy(translation = translation + Vector3(x, y, z))

    fun rotate(x: Float = 0f, y: Float = 0f, z: Float = 0f): TransformState =
        copy(rotation = rotation + Vector3(x, y, z))

    fun scale(x: Float = 1f, y: Float = 1f, z: Float = 1f): TransformState =
        copy(scale = scale * Vector3(x, y, z))

    fun buildModelMatrix(): FloatArray {
        val matrix = FloatArray(16)
        Matrix.setIdentityM(matrix, 0)
        Matrix.translateM(matrix, 0, translation.x, translation.y, translation.z)
        Matrix.rotateM(matrix, 0, rotation.x, 1f, 0f, 0f)
        Matrix.rotateM(matrix, 0, rotation.y, 0f, 1f, 0f)
        Matrix.rotateM(matrix, 0, rotation.z, 0f, 0f, 1f)
        Matrix.scaleM(matrix, 0, scale.x, scale.y, scale.z)
        return matrix
    }
}
