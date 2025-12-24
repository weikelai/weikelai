# 身份证OCR识别应用

基于Android平台的身份证识别应用，使用腾讯云OCR服务实现身份证信息的自动识别和提取。

## 📋 项目简介

本应用是一个Android身份证识别工具，用户可以通过拍照或选择图片的方式，快速识别身份证上的关键信息，包括姓名、性别、民族、出生日期、地址和身份证号码。

## ✨ 功能特性

- 📸 **拍照识别**：支持使用设备摄像头拍摄身份证照片进行识别
- 🖼️ **图片选择**：支持从相册中选择身份证图片进行识别
- 🔄 **自动旋转**：自动根据EXIF信息调整图片方向
- 🔐 **Base64编码**：自动将图片转换为Base64编码格式
- ☁️ **云端识别**：使用腾讯云OCR API进行高精度识别
- 📊 **结果展示**：清晰展示识别出的身份证信息

## 🛠️ 环境要求

- **Android Studio**：Arctic Fox 或更高版本
- **JDK**：JDK 8 或更高版本
- **Android SDK**：API Level 21 (Android 5.0) 或更高
- **网络权限**：需要网络连接以调用腾讯云API
- **相机权限**：需要相机权限以进行拍照功能

## ⚙️ 配置说明

### 1. 腾讯云OCR服务开通

**重要：在使用本应用前，必须先开通腾讯云OCR服务！**

#### 步骤1：开通OCR服务

1. 访问 [腾讯云控制台](https://console.cloud.tencent.com/)
2. 登录您的账号（如果没有账号，需要先注册）
3. 进入 **产品与服务** → **文字识别 OCR**
4. 点击 **立即开通** 或 **购买资源包**
5. 选择 **身份证识别** 服务
6. 根据提示完成开通流程

#### 步骤2：获取API密钥

1. 在腾讯云控制台，点击右上角头像
2. 选择 **访问管理** → **API密钥管理**
3. 创建密钥或查看现有密钥
4. 获取 `SecretId` 和 `SecretKey`

#### 步骤3：配置API密钥

打开文件：`src/main/java/com/example/chapter03/util/TecentHttpUtil.java`

修改以下配置：

```java
private static final String SECRET_ID = "AKIDWwGQWUO6e084oZIwUxJYdeaL6mGakhW9";  // 替换为您的SecretId
private static final String SECRET_KEY = "Tp00oJdkfO9oLqRyMYr8FCzbAav3yhGd"; // 替换为您的SecretKey
```

**注意**：如果遇到 `FailedOperation.UnOpenError` 错误，说明服务未开通，请按照步骤1开通服务。

### 2. 权限配置

确保 `AndroidManifest.xml` 中包含以下权限：

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.CAMERA"/>
```

### 3. FileProvider配置

应用已配置FileProvider用于拍照功能，确保 `AndroidManifest.xml` 中包含：

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="com.example.chapter03.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

## 📱 使用方法

### 步骤1：启动应用

打开应用后，会看到主界面（`OcrMainActivity`），界面中央有一个"拍照"按钮。

### 步骤2：拍照或选择图片

- **方式一：拍照**
  1. 点击"拍照"按钮
  2. 授予相机权限（首次使用）
  3. 对准身份证进行拍照
  4. 确认照片后返回应用

- **方式二：使用本地图片（测试）**
  - 修改 `ImageProcessing.java` 中的代码，使用本地图片资源进行测试

### 步骤3：图片处理

拍摄或选择图片后，应用会自动：
1. 加载图片并显示预览
2. 根据EXIF信息自动旋转图片
3. 将图片转换为Base64编码
4. 调用腾讯云OCR API进行识别

### 步骤4：查看识别结果

识别完成后，会自动跳转到结果页面（`ImageProcessing`），显示以下信息：
- **姓名**
- **性别**
- **民族**
- **出生日期**
- **地址**
- **身份证号**

## 📂 项目结构

```
chapter03/
├── src/
│   ├── main/
│   │   ├── java/com/example/chapter03/
│   │   │   ├── OcrMainActivity.java          # 主活动：拍照功能
│   │   │   ├── ImageProcessing.java         # 图片处理活动：OCR识别
│   │   │   └── util/
│   │   │       ├── Base64Util.java           # Base64编码工具
│   │   │       └── TecentHttpUtil.java       # 腾讯云API调用工具
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_ocr_main.xml    # 主界面布局
│   │   │   │   └── activity_image_processing.xml  # 结果展示布局
│   │   │   └── xml/
│   │   │       └── file_paths.xml            # FileProvider路径配置
│   │   └── AndroidManifest.xml               # 应用清单文件
│   └── README.md                             # 本文件
```

## 🔧 技术说明

### 核心功能模块

#### 1. 拍照功能 (`OcrMainActivity.java`)
- 使用 `MediaStore.ACTION_IMAGE_CAPTURE` Intent启动相机
- 使用FileProvider处理Android 7.0+的文件访问
- 根据EXIF信息自动旋转图片

#### 2. 图片处理 (`ImageProcessing.java`)
- 加载图片并转换为Bitmap
- 将Bitmap转换为字节数组
- Base64编码处理
- 调用OCR API并解析结果

#### 3. Base64编码 (`Base64Util.java`)
- 使用Android内置的Base64工具类
- 将图片字节数组编码为Base64字符串

#### 4. 网络请求 (`TecentHttpUtil.java`)
- 实现腾讯云API V3签名算法
- 使用OkHttp进行HTTP请求
- 处理API响应和错误

### 业务流程

```
启动应用 → 点击拍照 → 拍摄/选择图片 → 图片预处理（旋转） → 
Base64编码 → 调用腾讯云API → 解析JSON结果 → 显示识别信息
```

## ⚠️ 注意事项

1. **服务开通**：**必须先在腾讯云控制台开通OCR服务**，否则会返回 `FailedOperation.UnOpenError` 错误
2. **API密钥安全**：请妥善保管您的腾讯云API密钥，不要将密钥提交到公开代码仓库
3. **网络连接**：应用需要网络连接才能调用OCR服务
4. **图片质量**：建议使用清晰、光线充足的身份证照片，分辨率建议500*800以上，以提高识别准确率
5. **隐私保护**：身份证信息属于敏感信息，请确保应用的安全性
6. **API配额**：注意腾讯云OCR服务的调用次数限制和费用
7. **错误处理**：应用已实现错误检测，如果服务未开通或出现其他错误，会在界面上显示具体错误信息

## 🐛 常见问题

### Q: 显示"服务未开通，请前往控制台开通相应服务"错误？
A: 这是 `FailedOperation.UnOpenError` 错误，解决方法：
1. 登录 [腾讯云控制台](https://console.cloud.tencent.com/)
2. 进入 **文字识别 OCR** 服务
3. 开通 **身份证识别** 服务
4. 确保账户有足够余额或已购买资源包
5. 重新运行应用

### Q: 识别失败怎么办？
A: 请检查：
- **服务是否已开通**（最常见问题）
- 网络连接是否正常
- API密钥是否正确配置
- 图片是否清晰（分辨率建议500*800以上）
- 查看Logcat日志获取详细错误信息

### Q: 拍照后图片方向不对？
A: 应用已实现自动旋转功能，如果仍有问题，请检查EXIF信息是否正确。

### Q: 如何测试而不使用真实拍照？
A: 可以在 `ImageProcessing.java` 中设置 `USE_LOCAL_TEST_IMAGE = true`，使用本地图片资源进行测试。

### Q: API返回的字段名是什么？
A: 根据腾讯云OCR API文档，返回的字段名为：
- `Name` - 姓名
- `Sex` - 性别
- `Nation` - 民族
- `Birth` - 出生日期
- `Address` - 地址
- `IdNum` - 身份证号

### Q: 如何查看详细的API返回信息？
A: 查看Android Studio的Logcat，过滤标签 `ImageProcessing`，可以看到完整的JSON返回和解析过程。

## 📝 更新日志

### v1.0.0
- 初始版本发布
- 实现拍照和OCR识别功能
- 支持身份证信息识别和展示

## 📄 许可证

本项目仅供学习使用。

## 👨‍💻 开发者

如有问题或建议，请联系开发者。

---

**提示**：使用本应用前，请确保已正确配置腾讯云API密钥，并了解相关服务费用。

