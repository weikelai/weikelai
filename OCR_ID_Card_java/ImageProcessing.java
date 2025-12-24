package com.example.chapter03;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter03.util.Base64Util;
import com.example.chapter03.util.TecentHttpUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * ImageProcessing活动：负责接收图片，保存图片，对图片进行base64编码处理，v3签名，并且进行网络服务请求，然后将其展示在页面中
 */
public class ImageProcessing extends AppCompatActivity {
    private static final String TAG = "ImageProcessing";
    
    // 测试模式开关：默认关闭。仅在没有传入图片路径时，用于本地自测。
    private static final boolean USE_LOCAL_TEST_IMAGE = false;
    
    // 本地测试图片资源ID：使用drawable目录下的test_idcard.jpg
    // 注意：Android资源名称在编译时会转换为小写，所以test_idcard.jpg对应R.drawable.test_idcard
    private static final int TEST_IMAGE_RES_ID = R.drawable.test_idcard;
    
    private ImageView imageView;
    private Handler handler;
    private TextView nameTextView;      // 姓名
    private TextView sexTextView;       // 性别
    private TextView nationTextView;    // 民族
    private TextView birthTextView;     // 出生日期
    private TextView addressTextView;   // 地址
    private TextView idNumTextView;     // 身份证号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);

        imageView = findViewById(R.id.imageView2);
        
        // 初始化各个TextView，顺序与布局文件一致
        nameTextView = findViewById(R.id.nameTextView);
        sexTextView = findViewById(R.id.sexTextView);
        nationTextView = findViewById(R.id.nationTextView);
        birthTextView = findViewById(R.id.birthTextView);
        addressTextView = findViewById(R.id.addressTextView);
        idNumTextView = findViewById(R.id.idNumTextView);

        // 初始化Handler用于处理识别结果
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String request = bundle.getString("request");
                if (request != null) {
                    Log.d(TAG, "收到识别结果: " + request);
                }
            }
        };

        // 优先使用从 OcrMainActivity 传递过来的图片路径，若没有则可选用本地测试图片
        Bitmap bitmap = null;

        // 1) 先尝试读取外部传入的图片路径
        String imagePath = getIntent().getStringExtra("image_path");
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                bitmap = BitmapFactory.decodeFile(imagePath);
                if (bitmap == null) {
                    toast("无法加载图片");
                    Log.e(TAG, "无法加载图片: " + imagePath);
                    return;
                }
                Log.d(TAG, "图片已从文件加载: " + imagePath);
            } else {
                Log.e(TAG, "图片文件不存在: " + imagePath);
            }
        } else {
            Log.w(TAG, "未获取到图片路径，image_path 为空");
        }

        // 2) 如果外部路径不可用且允许测试模式，则回退到本地测试图片
        if (bitmap == null && USE_LOCAL_TEST_IMAGE) {
            Log.d(TAG, "使用测试模式：加载本地drawable资源图片");
            try {
                bitmap = BitmapFactory.decodeResource(getResources(), TEST_IMAGE_RES_ID);
                if (bitmap == null) {
                    toast("无法加载测试图片资源");
                    Log.e(TAG, "无法加载测试图片资源，ID: " + TEST_IMAGE_RES_ID);
                    return;
                }
                Log.d(TAG, "测试图片已加载，资源ID: " + TEST_IMAGE_RES_ID);
            } catch (Exception e) {
                toast("加载测试图片失败: " + e.getMessage());
                Log.e(TAG, "加载测试图片失败", e);
                return;
            }
        }

        // 3) 外部路径不可用且不允许测试模式，则直接提示错误
        if (bitmap == null) {
            toast("未获取到有效的图片，请重新拍照或选择图片");
            Log.e(TAG, "未获取到有效的图片，image_path=" + imagePath);
            return;
        }
        
        // 显示图片
        if (bitmap != null) {
            // 设置图片显示方式：fitCenter确保图片完整显示且不变形
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageBitmap(bitmap);
            Log.d(TAG, "图片已显示在ImageView中，尺寸: " + bitmap.getWidth() + "x" + bitmap.getHeight());

            // 图片处理：将Bitmap转换为字节数组
            byte[] bitmapBytes = bitmapToByteArray(bitmap);
            Log.d(TAG, "图片已转换为字节数组，大小: " + bitmapBytes.length + " bytes");

            // 对字节数组进行Base64编码
            String base64EncodedString = Base64Util.encode(bitmapBytes);
            Log.d(TAG, "Base64编码完成，长度: " + base64EncodedString.length());

            // 调用API：使用腾讯云OCR服务进行身份证识别
            TecentHttpUtil.getIdCardDetails(base64EncodedString, new TecentHttpUtil.SimpleCallBack() {
                @Override
                public void Succ(String result) {
                    // 处理成功的结果
                    Log.d(TAG, "身份证识别成功: " + result);
                    
                    // 结果显示：解析并显示识别结果
                    runOnUiThread(() -> dataget(result));

                    Message message = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("request", result);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }

                @Override
                public void error() {
                    // 处理错误的情况
                    Log.e(TAG, "身份证识别失败");
                    runOnUiThread(() -> toast("身份证识别失败，请检查网络连接或图片质量"));
                }
            });
        }
    }

    /**
     * 将Bitmap转换为字节数组
     * 压缩图片，避免过大，使用JPEG格式，质量80
     */
    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 压缩一下图片，避免过大，这里用JPEG，质量80
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 显示Toast提示信息
     */
    private void toast(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

    /**
     * 身份证识别结果数据类
     */
    public static class IDCardResult {
        private Response Response;

        public Response getResponse() {
            return Response;
        }

        public void setResponse(Response Response) {
            this.Response = Response;
        }

        public static class Response {
            private String Address;  // 地址
            private String Birth;    // 出生日期
            private String IdNum;    // 身份证号
            private String Name;      // 姓名
            private String Nation;   // 民族
            private String Sex;      // 性别

            public String getAddress() {
                return Address;
            }
            public String getBirth() {
                return Birth;
            }
            public String getIdNum() {
                return IdNum;
            }
            public String getName() {
                return Name;
            }
            public String getNation() {
                return Nation;
            }
            public String getSex() {
                return Sex;
            }
        }
    }

    /**
     * 结果解析与显示：解析API返回的JSON数据，提取身份证信息并显示在界面上
     * 使用JSONObject直接解析，避免Gson字段映射问题
     */
    private void dataget(String result) {
        try {
            Log.d(TAG, "开始解析JSON结果，长度: " + result.length());
            Log.d(TAG, "完整JSON结果: " + result);
            
            // 使用JSONObject直接解析，更灵活
            JSONObject jsonObject = new JSONObject(result);
            
            // 检查是否有Response字段
            if (!jsonObject.has("Response")) {
                Log.e(TAG, "JSON中没有Response字段");
                Log.d(TAG, "JSON键: " + jsonObject.keys().toString());
                runOnUiThread(() -> toast("API返回格式错误：缺少Response字段"));
                return;
            }
            
            JSONObject responseObj = jsonObject.getJSONObject("Response");
            
            // 首先检查是否有Error字段（API返回错误）
            if (responseObj.has("Error")) {
                JSONObject errorObj = responseObj.getJSONObject("Error");
                String errorCode = errorObj.optString("Code", "未知错误");
                String errorMessage = errorObj.optString("Message", "未知错误信息");
                
                Log.e(TAG, "API返回错误: " + errorCode + " - " + errorMessage);
                
                runOnUiThread(() -> {
                    toast("识别失败: " + errorMessage);
                    // 显示错误信息在界面上
                    if (nameTextView != null) {
                        nameTextView.setText("错误: " + errorMessage);
                    }
                    if (sexTextView != null) {
                        sexTextView.setText("错误代码: " + errorCode);
                    }
                    // 清空其他字段
                    if (nationTextView != null) nationTextView.setText("民族: ");
                    if (birthTextView != null) birthTextView.setText("出生日期: ");
                    if (addressTextView != null) addressTextView.setText("地址: ");
                    if (idNumTextView != null) idNumTextView.setText("身份证号: ");
                });
                return;
            }
            
            // 打印所有Response中的键，便于调试
            Log.d(TAG, "=== Response中的所有键 ===");
            if (responseObj != null) {
                java.util.Iterator<String> keys = responseObj.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = responseObj.get(key);
                    Log.d(TAG, key + " = " + value);
                }
            }
            
            // 提取各个字段，尝试多种可能的字段名
            String name = getStringSafely(responseObj, "Name", "姓名");
            String sex = getStringSafely(responseObj, "Sex", "性别");
            String nation = getStringSafely(responseObj, "Nation", "民族");
            String birth = getStringSafely(responseObj, "Birth", "出生日期");
            String address = getStringSafely(responseObj, "Address", "地址");
            String idNum = getStringSafely(responseObj, "IdNum", "身份证号");
            
            // 打印识别结果到日志
            Log.d(TAG, "=== 识别结果 ===");
            Log.d(TAG, "姓名: " + name);
            Log.d(TAG, "性别: " + sex);
            Log.d(TAG, "民族: " + nation);
            Log.d(TAG, "出生日期: " + birth);
            Log.d(TAG, "地址: " + address);
            Log.d(TAG, "身份证号: " + idNum);

            // 确保在主线程更新UI
            final String finalName = name;
            final String finalSex = sex;
            final String finalNation = nation;
            final String finalBirth = birth;
            final String finalAddress = address;
            final String finalIdNum = idNum;
            
            runOnUiThread(() -> {
                // 将提取的信息显示在界面上的各个TextView中，添加标签
                // 显示顺序与布局文件一致：姓名、性别、民族、出生日期、地址、身份证号
                if (nameTextView != null) {
                    nameTextView.setText("姓名: " + (finalName != null && !finalName.isEmpty() ? finalName : "未识别"));
                }
                if (sexTextView != null) {
                    sexTextView.setText("性别: " + (finalSex != null && !finalSex.isEmpty() ? finalSex : "未识别"));
                }
                if (nationTextView != null) {
                    nationTextView.setText("民族: " + (finalNation != null && !finalNation.isEmpty() ? finalNation : "未识别"));
                }
                if (birthTextView != null) {
                    birthTextView.setText("出生日期: " + (finalBirth != null && !finalBirth.isEmpty() ? finalBirth : "未识别"));
                }
                if (addressTextView != null) {
                    addressTextView.setText("地址: " + (finalAddress != null && !finalAddress.isEmpty() ? finalAddress : "未识别"));
                }
                if (idNumTextView != null) {
                    idNumTextView.setText("身份证号: " + (finalIdNum != null && !finalIdNum.isEmpty() ? finalIdNum : "未识别"));
                }
                
                toast("识别完成");
                Log.d(TAG, "UI已更新");
            });
        } catch (org.json.JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSON解析失败: " + e.getMessage());
            Log.e(TAG, "异常堆栈: ", e);
            runOnUiThread(() -> toast("JSON解析失败: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "解析数据失败: " + e.getMessage());
            Log.e(TAG, "异常堆栈: ", e);
            runOnUiThread(() -> toast("解析数据失败: " + e.getMessage()));
        }
    }
    
    /**
     * 安全地从JSONObject中获取字符串值
     * @param jsonObject JSON对象
     * @param key 键名
     * @param logKey 日志中显示的键名（用于调试）
     * @return 字符串值，如果不存在或为空则返回null
     */
    private String getStringSafely(JSONObject jsonObject, String key, String logKey) {
        try {
            if (jsonObject.has(key) && !jsonObject.isNull(key)) {
                String value = jsonObject.getString(key);
                Log.d(TAG, logKey + " (" + key + ") = " + value);
                return value;
            } else {
                Log.w(TAG, logKey + " (" + key + ") 字段不存在或为null");
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "获取" + logKey + " (" + key + ") 失败: " + e.getMessage());
            return null;
        }
    }
}
