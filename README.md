# Orbit — BBModel 解析转换库 v2.0

Orbit 是 [ArcartX](https://github.com/17Artist/ArcartX_Plugin) 项目的 BBModel 解析转换库，用于将 Blockbench 的 `.bbmodel` 文件转换为 ArcartX 自定义的类 Geometry 格式 JSON 模型，供 ArcartX Mod 的渲染引擎使用。

## 核心特性

- **网格（Mesh）支持**：完整支持 Blockbench 通用模型（Free Format）的网格功能，包括三角形和四边形混合模式
- **立方体（Cube）支持**：支持 Box UV 和 Per-Face UV 两种模式
- **多贴图分模型**：自动按贴图拆分为多个独立模型，适配渲染引擎单贴图渲染限制
- **动画解析**：支持 Linear、Step、Catmullrom、Bezier 四种插值模式
- **Camera 支持**：解析 camera 元素及其动画通道（position、rotation、fov）
- **Locator 支持**：解析定位器元素
- **序列帧 mcmeta**：完整生成动画元数据，支持自定义帧序、帧插值、非正方形帧
- **贴图解析**：自动解码 Base64 内嵌贴图，支持发光贴图（`_glow`）和特效贴图（`_vfx`）
- **V4 / V5 双版本兼容**：同时支持 Blockbench V4（outliner 内嵌骨骼数据）和 V5（groups + outliner 分离结构）两种格式
- **TextureMesh 支持**：支持纹理网格类型元素

## 依赖

- Java 11+
- Jackson Databind 2.17.x

## 快速开始

```java
OrbitBBModelConverter converter = new OrbitBBModelConverter();
GeoModel geoModel = converter.parse(inputStream);

if (geoModel.hasError()) {
    System.err.println(geoModel.error().getMessage());
    return;
}

for (Model model : geoModel.models()) {
    String geoJson = model.modelSrc();       // Geometry JSON
    byte[] texture = model.mainTextureSrc();  // PNG 贴图字节
    String mcmeta = model.mainTextureMetaSrc(); // mcmeta JSON（可能为 null）
}

String animationJson = geoModel.animationSrc(); // 动画 JSON（可能为 null）
```

## 输出格式

### Geometry JSON

```json
{
  "format_version": "1.12.0",
  "minecraft:geometry": [{
    "description": { "texture_width": 64, "texture_height": 64, ... },
    "bones": [{
      "name": "bone_name",
      "pivot": [x, y, z],
      "cubes": [{ "size": [...], "origin": [...], "uv": [...] }],
      "meshes": [{
        "name": "mesh_name",
        "vertices": [[x,y,z], ...],
        "normals": [[nx,ny,nz], ...],
        "uvs": [[u,v], ...],
        "triangles": [0,1,2, ...],
        "quads": [0,1,2,3, ...]
      }],
      "locators": {},
      "cameras": {}
    }]
  }]
}
```

### 网格格式说明

| 字段          | 类型          | 说明                               |
|-------------|-------------|----------------------------------|
| `vertices`  | `float[][]` | 展开后的顶点位置，每个 `[x, y, z]`，已做 X 轴取反 |
| `normals`   | `float[][]` | 面法线，与顶点一一对应                      |
| `uvs`       | `float[][]` | UV 坐标，已归一化（除以贴图宽高），与顶点一一对应       |
| `triangles` | `int[]`     | 三角形索引，每 3 个为一组                   |
| `quads`     | `int[]`     | 四边形索引，每 4 个为一组                   |

### 渲染端参考

```java
// 渲染三角形
bufferBuilder.begin(DrawMode.TRIANGLES, VertexFormats.POSITION_TEXTURE_NORMAL);
for (int i = 0; i < triangles.length; i += 3) {
    emitVertex(bufferBuilder, vertices[triangles[i]], uvs[triangles[i]], normals[triangles[i]]);
    emitVertex(bufferBuilder, vertices[triangles[i+1]], uvs[triangles[i+1]], normals[triangles[i+1]]);
    emitVertex(bufferBuilder, vertices[triangles[i+2]], uvs[triangles[i+2]], normals[triangles[i+2]]);
}
tessellator.draw();

// 渲染四边形
bufferBuilder.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_NORMAL);
for (int i = 0; i < quads.length; i += 4) {
    emitVertex(bufferBuilder, vertices[quads[i]], uvs[quads[i]], normals[quads[i]]);
    emitVertex(bufferBuilder, vertices[quads[i+1]], uvs[quads[i+1]], normals[quads[i+1]]);
    emitVertex(bufferBuilder, vertices[quads[i+2]], uvs[quads[i+2]], normals[quads[i+2]]);
    emitVertex(bufferBuilder, vertices[quads[i+3]], uvs[quads[i+3]], normals[quads[i+3]]);
}
tessellator.draw();
```

## 项目结构

```
priv.seventeen.artist.orbit
├── OrbitBBModelConverter.java          // 主入口
├── loader/
│   ├── BBModelReader.java              // bbmodel 反序列化
│   └── component/                      // 数据模型
│       ├── BBModel, BBElement, BBMeshFace, BBTexture, ...
│       └── Outliner, OutlinerDeserializer, ...
├── converter/
│   ├── wrapper/                        // 模型包装
│   │   ├── WrappedModel.java
│   │   └── WrappedOutliner.java
│   └── builder/                        // JSON 构建
│       ├── GeometryBuilder.java
│       ├── BoneBuilder.java
│       ├── MeshBuilder.java            // 网格转换核心
│       ├── AnimationBuilder.java
│       └── McMetaBuilder.java
├── calculator/
│   ├── BezierConverter.java
│   └── VisibleBoundsCalculator.java
└── result/
    ├── GeoModel.java
    └── Model.java
```

## 构建

```bash
./gradlew build
```

## 测试

```bash
java -cp <classpath> priv.seventeen.artist.orbit.TestParser model.bbmodel output
```

## 行为约定与兼容性

本库并非标准geo格式模型，约定如下。

| 项目                                     | 约定                                                                              |
|----------------------------------------|---------------------------------------------------------------------------------|
| 骨骼 `pivot` / `rotation`                | X 轴取反；`rotation` 额外取反 Y 轴                                                       |
| Cube `origin`                          | X 轴按 `-(max(from,to)[0] + (size<0?size:0))` 计算；`java_*` 格式整体 -8 偏移              |
| Cube `pivot` / `rotation`              | 同骨骼约定（X 取反、rotation 再取反 Y）                                                      |
| Cube UV（per-face）                      | `up` / `down` 面 UV 起点加上 size 后整体取反（匹配 Blockbench 官方导出）                          |
| Locator / Camera `offset` / `rotation` | **不做任何取反**（与裸 Blockbench 导出不同）                                                  |
| `description.texture_width/height`     | 优先取贴图 `uv_width/uv_height`；缺失或为 0 时回退到 `model.resolution`（兼容 V4）                |
| `description` 字段                       | 不输出 `identifier`                                                                |
| `bones[].cubes/locators/cameras`       | 即使为空也输出空容器                                                                      |
| `visible_bounds_*`                     | 采用 +8 偏移后的径向半径算法，并结合 `model.visible_box` 元数据                                    |
| V5 (格式 5.0) 骨骼树                        | `outliner` 仅存 UUID 树，数据从 `groups[]` 按 UUID 合并填充；纯元素引用（UUID + 无 children）保持重定向语义 |
| 动画 Bezier 插值                           | 使用 `current.bezierRight*` + `next.bezierLeft*` 作为段首/段末控制点（语义正确的组合）              |
| 动画 V5 值修正                              | rotation 通道 X/Y 取反，position 通道 X 取反                                             |
| 多贴图拆分                                  | `bedrock` / `animated_*` / `bedrock*` 视为单贴图格式；其余按贴图索引拆分为多个 `Model`              |

## 许可证

Apache License 2.0
