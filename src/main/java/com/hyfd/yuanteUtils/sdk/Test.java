package com.hyfd.yuanteUtils.sdk;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.ToolHttp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Test
{
    /** 
    * 以行为单位读取文件，常用于读面向行的格式化文件 
    *  
    * @param fileName  
    *            文件名 
    */
    public static String readFileByLines(String fileName)
    {
        File file = new File(fileName);
        String content = new String();
        BufferedReader reader = null;
        try
        {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束  
            while ((tempString = reader.readLine()) != null)
            {
                content += tempString;
            }
            reader.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                } catch (IOException e1)
                {
                }
            }
        }

        return content;
    }

    public static void main(String args[])
    {
        /*
         * 私钥格式的证书为pkcs8
         * linux 下生成证书步骤
         * 1、私钥
         * openssl genrsa -out private.pem 1024
         * 2、公钥
         * openssl rsa -in private.pem  -pubout -out public.pem
         * 3、转换成pkcs8格式证书步骤
         * openssl pkcs8 -topk8 -inform PEM -in private.pem  -outform PEM -nocrypt 
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("serviceId","17156781333");
        jsonObject.put("systemId","0");
        jsonObject.put("dealerId","A100522653");
        jsonObject.put("paymentFlowNumber","20221105090422");
        jsonObject.put("payFee","1000");
        jsonObject.put("operator","haobai");
        jsonObject.put("reUrl","http://114.55.26.121:9001/bosspaybill/status/YuanTe");

        //从文件读取公钥和私钥
        /*		String privateKey = readFileByLines("X:/private.pkcs8");
        		privateKey = privateKey.replace("-----BEGIN PRIVATE KEY-----","");
        		privateKey = privateKey.replace("-----END PRIVATE KEY-----","");
        		String publickey = readFileByLines("X:/public.pem");
        		publickey = publickey.replace("-----BEGIN PUBLIC KEY-----","");
        		publickey = publickey.replace("-----END PUBLIC KEY-----","");*/

        //该用例可用
        String publickey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAozzGgVMuApP5B50v+F7BSgVlelBIczmQ5Op8dP9tCSn2LnvosT22dauxAIp1Q0VneOTX/ZdYxnvMSvjT8Ll6IJqkOC1DdB08l6OwcLoPpFc601LDNChiGaf94Fy70QrgZEu2Jy18fRqYSUBg+sfmGOkzH5pOl+/Fs36UY6FwMn3yvDEh/s7wCA7vHbqABAlLKZpjo/dcw0d2IBBi/pMvwCgjfBwg9pVaVt6/QXxDErcqawCUhnrLEjYY4bBHjmsIr1JAQlwzbdLpceSJMuRC+we3+9Pp4P8034lijkqX6iDEIiRz1F5HzFf8fVlCZeO6tKhV+s4ROvBWY9p239ejaQIDAQAB";
        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCjPMaBUy4Ck/kHnS/4XsFKBWV6UEhzOZDk6nx0/20JKfYue+ixPbZ1q7EAinVDRWd45Nf9l1jGe8xK+NPwuXogmqQ4LUN0HTyXo7Bwug+kVzrTUsM0KGIZp/3gXLvRCuBkS7YnLXx9GphJQGD6x+YY6TMfmk6X78WzfpRjoXAyffK8MSH+zvAIDu8duoAECUspmmOj91zDR3YgEGL+ky/AKCN8HCD2lVpW3r9BfEMStyprAJSGessSNhjhsEeOawivUkBCXDNt0ulx5Iky5EL7B7f70+ng/zTfiWKOSpfqIMQiJHPUXkfMV/x9WUJl47q0qFX6zhE68FZj2nbf16NpAgMBAAECggEAddXwgfjWtNu2oC1zLHrSUynUGFiOBEeg9e7jSt38Cup0oRenYRLofMck7fwFLDuUtyuTU6eGWTmSvugirppls9WOCFN8ZvyX+esa+jUGB59Egroy2ZrxPg4L8YNfvUZ5t00EQAHQ5Mpm9jH4D/BvhLoKwpx0ca+PasRMQCOQx5EGl638gPqNrY90ac3QuqApDWn3OiGLEYP/ki9WtG5BwWtoORSOfI7bxewzPuSSwGMW7CBgaAj6nDv/qQ0BpPYw+/H8h5j6YJJl82ydO433XF1ZrsLjYVi2hLh+bMswNc9qalfvLODRMfq+McH3WKoe48xYjW9qjhTbQL+luPGcgQKBgQDPMnc9svH8Htq7woc7tcR1xD7bX3WtoioMb2ydkvUIqlhls9Uut9zrUF4dJK04QBAhNTwWcjd9dLsw5VbnXRg3Knm2VYv3PlpCQYkljV4R4paMukWVbLgrIgDmoOQbzXViC9rdcEDjohwbBZ4PWyJbWIWOxdSgbnrHEyyoP6QVsQKBgQDJr6JUjXPMU8F6Ko61Vq20e1D0jR8DvTOX0Hl439RiDv4Rnw+bodduz2UGNB8PYn6meFiCKvYAD2pslRJ39uIR/jbsoNC45SeXCY4uUWZsQpYYT40/M/muB0hjIw4HAex8Bj8ledSDLCT3+GYO+QUtesdEU55MndFM0DCT7Ux/OQKBgQDHJdtkay2ZRdK5azTuGDxXcSN1WSBTnq1JvIMqRUjNZmWGgz3hJInrhxiebFUt3q1iLbeuX2OkauFNEvHfeSRAScwoi7r3DnBUhIccl+8Vw7MuWg9tmsy9cHZrNbNqYaV0cq6P7kgAQx0+f7y3R8ITCFd+rfk7plTOKUjgAiNlUQKBgB3sA75dmg93YZ35UGDQ8kZzgSg3A6HvYQcl88+eDYlaxhDhM1SYpziWZluMQgtrRnT2J9NwAj0yDEz9tNa9dv7KY2Wp2i2EOeJrlX+Drhljq5cBvmfhEyrhg7jJi5w4idhAqP+rWn73fqiXXrTb9wuvSn71lOezBklKHdrketPpAoGBAKDysfsamIWlNXpVJeK8UHvDQF0i7DFfdkUnESCTw8Ytx82sFU+zGsugVK+/G18VwcSHUuT2G4AcfA1Olr11dK5ErzB+WUhdacijbEwwBssDYBVKkVwpjY6W5ZJCgyCoQkmt0gN7ZxPpCpgFzuKM5BFe7hFiwTPILKJNxIpvm0Xi";
        System.out.println(privateKey);
        System.out.println(publickey);
        TencentSignature signature = new TencentSignature();
        try
        {
            String sign = signature.rsa256Sign(jsonObject.toString(), privateKey, TencentConstants.CHARSET_GBK);
//            System.out.println(sign);
            String signTemp = URLEncoder.encode(sign, "GBK");
            System.out.println(signTemp);
//            myUrl += "&sign=" + signTemp;
//            boolean result = signature.rsa256CheckContent(jsonObject.toString(), sign,publickey, TencentConstants.CHARSET_GBK);
//            System.out.println(result);
            String url = "http://61.135.223.110:8082/dealerPayService/orderPayAction.do?1=1&sign="+signTemp;
            String ret = ToolHttp.post(false, url, jsonObject.toString(), null);
            System.out.println(ret);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
