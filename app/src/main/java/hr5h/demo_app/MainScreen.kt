package hr5h.demo_app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import hr5h.demo_app.components.ChipGroup
import hr5h.glviz.core.OpenGLScene
import hr5h.glviz.shapes.ShapeType

private inline fun SnapshotStateList<SceneObjectState>.updateObject(
    index: Int,
    update: (SceneObjectState) -> SceneObjectState
) {
    if (index in indices) {
        this[index] = update(this[index])
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val modelPath = "models/cube.obj"
    val modelTexturePath = "models/cube.png"

    val listOfShapes = listOf(
        ShapeType.TRIANGLE.toString(),
        ShapeType.SQUARE.toString(),
        ShapeType.CUBE.toString(),
        ShapeType.PYRAMID.toString(),
        "Модель"
    )

    var selectedShapeType by remember { mutableStateOf(ShapeType.TRIANGLE.toString()) }
    val sceneObjects = remember { mutableStateListOf<SceneObjectState>(SceneObjectState.Shape(ShapeType.TRIANGLE)) }
    var selectedObjectIndex by remember { mutableIntStateOf(0) }

    val latestSelectedIndex by rememberUpdatedState(selectedObjectIndex)
    val sceneSnapshot = sceneObjects.toList()

    Column(modifier = modifier) {
        OpenGLScene(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val idx = latestSelectedIndex
                        sceneObjects.updateObject(idx) { current ->
                            var updated = current.rotate(
                                x = pan.y * 0.5f,
                                y = pan.x * 0.5f,
                            )
                            if (zoom != 1f) {
                                updated = updated.scale(x = zoom, y = zoom, z = zoom)
                            }
                            updated
                        }
                    }
                }
        ) {
            for (obj in sceneSnapshot) {
                when (obj) {
                    is SceneObjectState.Shape -> when (obj.type) {
                        ShapeType.TRIANGLE -> Triangle {
                            applyTransformState(obj.transformState)
                        }

                        ShapeType.SQUARE -> Square {
                            applyTransformState(obj.transformState)
                        }

                        ShapeType.CUBE -> Cube {
                            applyTransformState(obj.transformState)
                        }

                        ShapeType.PYRAMID -> Pyramid {
                            applyTransformState(obj.transformState)
                        }

                        ShapeType.MODEL -> error("Unreachable")
                    }

                    is SceneObjectState.Model -> Model(
                        modelPath = obj.modelPath,
                        texturePath = obj.texturePath,
                    ) {
                        applyTransformState(obj.transformState)
                    }
                }
            }
        }

        ChipGroup(
            chipList = listOfShapes,
            selected = selectedShapeType
        ) { selected ->
            if (selected == selectedShapeType) return@ChipGroup
            selectedShapeType = selected

            sceneObjects.clear()
            if (selected == "Модель") {
                sceneObjects.add(SceneObjectState.Model(modelPath, modelTexturePath))
                selectedObjectIndex = 0
            } else {
                val type = ShapeType.valueOf(selected)
                sceneObjects.add(SceneObjectState.Shape(type))
                selectedObjectIndex = 0
            }
        }

        if (sceneSnapshot.isNotEmpty()) {
            Text(
                text = "Выберите объект для перемещения:",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelMedium
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(sceneSnapshot) { index, obj ->
                    val isSelected = index == selectedObjectIndex
                    Text(
                        text = "Объект ${index + 1}: ${obj.type.defaultDisplayName}",
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable {
                                selectedObjectIndex = index
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (selectedShapeType == "Модель") {
                        sceneObjects.add(SceneObjectState.Model(modelPath, modelTexturePath))
                        selectedObjectIndex = sceneObjects.size - 1
                    } else {
                        val type = ShapeType.valueOf(selectedShapeType)
                        sceneObjects.add(SceneObjectState.Shape(type))
                        selectedObjectIndex = sceneObjects.size - 1
                    }
                }
            ) { Text("Добавить на сцену") }

            Button(onClick = {
                sceneObjects.clear()
                selectedObjectIndex = -1
            }) { Text("Очистить сцену") }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val step = 0.1f
            Button(onClick = {
                sceneObjects.updateObject(selectedObjectIndex) { it.translate(x = -step) }
            }) { Text("x-") }
            Button(onClick = {
                sceneObjects.updateObject(selectedObjectIndex) { it.translate(x = step) }
            }) { Text("x+") }
            Button(onClick = {
                sceneObjects.updateObject(selectedObjectIndex) { it.translate(y = step) }
            }) { Text("y+") }
            Button(onClick = {
                sceneObjects.updateObject(selectedObjectIndex) { it.translate(y = -step) }
            }) { Text("y-") }
            Button(onClick = {
                sceneObjects.updateObject(selectedObjectIndex) { it.translate(z = step) }
            }) { Text("z+") }
            Button(onClick = {
                sceneObjects.updateObject(selectedObjectIndex) { it.translate(z = -step) }
            }) { Text("z-") }
        }
    }
}
