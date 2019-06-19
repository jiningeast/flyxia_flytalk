package com.flyxia.flytalk.alipayDomain.dto;

import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/5/10 14:06
 */
public class Merchandise {
    //订单号
    private String out_trade_no;
    //产品代码
    private String product_code;
    //总金额
    private Double total_amount;
    //商品名称
    private String subject;
    //商品描述
    private String body;
    //回调参数
    private String passback_params;
    //扩展业务参数
    private Map extend_params;

    public Merchandise() {
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public Double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Double total_amount) {
        this.total_amount = total_amount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPassback_params() {
        return passback_params;
    }

    public void setPassback_params(String passback_params) {
        this.passback_params = passback_params;
    }

    public Map getExtend_params() {
        return extend_params;
    }

    public void setExtend_params(Map extend_params) {
        this.extend_params = extend_params;
    }
}
