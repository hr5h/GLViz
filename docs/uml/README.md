# UML Diagrams

Актуальные диаграммы архитектуры GLViz.

- `01-dsl-layer.md` — DSL-слой: `OpenGLSceneScope`, `ShapeTransformScope`, сборка `SceneShape`, `TransformState`
- `02-compose-to-gl-sync.md` — sequence: Compose state → DSL → `GLRenderer` → GL thread
- `03-scene-domain-layer.md` — доменная модель сцены: `OpenGLSceneScope` → `SceneShape` → `ShapeData` → `Scene`
- `04-opengl-render-layer.md` — рендер-слой: `GLRenderer`, шейдеры, текстуры, `replaceShapes`
- `05-overall-architecture.md` — общая class-level архитектура app + glviz
- `06-package-components.md` — package/component-level схема модулей
