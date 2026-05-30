# UML: Package Components

```mermaid
flowchart LR
  subgraph UI["app / ui layer (Compose)"]
    MainActivity["MainActivity"]
    MainScreen["MainScreen"]
    SceneObjectState["SceneObjectState (sealed)"]
    ChipGroup["components/ChipGroup"]
    Theme["ui/theme/*"]
  end

  subgraph Bridge["glviz / compose bridge"]
    OpenGLScene["OpenGLScene (@Composable)"]
  end

  subgraph DSL["glviz / DSL layer"]
    OpenGLSceneScope["OpenGLSceneScope"]
    ShapeTransformScope["ShapeTransformScope"]
    TransformState["TransformState + Vector3"]
    ShapeType["ShapeType (enum)"]
    SceneShape["SceneShape"]
  end

  subgraph Render["glviz / OpenGL render layer"]
    GLRenderer["GLRenderer (GLSurfaceView.Renderer)"]
    Scene["Scene"]
  end

  subgraph Geometry["glviz / geometry contracts and primitives"]
    ShapeDefinition["ShapeDefinition (sealed interface)"]
    ShapeData["ShapeData"]
    Triangle["TriangleShape"]
    Square["SquareShape"]
    Cube["CubeShape"]
    Pyramid["PyramidShape"]
  end

  subgraph Models["glviz / model loading"]
    ObjLoader["ObjLoader"]
    ObjMeshData["ObjMeshData"]
  end

  MainActivity --> MainScreen
  MainScreen --> ChipGroup
  MainScreen --> Theme
  MainScreen --> SceneObjectState
  MainScreen --> OpenGLScene
  MainScreen --> ShapeType

  SceneObjectState --> TransformState
  SceneObjectState --> ShapeType

  OpenGLScene --> OpenGLSceneScope
  OpenGLScene --> GLRenderer
  OpenGLSceneScope --> ShapeTransformScope
  OpenGLSceneScope --> SceneShape
  OpenGLSceneScope --> ShapeDefinition
  OpenGLSceneScope --> ShapeData
  OpenGLSceneScope --> ObjLoader

  GLRenderer --> Scene
  GLRenderer --> SceneShape
  Scene --> SceneShape
  SceneShape --> ShapeData

  ShapeDefinition --> ShapeData
  Triangle -.implements.-> ShapeDefinition
  Square -.implements.-> ShapeDefinition
  Cube -.implements.-> ShapeDefinition
  Pyramid -.implements.-> ShapeDefinition

  ObjLoader --> ObjMeshData
  SceneObjectState --> OpenGLSceneScope : applyTransformState via DSL
```
