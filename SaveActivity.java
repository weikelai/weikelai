/*1.老师的方法
package com.example.chapter03;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import cn.hutool.crypto.SmUtil;
import  cn.hutool.crypto.symmetric.SM4;


public class SaveActivity extends AppCompatActivity {

    private static final String KEY = "1234567890123456";

    private static final String ALGORITHM_NAME = "SM4";
    private static final String TRANSFORMATION = "SM4";
    private static byte[] iv=new byte[16] ;
    File inputFile,outputFile;
    Button button1,button2,button3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_save);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sp=getSharedPreferences("xr", Context.MODE_PRIVATE);

        button1=findViewById(R.id.savebutton1);
        button2=findViewById(R.id.savebutton2);
        button3=findViewById(R.id.savebutton3);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor=sp.edit();
                String str=encrypt("lgy///////");
                editor.putString("name",str);
                editor.putInt("age",20);
                editor.commit();
            }
        });

        // Context context=this;
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s= sp.getString("name","")
                        +"/"+sp.getInt("age",18);
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }
        });

        String key=Base64.encodeToString(generateKey(),Base64.NO_WRAP);

        File f1=createTextFile(this,"file1.txt");
        File f2=createTextFile(this,"file2.txt");

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str="xr...";
                byte[] data=str.getBytes(StandardCharsets.UTF_8);

                Log.d("xr","processFile....");

                encryptFile(key,f1,f2);
            }
        });
    }

    public String encrypt(String content) {
        try {
            SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(content.getBytes());
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] generateKey() {
        byte[] key = new byte[16]; // 128 bit
        new SecureRandom().nextBytes(key);
        return key;
    }

    public static void encryptFile(String key, File inputFile, File outputFile) {
        // 解码 Base64 密钥
        byte[] keyBytes = Base64.decode(key, Base64.NO_WRAP);

        // 使用 Hutool 的 SM4
        SM4 sm4 = (SM4) SmUtil.sm4(keyBytes);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] fileData = new byte[(int) inputFile.length()];
            fis.read(fileData);

            byte[] encryptedData = sm4.encrypt(fileData);
            fos.write(encryptedData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File createTextFile(Context context, String fileName) {
        try {
            // 直接使用 Context 的 openFileOutput 方法
            FileOutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            out.write("".getBytes());
            out.close();

            File file = new File(context.getFilesDir(), fileName);
            Log.d("FileCreation", "文件创建成功: " + file.getAbsolutePath());
            return file;

        } catch (IOException e) {
            Log.e("FileCreation", "文件创建失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}*/
//我的方法
package com.example.chapter03;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;


public class SaveActivity extends AppCompatActivity {
    Button button1, button2, button3;

    // AES加密密钥（16字节）
    private static final String AES_KEY = "1234567890123456";
    // SM4加密密钥（必须16字节）
    private static final String SM4_KEY = "sm4demo123456789";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_save);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sp = getSharedPreferences("lgy", Context.MODE_PRIVATE);

        // 按钮1: 加密保存到SharedPreferences (使用AES)
        button1 = findViewById(R.id.savebutton1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SharedPreferences.Editor editor = sp.edit();
                    // 加密数据后再存储
                    String encryptedName = encryptAES("lgy");
                    String encryptedAge = encryptAES("20");

                    editor.putString("name", encryptedName);
                    editor.putString("age", encryptedAge);
                    editor.apply();

                    Toast.makeText(getApplicationContext(), "数据已加密保存(AES)", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "加密失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 按钮2: 从SharedPreferences读取并解密 (使用AES)
        button2 = findViewById(R.id.savebutton2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // 读取并解密数据
                    String encryptedName = sp.getString("name", "");
                    String encryptedAge = sp.getString("age", "");

                    String decryptedName = decryptAES(encryptedName);
                    String decryptedAge = decryptAES(encryptedAge);

                    String s = decryptedName + "/" + decryptedAge;
                    Toast.makeText(getApplicationContext(), "解密后的数据(AES): " + s, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "解密失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 按钮3: 使用SM4加密文件（file1.txt -> file2.txt）
        button3 = findViewById(R.id.savebutton3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // 1. 创建file1.txt并写入测试数据
                    FileOutputStream fos1 = openFileOutput("file1.txt", Context.MODE_PRIVATE);
                    String testContent = "Hello SM4! 这是测试数据，将被加密保存到file2.txt";
                    fos1.write(testContent.getBytes(StandardCharsets.UTF_8));
                    fos1.close();

                    // 2. 使用SM4加密file1.txt的内容，保存到file2.txt
                    processFileSM4("file1.txt", "file2.txt");

                    // 3. 读取加密后的内容验证
                    FileInputStream fis2 = openFileInput("file2.txt");
                    byte[] encryptedData = new byte[fis2.available()];
                    fis2.read(encryptedData);
                    fis2.close();

                    Toast.makeText(getApplicationContext(),
                            "SM4加密成功！\n原文件: file1.txt\n加密文件: file2.txt\n加密后大小: " + encryptedData.length + " 字节",
                            Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "SM4加密失败: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // ==================== AES 加密/解密方法 ====================

    /**
     * AES加密方法
     */
    public String encryptAES(String content) {
        try {
            SecretKeySpec key = new SecretKeySpec(AES_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(content.getBytes());
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * AES解密方法
     */
    public String decryptAES(String encryptedContent) {
        try {
            SecretKeySpec key = new SecretKeySpec(AES_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.decode(encryptedContent, Base64.DEFAULT);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ==================== SM4 文件加密方法（使用Hutool） ====================

    /**
     * 使用SM4算法加密文件
     * 将inputFileName的内容加密后保存到outputFileName
     *
     * @param inputFileName 输入文件名（例如：file1.txt）
     * @param outputFileName 输出文件名（例如：file2.txt）
     */
    private void processFileSM4(String inputFileName, String outputFileName) throws Exception {
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            // 创建SM4加密对象（使用Hutool的SmUtil）
            // 方式1：使用指定密钥
            SymmetricCrypto sm4 = SmUtil.sm4(SM4_KEY.getBytes());

            // 方式2：也可以使用完整构造（效果相同）
            // SymmetricCrypto sm4 = new SymmetricCrypto("SM4/ECB/PKCS5Padding", SM4_KEY.getBytes());

            // 方式3：不指定密钥（自动生成）
            // SymmetricCrypto sm4 = SmUtil.sm4();

            // 1. 读取原始文件内容
            fis = openFileInput(inputFileName);
            byte[] originalData = new byte[fis.available()];
            fis.read(originalData);

            // 2. 使用SM4加密
            byte[] encryptedData = sm4.encrypt(originalData);

            // 3. 将加密后的数据写入输出文件
            fos = openFileOutput(outputFileName, Context.MODE_PRIVATE);
            fos.write(encryptedData);

            // 记录日志
            android.util.Log.d("SM4加密", "原始数据大小: " + originalData.length + " 字节");
            android.util.Log.d("SM4加密", "加密后大小: " + encryptedData.length + " 字节");
            android.util.Log.d("SM4加密", "加密成功: " + inputFileName + " -> " + outputFileName);

        } finally {
            // 确保关闭文件流
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 使用SM4算法解密文件（可选，用于验证）
     *
     * @param encryptedFileName 加密文件名
     * @param decryptedFileName 解密后的文件名
     */
    private void decryptFileSM4(String encryptedFileName, String decryptedFileName) throws Exception {
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            // 创建SM4解密对象（密钥必须与加密时相同）
            SymmetricCrypto sm4 = SmUtil.sm4(SM4_KEY.getBytes());

            // 1. 读取加密文件
            fis = openFileInput(encryptedFileName);
            byte[] encryptedData = new byte[fis.available()];
            fis.read(encryptedData);

            // 2. 使用SM4解密
            byte[] decryptedData = sm4.decrypt(encryptedData);

            // 3. 将解密后的数据写入文件
            fos = openFileOutput(decryptedFileName, Context.MODE_PRIVATE);
            fos.write(decryptedData);

            // 验证内容
            String decryptedContent = new String(decryptedData, StandardCharsets.UTF_8);
            android.util.Log.d("SM4解密", "解密后内容: " + decryptedContent);

        } finally {
            if (fis != null) fis.close();
            if (fos != null) fos.close();
        }
    }

    /**
     * SM4字符串加密示例（可选）
     */
    public String encryptStringSM4(String plaintext) {
        SymmetricCrypto sm4 = SmUtil.sm4(SM4_KEY.getBytes());
        return sm4.encryptHex(plaintext); // 返回16进制字符串
    }

    /**
     * SM4字符串解密示例（可选）
     */
    public String decryptStringSM4(String ciphertext) {
        SymmetricCrypto sm4 = SmUtil.sm4(SM4_KEY.getBytes());
        return sm4.decryptStr(ciphertext);
    }
}