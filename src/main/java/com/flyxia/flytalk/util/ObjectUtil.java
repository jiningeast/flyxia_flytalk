package com.flyxia.flytalk.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * @author automannn@163.com
 * @time 2019/4/27 10:19
 */
public final class ObjectUtil {

    //属性都存在
    public static boolean isNotBlank(Object o) {
        Field[] fields  = o.getClass().getFields();
        for (int i=0;i<fields.length;i++){
            Field f =fields[i];
            f.setAccessible(true);
            try {
                Object o1=f.get(o);
            } catch (IllegalAccessException e) {
                return  false;
            }
        }
        return true;
    }

    //将属性封装成对象
    public static Object build(Class var1, Map map){
        Field[] fs=var1.getFields();
        Object target=null;
        try {
            target=var1.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        for (int i=0;i<fs.length;i++){
                Field f =fs[i];
                f.setAccessible(true);
                String fname= f.getName();
                Object fvalue=null;

                if(((fvalue=map.get(fname))!=null)){
                    try {
                        f.set(target,fvalue);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        }

        return  target;
    }
}
