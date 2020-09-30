package cn.turing.firecontrol.server.util;

import java.security.interfaces.RSAPublicKey;

public class password {
	public static void main(String[] args) throws Exception {
		//公钥字符串
		String publicKeyReturn = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCpI+jEB9SGDTrC2RdOaIh7foRMjEsjnB0x8J22l3MCHQGKbLybd5kZoFgJHQHsqPWGeQlwljjwIyxOE9Qgvfch2659ZH7IqBj1hsvNfgR5pcGFF8BI/pddOtPPOC/P3KUQJJu5bP4q2tZM1lH7BVdokX+mds42jnmP6EKlheE/+QIDAQAB";
		//还原公钥
		RSAPublicKey pubKey = RSAUtils.restorePublicKey(Base64Utils.decode(publicKeyReturn));
		//mi 为加密后的密文 654321 为明文密码 
        String mi = RSAUtils.encryptByPublicKey("password", pubKey);
        System.out.println(mi);

	}
}
