package cn.itcast.core.service;

import cn.itcast.common.utils.HttpClient;
import cn.itcast.core.pojo.log.PayLog;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import org.opensaml.xml.signature.Y;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;
    @Override
    public Map<String, String> createNative(String name) {
         PayLog payLog = (PayLog) redisTemplate.boundHashOps("payLog").get(name);
         String url="https://api.mch.weixin.qq.com/pay/unifiedorder";
        HttpClient httpClient = new HttpClient(url);
        httpClient.setHttps(true);
        //入参
        Map<String,String > param=new HashMap<>();
//        公众账号ID	appid	是	String(32)	wxd678efh567hg6787	微信支付分配的公众账号ID（企业号corpid即为此appId）
        param.put("appid",appid);
//        商户号	mch_id	是	String(32)	1230000109	微信支付分配的商户号
        param.put("mch_id",partner);
//        设备号	device_info	否	String(32)	013467007045764	自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
//        随机字符串	nonce_str	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，长度要求在32位以内。推荐随机数生成算法
        param.put("nonce_str", WXPayUtil.generateNonceStr());

//        商品描述	body	是	String(128)	腾讯充值中心-QQ会员充值
        param.put("body", "品优购项目微信付款测试");
//        商品简单描述，该字段请按照规范传递，具体请见参数规定
//
//        商户订单号	out_trade_no	是	String(32)	20150806125346	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|* 且在同一个商户号下唯一。详见商户订单号
        param.put("out_trade_no", payLog.getOutTradeNo());

//        标价币种	fee_type	否	String(16)	CNY	符合ISO 4217标准的三位字母代码，默认人民币：CNY，详细列表请参见货币类型
//        标价金额	total_fee	是	Int	88	订单总金额，单位为分，详见支付金额
        //param.put("total_fee", String.valueOf(payLog.getTotalFee()));
        param.put("total_fee", "1");
//        终端IP	spbill_create_ip	是	String(64)	123.12.12.123	支持IPV4和IPV6两种格式的IP地址。调用微信支付API的机器IP
        param.put("spbill_create_ip", "127.0.0.1");
//        交易起始时间	time_start	否	String(14)	20091225091010	订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则
//        交易结束时间	time_expire	否	String(14)	20091227091010
//        订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。订单失效时间是针对订单号而言的，由于在请求支付的时候有一个必传参数prepay_id只有两小时的有效期，所以在重入时间超过2小时的时候需要重新请求下单接口获取新的prepay_id。其他详见时间规则
//
//        建议：最短失效时间间隔大于1分钟
//
//        订单优惠标记	goods_tag	否	String(32)	WXG	订单优惠标记，使用代金券或立减优惠功能时需要的参数，说明详见代金券或立减优惠
//        通知地址	notify_url	是	String(256)	http://www.weixin.qq.com/wxpay/pay.php	异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
        param.put("notify_url", "http://itcast.cn");
//        交易类型	trade_type	是	String(16)	JSAPI
        param.put("trade_type", "NATIVE");


        try {
//        签名	sign	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	通过签名算法计算得出的签名值，详见签名生成算法
            String s = WXPayUtil.generateSignedXml(param, partnerkey);
//        签名类型	sign_type	否	String(32)	MD5	签名类型，默认为MD5，支持HMAC-SHA256和MD5。
            httpClient.setXmlParam(s);
            httpClient.post();
            String content = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            map.put("total_fee",String.valueOf(payLog.getTotalFee()));
            map.put("out_trade_no", payLog.getOutTradeNo());
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException(e);
        }
    }
        //调用查询
    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) {
        String url="https://api.mch.weixin.qq.com/pay/orderquery";
        HttpClient httpClient = new HttpClient(url);
        httpClient.setHttps(true);
        //入参
        Map<String,String > param=new HashMap<>();
//        公众账号ID	appid	是	String(32)	wxd678efh567hg6787	微信支付分配的公众账号ID（企业号corpid即为此appId）
        param.put("appid",appid);
//        商户号	mch_id	是	String(32)	1230000109	微信支付分配的商户号
        param.put("mch_id",partner);
//        微信订单号	transaction_id	二选一	String(32)	1009660380201506130728806387	微信的订单号，建议优先使用
//        商户订单号	out_trade_no	String(32)	20150806125346	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。 详见商户订单号
        param.put("out_trade_no", out_trade_no);
//        随机字符串	nonce_str	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	随机字符串，不长于32位。推荐随机数生成算法
        param.put("nonce_str", WXPayUtil.generateNonceStr());

        try {
            //        签名	sign	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	通过签名算法计算得出的签名值，详见签名生成算法
            String s = WXPayUtil.generateSignedXml(param, partnerkey);
//        签名类型	sign_type	否	String(32)	MD5	签名类型，默认为MD5，支持HMAC-SHA256和MD5。
            httpClient.setXmlParam(s);
            httpClient.post();
            String content = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}
