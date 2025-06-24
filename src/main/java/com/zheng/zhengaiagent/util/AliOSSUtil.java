package com.zheng.zhengaiagent.util;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * AliOSS 工具类
 */
public class AliOSSUtil {

    private final OSS ossClient;
    private final String bucketName;

    /**
     * 构造函数
     *
     * @param endpoint        阿里云OSS endpoint (e.g. "https://oss-cn-hangzhou.aliyuncs.com")
     * @param accessKeyId     阿里云accessKeyId
     * @param accessKeySecret 阿里云accessKeySecret
     * @param bucketName      存储桶名称
     */
    public AliOSSUtil(String endpoint, String accessKeyId, String accessKeySecret, String bucketName) {
        this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        this.bucketName = bucketName;
    }

    /**
     * 上传文件到OSS
     *
     * @param objectName OSS中的对象名称（包含路径）
     * @param file       要上传的文件
     * @return 文件URL
     */
    public String uploadFile(String objectName, File file) {
        try {
            ossClient.putObject(bucketName, objectName, file);
            return generateUrl(objectName);
        } catch (OSSException | ClientException e) {
            throw new RuntimeException("上传文件到OSS失败", e);
        }
    }

    /**
     * 上传文件流到OSS
     *
     * @param objectName OSS中的对象名称（包含路径）
     * @param inputStream 文件输入流
     * @return 文件URL
     */
    public String uploadFile(String objectName, InputStream inputStream) {
        try {
            ossClient.putObject(bucketName, objectName, inputStream);
            return generateUrl(objectName);
        } catch (OSSException | ClientException e) {
            throw new RuntimeException("上传文件流到OSS失败", e);
        }
    }

    /**
     * 上传字节数组到OSS
     *
     * @param objectName OSS中的对象名称（包含路径）
     * @param bytes      字节数组
     * @return 文件URL
     */
    public String uploadFile(String objectName, byte[] bytes) {
        try {
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
            return generateUrl(objectName);
        } catch (OSSException | ClientException e) {
            throw new RuntimeException("上传字节数组到OSS失败", e);
        }
    }

    /**
     * 下载文件到本地
     *
     * @param objectName OSS中的对象名称（包含路径）
     * @param localFile  本地文件路径
     */
    public void downloadFile(String objectName, String localFile) {
        try {
            ossClient.getObject(new GetObjectRequest(bucketName, objectName), new File(localFile));
        } catch (OSSException | ClientException e) {
            throw new RuntimeException("从OSS下载文件失败", e);
        }
    }

    /**
     * 获取文件输入流
     *
     * @param objectName OSS中的对象名称（包含路径）
     * @return 文件输入流
     */
    public InputStream getFileStream(String objectName) {
        try {
            OSSObject ossObject = ossClient.getObject(bucketName, objectName);
            return ossObject.getObjectContent();
        } catch (OSSException | ClientException e) {
            throw new RuntimeException("获取OSS文件流失败", e);
        }
    }

    /**
     * 删除文件
     *
     * @param objectName OSS中的对象名称（包含路径）
     */
    public void deleteFile(String objectName) {
        try {
            ossClient.deleteObject(bucketName, objectName);
        } catch (OSSException | ClientException e) {
            throw new RuntimeException("删除OSS文件失败", e);
        }
    }

    /**
     * 批量删除文件
     *
     * @param objectNames 要删除的文件名列表
     */
    public void deleteFiles(List<String> objectNames) {
        try {
            DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName);
            request.setKeys(objectNames);
            ossClient.deleteObjects(request);
        } catch (OSSException | ClientException e) {
            throw new RuntimeException("批量删除OSS文件失败", e);
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param objectName OSS中的对象名称（包含路径）
     * @return 是否存在
     */
    public boolean doesObjectExist(String objectName) {
        try {
            return ossClient.doesObjectExist(bucketName, objectName);
        } catch (OSSException | ClientException e) {
            throw new RuntimeException("检查OSS文件是否存在失败", e);
        }
    }

    /**
     * 列举指定前缀的文件
     *
     * @param prefix 前缀
     * @return 文件列表
     */
    public List<String> listFiles(String prefix) {
        List<String> files = new ArrayList<>();
        try {
            ObjectListing objectListing = ossClient.listObjects(bucketName, prefix);
            for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                files.add(objectSummary.getKey());
            }
            return files;
        } catch (OSSException | ClientException e) {
            throw new RuntimeException("列举OSS文件失败", e);
        }
    }

    /**
     * 获取文件的URL
     *
     * @param objectName OSS中的对象名称（包含路径）
     * @return 文件URL
     */
    public String generateUrl(String objectName) {
        try {
            // 设置URL过期时间为10年
            Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
            URL url = ossClient.generatePresignedUrl(bucketName, objectName, expiration);
            return url.toString().split("\\?")[0]; // 去掉签名参数
        } catch (OSSException | ClientException e) {
            throw new RuntimeException("生成OSS文件URL失败", e);
        }
    }

    /**
     * 获取带签名的临时访问URL
     *
     * @param objectName OSS中的对象名称（包含路径）
     * @param expireTime 过期时间（毫秒）
     * @return 带签名的URL
     */
    public String generatePresignedUrl(String objectName, long expireTime) {
        try {
            Date expiration = new Date(System.currentTimeMillis() + expireTime);
            URL url = ossClient.generatePresignedUrl(bucketName, objectName, expiration);
            return url.toString();
        } catch (OSSException | ClientException e) {
            throw new RuntimeException("生成OSS签名URL失败", e);
        }
    }

    /**
     * 关闭OSSClient
     */
    public void shutdown() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}