/**
 *
 * Licensed Property to China UnionPay Co., Ltd.
 * 
 * (C) Copyright of China UnionPay Co., Ltd. 2010
 *     All Rights Reserved.
 *
 * 
 * Modification History:
 * =============================================================================
 *   Author         Date          Description
 *   ------------ ---------- ---------------------------------------------------
 *   xshu       2014-05-28      MPI工具类
 * =============================================================================
 */
package com.flyxia.flytalk.unipayDomain.sdkconfig.util;


import com.flyxia.flytalk.unipayDomain.sdkconfig.UnionPaySDKConfig;
import com.flyxia.flytalk.unipayDomain.sdkconfig.UnionSDKConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * 
 * @ClassName SDKUtil
 * @Description acpsdk工具类
 * @date 2016-7-22 下午4:06:18
 */
public class SDKUtil {

	/**
	 * 根据signMethod的值，提供三种计算签名的方法
	 * 
	 * @param data
	 *            待签名数据Map键值对形式
	 * @param encoding
	 *            编码
	 * @return 签名是否成功
	 */
	public static boolean sign(Map<String, String> data, String encoding) {
		
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		String signMethod = data.get(UnionSDKConstants.param_signMethod);
		String version = data.get(UnionSDKConstants.param_version);
		if (!UnionSDKConstants.VERSION_1_0_0.equals(version) && !UnionSDKConstants.VERSION_5_0_1.equals(version) && isEmpty(signMethod)) {
			return false;
		}
		
		if (isEmpty(version)) {
			return false;
		}
		if (UnionSDKConstants.SIGNMETHOD_RSA.equals(signMethod)|| UnionSDKConstants.VERSION_1_0_0.equals(version) || UnionSDKConstants.VERSION_5_0_1.equals(version)) {
			if (UnionSDKConstants.VERSION_5_0_0.equals(version)|| UnionSDKConstants.VERSION_1_0_0.equals(version) || UnionSDKConstants.VERSION_5_0_1.equals(version)) {
				// 设置签名证书序列号
				data.put(UnionSDKConstants.param_certId, CertUtil.getSignCertId());
				// 将Map信息转换成key1=value1&key2=value2的形式
				String stringData = coverMap2String(data);
				byte[] byteSign = null;
				String stringSign = null;
				try {
					// 通过SHA1进行摘要并转16进制
					byte[] signDigest = SecureUtil
							.sha1X16(stringData, encoding);
					byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft(
							CertUtil.getSignCertPrivateKey(), signDigest));
					stringSign = new String(byteSign);
					// 设置签名域值
					data.put(UnionSDKConstants.param_signature, stringSign);
					return true;
				} catch (Exception e) {
					return false;
				}
			} else if (UnionSDKConstants.VERSION_5_1_0.equals(version)) {
				// 设置签名证书序列号
				data.put(UnionSDKConstants.param_certId, CertUtil.getSignCertId());
				// 将Map信息转换成key1=value1&key2=value2的形式
				String stringData = coverMap2String(data);
				byte[] byteSign = null;
				String stringSign = null;
				try {
					// 通过SHA256进行摘要并转16进制
					byte[] signDigest = SecureUtil
							.sha256X16(stringData, encoding);
					byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft256(
							CertUtil.getSignCertPrivateKey(), signDigest));
					stringSign = new String(byteSign);
					// 设置签名域值
					data.put(UnionSDKConstants.param_signature, stringSign);
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		} else if (UnionSDKConstants.SIGNMETHOD_SHA256.equals(signMethod)) {
			return signBySecureKey(data, UnionPaySDKConfig.getConfig()
					.getSecureKey(), encoding);
		}
		return false;
	}

	/**
	 * 通过传入的证书绝对路径和证书密码读取签名证书进行签名并返回签名值<br>
	 * 
	 * @param data
	 *            待签名数据Map键值对形式
	 * @param encoding
	 *            编码
	 * @return 签名值
	 */
	public static boolean signBySecureKey(Map<String, String> data, String secureKey,
			String encoding) {
		
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		if (isEmpty(secureKey)) {
			return false;
		}
		String signMethod = data.get(UnionSDKConstants.param_signMethod);
		if (isEmpty(signMethod)) {
			return false;
		}
		
		if (UnionSDKConstants.SIGNMETHOD_SHA256.equals(signMethod)) {
			// 将Map信息转换成key1=value1&key2=value2的形式
			String stringData = coverMap2String(data);
			String strBeforeSha256 = stringData
					+ UnionSDKConstants.AMPERSAND
					+ SecureUtil.sha256X16Str(secureKey, encoding);
			String strAfterSha256 = SecureUtil.sha256X16Str(strBeforeSha256,
					encoding);
			// 设置签名域值
			data.put(UnionSDKConstants.param_signature, strAfterSha256);
			return true;
		}
		return false;
	}

	/**
	 * 通过传入的签名密钥进行签名并返回签名值<br>
	 * 
	 * @param data
	 *            待签名数据Map键值对形式
	 * @param encoding
	 *            编码
	 * @param certPath
	 *            证书绝对路径
	 * @param certPwd
	 *            证书密码
	 * @return 签名值
	 */
	public static boolean signByCertInfo(Map<String, String> data,
			String certPath, String certPwd, String encoding) {
		
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		if (isEmpty(certPath) || isEmpty(certPwd)) {
			return false;
		}
		String signMethod = data.get(UnionSDKConstants.param_signMethod);
		String version = data.get(UnionSDKConstants.param_version);
		if (!UnionSDKConstants.VERSION_1_0_0.equals(version) && !UnionSDKConstants.VERSION_5_0_1.equals(version) && isEmpty(signMethod)) {
			return false;
		}
		if (isEmpty(version)) {
			return false;
		}
		
		if (UnionSDKConstants.SIGNMETHOD_RSA.equals(signMethod) || UnionSDKConstants.VERSION_1_0_0.equals(version) || UnionSDKConstants.VERSION_5_0_1.equals(version)) {
			if (UnionSDKConstants.VERSION_5_0_0.equals(version) || UnionSDKConstants.VERSION_1_0_0.equals(version) || UnionSDKConstants.VERSION_5_0_1.equals(version)) {
				// 设置签名证书序列号
				data.put(UnionSDKConstants.param_certId, CertUtil.getCertIdByKeyStoreMap(certPath, certPwd));
				// 将Map信息转换成key1=value1&key2=value2的形式
				String stringData = coverMap2String(data);
				byte[] byteSign = null;
				String stringSign = null;
				try {
					// 通过SHA1进行摘要并转16进制
					byte[] signDigest = SecureUtil
							.sha1X16(stringData, encoding);
					byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft(
							CertUtil.getSignCertPrivateKeyByStoreMap(certPath, certPwd), signDigest));
					stringSign = new String(byteSign);
					// 设置签名域值
					data.put(UnionSDKConstants.param_signature, stringSign);
					return true;
				} catch (Exception e) {
					return false;
				}
			} else if (UnionSDKConstants.VERSION_5_1_0.equals(version)) {
				// 设置签名证书序列号
				data.put(UnionSDKConstants.param_certId, CertUtil.getCertIdByKeyStoreMap(certPath, certPwd));
				// 将Map信息转换成key1=value1&key2=value2的形式
				String stringData = coverMap2String(data);
				byte[] byteSign = null;
				String stringSign = null;
				try {
					// 通过SHA256进行摘要并转16进制
					byte[] signDigest = SecureUtil
							.sha256X16(stringData, encoding);
					byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft256(
							CertUtil.getSignCertPrivateKeyByStoreMap(certPath, certPwd), signDigest));
					stringSign = new String(byteSign);
					// 设置签名域值
					data.put(UnionSDKConstants.param_signature, stringSign);
					return true;
				} catch (Exception e) {
					return false;
				}
			}
			
		} 
		return false;
	}
	
	/**
	 * 验证签名
	 * 
	 * @param resData
	 *            返回报文数据
	 * @param encoding
	 *            编码格式
	 * @return
	 */
	public static boolean validateBySecureKey(Map<String, String> resData, String secureKey, String encoding) {
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		String signMethod = resData.get(UnionSDKConstants.param_signMethod);
		if (UnionSDKConstants.SIGNMETHOD_SHA256.equals(signMethod)) {
			// 1.进行SHA256验证
			String stringSign = resData.get(UnionSDKConstants.param_signature);
			// 将Map信息转换成key1=value1&key2=value2的形式
			String stringData = coverMap2String(resData);

			String strBeforeSha256 = stringData
					+ UnionSDKConstants.AMPERSAND
					+ SecureUtil.sha256X16Str(secureKey, encoding);
			String strAfterSha256 = SecureUtil.sha256X16Str(strBeforeSha256,
					encoding);
			return stringSign.equals(strAfterSha256);
		}
		return false;
	}
	
	/**
	 * 验证签名
	 * 
	 * @param resData
	 *            返回报文数据
	 * @param encoding
	 *            编码格式
	 * @return
	 */
	public static boolean validate(Map<String, String> resData, String encoding) {
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		String signMethod = resData.get(UnionSDKConstants.param_signMethod);
		String version = resData.get(UnionSDKConstants.param_version);
		if (UnionSDKConstants.SIGNMETHOD_RSA.equals(signMethod) || UnionSDKConstants.VERSION_1_0_0.equals(version) || UnionSDKConstants.VERSION_5_0_1.equals(version)) {
			// 获取返回报文的版本号
			if (UnionSDKConstants.VERSION_5_0_0.equals(version) || UnionSDKConstants.VERSION_1_0_0.equals(version) || UnionSDKConstants.VERSION_5_0_1.equals(version)) {
				String stringSign = resData.get(UnionSDKConstants.param_signature);

				// 从返回报文中获取certId ，然后去证书静态Map中查询对应验签证书对象
				String certId = resData.get(UnionSDKConstants.param_certId);

				// 将Map信息转换成key1=value1&key2=value2的形式
				String stringData = coverMap2String(resData);

				try {
					// 验证签名需要用银联发给商户的公钥证书.
					return SecureUtil.validateSignBySoft(CertUtil
							.getValidatePublicKey(certId), SecureUtil
							.base64Decode(stringSign.getBytes(encoding)),
							SecureUtil.sha1X16(stringData, encoding));
				} catch (UnsupportedEncodingException e) {

				} catch (Exception e) {

				}
			} else if (UnionSDKConstants.VERSION_5_1_0.equals(version)) {
				// 1.从返回报文中获取公钥信息转换成公钥对象
				String strCert = resData.get(UnionSDKConstants.param_signPubKeyCert);
//				LogUtil.writeLog("验签公钥证书：["+strCert+"]");
				X509Certificate x509Cert = CertUtil.genCertificateByStr(strCert);
				if(x509Cert == null) {

					return false;
				}
				// 2.验证证书链
				if (!CertUtil.verifyCertificate(x509Cert)) {

					return false;
				}
				
				// 3.验签
				String stringSign = resData.get(UnionSDKConstants.param_signature);

				// 将Map信息转换成key1=value1&key2=value2的形式
				String stringData = coverMap2String(resData);

				try {
					// 验证签名需要用银联发给商户的公钥证书.
					boolean result = SecureUtil.validateSignBySoft256(x509Cert
							.getPublicKey(), SecureUtil.base64Decode(stringSign
							.getBytes(encoding)), SecureUtil.sha256X16(
							stringData, encoding));

					return result;
				} catch (UnsupportedEncodingException e) {

				} catch (Exception e) {

				}
			}

		} else if (UnionSDKConstants.SIGNMETHOD_SHA256.equals(signMethod)) {
			// 1.进行SHA256验证
			String stringSign = resData.get(UnionSDKConstants.param_signature);

			// 将Map信息转换成key1=value1&key2=value2的形式
			String stringData = coverMap2String(resData);

			String strBeforeSha256 = stringData
					+ UnionSDKConstants.AMPERSAND
					+ SecureUtil.sha256X16Str(UnionPaySDKConfig.getConfig()
							.getSecureKey(), encoding);
			String strAfterSha256 = SecureUtil.sha256X16Str(strBeforeSha256,
					encoding);
			boolean result =  stringSign.equals(strAfterSha256);

			return result;
		}
		return false;
	}

	/**
	 * 将Map中的数据转换成key1=value1&key2=value2的形式 不包含签名域signature
	 * 
	 * @param data
	 *            待拼接的Map数据
	 * @return 拼接好后的字符串
	 */
	public static String coverMap2String(Map<String, String> data) {
		TreeMap<String, String> tree = new TreeMap<String, String>();
		Iterator<Entry<String, String>> it = data.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			if (UnionSDKConstants.param_signature.equals(en.getKey().trim())) {
				continue;
			}
			tree.put(en.getKey(), en.getValue());
		}
		it = tree.entrySet().iterator();
		StringBuffer sf = new StringBuffer();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			sf.append(en.getKey() + UnionSDKConstants.EQUAL + en.getValue()
					+ UnionSDKConstants.AMPERSAND);
		}
		return sf.substring(0, sf.length() - 1);
	}


	/**
	 * 兼容老方法 将形如key=value&key=value的字符串转换为相应的Map对象
	 * 
	 * @param result
	 * @return
	 */
	public static Map<String, String> coverResultString2Map(String result) {
		return convertResultStringToMap(result);
	}

	/**
	 * 将形如key=value&key=value的字符串转换为相应的Map对象
	 * 
	 * @param result
	 * @return
	 */
	public static Map<String, String> convertResultStringToMap(String result) {
		Map<String, String> map = null;

		if (result != null && !"".equals(result.trim())) {
			if (result.startsWith("{") && result.endsWith("}")) {
				result = result.substring(1, result.length() - 1);
			}
			map = parseQString(result);
		}

		return map;
	}

	
	/**
	 * 解析应答字符串，生成应答要素
	 * 
	 * @param str
	 *            需要解析的字符串
	 * @return 解析的结果map
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, String> parseQString(String str) {

		Map<String, String> map = new HashMap<String, String>();
		int len = str.length();
		StringBuilder temp = new StringBuilder();
		char curChar;
		String key = null;
		boolean isKey = true;
		boolean isOpen = false;//值里有嵌套
		char openName = 0;
		if(len>0){
			for (int i = 0; i < len; i++) {// 遍历整个带解析的字符串
				curChar = str.charAt(i);// 取当前字符
				if (isKey) {// 如果当前生成的是key
					
					if (curChar == '=') {// 如果读取到=分隔符 
						key = temp.toString();
						temp.setLength(0);
						isKey = false;
					} else {
						temp.append(curChar);
					}
				} else  {// 如果当前生成的是value
					if(isOpen){
						if(curChar == openName){
							isOpen = false;
						}
						
					}else{//如果没开启嵌套
						if(curChar == '{'){//如果碰到，就开启嵌套
							isOpen = true;
							openName ='}';
						}
						if(curChar == '['){
							isOpen = true;
							openName =']';
						}
					}
					
					if (curChar == '&' && !isOpen) {// 如果读取到&分割符,同时这个分割符不是值域，这时将map里添加
						putKeyValueToMap(temp, isKey, key, map);
						temp.setLength(0);
						isKey = true;
					} else {
						temp.append(curChar);
					}
				}
				
			}
			putKeyValueToMap(temp, isKey, key, map);
		}
		return map;
	}

	private static void putKeyValueToMap(StringBuilder temp, boolean isKey,
			String key, Map<String, String> map) {
		if (isKey) {
			key = temp.toString();
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, "");
		} else {
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, temp.toString());
		}
	}

	/**
	 * 
	 * 获取应答报文中的加密公钥证书,并存储到本地,并备份原始证书<br>
	 * 更新成功则返回1，无更新返回0，失败异常返回-1。
	 * 
	 * @param resData
	 * @param encoding
	 * @return
	 */
	public static int getEncryptCert(Map<String, String> resData,
			String encoding) {
		String strCert = resData.get(UnionSDKConstants.param_encryptPubKeyCert);
		String certType = resData.get(UnionSDKConstants.param_certType);
		if (isEmpty(strCert) || isEmpty(certType))
			return -1;
		X509Certificate x509Cert = CertUtil.genCertificateByStr(strCert);
		if (UnionSDKConstants.CERTTYPE_01.equals(certType)) {
			// 更新敏感信息加密公钥
			if (!CertUtil.getEncryptCertId().equals(
					x509Cert.getSerialNumber().toString())) {
				// ID不同时进行本地证书更新操作
				String localCertPath = UnionPaySDKConfig.getConfig().getEncryptCertPath();
				String newLocalCertPath = genBackupName(localCertPath);
				// 1.将本地证书进行备份存储
				if (!copyFile(localCertPath, newLocalCertPath))
					return -1;
				// 2.备份成功,进行新证书的存储
				if (!writeFile(localCertPath, strCert, encoding))
					return -1;

				CertUtil.resetEncryptCertPublicKey();
				return 1;
			}else {
				return 0;
			}

		} else if (UnionSDKConstants.CERTTYPE_02.equals(certType)) {
//			// 更新磁道加密公钥
//			if (!CertUtil.getEncryptTrackCertId().equals(
//					x509Cert.getSerialNumber().toString())) {
//				// ID不同时进行本地证书更新操作
//				String localCertPath = UnionPaySDKConfig.getConfig().getEncryptTrackCertPath();
//				String newLocalCertPath = genBackupName(localCertPath);
//				// 1.将本地证书进行备份存储
//				if (!copyFile(localCertPath, newLocalCertPath))
//					return -1;
//				// 2.备份成功,进行新证书的存储
//				if (!writeFile(localCertPath, strCert, encoding))
//					return -1;
//				LogUtil.writeLog("save new encryptPubKeyCert success");
//				CertUtil.resetEncryptTrackCertPublicKey();
//				return 1;
//			}else {
				return 0;
//			}
		}else {
			return -1;
		}
	}
	
	/**
	 * 文件拷贝方法
	 * 
	 * @param srcFile
	 *            源文件
	 * @param destFile
	 *            目标文件
	 * @return
	 * @throws IOException
	 */
	public static boolean copyFile(String srcFile, String destFile) {
		boolean flag = false;
		FileInputStream fin = null;
		FileOutputStream fout = null;
		FileChannel fcin = null;
		FileChannel fcout = null;
		try {
			// 获取源文件和目标文件的输入输出流
			fin = new FileInputStream(srcFile);
			fout = new FileOutputStream(destFile);
			// 获取输入输出通道
			fcin = fin.getChannel();
			fcout = fout.getChannel();
			// 创建缓冲区
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (true) {
				// clear方法重设缓冲区，使它可以接受读入的数据
				buffer.clear();
				// 从输入通道中将数据读到缓冲区
				int r = fcin.read(buffer);
				// read方法返回读取的字节数，可能为零，如果该通道已到达流的末尾，则返回-1
				if (r == -1) {
					flag = true;
					break;
				}
				// flip方法让缓冲区可以将新读入的数据写入另一个通道
				buffer.flip();
				// 从输出通道中将数据写入缓冲区
				fcout.write(buffer);
			}
			fout.flush();
		} catch (IOException e) {

		} finally {
			try {
				if (null != fin)
					fin.close();
				if (null != fout)
					fout.close();
				if (null != fcin)
					fcin.close();
				if (null != fcout)
					fcout.close();
			} catch (IOException ex) {

			}
		}
		return flag;
	}
	
	/**
	 * 写文件方法
	 * 
	 * @param filePath
	 *            文件路径
	 * @param fileContent
	 *            文件内容
	 * @param encoding
	 *            编码
	 * @return
	 */
	public static boolean writeFile(String filePath, String fileContent,
			String encoding) {
		FileOutputStream fout = null;
		FileChannel fcout = null;
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		
		try {
			fout = new FileOutputStream(filePath);
			// 获取输出通道
			fcout = fout.getChannel();
			// 创建缓冲区
			// ByteBuffer buffer = ByteBuffer.allocate(1024);
			ByteBuffer buffer = ByteBuffer.wrap(fileContent.getBytes(encoding));
			fcout.write(buffer);
			fout.flush();
		} catch (FileNotFoundException e) {

			return false;
		} catch (IOException ex) {

			return false;
		} finally {
			try {
				if (null != fout)
					fout.close();
				if (null != fcout)
					fcout.close();
			} catch (IOException ex) {

				return false;
			}
		}
		return true;
	}
	
	/**
	 * 将传入的文件名(xxx)改名 <br>
	 * 结果为： xxx_backup.cer
	 * 
	 * @param fileName
	 * @return
	 */
	public static String genBackupName(String fileName) {
		if (isEmpty(fileName))
			return "";
		int i = fileName.lastIndexOf(UnionSDKConstants.POINT);
		String leftFileName = fileName.substring(0, i);
		String rightFileName = fileName.substring(i + 1);
		String newFileName = leftFileName + "_backup" + UnionSDKConstants.POINT + rightFileName;
		return newFileName;
	}
	

	public static byte[] readFileByNIO(String filePath) {
		FileInputStream in = null;
		FileChannel fc = null;
		ByteBuffer bf = null;
		try {
			in = new FileInputStream(filePath);
			fc = in.getChannel();
			bf = ByteBuffer.allocate((int) fc.size());
			fc.read(bf);
			return bf.array();
		} catch (Exception e) {

			return null;
		} finally {
			try {
				if (null != fc) {
					fc.close();
				}
				if (null != in) {
					in.close();
				}
			} catch (Exception e) {

				return null;
			}
		}
	}
	
	/**
	 * 过滤请求报文中的空字符串或者空字符串
	 * @param contentData
	 * @return
	 */
	public static Map<String, String> filterBlank(Map<String, String> contentData){

		Map<String, String> submitFromData = new HashMap<String, String>();
		Set<String> keyset = contentData.keySet();
		
		for(String key:keyset){
			String value = contentData.get(key);
			if (value != null && !"".equals(value.trim())) {
				// 对value值进行去除前后空处理
				submitFromData.put(key, value.trim());
			}
		}
		return submitFromData;
	}
	
	/**
	 * 解压缩.
	 * 
	 * @param inputByte
	 *            byte[]数组类型的数据
	 * @return 解压缩后的数据
	 * @throws IOException
	 */
	public static byte[] inflater(final byte[] inputByte) throws IOException {
		int compressedDataLength = 0;
		Inflater compresser = new Inflater(false);
		compresser.setInput(inputByte, 0, inputByte.length);
		ByteArrayOutputStream o = new ByteArrayOutputStream(inputByte.length);
		byte[] result = new byte[1024];
		try {
			while (!compresser.finished()) {
				compressedDataLength = compresser.inflate(result);
				if (compressedDataLength == 0) {
					break;
				}
				o.write(result, 0, compressedDataLength);
			}
		} catch (Exception ex) {
			System.err.println("Data format error!\n");
			ex.printStackTrace();
		} finally {
			o.close();
		}
		compresser.end();
		return o.toByteArray();
	}

	/**
	 * 压缩.
	 * 
	 * @param inputByte
	 *            需要解压缩的byte[]数组
	 * @return 压缩后的数据
	 * @throws IOException
	 */
	public static byte[] deflater(final byte[] inputByte) throws IOException {
		int compressedDataLength = 0;
		Deflater compresser = new Deflater();
		compresser.setInput(inputByte);
		compresser.finish();
		ByteArrayOutputStream o = new ByteArrayOutputStream(inputByte.length);
		byte[] result = new byte[1024];
		try {
			while (!compresser.finished()) {
				compressedDataLength = compresser.deflate(result);
				o.write(result, 0, compressedDataLength);
			}
		} finally {
			o.close();
		}
		compresser.end();
		return o.toByteArray();
	}
	
	/**
	 * 判断字符串是否为NULL或空
	 * 
	 * @param s
	 *            待判断的字符串数据
	 * @return 判断结果 true-是 false-否
	 */
	public static boolean isEmpty(String s) {
		return null == s || "".equals(s.trim());
	}
	
}
