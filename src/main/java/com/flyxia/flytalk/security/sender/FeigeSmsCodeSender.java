package com.flyxia.flytalk.security.sender;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author automannn@163.com
 * @time 2019/4/28 10:36
 */

public class FeigeSmsCodeSender implements SmsCodeSender {
    private static String FEIGE_ACCOUNT="13404081072";
    private static String FEIGE_PWD="976d312e689e1e874e102419a";
    private static String FEIGE_SIGN_ID="121883";


    @Override
    public void send(String mobile, String code) {

            CloseableHttpClient client = null;
            CloseableHttpResponse response = null;

            try {
                List<BasicNameValuePair> formparams = new ArrayList<>();
                formparams.add(new BasicNameValuePair("Account",FEIGE_ACCOUNT));
                formparams.add(new BasicNameValuePair("Pwd",FEIGE_PWD));
                formparams.add(new BasicNameValuePair("Content","您的验证码是"+code));
                formparams.add(new BasicNameValuePair("Mobile",mobile));
                formparams.add(new BasicNameValuePair("SignId",FEIGE_SIGN_ID));

                HttpPost httpPost =  new HttpPost("http://api.feige.ee/SmsService/Send");
                httpPost.setEntity(new UrlEncodedFormEntity(formparams,"utf-8"));

                client= HttpClients.createDefault();
                response = client.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);

                System.out.println("短信已发送");
                System.out.println(result);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (response!=null){
                    try {
                        response.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (client!= null){
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    }
}
