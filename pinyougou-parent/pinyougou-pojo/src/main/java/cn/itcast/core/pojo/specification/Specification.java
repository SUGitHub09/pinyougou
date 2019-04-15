package cn.itcast.core.pojo.specification;

import java.io.Serializable;

public class Specification implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 名称
     */
    private String specName;

    /*
    * 审核状态
    *
    * */
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName == null ? null : specName.trim();
    }

    @Override
    public String toString() {
        return "Specification{" +
                "id=" + id +
                ", specName='" + specName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}