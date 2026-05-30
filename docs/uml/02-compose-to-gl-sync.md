# UML: Compose to GL Sync

```mermaid
sequenceDiagram
participant UI as MainScreen / SceneObjectState
participant OGS as OpenGLScene(@Composable)
participant Scope as OpenGLSceneScope
participant Renderer as GLRenderer
participant Scene as Scene
participant GL as GL thread (onDrawFrame)

UI->>OGS: recomposition(content lambda)
OGS->>OGS: remember(context) { GLRenderer(assets) }
OGS->>OGS: rememberUpdatedState(content)
OGS->>OGS: AndroidView(factory = GLSurfaceView + renderer)

Note over OGS,Scope: SideEffect runs after successful composition
OGS->>Scope: OpenGLSceneScope().apply(content)
UI->>Scope: Triangle/Cube/Model + applyTransformState(...)
alt primitive shape
  Scope->>Scope: shapeDefinitions[type].createShapeDataList()
  Scope->>Scope: TransformState.buildModelMatrix()
  Scope->>Scope: SceneShape(shapeData, matrix)
else custom model
  Scope->>Scope: ObjLoader.load(modelPath)
  Scope->>Scope: ShapeData.createShapeData(vertices, ...)
  Scope->>Scope: SceneShape(shapeData, matrix)
end
Scope-->>OGS: shapes: List~SceneShape~
OGS->>Renderer: syncScene(shapes)
Renderer->>Renderer: pendingSync = shapes

GL->>Renderer: onDrawFrame()
Renderer->>Scene: replaceShapes(pendingSync)
Renderer->>Scene: draw each SceneShape with MVP + textures
```
