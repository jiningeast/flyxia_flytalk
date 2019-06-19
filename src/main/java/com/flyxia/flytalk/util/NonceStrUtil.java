package com.flyxia.flytalk.util;


/**
 * @author automannn@163.com
 * @time 2019/5/13 16:01
 */

//随机数生成
public class NonceStrUtil {
  private static final   char[] basechar = new char[]{'d','u','v','w','x','d','e','f','g','h','i',
            'o','p','q','a','b','c','r','s','y','z','j','k','l','m','n'};
  private static int lenth=16;

  //不配置长度的话，默认长度为16
  public static void config(int length){
      if (length<=0){
          System.out.println("参数错误，配置失败!");
          return;
      }
      lenth = length;
  }

  public static String getStr(){
      char[] result =new char[lenth];
      for (int i=0;i<lenth;i++){
          int r = getRandomRoundNum(0,25);
          if (r%2==0)result[i]=(basechar[r]);
          else result[i]=Character.toUpperCase(basechar[r]);
      }
      return  new String(result);
  }

  private static int getRandomRoundNum(int min,int max){
      return (int) Math.floor(Math.random()*(max-min+1)+min);
  }


}
