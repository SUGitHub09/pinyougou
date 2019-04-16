package vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;


public class OrderVo implements Serializable {
    private Long goodsId;
    //商品名称
    private String goodsName;
    //商品数量
    private Integer num;
    //销售额
    private BigDecimal paymentVolume;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public BigDecimal getPaymentVolume() {
        return paymentVolume;
    }

    public void setPaymentVolume(BigDecimal paymentVolume) {
        this.paymentVolume = paymentVolume;
    }

    @Override
    public String toString() {
        return "OrderVo{" +
                "goodsId=" + goodsId +
                ", goodsName='" + goodsName + '\'' +
                ", num=" + num +
                ", paymentVolume=" + paymentVolume +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderVo orderVo = (OrderVo) o;
        return Objects.equals(goodsId, orderVo.goodsId) &&
                Objects.equals(goodsName, orderVo.goodsName) &&
                Objects.equals(num, orderVo.num) &&
                Objects.equals(paymentVolume, orderVo.paymentVolume);
    }

    @Override
    public int hashCode() {

        return Objects.hash(goodsId, goodsName, num, paymentVolume);
    }
}
