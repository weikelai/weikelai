package com.example.chapter03;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class OcrMainActivity extends AppCompatActivity {

    private static final String TAG = "OcrMainActivity";
    private static final int TAKE_PHOTO = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_GALLERY_PERMISSION = 101;
    private static final int PICK_IMAGE = 2;
    
    private Uri imageUri;
    private File outputImage;
    private Button button;
    private Button uploadButton;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_main);

        Log.d(TAG, "onCreate");

        button = findViewById(R.id.photo);
        uploadButton = findViewById(R.id.upload);
        imageView = findViewById(R.id.imageView);

        // 确保按钮在最上层，可点击
        button.bringToFront();
        uploadButton.bringToFront();

        // 点击拍照按钮
        button.setOnClickListener(v -> {
            Log.d(TAG, "拍照按钮被点击");
            // 打点日志，确认点击事件有响应
            Toast.makeText(OcrMainActivity.this, "准备打开相机...", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(OcrMainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(OcrMainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {
                openCamera();
            }
        });

        // 上传图片按钮
        uploadButton.setOnClickListener(v -> {
            Log.d(TAG, "上传按钮被点击");
            Toast.makeText(OcrMainActivity.this, "选择一张身份证照片...", Toast.LENGTH_SHORT).show();
            requestGalleryPermissionAndOpen();
        });
    }

    /**
     * 打开相机进行拍照
     * 创建File对象，用于存储拍照后的图片
     */
    private void openCamera() {
        Log.d(TAG, "openCamera()");
        Toast.makeText(this, "正在打开相机", Toast.LENGTH_SHORT).show();

        // 创建File对象，用于存储拍照后的图片
        outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 将File对象转换为Uri对象
        imageUri = getUriForFile(outputImage);

        // 启动相机程序
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivityForResult(intent, TAKE_PHOTO);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "无法启动相机: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getUriForFile(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(this, "com.example.chapter03.fileprovider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult requestCode=" + requestCode + " resultCode=" + resultCode);
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                // 将拍摄的照片显示出来
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                if (bitmap != null) {
                    // 根据图片的EXIF信息调整图片的方向
                    imageView.setImageBitmap(rotateIfRequired(bitmap));
                    
                    // 图片传递：将图片的绝对路径通过Intent传递给ImageProcessing活动
                    Intent intent = new Intent(OcrMainActivity.this, ImageProcessing.class);
                    intent.putExtra("image_path", outputImage.getAbsolutePath());
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "处理图片失败", Toast.LENGTH_SHORT).show();
            }
        }
        // 处理从相册选择的图片
        else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedUri = data.getData();
            if (selectedUri != null) {
                try {
                    String savedPath = copyUriToCache(selectedUri);
                    if (savedPath != null) {
                        // 将选中图片显示在预览
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.fromFile(new File(savedPath))));
                        imageView.setImageBitmap(bitmap);

                        Log.d(TAG, "相册图片已保存到缓存: " + savedPath);
                        Intent intent = new Intent(OcrMainActivity.this, ImageProcessing.class);
                        intent.putExtra("image_path", savedPath);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "复制图片失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "处理相册图片失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 根据图片的EXIF信息调整图片的方向
     */
    private Bitmap rotateIfRequired(Bitmap bitmap) {
        try {
            ExifInterface exif = new ExifInterface(outputImage.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            return rotateBitmap(bitmap, orientation);
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    /**
     * 根据方向信息旋转图片
     */
    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，可以再次尝试启动相机
                Toast.makeText(this, "相机权限已授予，即将打开相机", Toast.LENGTH_SHORT).show();
                openCamera();
            } else {
                // 权限被拒绝，向用户显示信息，并引导前往设置页开启权限
                Toast.makeText(this, "需要相机权限才能拍照，请在系统设置中为本应用开启相机权限", Toast.LENGTH_LONG).show();

                // 如果用户勾选了“不再询问”，直接跳转到应用设置页
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    openAppSettings();
                }
            }
        } else if (requestCode == REQUEST_GALLERY_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "存储/媒体读取权限已授予，即将打开相册", Toast.LENGTH_SHORT).show();
                openGallery();
            } else {
                Toast.makeText(this, "需要存储/媒体读取权限才能选择图片，请在系统设置中开启", Toast.LENGTH_LONG).show();
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, getReadPermissionName())) {
                    openAppSettings();
                }
            }
        }
    }

    /**
     * 申请相册读取权限并打开相册
     */
    private void requestGalleryPermissionAndOpen() {
        String readPerm = getReadPermissionName();
        if (ContextCompat.checkSelfPermission(this, readPerm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{readPerm}, REQUEST_GALLERY_PERMISSION);
        } else {
            openGallery();
        }
    }

    /**
     * Android 13+ 使用 READ_MEDIA_IMAGES，其余使用 READ_EXTERNAL_STORAGE
     */
    private String getReadPermissionName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            return Manifest.permission.READ_EXTERNAL_STORAGE;
        }
    }

    /**
     * 打开相册选择图片
     */
    private void openGallery() {
        Log.d(TAG, "openGallery()");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "选择身份证照片"), PICK_IMAGE);
    }

    /**
     * 将选中的Uri复制到应用缓存目录，返回文件路径
     */
    private String copyUriToCache(Uri uri) {
        ContentResolver resolver = getContentResolver();
        String fileName = getFileName(uri);
        if (fileName == null || fileName.isEmpty()) {
            fileName = "picked_" + UUID.randomUUID() + ".jpg";
        }
        File dest = new File(getExternalCacheDir(), fileName);

        try (InputStream in = resolver.openInputStream(uri);
             OutputStream out = new FileOutputStream(dest)) {
            if (in == null) {
                Log.e(TAG, "InputStream is null for uri: " + uri);
                return null;
            }
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            return dest.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "复制Uri到缓存失败: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取文件名
     */
    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    /**
     * 打开应用详情设置页，方便用户手动开启权限
     */
    private void openAppSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
