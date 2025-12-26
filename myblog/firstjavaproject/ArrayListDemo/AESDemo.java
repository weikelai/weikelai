package com.myblog.firstjavaproject.ArrayListDemo;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-11-04
 * @Description: AES算法加密解密实验
 * @Version: 1.0
 */

public class AESDemo {
    // 生成AES密钥（支持128、192、256位）
    public static SecretKey generateKey(int keySize) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keySize); // 128、192、256
        return keyGen.generateKey();
    }

    // 将SecretKey转为Base64字符串
    public static String keyToBase64(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    // 从Base64字符串还原SecretKey
    public static SecretKey keyFromBase64(String base64Key) {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    // 生成随机IV（16字节）
    public static byte[] generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    // AES加密
    public static byte[] encrypt(byte[] plainData, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(plainData);
    }

    // AES解密
    public static byte[] decrypt(byte[] cipherData, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(cipherData);
    }

    // 加密文件 -> 输出密文文件
    public static void encryptFile(String inputPath, String outputPath, SecretKey key, byte[] iv) throws Exception {
        byte[] plainBytes = Files.readAllBytes(Paths.get(inputPath));
        byte[] cipherBytes = encrypt(plainBytes, key, iv);
        Files.write(Paths.get(outputPath), cipherBytes, StandardOpenOption.CREATE);
        System.out.println("文件加密完成：" + outputPath);
    }

    // 解密文件 -> 输出明文文件
    public static void decryptFile(String inputPath, String outputPath, SecretKey key, byte[] iv) throws Exception {
        byte[] cipherBytes = Files.readAllBytes(Paths.get(inputPath));
        byte[] plainBytes = decrypt(cipherBytes, key, iv);
        Files.write(Paths.get(outputPath), plainBytes, StandardOpenOption.CREATE);
        System.out.println("文件解密完成：" + outputPath);
    }

    // 测试主函数
    public static void main(String[] args) {
        try {
            // 路径配置
            String plainFile = "E:\\learn_java\\java\\out\\planText1.txt";
            String cipherFile = "E:\\learn_java\\java\\out\\cipher.txt";
            String decryptFile = "E:\\learn_java\\java\\out\\planText2.txt";

            // 1、生成AES密钥（256位）
            SecretKey key = generateKey(256);
            String keyBase64 = keyToBase64(key);
            System.out.println(" AES密钥(Base64): " + keyBase64);

            // 2、生成随机IV
            byte[] iv = generateIV();
            System.out.println(" IV(Base64): " + Base64.getEncoder().encodeToString(iv));

            // 3、加密文件
            encryptFile(plainFile, cipherFile, key, iv);

            // 4、解密文件
            decryptFile(cipherFile, decryptFile, key, iv);

            // 5、验证
            byte[] original = Files.readAllBytes(Paths.get(plainFile));
            byte[] decrypted = Files.readAllBytes(Paths.get(decryptFile));
            boolean same = java.util.Arrays.equals(original, decrypted);
            System.out.println(" 解密结果与原文是否一致: " + same);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
