package com.example.chapter03;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class OcrMainActivity extends AppCompatActivity {

    private static final int TAKE_PHOTO = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    
    private Uri imageUri;
    private File outputImage;
    private Button button;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_main);

        button = findViewById(R.id.photo);
        imageView = findViewById(R.id.imageView);

        button.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(OcrMainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(OcrMainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {
                openCamera();
            }
        });
    }

    private void openCamera() {
        Toast.makeText(this, "正在打开相机", Toast.LENGTH_SHORT).show();

        outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageUri = getUriForFile(outputImage);

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
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                // 将拍摄的照片显示出来
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                if (bitmap != null) {
                    imageView.setImageBitmap(rotateIfRequired(bitmap));
                    
                    // 启动新的 Activity 处理图片
                    Intent intent = new Intent(OcrMainActivity.this, ImageProcessing.class);
                    intent.putExtra("image_path", outputImage.getAbsolutePath());
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "处理图片失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

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
                // 权限被授予，可以再次尝试启动
                openCamera();
            } else {
                // 权限被拒绝，向用户显示信息
                Toast.makeText(this, "需要相机权限才能拍照", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
