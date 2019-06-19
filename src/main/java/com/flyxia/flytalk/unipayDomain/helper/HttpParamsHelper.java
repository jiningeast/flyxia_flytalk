package com.flyxia.flytalk.unipayDomain.helper;

import com.flyxia.flytalk.unipayDomain.sdkconfig.UnionSDKConstants;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author automannn@163.com
 * @time 2019/5/23 11:44
 */
public class HttpParamsHelper {

    /**
     * 组装请求，返回报文字符串用于显示
     * @param data
     * @return
     */
    public static String genHtmlResult(Map<String, String> data){

        TreeMap<String, String> tree = new TreeMap<String, String>();
        Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> en = it.next();
            tree.put(en.getKey(), en.getValue());
        }
        it = tree.entrySet().iterator();
        StringBuffer sf = new StringBuffer();
        while (it.hasNext()) {
            Map.Entry<String, String> en = it.next();
            String key = en.getKey();
            String value =  en.getValue();
            if("respCode".equals(key)){
                sf.append("<b>"+key + UnionSDKConstants.EQUAL + value+"</br></b>");
            }else
                sf.append(key + UnionSDKConstants.EQUAL + value+"</br>");
        }
        return sf.toString();
    }


}
