# UML Diagrams

Актуальные диаграммы архитектуры GLViz.

- `01-dsl-layer.md` — DSL-слой: `OpenGLSceneScope`, `ShapeTransformScope`, `ShapeDescription`, `TransformState`
- `02-compose-to-gl-sync.md` — sequence: Compose state → DSL → `GLRenderer` → GL thread
- `03-scene-domain-layer.md` — доменная модель сцены: `ShapeDescription` → `SceneShape` → `ShapeData`
- `04-opengl-render-layer.md` — рендер-слой: `GLRenderer`, шейдеры, текстуры, `ShapeDefinition`
- `05-overall-architecture.md` — общая class-level архитектура app + glviz
- `06-package-components.md` — package/component-level схема модулей
