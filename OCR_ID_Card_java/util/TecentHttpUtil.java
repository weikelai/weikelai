package com.example.chapter03.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TecentHttpUtil {
    private static final String TAG = "TecentHttpUtil";
    // 请替换为您自己的 SecretId 和 SecretKey
    private static final String SECRET_ID = "YOUR_SECRET_ID";
    private static final String SECRET_KEY = "YOUR_SECRET_KEY";
    
    private static final String HOST = "ocr.tencentcloudapi.com";
    private static final String ACTION = "IDCardOCR";
    private static final String VERSION = "2018-11-19";
    private static final String REGION = "ap-beijing";
    private static final String SERVICE = "ocr";

    public interface SimpleCallBack {
        void Succ(String result);
        void error();
    }

    public static void getIdCardDetails(String base64Image, final SimpleCallBack callBack) {
        new Thread(() -> {
            try {
                // 1. 构建请求参数
                JSONObject params = new JSONObject();
                params.put("ImageBase64", base64Image);
                // "CardSide": "FRONT" // 默认是FRONT，也可以指定
                
                String payload = params.toString();

                // 2. 计算签名
                String[] auth = calculateSignature(payload);
                String timestamp = auth[0];
                String authorization = auth[1];

                // 3. 发送请求
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(payload, MediaType.parse("application/json; charset=utf-8"));

                Request request = new Request.Builder()
                        .url("https://" + HOST)
                        .addHeader("Host", HOST)
                        .addHeader("X-TC-Action", ACTION)
                        .addHeader("X-TC-Version", VERSION)
                        .addHeader("X-TC-Timestamp", timestamp)
                        .addHeader("X-TC-Region", REGION)
                        .addHeader("Authorization", authorization)
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Request failed", e);
                        new Handler(Looper.getMainLooper()).post(callBack::error);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String result = response.body().string();
                        Log.d(TAG, "Response: " + result);
                        if (response.isSuccessful()) {
                            new Handler(Looper.getMainLooper()).post(() -> callBack.Succ(result));
                        } else {
                            new Handler(Looper.getMainLooper()).post(callBack::error);
                        }
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Exception", e);
                new Handler(Looper.getMainLooper()).post(callBack::error);
            }
        }).start();
    }

    private static String[] calculateSignature(String payload) throws Exception {
        // 1. 拼接规范请求串
        String httpRequestMethod = "POST";
        String canonicalUri = "/";
        String canonicalQueryString = "";
        String canonicalHeaders = "content-type:application/json; charset=utf-8\n" + "host:" + HOST + "\n";
        String signedHeaders = "content-type;host";
        String hashedRequestPayload = sha256Hex(payload);
        String canonicalRequest = httpRequestMethod + "\n" + canonicalUri + "\n" + canonicalQueryString + "\n"
                + canonicalHeaders + "\n" + signedHeaders + "\n" + hashedRequestPayload;

        // 2. 拼接待签名字符串
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime() / 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateStr = sdf.format(date);
        String credentialScope = dateStr + "/" + SERVICE + "/" + "tc3_request";
        String hashedCanonicalRequest = sha256Hex(canonicalRequest);
        String stringToSign = "TC3-HMAC-SHA256" + "\n" + timestamp + "\n" + credentialScope + "\n" + hashedCanonicalRequest;

        // 3. 计算签名
        byte[] secretDate = hmac256(("TC3" + SECRET_KEY).getBytes(StandardCharsets.UTF_8), dateStr);
        byte[] secretService = hmac256(secretDate, SERVICE);
        byte[] secretSigning = hmac256(secretService, "tc3_request");
        String signature = bytesToHex(hmac256(secretSigning, stringToSign));

        // 4. 拼接 Authorization
        String authorization = "TC3-HMAC-SHA256 " + "Credential=" + SECRET_ID + "/" + credentialScope + ", "
                + "SignedHeaders=" + signedHeaders + ", " + "Signature=" + signature;

        return new String[]{timestamp, authorization};
    }

    private static byte[] hmac256(byte[] key, String msg) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
        mac.init(secretKeySpec);
        return mac.doFinal(msg.getBytes(StandardCharsets.UTF_8));
    }

    private static String sha256Hex(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(d);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
