package com.flyxia.flytalk.wechatDomain.util;

import com.flyxia.flytalk.util.XMLutil;

import java.beans.XMLDecoder;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/5/13 14:56
 */
public class BuildXmlRequestFactory {
    //根节点的名称
    private String baseNodeName;
    //需要组装的成xml结点的实体类
    private ArrayList<Object> oList;

    public static BuildXmlRequestFactory load(String baseNodeName,ArrayList<Object> oList){
        return new BuildXmlRequestFactory(baseNodeName,oList);
    }

    private BuildXmlRequestFactory(String baseNodeName,ArrayList<Object> oList){
        this.baseNodeName= baseNodeName;
        this.oList = oList;
    }

    public String build(){
        if (baseNodeName==null||oList==null) return "错误!";
        StringBuilder sb = new StringBuilder("");
        sb.append("<"+baseNodeName+">");

        for (Object o:oList) sb.append(XMLutil.bean2xml(o));

        sb.append("</"+baseNodeName+">");

        return sb.toString();
    }


}
