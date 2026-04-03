package com.bsfood.recipegenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 营养数据实体类
 */
@TableName("nutrition")
public class Nutrition {
    /**
     * 营养ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 食材ID
     */
    private Long foodId;
    
    /**
     * 热量（大卡/100g）
     */
    private Double calorie;
    
    /**
     * 蛋白质（g/100g）
     */
    private Double protein;
    
    /**
     * 脂肪（g/100g）
     */
    private Double fat;
    
    /**
     * 碳水化合物（g/100g）
     */
    private Double carbohydrate;
    
    /**
     * 维生素含量
     */
    private String vitamin;
    
    /**
     * 矿物质含量
     */
    private String mineral;

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public Double getCalorie() {
        return calorie;
    }

    public void setCalorie(Double calorie) {
        this.calorie = calorie;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

    public Double getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(Double carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public String getVitamin() {
        return vitamin;
    }

    public void setVitamin(String vitamin) {
        this.vitamin = vitamin;
    }

    public String getMineral() {
        return mineral;
    }

    public void setMineral(String mineral) {
        this.mineral = mineral;
    }
}