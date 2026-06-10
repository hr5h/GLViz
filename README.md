# GLViz

`GLViz` - Android-библиотека для встраивания простой `3D`-сцены в интерфейс на базе `Jetpack Compose` с рендерингом через `OpenGL ES 2.0`.

Библиотека ориентирована на сценарии, в которых графика должна быть частью обычного экрана приложения, а не отдельным игровым контуром. Пользователь работает с composable-компонентом `OpenGLScene` и описывает содержимое сцены через DSL.

## Для чего нужна библиотека

`GLViz` подходит для задач, где требуется:

- встроить графическую сцену в экран `Compose`;
- отображать базовые `3D`-примитивы;
- подключать внешние `OBJ`-модели;
- применять к объектам перемещение, поворот и масштабирование;
- использовать текстуры для загружаемых моделей;
- работать с графикой без прямого обращения к низкоуровневому API `OpenGL ES`.

## Возможности

- composable-компонент `OpenGLScene` как точка входа в библиотеку;
- DSL-описание сцены внутри `Compose`;
- встроенные примитивы: `Triangle`, `Square`, `Cube`, `Pyramid`;
- загрузка внешних моделей в формате `OBJ`;
- поддержка текстур для моделей при наличии `UV`-координат;
- трансформации объектов: `translate`, `rotate`, `scale`;
- передача собственного цвета для встроенных примитивов;
- синхронизация состояния `Compose` со сценой рендерера.

## Требования

- Android `minSdk 24`
- Kotlin / Compose
- `OpenGL ES 2.0`

## Подключение

### Вариант 1. Локальный модуль

Если библиотека используется как модуль внутри проекта, добавьте зависимость в `build.gradle.kts` модуля приложения:

```kotlin
dependencies {
    implementation(project(":glviz"))
}
```

### Вариант 2. Подключение через JitPack

Если библиотека опубликована через GitHub/JitPack, добавьте репозиторий в `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

После этого подключите библиотеку в модуле приложения:

```kotlin
dependencies {
    implementation("com.github.hr5h:GLViz:1.0.0")
}
```

## Базовое использование

Точкой интеграции библиотеки в интерфейс является `OpenGLScene`.

```kotlin
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import hr5h.glviz.core.OpenGLScene

@Composable
fun SceneScreen() {
    OpenGLScene(
        modifier = Modifier.fillMaxSize()
    ) {
        Triangle()
    }
}
```

## Примеры сцены

### Встроенные примитивы

```kotlin
OpenGLScene {
    Triangle(color = floatArrayOf(1f, 0f, 0f, 1f))

    Cube(color = floatArrayOf(0.2f, 0.6f, 1f, 1f)) {
        translate(x = -1.0f, z = -1.5f)
        rotate(y = 25f)
        scale(x = 0.8f, y = 0.8f, z = 0.8f)
    }

    Pyramid(color = floatArrayOf(1f, 0.8f, 0.2f, 1f)) {
        translate(x = 1.0f, z = -1.2f)
        rotate(y = -20f)
    }
}
```

### Внешняя `OBJ`-модель

```kotlin
OpenGLScene {
    Model(
        modelPath = "models/cat.obj",
        texturePath = "models/cat.png"
    ) {
        translate(y = -0.45f, z = -1.2f)
        rotate(x = -8f, y = 28f)
        scale(x = 1.35f, y = 1.35f, z = 1.35f)
    }
}
```

## Трансформации

Для любого объекта сцены можно задавать преобразования:

```kotlin
OpenGLScene {
    Square {
        translate(x = 0.4f, y = 0.2f, z = -1.0f)
        rotate(z = 25f)
        scale(x = 1.2f, y = 1.2f, z = 1f)
    }
}
```

Поддерживаются:

- `translate(x, y, z)` - перемещение;
- `rotate(x, y, z)` - поворот;
- `scale(x, y, z)` - масштабирование.

## Работа с моделями и текстурами

Для загрузки внешней модели используется метод `Model(...)`.

```kotlin
Model(
    modelPath = "models/example.obj",
    texturePath = "models/example.png"
)
```

Особенности:

- библиотека читает модель из `assets`;
- для загрузки используется формат `OBJ`;
- если у модели есть `UV`-координаты и указан `texturePath`, объект будет отображаться с текстурой;
- при отсутствии текстуры используется одноцветный режим рендеринга.

## Как это работает

На уровне API сцена описывается внутри `OpenGLScene` с помощью DSL-вызовов. Библиотека формирует внутренний список объектов сцены и передаёт его в `GLRenderer`, который выполняет отрисовку средствами `OpenGL ES 2.0`.

Упрощённая цепочка выглядит так:

`Compose state -> OpenGLScene -> OpenGLSceneScope -> SceneShape -> GLRenderer -> OpenGL ES`

## Основные публичные элементы

- `hr5h.glviz.core.OpenGLScene`
- `hr5h.glviz.core.OpenGLSceneScope`
- `hr5h.glviz.core.ShapeTransformScope`

Основные DSL-вызовы:

- `Triangle(...)`
- `Square(...)`
- `Cube(...)`
- `Pyramid(...)`
- `Model(...)`

## Ограничения текущей версии

- библиотека ориентирована на базовые сценарии `3D`-визуализации;
- используется `OpenGL ES 2.0`;
- формат внешних моделей ограничен `OBJ`;
- система материалов и освещения реализована в упрощённом виде;
- библиотека предназначена для встраивания сцены в `Compose`, а не для разработки полноценных игровых движков.

## Демонстрационное приложение

В репозитории присутствует demo-приложение, показывающее:

- отображение встроенных примитивов;
- подключение `OBJ`-модели;
- применение трансформаций;
- работу библиотеки внутри обычного `Compose`-экрана.
