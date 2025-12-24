# 网络数据传输综合演示 Activity

## 功能概述

`NetworkComprehensiveActivity` 是一个综合演示Activity，整合了三种网络数据传输功能，每种功能都提供了**原生方式**和**第三方库方式**两种实现：

1. **文字请求（网页请求）**
   - 原生方式：`HttpURLConnection`
   - 第三方方式：`OkHttp`

2. **图片请求**
   - 原生方式：`HttpURLConnection` + `BitmapFactory`
   - 第三方方式：`Glide`

3. **流媒体（视频播放）**
   - 原生方式：`MediaPlayer` + `VideoView`
   - 第三方方式：`ExoPlayer`

## 技术栈

### 原生方式
- **HttpURLConnection**：Java标准库，用于HTTP请求
- **MediaPlayer/VideoView**：Android原生视频播放组件

### 第三方库
- **OkHttp 4.9.0**：Square公司开发的HTTP客户端库
- **Glide 4.16.0**：Google推荐的图片加载库
- **ExoPlayer 2.19.1**：Google开发的媒体播放库

## 使用方法

### 1. 启动Activity

可以通过以下方式启动：

```java
Intent intent = new Intent(context, NetworkComprehensiveActivity.class);
startActivity(intent);
```

或者在AndroidManifest.xml中配置为启动Activity。

### 2. 文字请求功能

#### 原生方式（HttpURLConnection）
1. 在URL输入框中输入要请求的URL（默认：`https://httpbin.org/get`）
2. 点击"原生方式 (HttpURLConnection)"按钮
3. 结果会显示在下方的文本区域

**特点**：
- 使用Java标准库，无需额外依赖
- 需要手动处理线程切换
- 需要手动管理连接和资源释放

#### 第三方方式（OkHttp）
1. 在URL输入框中输入要请求的URL
2. 点击"第三方方式 (OkHttp)"按钮
3. 结果会显示在下方的文本区域

**特点**：
- 自动处理线程切换（回调在主线程）
- 支持连接池和请求队列
- 更简洁的API
- 支持拦截器和缓存

### 3. 图片请求功能

#### 原生方式（HttpURLConnection）
1. 在图片URL输入框中输入图片地址（默认：`https://httpbin.org/image/png`）
2. 点击"原生方式 (HttpURLConnection)"按钮
3. 图片会显示在下方的ImageView中

**实现流程**：
```java
URL → HttpURLConnection → InputStream → BitmapFactory → ImageView
```

#### 第三方方式（Glide）
1. 在图片URL输入框中输入图片地址
2. 点击"第三方方式 (Glide)"按钮
3. 图片会显示在下方的ImageView中

**特点**：
- 自动处理图片缓存（内存+磁盘）
- 自动处理图片压缩和内存管理
- 支持占位图和错误图
- 支持GIF、WebP等格式
- 自动处理生命周期

### 4. 流媒体（视频播放）功能

#### 原生方式（MediaPlayer + VideoView）
1. 在视频URL输入框中输入视频地址（默认：`https://www.w3schools.com/html/mov_bbb.mp4`）
2. 点击"原生方式 (MediaPlayer)"按钮
3. 视频会在VideoView中播放

**特点**：
- Android原生组件，无需额外依赖
- 功能相对简单
- 对某些视频格式支持有限

#### 第三方方式（ExoPlayer）
1. 在视频URL输入框中输入视频地址
2. 点击"第三方方式 (ExoPlayer)"按钮
3. 视频会在ExoPlayer的PlayerView中播放

**特点**：
- 支持更多视频格式和协议（HLS、DASH等）
- 更好的性能和缓冲控制
- 支持字幕、多音轨等高级功能
- 可自定义播放器UI

## 代码结构说明

### 主要方法

#### 文字请求
- `loadTextWithNative(String url)` - 使用HttpURLConnection请求文字
- `loadTextWithOkHttp(String url)` - 使用OkHttp请求文字

#### 图片请求
- `loadImageWithNative(String url)` - 使用HttpURLConnection加载图片
- `downloadImage(String url)` - 下载图片的辅助方法
- `loadImageWithGlide(String url)` - 使用Glide加载图片

#### 视频播放
- `playVideoWithNative(String url)` - 使用MediaPlayer播放视频
- `playVideoWithExoPlayer(String url)` - 使用ExoPlayer播放视频

### 资源管理

Activity在以下生命周期方法中管理资源：

- `onPause()` - 暂停播放
- `onResume()` - 恢复播放（如果需要）
- `onDestroy()` - 释放所有播放器资源

## 第三方库官方文档

### OkHttp
- **官方文档**：https://square.github.io/okhttp/
- **GitHub**：https://github.com/square/okhttp
- **主要特性**：
  - HTTP/2支持
  - 连接池复用
  - 透明GZIP压缩
  - 响应缓存

### Glide
- **官方文档**：https://bumptech.github.io/glide/
- **GitHub**：https://github.com/bumptech/glide
- **主要特性**：
  - 自动内存和磁盘缓存
  - 图片转换和动画
  - 生命周期感知
  - 支持多种图片格式

### ExoPlayer
- **官方文档**：https://exoplayer.dev/
- **GitHub**：https://github.com/google/ExoPlayer
- **主要特性**：
  - 支持多种媒体格式
  - 自适应流媒体（HLS、DASH）
  - 可扩展的架构
  - 丰富的播放控制API

## 原生 vs 第三方对比

| 功能 | 原生方式 | 第三方方式 | 推荐 |
|------|---------|-----------|------|
| **文字请求** | HttpURLConnection | OkHttp | 第三方（更简洁） |
| **图片加载** | HttpURLConnection + BitmapFactory | Glide | 第三方（功能强大） |
| **视频播放** | MediaPlayer + VideoView | ExoPlayer | 第三方（功能丰富） |

### 原生方式的优势
- 无需额外依赖
- 包体积小
- 系统级支持

### 第三方库的优势
- 功能更强大
- API更简洁
- 更好的性能和稳定性
- 活跃的社区支持

## 测试URL示例

### 文字请求
- `https://httpbin.org/get` - 返回GET请求信息
- `https://httpbin.org/post` - 返回POST请求信息（需要修改为POST）
- `https://api.github.com/users/octocat` - GitHub API示例

### 图片请求
- `https://httpbin.org/image/png` - PNG格式图片
- `https://httpbin.org/image/jpeg` - JPEG格式图片
- `https://picsum.photos/800/600` - 随机图片

### 视频播放
- `https://www.w3schools.com/html/mov_bbb.mp4` - 示例视频
- `https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4` - 大文件视频

## 注意事项

1. **网络权限**：确保AndroidManifest.xml中已声明INTERNET权限
2. **线程处理**：原生方式需要在后台线程执行，UI更新需切换到主线程
3. **资源释放**：播放器资源需要在onDestroy中正确释放
4. **错误处理**：实际项目中应添加更完善的错误处理和重试机制
5. **HTTPS**：现代应用应优先使用HTTPS协议

## 扩展建议

1. **添加POST请求示例**：演示如何发送POST请求
2. **添加文件上传功能**：演示文件上传
3. **添加下载进度显示**：显示下载进度条
4. **添加缓存策略配置**：演示不同的缓存策略
5. **添加网络状态检测**：检测网络连接状态

## 总结

本Activity完整演示了网络数据传输的三种主要场景，每种场景都提供了原生和第三方两种实现方式，帮助理解：

1. **原生API的使用**：了解底层实现原理
2. **第三方库的优势**：提高开发效率
3. **技术选型**：根据项目需求选择合适的方案

通过对比学习，可以更好地理解网络编程的核心概念和最佳实践。

