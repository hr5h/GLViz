package hr5h.glviz.core

sealed class Transform {
    data class Translate(val dx: Float, val dy: Float, val dz: Float) : Transform()
    data class Rotate(val angle: Float, val x: Float, val y: Float, val z: Float) : Transform()
    data class Scale(val sx: Float, val sy: Float, val sz: Float) : Transform()
}
