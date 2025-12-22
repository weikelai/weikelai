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

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ImageProcessing extends AppCompatActivity {
    ImageView imageView;
    private Handler handler;
    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView textView5;
    TextView textView6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);

        imageView = findViewById(R.id.imageView2);
        
        textView1 = findViewById(R.id.addressTextView);
        textView2 = findViewById(R.id.birthTextView);
        textView3 = findViewById(R.id.idNumTextView);
        textView4 = findViewById(R.id.nameTextView);
        textView5 = findViewById(R.id.nationTextView);
        textView6 = findViewById(R.id.sexTextView);

        // 初始化Handler
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String request = bundle.getString("request");
                if (request != null) {
                   // toast("识别完成");
                }
            }
        };

        // 接收从 MainActivity 传递过来的图片路径
        String imagePath = getIntent().getStringExtra("image_path");
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                
                // 显示图片
                imageView.setImageBitmap(bitmap);

                // 将Bitmap转换为字节数组
                byte[] bitmapBytes = bitmapToByteArray(bitmap);

                // 对字节数组进行Base64编码
                String base64EncodedString = Base64Util.encode(bitmapBytes);

                // 调用身份证识别接口
                TecentHttpUtil.getIdCardDetails(base64EncodedString, new TecentHttpUtil.SimpleCallBack() {
                    @Override
                    public void Succ(String result) {
                        // 处理成功的结果
                        Log.d("IDCardOCR", "身份证识别成功: " + result);
                        
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
                        Log.e("IDCardOCR", "身份证识别失败");
                        runOnUiThread(() -> toast("身份证识别失败"));
                    }
                });
            } else {
                 toast("图片文件不存在");
            }
        } else {
            // 如果没有传图片路径，尝试加载默认图片（测试用，如果需要）
            // toast("未获取到图片路径");
        }
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 压缩一下图片，避免过大，这里用JPEG，质量80
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void toast(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

    public class IDCardResult {
        private Response Response;

        public Response getResponse() {
            return Response;
        }

        public void setResponse(Response Response) {
            this.Response = Response;
        }

        public class Response {
            private String Address;
            private String Birth;
            private String IdNum;
            private String Name;
            private String Nation;
            private String Sex;

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

    public void dataget(String result) {
        try {
            Gson gson = new Gson();
            IDCardResult idCardResult = gson.fromJson(result, IDCardResult.class);

            if (idCardResult != null && idCardResult.getResponse() != null) {
                String address = idCardResult.getResponse().getAddress();
                String birth = idCardResult.getResponse().getBirth();
                String idNum = idCardResult.getResponse().getIdNum();
                String name = idCardResult.getResponse().getName();
                String nation = idCardResult.getResponse().getNation();
                String sex = idCardResult.getResponse().getSex();

                textView1.setText(address);
                textView2.setText(birth);
                textView3.setText(idNum);
                textView4.setText(name);
                textView5.setText(nation);
                textView6.setText(sex);
            }
        } catch (Exception e) {
            e.printStackTrace();
            toast("解析数据失败: " + e.getMessage());
        }
    }
}
