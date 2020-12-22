package com.fy.baselibrary.utils.security;

import android.support.annotation.NonNull;
import android.util.Base64;

import com.fy.baselibrary.utils.FileUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密工具类
 * Created by fangs on 2017/5/18.
 */
public class EncryptUtils {

	private EncryptUtils() {
        /* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * AES 加密
	 * @param str 待加密字符串
	 * @param password 加密秘钥
	 * @return 加密后字符串
	 */
	public static String aesEncrypt(String str, String password) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(password.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			String strTmp = Base64.encodeToString(cipher.doFinal(str.getBytes()), Base64.DEFAULT);
			return strTmp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * AES 解密
	 * @param str 待解密字符串
	 * @param password 秘钥
	 * @return 解密后字符串
	 */
	public static String aesDecrypt(String str, String password) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(password.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			String strTmp = new String(cipher.doFinal(Base64.decode(str, Base64.DEFAULT)));
			return strTmp;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return str;
	}

	public static String replaceBlank(String str) {
		String dest = "";
		if (str!=null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 根据传入的 String 生成MD5
	 * @param s
	 */
	public static String getMD5(String s) {
		char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
//        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		try {
			byte[] btInput = s.getBytes("utf-8");
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	//文件加密的实现方法
	public void encryptFile(@NonNull String filePathName, @NonNull String encryptedFilePathName, String password) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		CipherInputStream cis = null;

		try {
			FileUtils.fileIsExists(encryptedFilePathName);

			fis = new FileInputStream(filePathName);
			fos = new FileOutputStream(encryptedFilePathName);

			//秘钥自动生成
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			Key key = keyGenerator.generateKey();


			byte[] keyValue = key.getEncoded();

			fos.write(keyValue);//记录输入的加密密码的消息摘要

			SecretKeySpec encryptKey = new SecretKeySpec(keyValue, "AES");//加密秘钥

			byte[] ivValue = new byte[16];
			Random random = new Random(System.currentTimeMillis());
			random.nextBytes(ivValue);
			IvParameterSpec iv = new IvParameterSpec(ivValue);//获取系统时间作为IV

			fos.write(password.getBytes());//文件标识符

			fos.write(ivValue);    //记录IV
			Cipher cipher = Cipher.getInstance("AES/CFB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, encryptKey, iv);

			cis = new CipherInputStream(fis, cipher);

			byte[] buffer = new byte[1024];
			int n = 0;
			while ((n = cis.read(buffer)) != -1) {
				fos.write(buffer, 0, n);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != cis) cis.close();
				if (null != fos) fos.close();
				if (null != fis) fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//文件解密的实现代码
	public void decryptedFile(@NonNull String encryptedFilePathName, @NonNull String decryptedFilePathName, String password) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		CipherInputStream cis = null;
		try {
			FileUtils.fileIsExists(decryptedFilePathName);

			fis = new FileInputStream(encryptedFilePathName);
			fos = new FileOutputStream(decryptedFilePathName);

			byte[] fileIdentifier = new byte[15];

			byte[] keyValue = new byte[16];
			fis.read(keyValue);//读记录的文件加密密码的消息摘要
			fis.read(fileIdentifier);
			if (new String(fileIdentifier).equals(password)) {
				SecretKeySpec key = new SecretKeySpec(keyValue, "AES");
				byte[] ivValue = new byte[16];
				fis.read(ivValue);//获取IV值
				IvParameterSpec iv = new IvParameterSpec(ivValue);
				Cipher cipher = Cipher.getInstance("AES/CFB/PKCS5Padding");
				cipher.init(Cipher.DECRYPT_MODE, key, iv);

				cis = new CipherInputStream(fis, cipher);
				byte[] buffer = new byte[1024];
				int n = 0;
				while ((n = cis.read(buffer)) != -1) {
					fos.write(buffer, 0, n);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != cis) cis.close();
				if (null != fos) fos.close();
				if (null != fis) fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
