package com.hyfd.test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HaoBaiOid {

    private static java.security.SecureRandom sr = new java.security.SecureRandom();

    /**
     * Oid 构造子注解。
     */
    public HaoBaiOid() {
        super();
    }

    /**
     * 取得OID。 创建日期：(2001-6-20 20:06:41)
     *
     * @return java.lang.String
     */
    public static String getOid() {
        String sOid = System.currentTimeMillis() + "" + sr.nextInt()
                + sr.nextInt();
        if (sOid.length() < 32) {
            for (int i = sOid.length() + 1; i <= 32; i++) {
                sOid += "0";
            }
        } else if (sOid.length() > 32) {
            sOid = sOid.substring(0, 32);
        }

        sOid = sOid.replace('-', '0');

        return sOid;
    }

    /**
     * 取得16位RIID,根据时间+随机数获取。 创建日期：(2001-6-20 20:06:41)
     *
     * @return java.lang.String
     */
    public static String getRiidBy16() {
        Date dte = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSSS");
        return sdf.format(dte) + (int) ((Math.random() * 9 + 1) * 10);

    }
    /**
     * 取得32位RIID,根据时间+随机数获取。 创建日期：(2001-6-20 20:06:41)
     *
     * @return java.lang.String
     */
    public static String getRiidBy32() {
        Date dte = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSSS");
        return sdf.format(dte) +""+dte.getTime()+""+ (int) ((Math.random() * 9 + 1) * 1000);

    }
    /**
     * 此处插入方法说明。 创建日期：(2001-6-28 17:44:16)
     *
     * @param args java.lang.String[]
     */
    public static void main(String[] args) {
        @SuppressWarnings("unused")
        String sd = new String();
        for (int i = 0; i < 100; i++) {
            System.out.println(getOid() + i);
        }
    }
}
