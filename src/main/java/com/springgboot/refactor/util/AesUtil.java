package com.springgboot.refactor.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

/**
 * AES加解密工具类
 */
@Slf4j
public class AesUtil {
    private static final String DEFAULT_SECRET = "SpringBootRefactorApplication";

    private static final Cipher MY_DECRYPT_CIPHER = createCipher(Cipher.DECRYPT_MODE);

    private static final Cipher MY_ENCRYPT_CIPHER = createCipher(Cipher.ENCRYPT_MODE);

    /**
     * aes加密再经base64处理，进行返回
     *
     * @param content 待做加密处理的字符串
     * @return 加密结果
     */
    public static String aesEncryptAndBase64EncodeProcess(String content) {
        return new String(Base64.getUrlEncoder().encode(Objects.requireNonNull(encrypt(content))));
    }

    /**
     * base64解码，aes解密，返回原字符串
     *
     * @param content base64编码的aes加密字节数组
     * @return 解密结果
     */
    public static String base64DecodeAndAesDecrypt(String content) {
        return new String(Objects.requireNonNull(decrypt(Base64.getUrlDecoder().decode(content))));
    }

    /**
     * AES加密字符串
     *
     * @param content 需要被加密的字符串
     * @return 密文
     */
    private static byte[] encrypt(String content) {
        try {
            return MY_ENCRYPT_CIPHER.doFinal(content.getBytes());
        } catch (Exception e) {
            log.error("encrypt 加密失败 e:", e);
        }
        return null;
    }

    /**
     * 解密AES加密过的字符串
     *
     * @param content AES加密过过的内容
     * @return 明文
     */
    private static byte[] decrypt(byte[] content) {
        try {
            return MY_DECRYPT_CIPHER.doFinal(content);
        } catch (Exception e) {
            log.error("decrypt 解密失败 e:", e);
        }
        return null;
    }

    /**
     * 创建密码
     */
    private static Cipher createCipher(int cipherType) {
        Cipher cipher = null;
        try {
            /*
             * 创建AES的Key生产者
             */
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            /*
             * 利用用户密码作为随机数初始化出
             */
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(AesUtil.DEFAULT_SECRET.getBytes());
            kgen.init(128, random);
            /*
             * 128位的key生产者
             * 加密没关系，SecureRandom是生成安全随机数序列，password.getBytes()是种子，只要种子相同，序列就一样，所以解密只要有password就行
             * 根据用户密码，生成一个密钥
             */
            SecretKey secretKey = kgen.generateKey();
            /*
             * 返回基本编码格式的密钥，如果此密钥不支持编码，则返回 null
             */
            byte[] enCodeFormat = secretKey.getEncoded();
            /*
             * 转换为AES专用密钥
             */
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            /*
             * 创建密码器
             */
            cipher = Cipher.getInstance("AES");
            cipher.init(cipherType, key);
        } catch (Exception e) {
            log.error(
                    "AesUtil创建密码实体失败, errMsg:{}",
                    e.getMessage()
            );
        }
        return cipher;
    }

    public static void main(String[] args) {
        String aesEncryptAndBase64EncodeProcess = aesEncryptAndBase64EncodeProcess("231");
        System.out.println("加密结果:" + aesEncryptAndBase64EncodeProcess);
        String ss = base64DecodeAndAesDecrypt(aesEncryptAndBase64EncodeProcess);
        System.out.println("解密结果:" + ss);
    }
}
