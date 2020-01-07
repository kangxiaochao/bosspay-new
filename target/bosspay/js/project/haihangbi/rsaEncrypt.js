function rsaEncrypt(info){
	setMaxDigits(130); //131 => n的十六进制位数/2+3  
	var rsa_key ="8fa245dd89627d840113fb046c36d6b5e170f4527a078accd70d740a7985b64b8dd4d2ec419454097531279804f6d64870727dfe4014844cc1aa7a07f2b37834323f585e739636ee44eb741c579eabe4ef1cb652839981fd0e96475dd4d89c4e8a670632832e6b35ef77d51c1f7f14bfe8532abc603cedfaf57009ba5c604921";
	var key   = new RSAKeyPair("10001", '', rsa_key); //10001 => e的十六进制  
	info = encryptedString(key, base64encode(info));
	
	return info;
}