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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import hr5h.demo_app.components.ChipGroup
import hr5h.glviz.core.OpenGLScene
import hr5h.glviz.core.Transform
import hr5h.glviz.models.ObjLoader
import hr5h.glviz.shapes.ShapeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SceneObjectState(
    val type: ShapeType,
    val displayName: String,
    val vertices: FloatArray? = null,
    val color: FloatArray? = null,
    val vertexColors: FloatArray? = null,
    val texCoords: FloatArray? = null,
    val texturePath: String? = null,
) {
    val transforms = mutableListOf<Transform>()

    fun translate(dx: Float, dy: Float, dz: Float) {
        transforms.add(Transform.Translate(dx, dy, dz))
    }

    fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        transforms.add(Transform.Rotate(angle, x, y, z))
    }

    fun scale(sx: Float, sy: Float, sz: Float) {
        transforms.add(Transform.Scale(sx, sy, sz))
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
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
    val sceneObjects = remember { mutableStateListOf(SceneObjectState(ShapeType.TRIANGLE, "Треугольник")) }
    var selectedObjectIndex by remember { mutableIntStateOf(0) }
    var sceneVersion by remember { mutableIntStateOf(0) }

    val latestSelectedIndex by rememberUpdatedState(selectedObjectIndex)
    val currentSceneVersion = sceneVersion

    Column(modifier = modifier) {
        OpenGLScene(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val idx = latestSelectedIndex
                        if (idx in sceneObjects.indices) {
                            sceneObjects[idx].rotate(pan.y * 0.5f, 1f, 0f, 0f)
                            sceneObjects[idx].rotate(pan.x * 0.5f, 0f, 1f, 0f)
                            if (zoom != 1f) {
                                sceneObjects[idx].scale(zoom, zoom, zoom)
                            }
                            sceneVersion++
                        }
                    }
                }
        ) {
            val version = currentSceneVersion
            for (obj in sceneObjects) {
                when (obj.type) {
                    ShapeType.TRIANGLE -> Triangle(obj.displayName) {
                        applyAll(obj.transforms)
                    }

                    ShapeType.SQUARE -> Square(obj.displayName) {
                        applyAll(obj.transforms)
                    }

                    ShapeType.CUBE -> Cube(obj.displayName) {
                        applyAll(obj.transforms)
                    }

                    ShapeType.PYRAMID -> Pyramid(obj.displayName) {
                        applyAll(obj.transforms)
                    }

                    ShapeType.MODEL -> {
                        val v = obj.vertices ?: return@OpenGLScene
                        Model(
                            vertices = v,
                            name = obj.displayName,
                            color = obj.color ?: floatArrayOf(0.7f, 0.5f, 0.3f, 1f),
                            vertexColors = obj.vertexColors,
                            texCoords = obj.texCoords,
                            texturePath = obj.texturePath,
                        ) {
                            applyAll(obj.transforms)
                        }
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
                coroutineScope.launch {
                    val mesh = withContext(Dispatchers.IO) { ObjLoader.load(context, modelPath) }
                    withContext(Dispatchers.Main) {
                        if (mesh != null && mesh.vertices.size >= 9) {
                            sceneObjects.add(
                                SceneObjectState(
                                    ShapeType.MODEL,
                                    "Модель: ${modelPath.substringAfterLast('/')}",
                                    vertices = mesh.vertices,
                                    color = floatArrayOf(0.7f, 0.5f, 0.3f, 1f),
                                    vertexColors = mesh.vertexColors,
                                    texCoords = mesh.texCoords,
                                    texturePath = modelTexturePath,
                                )
                            )
                            selectedObjectIndex = 0
                            sceneVersion++
                        }
                    }
                }
            } else {
                val type = ShapeType.valueOf(selected)
                sceneObjects.add(SceneObjectState(type, selected))
                selectedObjectIndex = 0
                sceneVersion++
            }
        }

        if (sceneObjects.isNotEmpty()) {
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
                itemsIndexed(sceneObjects.toList()) { index, obj ->
                    val isSelected = index == selectedObjectIndex
                    Text(
                        text = "Объект ${index + 1}: ${obj.displayName}",
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
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (selectedShapeType == "Модель") {
                        coroutineScope.launch {
                            val mesh =
                                withContext(Dispatchers.IO) { ObjLoader.load(context, modelPath) }
                            withContext(Dispatchers.Main) {
                                if (mesh != null && mesh.vertices.size >= 9) {
                                    sceneObjects.add(
                                        SceneObjectState(
                                            ShapeType.MODEL,
                                            "Модель: ${modelPath.substringAfterLast('/')}",
                                            vertices = mesh.vertices,
                                            color = floatArrayOf(0.7f, 0.5f, 0.3f, 1f),
                                            vertexColors = mesh.vertexColors,
                                            texCoords = mesh.texCoords,
                                            texturePath = modelTexturePath,
                                        )
                                    )
                                    selectedObjectIndex = sceneObjects.size - 1
                                    sceneVersion++
                                }
                            }
                        }
                    } else {
                        val type = ShapeType.valueOf(selectedShapeType)
                        sceneObjects.add(SceneObjectState(type, selectedShapeType))
                        selectedObjectIndex = sceneObjects.size - 1
                        sceneVersion++
                    }
                }
            ) { Text("Добавить на сцену") }

            Button(onClick = {
                sceneObjects.clear()
                selectedObjectIndex = -1
                sceneVersion++
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
                if (selectedObjectIndex in sceneObjects.indices) {
                    sceneObjects[selectedObjectIndex].translate(-step, 0f, 0f)
                    sceneVersion++
                }
            }) { Text("x-") }
            Button(onClick = {
                if (selectedObjectIndex in sceneObjects.indices) {
                    sceneObjects[selectedObjectIndex].translate(step, 0f, 0f)
                    sceneVersion++
                }
            }) { Text("x+") }
            Button(onClick = {
                if (selectedObjectIndex in sceneObjects.indices) {
                    sceneObjects[selectedObjectIndex].translate(0f, step, 0f)
                    sceneVersion++
                }
            }) { Text("y+") }
            Button(onClick = {
                if (selectedObjectIndex in sceneObjects.indices) {
                    sceneObjects[selectedObjectIndex].translate(0f, -step, 0f)
                    sceneVersion++
                }
            }) { Text("y-") }
            Button(onClick = {
                if (selectedObjectIndex in sceneObjects.indices) {
                    sceneObjects[selectedObjectIndex].translate(0f, 0f, step)
                    sceneVersion++
                }
            }) { Text("z+") }
            Button(onClick = {
                if (selectedObjectIndex in sceneObjects.indices) {
                    sceneObjects[selectedObjectIndex].translate(0f, 0f, -step)
                    sceneVersion++
                }
            }) { Text("z-") }
        }
    }
}
