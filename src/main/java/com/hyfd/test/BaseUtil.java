package com.hyfd.test;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;

public class BaseUtil {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String str = "[0xe6][0x93][0x8d][0xe4][0xbd][0x9c][0xe6][0x88][0x90][0xe5][0x8a][0x9f]";
        str = str.replaceAll("\\[0x", "").replaceAll("\\]", "");
//    byte[] helloBytes = "这".getBytes();
//    System.out.println(helloBytes);
//    String helloHex = DatatypeConverter.printHexBinary(helloBytes);
//    String  ss = "0xE8BF99";
        System.out.printf("Hello hex: 0x%s\n", str);
//convert hex-encoded string back to original string
        byte[] decodedHex = DatatypeConverter.parseHexBinary(str);
        String decodedString = new String(decodedHex, "UTF-8");
        System.out.printf("Hello decoded : %s\n", decodedString);
    }
}

