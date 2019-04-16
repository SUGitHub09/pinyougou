package vo;

import cn.itcast.core.pojo.good.Goods;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class OrderVo implements Serializable {
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    private String goodsName;

    private BigDecimal price;

    private Integer goodsNum;

    private BigDecimal payment;

    /**
     * 订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端
     */
    private String sourceType;

    /**
     * 订单创建时间
     */
    private Date createTime;

    private String status;
/*
* 时间范围
*
* */
    private String date;
    /*
    * 订单日期
    * */
    private String day;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
	private String[] names;
    private String[] dates;
    private List<List<Map>> mapList;
    private List<Map> series;
    private Object[] moneys;
    private List<Object> nums;

    public List<Object> getNums() {
        return nums;
    }

    public void setNums(List<Object> nums) {
        this.nums = nums;
    }

    public Object[] getMoneys() {
        return moneys;
    }

    public void setMoneys(Object[] moneys) {
        this.moneys = moneys;
    }

    public List<Map> getSeries() {
        return series;
    }

    public void setSeries(List<Map> series) {
        this.series = series;
    }

    public List<List<Map>> getMapList() {
        return mapList;
    }

    public void setMapList(List<List<Map>> mapList) {
        this.mapList = mapList;
    }

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public String[] getDates() {
        return dates;
    }

    public void setDates(String[] dates) {
        this.dates = dates;
    }
}
