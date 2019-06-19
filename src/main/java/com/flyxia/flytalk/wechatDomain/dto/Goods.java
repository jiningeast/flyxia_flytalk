package com.flyxia.flytalk.wechatDomain.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author automannn@163.com
 * @time 2019/5/13 10:06
 */
//注意，接收到的 实体，必需转换为 xml的方式传输
public class Goods {

    //必传，商品描述： (格式：app名称-实际商品名称)
    private String body;

    //必传， 商户订单号(商户系统内部)
    private String out_trade_no;

    //必传，总金额（以分为单位的整数）
    private Integer total_fee;

    //必传，终端ip
    private String spbill_create_ip;

    //必传， 交易类型
    private String trade_type;

    //非必传
    private String detail;

    //非必传，商户自带的数据
    private String attach;


    public Goods() {
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public Integer getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(Integer total_fee) {
        this.total_fee = total_fee;
    }

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public void setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }
}
