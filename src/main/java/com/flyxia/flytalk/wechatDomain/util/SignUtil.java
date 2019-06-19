package com.flyxia.flytalk.wechatDomain.util;

import com.automannn.commonUtils.security.MD5;
import com.flyxia.flytalk.alipayDomain.AlipayConstants;
import com.flyxia.flytalk.wechatDomain.WechatPayConstants;

import java.security.MessageDigest;
import java.util.*;

/**
 * @author automannn@163.com
 * @time 2019/5/13 16:39
 */
public class SignUtil {

    //注：signKey为商户平台设置的密钥key
    public static String createWxPaySign(String signKey,SortedMap<String,Object> parameters) {
        StringBuilder sb = new StringBuilder();       // 多线程访问的情况下需要用StringBuffer
        Set es = parameters.keySet();                 // 所有参与传参的key按照accsii排序（升序）
        for (Object set : es) {
            String k = set.toString();
            Object v = parameters.get(k);
            sb.append(k)
                    .append("=")
                    .append(v.toString())
                    .append("&");
        }
        sb.append("key=")
                .append(signKey);
        return str2MD5(sb.toString(), "utf-8").toUpperCase();
    }

    private static String str2MD5(String data, String encode) {
        return MD5.generate(data,false);
    }

    public static boolean isTenpaySign(Map<String, String> map) {
        String characterEncoding="utf-8";
        String charset = "utf-8";
        String signFromAPIResponse = map.get("sign");
        if (signFromAPIResponse == null || signFromAPIResponse.equals("")) {
            System.out.println("API返回的数据签名数据不存在，有可能被第三方篡改!!!");
            return false;
        }
        System.out.println("服务器回包里面的签名是:" + signFromAPIResponse);
        //过滤空 设置 TreeMap
        SortedMap<String,String> packageParams = new TreeMap();

        for (String parameter : map.keySet()) {
            String parameterValue = map.get(parameter);
            String v = "";
            if (null != parameterValue) {
                v = parameterValue.trim();
            }
            packageParams.put(parameter, v);
        }

        StringBuffer sb = new StringBuffer();
        Set es = packageParams.entrySet();
        Iterator it = es.iterator();

        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            if(!"sign".equals(k) && null != v && !"".equals(v)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + WechatPayConstants.PRI_KEY);

        //将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较
        //算出签名
        String resultSign = "";
        String tobesign = sb.toString();

        if (null == charset || "".equals(charset)) {
            resultSign = MD5.generate(tobesign,false).toUpperCase();
        }else{
            try{
                resultSign = MD5.generate(tobesign,false).toUpperCase();
            }catch (Exception e) {
                resultSign = MD5.generate(tobesign,false).toUpperCase();
            }
        }

        String tenpaySign = ((String)packageParams.get("sign")).toUpperCase();
        return tenpaySign.equals(resultSign);
    }

}
