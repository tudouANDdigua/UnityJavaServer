package com.digua.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.binary.Base64;

public class EncryptWrapper {
	
	private Logger logger = LoggerFactory.getLogger(EncryptWrapper.class);
	
	public void testEncrypt() {
		//MD5:任意內容（二进制数据，字符串，文件）--->MD5值（）
		String str = "123456pwd";//密码的明文，存到后台的时候，MD5值，
		
		String md5Str = DigestUtils.md5Hex(str);
		logger.info("MD5"+md5Str);
		
		// SHA1加密:
		String sha1Str = DigestUtils.sha1Hex(str);
		logger.info("SHA1加密:"+sha1Str);

		// ShA256
		String sha256Str = DigestUtils.sha256Hex(str);
		logger.info("ShA256加密:"+sha256Str);

		// Base64编码
		String base64Str = Base64.encodeBase64String(str.getBytes());
		logger.info("Base64编码:"+base64Str);

		// Base64解码
		String base64DecodeStr = new String(Base64.decodeBase64(base64Str));
		logger.info("base64解密-->" + base64DecodeStr);
	}
}
