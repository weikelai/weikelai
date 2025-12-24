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

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * LocalIdCardActivity
 * 直接使用本地drawable中的test_idcard.jpg进行身份证识别，无需拍照或选择图片。
 */
public class LocalIdCardActivity extends AppCompatActivity {
    private static final String TAG = "LocalIdCardActivity";

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
        nameTextView = findViewById(R.id.nameTextView);
        sexTextView = findViewById(R.id.sexTextView);
        nationTextView = findViewById(R.id.nationTextView);
        birthTextView = findViewById(R.id.birthTextView);
        addressTextView = findViewById(R.id.addressTextView);
        idNumTextView = findViewById(R.id.idNumTextView);

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

        // 直接从drawable加载本地身份证示例图片
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test_idcard);
        if (bitmap == null) {
            toast("无法加载本地身份证图片");
            Log.e(TAG, "无法从drawable加载test_idcard.jpg");
            return;
        }

        // 显示图片
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageBitmap(bitmap);
        Log.d(TAG, "本地图片已显示，尺寸: " + bitmap.getWidth() + "x" + bitmap.getHeight());

        // 图片转Base64后请求识别
        byte[] bitmapBytes = bitmapToByteArray(bitmap);
        String base64EncodedString = Base64Util.encode(bitmapBytes);
        Log.d(TAG, "Base64编码完成，长度: " + base64EncodedString.length());

        TecentHttpUtil.getIdCardDetails(base64EncodedString, new TecentHttpUtil.SimpleCallBack() {
            @Override
            public void Succ(String result) {
                Log.d(TAG, "身份证识别成功: " + result);
                runOnUiThread(() -> dataget(result));

                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("request", result);
                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void error() {
                Log.e(TAG, "身份证识别失败");
                runOnUiThread(() -> toast("身份证识别失败，请检查网络或重试"));
            }
        });
    }

    /**
     * 将Bitmap转换为字节数组（JPEG，质量80）
     */
    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
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
     * 结果解析与显示：解析API返回的JSON数据，提取身份证信息并显示在界面上
     */
    private void dataget(String result) {
        try {
            Log.d(TAG, "开始解析JSON结果，长度: " + result.length());

            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.has("Response")) {
                Log.e(TAG, "JSON中没有Response字段");
                runOnUiThread(() -> toast("API返回格式错误：缺少Response字段"));
                return;
            }

            JSONObject responseObj = jsonObject.getJSONObject("Response");

            // 如果有错误码则直接提示
            if (responseObj.has("Error")) {
                JSONObject errorObj = responseObj.getJSONObject("Error");
                String errorMessage = errorObj.optString("Message", "未知错误");
                toast("识别失败: " + errorMessage);
                return;
            }

            String name = getStringSafely(responseObj, "Name", "姓名");
            String sex = getStringSafely(responseObj, "Sex", "性别");
            String nation = getStringSafely(responseObj, "Nation", "民族");
            String birth = getStringSafely(responseObj, "Birth", "出生日期");
            String address = getStringSafely(responseObj, "Address", "地址");
            String idNum = getStringSafely(responseObj, "IdNum", "身份证号");

            runOnUiThread(() -> {
                if (nameTextView != null) nameTextView.setText("姓名: " + defaultValue(name));
                if (sexTextView != null) sexTextView.setText("性别: " + defaultValue(sex));
                if (nationTextView != null) nationTextView.setText("民族: " + defaultValue(nation));
                if (birthTextView != null) birthTextView.setText("出生日期: " + defaultValue(birth));
                if (addressTextView != null) addressTextView.setText("地址: " + defaultValue(address));
                if (idNumTextView != null) idNumTextView.setText("身份证号: " + defaultValue(idNum));
                toast("识别完成");
            });
        } catch (Exception e) {
            Log.e(TAG, "解析数据失败", e);
            runOnUiThread(() -> toast("解析数据失败: " + e.getMessage()));
        }
    }

    /**
     * 安全地从JSONObject中获取字符串值
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

    private String defaultValue(String value) {
        return (value != null && !value.isEmpty()) ? value : "未识别";
    }
}

