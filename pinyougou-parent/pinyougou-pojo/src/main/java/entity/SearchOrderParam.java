package entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class SearchOrderParam implements Serializable {
    //支付状态  ：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价
    private String status;
    //时间范围  ：1、日订单  2、周订单  3、月订单
    private String timeStatus;
    /**
     * 订单创建时间
     */
    private Date createTime;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeStatus() {
        return timeStatus;
    }

    public void setTimeStatus(String timeStatus) {
        this.timeStatus = timeStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "SearchOrderParam{" +
                "status='" + status + '\'' +
                ", timeStatus='" + timeStatus + '\'' +
                ", createTime=" + createTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchOrderParam that = (SearchOrderParam) o;
        return Objects.equals(status, that.status) &&
                Objects.equals(timeStatus, that.timeStatus) &&
                Objects.equals(createTime, that.createTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(status, timeStatus, createTime);
    }
}