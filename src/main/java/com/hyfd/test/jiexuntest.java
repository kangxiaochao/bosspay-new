package com.hyfd.test;

import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.ToolMD5;

public class jiexuntest {
    public static void main(String[] args) {
//        String result = "16217085494-2-success|";
//        String[] split = result.split("\\|");
//        String unSplit = split[0];
//        System.out.println("unSplit = " + unSplit);
//        System.out.println(unSplit.split("-")[unSplit.split("-").length-1]);
     //       String re = "success";
        String str = "20.00";
        String substring = str.substring(0, str.lastIndexOf("."));

        System.out.println(substring);


//        try {
//            String name = "jx001";
//            String data = "16217085494-2";
//            String token = "38c15a0719d43d30284abe2021077928";
//            String sign = ToolMD5.encodeMD5Hex(data + token);
//            String url = "http://jiexuntongxun.com/api/charge.aspx?";
//
//            StringBuilder sb = new StringBuilder();
//            sb.append("name=" + name);
//            sb.append("&data=" + data);
//            sb.append("&sign=" + sign);
//
//            String orderurl = url + sb;
//            System.out.println("orderurl = " + orderurl);
//            //发送httppost请求
//            String dataresource = ToolHttp.post(false, orderurl, null, "application/x-www-form-urlencoded");
//            System.out.println("dataresource = " + dataresource);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }
}
