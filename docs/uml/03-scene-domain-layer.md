# UML: Scene and Domain Geometry Layer

```mermaid
classDiagram
direction LR

class OpenGLSceneScope {
  -shapes: MutableList~SceneShape~
  -addShape(type, block)
  +Model(modelPath, ...)
}

class Scene {
  -_shapes: MutableList~SceneShape~
  +shapes: List~SceneShape~
  +addShape(shapeData, modelMatrix?)
  +clearShapes()
  +replaceShapes(shapes)
}

class SceneShape {
  +shapeData: ShapeData
  +modelMatrix: FloatArray
}

class TransformState {
  +translation: Vector3
  +rotation: Vector3
  +scale: Vector3
  +buildModelMatrix(): FloatArray
}

class Vector3 {
  +x: Float
  +y: Float
  +z: Float
}

class ShapeData {
  +vertexBuffer: FloatBuffer
  +color: FloatArray
  +colorBuffer: FloatBuffer?
  +texCoordBuffer: FloatBuffer?
  +textureAssetPath: String?
  +vertexCount: Int
  +createShapeData(...): ShapeData
}

class ShapeType {
  <<enumeration>>
  TRIANGLE
  SQUARE
  CUBE
  PYRAMID
  MODEL
}

OpenGLSceneScope "1" o-- "*" SceneShape : builds in SideEffect
Scene "1" o-- "*" SceneShape : runtime copy on GL thread
SceneShape --> ShapeData
OpenGLSceneScope ..> TransformState : via ShapeTransformScope
OpenGLSceneScope ..> ShapeType : primitive lookup
TransformState --> Vector3

note for OpenGLSceneScope "Resolves geometry and transforms\ninto SceneShape before sync."
note for SceneShape "Render entity: geometry + model matrix."
note for Scene "replaceShapes() atomically updates\nthe drawable list from pendingSync."
```
