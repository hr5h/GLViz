package hr5h.demo_app

import hr5h.glviz.core.TransformState
import hr5h.glviz.shapes.ShapeType

sealed class SceneObjectState {
    abstract val type: ShapeType
    abstract val transformState: TransformState

    protected abstract fun withTransformState(state: TransformState): SceneObjectState

    fun translate(x: Float = 0f, y: Float = 0f, z: Float = 0f): SceneObjectState =
        withTransformState(transformState.translate(x, y, z))

    fun rotate(x: Float = 0f, y: Float = 0f, z: Float = 0f): SceneObjectState =
        withTransformState(transformState.rotate(x, y, z))

    fun scale(x: Float = 1f, y: Float = 1f, z: Float = 1f): SceneObjectState =
        withTransformState(transformState.scale(x, y, z))

    data class Shape(
        override val type: ShapeType,
        override val transformState: TransformState = TransformState(),
    ) : SceneObjectState() {

        override fun withTransformState(state: TransformState): SceneObjectState =
            copy(transformState = state)
    }

    data class Model(
        val modelPath: String,
        val texturePath: String? = null,
        override val transformState: TransformState = TransformState(),
    ) : SceneObjectState() {
        override val type: ShapeType = ShapeType.MODEL

        override fun withTransformState(state: TransformState): SceneObjectState =
            copy(transformState = state)
    }
}
