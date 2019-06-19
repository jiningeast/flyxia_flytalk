package com.flyxia.flytalk.util;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/5/13 14:36
 */
public class XMLutil {

    //xml转换成 map
    public static Map doXMLParse(String strxml) throws JDOMException, IOException {
        strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");

        if(null == strxml || "".equals(strxml)) {
            return null;
        }

        Map m = new HashMap();

        InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(in);
        Element root = doc.getRootElement();
        List list = root.getChildren();
        Iterator it = list.iterator();
        while(it.hasNext()) {
            Element e = (Element) it.next();
            String k = e.getName();
            String v = "";
            List children = e.getChildren();
            if(children.isEmpty()) {
                v = e.getTextNormalize();
            } else {
                v = getChildrenText(children);
            }

            m.put(k, v);
        }

        //关闭流
        in.close();
        return m;
    }

    private static String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if(!children.isEmpty()) {
            Iterator it = children.iterator();
            while(it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List list = e.getChildren();
                sb.append("<" + name + ">");
                if(!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }

        return sb.toString();
    }



    //xml转实体,分为自定义实体，与map的方式
    public static String bean2xml(Object o){
        if (o instanceof Map) return Map2Bean(o);

        Field[] fields = o.getClass().getFields();
        String[] nodeStringName = new String[fields.length];
        String[] nodeStringValue = new String[fields.length];
        for (int i=0;i<fields.length;i++){
            try {
                fields[i].setAccessible(true);
                //空的属性自动跳过
                if (fields[i].get(o)==null) continue;

                nodeStringName[i]= fields[i].getName();
                nodeStringValue[i]= (String) fields[i].get(o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return buildXML(nodeStringName,nodeStringValue);
    }

    //map形式的xml封装
    private static String Map2Bean(Object o) {
        StringBuilder sb = new StringBuilder("");
        Map<String,String> map = (Map<String,String>) o;
        for (Map.Entry e: map.entrySet()) sb.append(a((String) e.getKey(),(String) e.getValue()));
        return sb.toString();
    }

    //构建 xml的实际动作
    private static String buildXML(String[] nodeNames,String[] nodeValues){
        if (nodeNames.length!=nodeValues.length) return "错误!";
        StringBuilder sb = new StringBuilder("");
        for (int i=0;i<nodeNames.length;i++) sb.append(a(nodeNames[i],nodeValues[i]));
        return sb.toString();
    }

    //快捷组装
    private static String a(String k,String v){
        return "<"+k+">"+v+"</"+k+">";
    }
}
