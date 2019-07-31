package com.leyou.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "tb_spu")
@Data
public class Spu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long brandId;
    private Long cid1;// 1级类目
    private Long cid2;// 2级类目
    private Long cid3;// 3级类目
    private String title;// 标题
    private String subTitle;// 子标题
    private Boolean saleable;// 是否上架
    @JsonIgnore
    private Boolean valid;// 是否有效，逻辑删除用
    private Date createTime;// 创建时间

    /**
     * 表示不会返回
     */
    @JsonIgnore
    private Date lastUpdateTime;// 最后修改时间
	// 省略getter和setter

    /**
     * 品牌名字  不是数据库字段
     */
    @Transient
    private String brandName;

    /**
     * 商品分类名 不是数据库字段
     */
    @Transient
    private String categoryName;
}