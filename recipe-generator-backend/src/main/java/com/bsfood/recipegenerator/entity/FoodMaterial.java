package com.bsfood.recipegenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * 食材实体类
 */
@TableName("food_material")
public class FoodMaterial {
    /**
     * 食材ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 食材名称
     */
    private String name;
    
    /**
     * 食材类别
     */
    private String type;
    
    /**
     * 食材数量
     */
    private Double quantity;
    
    /**
     * 保质期（天）
     */
    private Integer shelfLife;
    
    /**
     * 营养数据ID
     */
    private Long nutritionId;
    
    /**
     * 创建时间
     */
    private Date createTime;

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Integer getShelfLife() {
        return shelfLife;
    }

    public void setShelfLife(Integer shelfLife) {
        this.shelfLife = shelfLife;
    }

    public Long getNutritionId() {
        return nutritionId;
    }

    public void setNutritionId(Long nutritionId) {
        this.nutritionId = nutritionId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}