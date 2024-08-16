package com.example.videoweb.utils;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

/**
 * @Author: chailei
 * @Date: 2024/8/9 14:34
 */
public class BlobUtil {

    /**
     * 将 Blob 对象转换为 Base64 编码的字符串。
     *
     * @param blob Blob 对象
     * @return Base64 编码的字符串
     * @throws IOException 如果读取 Blob 数据时出现错误
     */
    public static String convertBlobToBase64String(Blob blob) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = blob.getBinaryStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
        byte[] bytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static Blob bytesToBlob(byte[] bytes) {
        // 将字符串转换为字节数组
        Blob blob = null;
        try {
            // 创建 Blob 对象
            blob = new SerialBlob(bytes);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return blob;
    }

    public static byte[] blobToBytes(Blob blob) {
        try {
            if (blob != null) {
                return blob.getBytes(1, (int) blob.length());
            }
            return new byte[]{};
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
