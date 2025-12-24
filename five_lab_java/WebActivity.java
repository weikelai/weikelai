package com.example.chapter03;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebActivity extends AppCompatActivity {

    private ImageView imageView;
    private static final String TAG = "WebActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_web);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button button1 = findViewById(R.id.webbutton1);
        imageView = findViewById(R.id.imageView);

        button1.setOnClickListener(view -> {
            String urlString = "https://httpbin.org/image/png";

            // 方式一：使用第三方库 Glide
            // Glide 会自动处理缓存、线程切换、内存管理等
            loadWithGlide(urlString);

            // 方式二：使用原生 HttpURLConnection
            // 若要测试原生方式，请注释掉上面的 loadWithGlide，取消下面的注释
            //loadWithNative(urlString);
        });
    }

    /**
     * 方式一：使用 Glide 加载图片
     */
    private void loadWithGlide(String url) {
        Glide.with(this)
                .load(url)
                // 仅为了演示网络加载，这里跳过内存和磁盘缓存，实际使用不要跳过
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.mipmap.ic_launcher) // 正在加载时显示的占位图
                .error(R.mipmap.ic_launcher)       // 加载错误时显示的图
                .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Glide 加载失败: ", e);
                        Toast.makeText(WebActivity.this, "Glide 加载失败", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        Toast.makeText(WebActivity.this, "Glide 加载成功", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                })
                .into(imageView);
    }

    /**
     * 方式二：使用原生 API 加载图片
     */
    private void loadWithNative(String urlString) {
        new Thread(() -> {
            final Bitmap bitmap = downloadImage(urlString);
            runOnUiThread(() -> {
                if (bitmap != null) {
                    // 注意：这里去掉了手动 recycle 旧 Bitmap 的逻辑。
                    // 现在的 Glide 或系统会自动管理 ImageView 的 Drawable，手动 recycle 极易导致崩溃。
                    imageView.setImageBitmap(bitmap);
                    Toast.makeText(WebActivity.this, "原生方式加载成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WebActivity.this, "原生方式加载失败，请检查日志", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    // 封装的图片下载方法
    public Bitmap downloadImage(String path) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Android)");
            // 适当增加超时时间
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoInput(true);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = new BufferedInputStream(connection.getInputStream());
                return BitmapFactory.decodeStream(inputStream);
            } else {
                Log.e(TAG, "Server returned code: " + responseCode);
            }
        } catch (Exception e) {
            Log.e(TAG, "Network error", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}
