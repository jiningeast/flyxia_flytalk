package com.flyxia.flytalk.security.sender;


import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.stereotype.Component;

/**
 * @author automannn@163.com
 * @time 2019/4/28 9:16
 */
//@Component
public class AliyunSmsCodeSender implements SmsCodeSender {
    private static final String ACCESS_KEY="LTAIdrVK1hWNceTM";

    private static final String ACCESS_SECRET="poYujSKGFE3CISrQCzxYB3XF8DmuI4";

    @Override
    public void send(String mobile, String code) throws Exception {


        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESS_KEY, ACCESS_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("TemplateCode", "SMS_164279172");
        request.putQueryParameter("TemplateParam", "{\"code\":\""+code+"\"}");
        request.putQueryParameter("SignName", "小飞侠");

        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            throw new Exception(e.getMessage());
        } catch (ClientException e) {
            throw new Exception(e.getMessage());
        }
    }
}
