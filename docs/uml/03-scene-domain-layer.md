# UML: Scene and Domain Geometry Layer

```mermaid
classDiagram
direction LR

class Scene {
  -_shapes: MutableList~SceneShape~
  +shapes: List~SceneShape~
  +addShape(shapeData, modelMatrix)
  +clearShapes()
}

class SceneShape {
  +shapeData: ShapeData
  +modelMatrix: FloatArray
}

class ShapeDescription {
  +type: ShapeType
  +displayName: String
  +transforms: List~Transform~
  +customVertices: FloatArray?
  +customColor: FloatArray?
  +buildModelMatrix(): FloatArray
}

class Transform {
  <<sealed>>
}
class ShapeData {
  +vertexBuffer: FloatBuffer
  +color: FloatArray
  +vertexCount: Int
  +createShapeData(vertices, color) ShapeData
}

Scene "1" o-- "*" SceneShape
SceneShape --> ShapeData
ShapeDescription --> Transform
ShapeDescription --> ShapeType
```
