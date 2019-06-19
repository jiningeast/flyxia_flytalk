/**
 * @author automannn@163.com
 * @time 2019/5/23 11:51
 */
package com.flyxia.flytalk.unipayDomain.sdkconfig;

/*  银联官方提供的sdk包，删去了sm3安全相关的部分
*   集成之后，签名与验签，都是使用它自带的类。  没有使用commonUtils包的签名与验签工具
*
*   可能会出现错误的地方：  UnionPaySDKConfig--> 它会装载相应的参数（去配置文件中）
*   银联公钥与私钥 均以 配置文件的方式给出。 因此与 支付宝和微信逻辑有较大不同
* */