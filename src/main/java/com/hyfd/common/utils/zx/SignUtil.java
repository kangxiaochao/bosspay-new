package com.hyfd.common.utils.zx;

import org.apache.commons.codec.binary.Base64;

import java.util.Map;



/**
 * @author zeming.fan@swiftpass.cn
 *
 */
public class SignUtil {
    //private final static Logger log = LoggerFactory.getLogger(SignUtil.class);

    public static String sign(String preStr, String signType, String mchPrivateKey) throws Exception {
		RSAUtil.SignatureSuite suite = null;
		if ("RSA_1_1".equals(signType)) {
			suite = RSAUtil.SignatureSuite.SHA1;
		} else if ("RSA_1_256".equals(signType)) {
			suite = RSAUtil.SignatureSuite.SHA256;
		} else {
			throw new Exception("不支持的签名方式");
		}
        byte[] signBuf = RSAUtil.sign(suite, preStr.getBytes("UTF8"),
                mchPrivateKey);
        return new String(Base64.encodeBase64(signBuf), "UTF8");
    }
}
