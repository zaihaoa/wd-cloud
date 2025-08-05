package com.wd.system.basic.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.model.Id;
import com.wd.common.core.model.PageInfo;
import com.wd.common.core.util.AssertUtil;
import com.wd.common.core.util.I18nUtil;
import com.wd.common.mybatisplus.PageUtil;
import com.wd.common.redis.CacheRegion;
import com.wd.common.redis.RedisKey;
import com.wd.common.redis.util.RedisHelper;
import com.wd.system.basic.convert.UserConverter;
import com.wd.system.basic.domain.dto.UserAddDTO;
import com.wd.system.basic.domain.dto.UserPageDTO;
import com.wd.system.basic.domain.dto.UserPageParamDTO;
import com.wd.system.basic.domain.dto.UserUpdateDTO;
import com.wd.system.basic.domain.entity.User;
import com.wd.system.basic.domain.vo.UserPageVO;
import com.wd.system.basic.mapper.UserMapper;
import com.wd.system.basic.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserConverter userConverter;
    @Resource
    private RedisHelper redisHelper;

    @Override
    public PageInfo<UserPageVO> pageList(UserPageParamDTO pageParam) {

        int count = baseMapper.pageCount(pageParam);
        if (count <= 0) {
            return PageUtil.buildPageInfo(pageParam);
        }

        List<UserPageDTO> dbRecords = baseMapper.pageList(pageParam);

        List<UserPageVO> voRecords = convertUserPageVOList(dbRecords);

        return PageUtil.buildPageInfo(pageParam, count, voRecords);
    }

    private List<UserPageVO> convertUserPageVOList(List<UserPageDTO> dbRecords) {
        return dbRecords
                .stream()
                .map(dbRecord -> {
                    UserPageVO vo = userConverter.convertUserPageVO(dbRecord);


                    return vo;
                })
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long addUser(UserAddDTO userAddParam) {
        log.info("新增用户,参数:{}", JSON.toJSONString(userAddParam));

        User user = new User();

        user.setId(userAddParam.getId());
        user.setUsername(userAddParam.getUsername());
        user.setName(userAddParam.getName());
        user.setPassword(userAddParam.getPassword());
        user.setStatus(userAddParam.getStatus());
        user.setType(userAddParam.getType());
        user.setPhone(userAddParam.getPhone());
        user.setEmail(userAddParam.getEmail());
        user.setDeleteFlag(userAddParam.getDeleteFlag());

        return user.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateUser(UserUpdateDTO userUpdateParam) {
        log.info("更新用户,参数:{}", JSON.toJSONString(userUpdateParam));
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteUserById(Id idParam) {
        log.info("删除用户,参数:{}", JSON.toJSONString(idParam));
        Long id = idParam.getId();

        // 判断数据是否存在
        getUserByIdAssertExist(id);

        boolean success = baseMapper.deleteById(id) > 0;

        return success;
    }

    @Override
    public User getUserById(Serializable id) {
        if (id == null) {
            return null;
        }
        return baseMapper.selectById(id);
    }

    @Override
    public User getUserByIdAssertExist(Serializable id) {
        User record = getUserById(id);
        AssertUtil.notNull(record, () -> I18nUtil.message("data.not.exist"));
        return record;
    }

    @Override
    public Map<Long, String> getUserIdNameMap(Collection<Long> userIds) {
        // todo
        return getUserIdNameMap();
    }

    @Override
    public Map<Long, String> getUserIdNameMap() {

        String key = RedisHelper.joinKey(RedisKey.USER_ID_NAME_MAP);

        Map<Long, String> cache = redisHelper.get(CacheRegion.USER, key, new TypeReference<>() {
        });

        if (Objects.nonNull(cache)) {
            return cache;
        }

        // 用户ID和姓名
        Map<Long, String> userIdNameMap = baseMapper
                .selectList(Wrappers.<User>lambdaQuery()
                        .eq(User::getDeleteFlag, CommonConstant.NOT_DELETE_FLAG)
                        .select(User::getId, User::getName))
                .stream()
                .collect(Collectors.toMap(User::getId, User::getName));

        redisHelper.setEx(CacheRegion.USER, key, userIdNameMap, 3, TimeUnit.DAYS);

        return userIdNameMap;
    }

    @Override
    public User getByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        return baseMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
    }
}
