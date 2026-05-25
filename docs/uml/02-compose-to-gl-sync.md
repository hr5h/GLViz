# UML: Compose to GL Sync

```mermaid
sequenceDiagram
participant UI as Compose state/UI
participant OGS as OpenGLScene(@Composable)
participant Scope as OpenGLSceneScope
participant Renderer as GLRenderer
participant GL as GL thread (onDrawFrame)

UI->>OGS: recomposition(content lambda)
OGS->>OGS: remember { GLRenderer() }
OGS->>OGS: rememberUpdatedState(content)
OGS->>Scope: OpenGLSceneScope().apply(content)
Scope-->>OGS: shapes: List<ShapeDescription>
OGS->>Renderer: syncScene(shapes)

GL->>Renderer: onDrawFrame()
Renderer->>Renderer: rebuildScene(pendingSync) if exists
Renderer->>Renderer: draw all scene.shapes
```
