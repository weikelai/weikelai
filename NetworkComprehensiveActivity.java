package com.example.chapter03;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络数据传输综合演示Activity
 * 功能包括：
 * 1. 文字请求（网页请求）- 原生HttpURLConnection和第三方OkHttp
 * 2. 图片请求 - 原生HttpURLConnection和第三方Glide
 * 3. 流媒体（视频播放）- 原生MediaPlayer和第三方ExoPlayer
 */
public class NetworkComprehensiveActivity extends AppCompatActivity {
    private static final String TAG = "NetworkComprehensive";

    // UI组件
    private EditText etTextUrl, etImageUrl, etVideoUrl;
    private TextView tvTextResult, tvStatus;
    private ImageView ivImageResult;
    private VideoView vvVideoNative;
    private PlayerView pvVideoExoPlayer;
    private Button btnTextNative, btnTextOkHttp;
    private Button btnImageNative, btnImageGlide;
    private Button btnVideoNative, btnVideoExoPlayer;

    // 视频播放器
    private ExoPlayer exoPlayer;
    private MediaPlayer mediaPlayer;

    // 默认测试URL
    private static final String DEFAULT_TEXT_URL = "https://httpbin.org/get";
    private static final String DEFAULT_IMAGE_URL = "https://httpbin.org/image/png";
    private static final String DEFAULT_VIDEO_URL = "https://www.w3schools.com/html/mov_bbb.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_comprehensive);

        initViews();
        setupClickListeners();
    }

    /**
     * 初始化视图组件
     */
    private void initViews() {
        // 文字请求相关
        etTextUrl = findViewById(R.id.etTextUrl);
        tvTextResult = findViewById(R.id.tvTextResult);
        btnTextNative = findViewById(R.id.btnTextNative);
        btnTextOkHttp = findViewById(R.id.btnTextOkHttp);

        // 图片请求相关
        etImageUrl = findViewById(R.id.etImageUrl);
        ivImageResult = findViewById(R.id.ivImageResult);
        btnImageNative = findViewById(R.id.btnImageNative);
        btnImageGlide = findViewById(R.id.btnImageGlide);

        // 视频播放相关
        etVideoUrl = findViewById(R.id.etVideoUrl);
        vvVideoNative = findViewById(R.id.vvVideoNative);
        pvVideoExoPlayer = findViewById(R.id.pvVideoExoPlayer);
        btnVideoNative = findViewById(R.id.btnVideoNative);
        btnVideoExoPlayer = findViewById(R.id.btnVideoExoPlayer);

        // 状态提示
        tvStatus = findViewById(R.id.tvStatus);

        // 设置默认URL
        etTextUrl.setText(DEFAULT_TEXT_URL);
        etImageUrl.setText(DEFAULT_IMAGE_URL);
        etVideoUrl.setText(DEFAULT_VIDEO_URL);
    }

    /**
     * 设置点击监听器
     */
    private void setupClickListeners() {
        // ========== 文字请求 ==========
        btnTextNative.setOnClickListener(v -> {
            String url = etTextUrl.getText().toString().trim();
            if (url.isEmpty()) {
                url = DEFAULT_TEXT_URL;
            }
            loadTextWithNative(url);
        });

        btnTextOkHttp.setOnClickListener(v -> {
            String url = etTextUrl.getText().toString().trim();
            if (url.isEmpty()) {
                url = DEFAULT_TEXT_URL;
            }
            loadTextWithOkHttp(url);
        });

        // ========== 图片请求 ==========
        btnImageNative.setOnClickListener(v -> {
            String url = etImageUrl.getText().toString().trim();
            if (url.isEmpty()) {
                url = DEFAULT_IMAGE_URL;
            }
            loadImageWithNative(url);
        });

        btnImageGlide.setOnClickListener(v -> {
            String url = etImageUrl.getText().toString().trim();
            if (url.isEmpty()) {
                url = DEFAULT_IMAGE_URL;
            }
            loadImageWithGlide(url);
        });

        // ========== 视频播放 ==========
        btnVideoNative.setOnClickListener(v -> {
            String url = etVideoUrl.getText().toString().trim();
            if (url.isEmpty()) {
                url = DEFAULT_VIDEO_URL;
            }
            playVideoWithNative(url);
        });

        btnVideoExoPlayer.setOnClickListener(v -> {
            String url = etVideoUrl.getText().toString().trim();
            if (url.isEmpty()) {
                url = DEFAULT_VIDEO_URL;
            }
            playVideoWithExoPlayer(url);
        });
    }

    // ========== 文字请求方法 ==========

    /**
     * 方式一：使用原生HttpURLConnection请求文字内容
     */
    private void loadTextWithNative(String urlString) {
        updateStatus("正在使用原生方式请求文字内容...");
        tvTextResult.setText("正在加载...");

        new Thread(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Android)");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line).append("\n");
                    }

                    final String result = response.toString();
                    runOnUiThread(() -> {
                        tvTextResult.setText(result);
                        updateStatus("原生方式请求成功！");
                        Toast.makeText(this, "原生方式请求成功", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        tvTextResult.setText("请求失败，响应码: " + responseCode);
                        updateStatus("原生方式请求失败");
                        Toast.makeText(this, "请求失败: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "原生方式请求失败", e);
                runOnUiThread(() -> {
                    tvTextResult.setText("错误: " + e.getMessage());
                    updateStatus("原生方式请求失败: " + e.getMessage());
                    Toast.makeText(this, "请求失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    /**
     * 方式二：使用第三方OkHttp请求文字内容
     */
    private void loadTextWithOkHttp(String urlString) {
        updateStatus("正在使用OkHttp请求文字内容...");
        tvTextResult.setText("正在加载...");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "OkHttp请求失败", e);
                runOnUiThread(() -> {
                    tvTextResult.setText("错误: " + e.getMessage());
                    updateStatus("OkHttp请求失败: " + e.getMessage());
                    Toast.makeText(NetworkComprehensiveActivity.this, "请求失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    final String result = response.body().string();
                    runOnUiThread(() -> {
                        tvTextResult.setText(result);
                        updateStatus("OkHttp请求成功！");
                        Toast.makeText(NetworkComprehensiveActivity.this, "OkHttp请求成功", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        tvTextResult.setText("请求失败，响应码: " + response.code());
                        updateStatus("OkHttp请求失败");
                        Toast.makeText(NetworkComprehensiveActivity.this, "请求失败: " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    // ========== 图片请求方法 ==========

    /**
     * 方式一：使用原生HttpURLConnection加载图片
     */
    private void loadImageWithNative(String urlString) {
        updateStatus("正在使用原生方式加载图片...");
        ivImageResult.setImageDrawable(null);

        new Thread(() -> {
            android.graphics.Bitmap bitmap = downloadImage(urlString);
            runOnUiThread(() -> {
                if (bitmap != null) {
                    ivImageResult.setImageBitmap(bitmap);
                    updateStatus("原生方式加载图片成功！");
                    Toast.makeText(this, "原生方式加载成功", Toast.LENGTH_SHORT).show();
                } else {
                    updateStatus("原生方式加载图片失败");
                    Toast.makeText(this, "原生方式加载失败", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    /**
     * 下载图片的辅助方法
     */
    private android.graphics.Bitmap downloadImage(String urlString) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Android)");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoInput(true);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = new BufferedInputStream(connection.getInputStream());
                return android.graphics.BitmapFactory.decodeStream(inputStream);
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

    /**
     * 方式二：使用第三方Glide加载图片
     */
    private void loadImageWithGlide(String url) {
        updateStatus("正在使用Glide加载图片...");
        ivImageResult.setImageDrawable(null);

        Glide.with(this)
                .load(url)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Glide加载失败", e);
                        runOnUiThread(() -> {
                            updateStatus("Glide加载图片失败");
                            Toast.makeText(NetworkComprehensiveActivity.this, "Glide加载失败", Toast.LENGTH_SHORT).show();
                        });
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        runOnUiThread(() -> {
                            updateStatus("Glide加载图片成功！");
                            Toast.makeText(NetworkComprehensiveActivity.this, "Glide加载成功", Toast.LENGTH_SHORT).show();
                        });
                        return false;
                    }
                })
                .into(ivImageResult);
    }

    // ========== 视频播放方法 ==========

    /**
     * 方式一：使用原生MediaPlayer播放视频
     */
    private void playVideoWithNative(String urlString) {
        updateStatus("正在使用原生MediaPlayer播放视频...");

        // 隐藏ExoPlayer，显示VideoView
        pvVideoExoPlayer.setVisibility(View.GONE);
        vvVideoNative.setVisibility(View.VISIBLE);

        // 停止之前的播放
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        try {
            vvVideoNative.setVideoURI(Uri.parse(urlString));
            vvVideoNative.setOnPreparedListener(mp -> {
                updateStatus("原生MediaPlayer准备完成，开始播放");
                Toast.makeText(this, "原生方式播放开始", Toast.LENGTH_SHORT).show();
                mp.start();
            });
            vvVideoNative.setOnErrorListener((mp, what, extra) -> {
                updateStatus("原生MediaPlayer播放错误");
                Toast.makeText(this, "播放错误", Toast.LENGTH_SHORT).show();
                return true;
            });
            vvVideoNative.setOnCompletionListener(mp -> {
                updateStatus("原生MediaPlayer播放完成");
            });
            vvVideoNative.requestFocus();
        } catch (Exception e) {
            Log.e(TAG, "原生方式播放视频失败", e);
            updateStatus("原生方式播放失败: " + e.getMessage());
            Toast.makeText(this, "播放失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 方式二：使用第三方ExoPlayer播放视频
     */
    private void playVideoWithExoPlayer(String urlString) {
        updateStatus("正在使用ExoPlayer播放视频...");

        // 隐藏VideoView，显示ExoPlayer
        vvVideoNative.setVisibility(View.GONE);
        pvVideoExoPlayer.setVisibility(View.VISIBLE);

        // 释放之前的播放器
        if (exoPlayer != null) {
            exoPlayer.release();
        }

        try {
            exoPlayer = new ExoPlayer.Builder(this).build();
            pvVideoExoPlayer.setPlayer(exoPlayer);

            MediaItem mediaItem = MediaItem.fromUri(urlString);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();

            updateStatus("ExoPlayer准备完成，开始播放");
            Toast.makeText(this, "ExoPlayer播放开始", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "ExoPlayer播放视频失败", e);
            updateStatus("ExoPlayer播放失败: " + e.getMessage());
            Toast.makeText(this, "播放失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 更新状态提示
     */
    private void updateStatus(String status) {
        tvStatus.setText(status);
        Log.d(TAG, status);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放ExoPlayer资源
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
        // 释放MediaPlayer资源
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        // 停止VideoView播放
        if (vvVideoNative != null) {
            vvVideoNative.stopPlayback();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停ExoPlayer
        if (exoPlayer != null) {
            exoPlayer.pause();
        }
        // 暂停VideoView
        if (vvVideoNative != null && vvVideoNative.isPlaying()) {
            vvVideoNative.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 恢复播放（如果需要）
    }
}

