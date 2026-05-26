# UML: Scene and Domain Geometry Layer

```mermaid
classDiagram
direction LR

class Scene {
  -_shapes: MutableList~SceneShape~
  +shapes: List~SceneShape~
  +addShape(shapeData, modelMatrix?)
  +clearShapes()
}

class SceneShape {
  +shapeData: ShapeData
  +modelMatrix: FloatArray
}

class ShapeDescription {
  +type: ShapeType
  +transformState: TransformState
  +customVertices: FloatArray?
  +customColor: FloatArray?
  +customVertexColors: FloatArray?
  +customTexCoords: FloatArray?
  +customTexturePath: String?
  +buildModelMatrix(): FloatArray
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

Scene "1" o-- "*" SceneShape
SceneShape --> ShapeData
ShapeDescription --> TransformState
ShapeDescription --> ShapeType
TransformState --> Vector3

note for ShapeDescription "Declarative DSL snapshot.\nConverted to SceneShape in GLRenderer.rebuildScene()."
note for SceneShape "Runtime render entity:\ngeometry + model matrix."
```
