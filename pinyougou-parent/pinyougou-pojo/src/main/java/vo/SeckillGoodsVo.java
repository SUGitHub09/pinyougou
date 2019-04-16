package vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SeckillGoodsVo implements Serializable {
    private Long id;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 标题
     */
    private String title;


    @Override
    public String toString() {
        return "seckillGoodsVo{" +
                "id=" + id +
                ", goodsName=" + goodsName +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", costPrice=" + costPrice +
                ", sellerId='" + sellerId + '\'' +
                ", status='" + status + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**

     * 原价格
     */
    private BigDecimal price;

    /**
     * 秒杀价格
     */
    private BigDecimal costPrice;

    /**
     * 商家ID
     */
    private String sellerId;


    /**
     * 审核状态
     */
    private String status;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;
}
