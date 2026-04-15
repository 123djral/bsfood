package com.bsfood.recipegenerator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bsfood.recipegenerator.entity.UserCollection;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户收藏Mapper
 */
@Mapper
public interface UserCollectionMapper extends BaseMapper<UserCollection> {
}
