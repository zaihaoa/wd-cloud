package com.wd.system.basic.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.context.SystemContext;
import com.wd.common.core.exception.PromptException;
import com.wd.common.core.model.IdDTO;
import com.wd.common.core.model.PageInfo;
import com.wd.common.core.model.SelectCombobox;
import com.wd.common.core.objectcompare.ObjectCompareUtil;
import com.wd.common.core.util.AssertUtil;
import com.wd.common.core.util.CodeDescEnumUtil;
import com.wd.common.core.util.I18nUtil;
import com.wd.common.mybatisplus.PageUtil;
import com.wd.common.mybatisplus.SuperMapper;
import com.wd.common.redis.CacheRegion;
import com.wd.common.redis.RedisKey;
import com.wd.common.redis.util.RedisHelper;
import com.wd.system.basic.convert.UserConverter;
import com.wd.system.basic.domain.dto.*;
import com.wd.system.basic.domain.entity.User;
import com.wd.system.basic.domain.entity.Role;
import com.wd.system.basic.domain.entity.UserRoleRelevance;
import com.wd.system.basic.domain.enums.CommonLogBusinessTypeEnum;
import com.wd.system.basic.domain.enums.UserStatusEnum;
import com.wd.system.basic.domain.enums.UserTypeEnum;
import com.wd.system.basic.domain.vo.UserDetailVO;
import com.wd.system.basic.domain.vo.UserInfoVO;
import com.wd.system.basic.domain.vo.UserPageVO;
import com.wd.system.basic.service.MenuService;
import com.wd.system.basic.service.PermissionService;
import com.wd.system.system.api.basic.dto.UserPermissionDTO;
import com.wd.system.system.api.basic.vo.UserRoleVO;
import com.wd.system.basic.mapper.UserMapper;
import com.wd.system.basic.mapper.RoleMapper;
import com.wd.system.basic.mapper.UserRoleRelevanceMapper;
import com.wd.system.basic.service.CommonLogService;
import com.wd.system.basic.service.UserService;
import com.wd.system.basic.util.UserHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import tools.jackson.core.type.TypeReference;

import java.io.Serializable;
import java.util.*;
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
    @Resource
    private CommonLogService commonLogService;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private UserRoleRelevanceMapper userRoleRelevanceMapper;
    @Resource
    private PermissionService permissionService;
    @Resource
    private MenuService menuService;

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

        Map<Long, String> userIdNameMap = getUserIdNameMap();
        Map<Long, List<UserRoleVO>> userRoleMap = getUserRoleMap(dbRecords.stream().map(UserPageDTO::getId).toList());

        return dbRecords
                .stream()
                .map(dbRecord -> {
                    UserPageVO vo = userConverter.convertUserPageVO(dbRecord);
                    vo.setStatusDesc(CodeDescEnumUtil.getDescByCode(UserStatusEnum.class, vo.getStatus()));
                    vo.setTypeDesc(CodeDescEnumUtil.getDescByCode(UserTypeEnum.class, vo.getType()));

                    vo.setCreateUserName(userIdNameMap.get(vo.getCreateUserId()));
                    vo.setUpdateUserName(userIdNameMap.get(vo.getUpdateUserId()));
                    vo.setRoles(userRoleMap.getOrDefault(vo.getId(), List.of()));

                    return vo;
                })
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long addUser(UserAddDTO userAddParam) {

        List<Long> roleIds = validateRoleIds(userAddParam.getRoleIds());
        String username = userAddParam.getUsername();
        String name = userAddParam.getName();
        String password = userAddParam.getPassword();
        String confirmPassword = userAddParam.getConfirmPassword();
        String phone = userAddParam.getPhone();
        String email = userAddParam.getEmail();
        log.info("新增用户,username:{},name:{},phone:{},email:{}", username, name, phone, email);

        Assert.isTrue(Objects.equals(password, confirmPassword), "两次密码输入不一致");
        Assert.isTrue(!Objects.equals(name, CommonConstant.SYSTEM_USER_NAME), "姓名不能使用" + CommonConstant.SYSTEM_USER_NAME);

        Long usernameCount = baseMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
        Assert.isTrue(usernameCount <= 0, "用户名已存在");

        if (StringUtils.isNotBlank(phone)) {
            Long phoneCount = baseMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getPhone, phone));
            Assert.isTrue(phoneCount <= 0, "手机号已存在");
        }

        if (StringUtils.isNotBlank(email)) {
            Long emailCount = baseMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getEmail, email));
            Assert.isTrue(emailCount <= 0, "邮箱已存在");
        }

        User user = new User();
        long id = IdWorker.getId();
        user.setId(id);
        user.setUsername(username);
        user.setName(name);
        user.setPassword(UserHelper.encryptPassword(password));
        user.setStatus(UserStatusEnum.ENABLED.getCode());
        user.setType(UserTypeEnum.NORMAL.getCode());
        user.setPhone(Optional.ofNullable(phone).orElse(""));
        user.setEmail(Optional.ofNullable(email).orElse(""));
        user.setDeleteFlag(CommonConstant.NOT_DELETE_FLAG);

        baseMapper.insert(user);
        replaceUserRoles(id, roleIds);

        // 添加日志
        addLog(id, "新增", "新增用户");

        // 清除缓存
        clearUserCacheAfterCommit();

        return id;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateUser(UserUpdateDTO userUpdateParam) {
        log.info("更新用户,参数:{}", JSON.toJSONString(userUpdateParam));

        List<Long> roleIds = validateRoleIds(userUpdateParam.getRoleIds());
        Long id = userUpdateParam.getId();
        String username = userUpdateParam.getUsername();
        String name = userUpdateParam.getName();
        String phone = userUpdateParam.getPhone();
        String email = userUpdateParam.getEmail();

        // 用户行锁串行化同一用户的资料和角色覆盖保存。
        User user = baseMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getId, id)
                .last("FOR UPDATE"));
        AssertUtil.notNull(user, () -> I18nUtil.message("data.not.exist"));

        Long usernameCount = baseMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getUsername, username).ne(User::getId, id));
        Assert.isTrue(usernameCount <= 0, "用户名已存在");

        Assert.isTrue(!Objects.equals(name, CommonConstant.SYSTEM_USER_NAME), "姓名不能使用" + CommonConstant.SYSTEM_USER_NAME);

        if (StringUtils.isNotBlank(phone)) {
            Long phoneCount = baseMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getPhone, phone).ne(User::getId, id));
            Assert.isTrue(phoneCount <= 0, "手机号已存在");
        }

        if (StringUtils.isNotBlank(email)) {
            Long emailCount = baseMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getEmail, email).ne(User::getId, id));
            Assert.isTrue(emailCount <= 0, "邮箱已存在");
        }

        String compareLog = ObjectCompareUtil.compareLog(user, userUpdateParam);

        baseMapper.update(Wrappers.<User>lambdaUpdate()
                .set(User::getUsername, username)
                .set(User::getName, name)
                .set(User::getPhone, Optional.ofNullable(phone).orElse(""))
                .set(User::getEmail, Optional.ofNullable(email).orElse(""))
                .eq(User::getId, id));
        replaceUserRoles(id, roleIds);

        // 添加日志
        if (StringUtils.isNotBlank(compareLog)) {
            addLog(id, "编辑", "编辑用户，" + compareLog);
        }

        // 清除缓存
        clearUserCacheAfterCommit();

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteUserById(IdDTO idParam) {
        log.info("删除用户,参数:{}", JSON.toJSONString(idParam));
        Long id = idParam.getId();

        // 判断数据是否存在
        User user = getUserByIdAssertExist(id);
        if (UserTypeEnum.SYSTEM.getCode().equals(user.getType())) {
            throw new PromptException("系统用户不能删除");
        }

        boolean success = baseMapper.update(Wrappers.<User>lambdaUpdate()
                .set(User::getDeleteFlag, id)
                .eq(User::getId, id)) > 0;

//        userRoleRelevanceService.remove(Wrappers.<UserRoleRelevance>lambdaQuery().eq(UserRoleRelevance::getUserId, userId));
//        userDataPermissionRelevanceService.remove(Wrappers.<UserDataPermissionRelevance>lambdaQuery().eq(UserDataPermissionRelevance::getUserId, userId));

        // 添加日志
        addLog(id, "删除", "删除用户");

        // 登出系统
        StpUtil.logout(id);

        // 清除缓存
        clearUserCacheAfterCommit();

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

        Map<Long, String> userIdNameMap = getUserIdNameMap();
        if (CollUtil.isEmpty(userIdNameMap) || CollUtil.isEmpty(userIds)) {
            return userIdNameMap;
        }

        Map<Long, String> result = Maps.newHashMap();

        userIdNameMap.forEach((k, v) -> {
            if (userIds.contains(k)) {
                result.put(k, v);
            }
        });

        return result;
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

        userIdNameMap.put(CommonConstant.SYSTEM_USER_ID, CommonConstant.SYSTEM_USER_NAME);

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

    private void addLog(Long businessKey, String operationType, String content) {
        CommonLogAddDTO commonLog = CommonLogAddDTO.builder()
                .businessType(CommonLogBusinessTypeEnum.USER.getCode())
                .businessKey(String.valueOf(businessKey))
                .operationType(operationType)
                .content(content)
                .build();
        commonLogService.addLog(commonLog);
    }

    private void clearUserCache() {
        redisHelper.deleteByCacheRegion(CacheRegion.USER);
        redisHelper.deleteByCacheRegion(CacheRegion.PERMISSION);
    }

    private void clearUserCacheAfterCommit() {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            clearUserCache();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                clearUserCache();
            }
        });
    }

    private List<Long> validateRoleIds(List<Long> roleIds) {
        AssertUtil.notEmpty(roleIds, "角色ID集合不能为空");
        List<Long> ids = new ArrayList<>(new LinkedHashSet<>(roleIds));
        AssertUtil.isTrue(ids.stream().allMatch(id -> id != null && id > 0), "角色ID必须为正数");
        long count = roleMapper.selectCount(Wrappers.<Role>lambdaQuery().in(Role::getId, ids));
        AssertUtil.isTrue(count == ids.size(), "角色不存在或已删除");
        return ids;
    }

    private void replaceUserRoles(Long userId, List<Long> roleIds) {
        userRoleRelevanceMapper.delete(Wrappers.<UserRoleRelevance>lambdaQuery()
                .eq(UserRoleRelevance::getUserId, userId));
        List<UserRoleRelevance> links = new ArrayList<>(roleIds.size());
        for (Long roleId : roleIds) {
            UserRoleRelevance link = new UserRoleRelevance();
            link.setId(IdWorker.getId());
            link.setUserId(userId);
            link.setRoleId(roleId);
            links.add(link);
        }
        for (List<UserRoleRelevance> batch : CollUtil.split(links, SuperMapper.DEFAULT_BATCH_SIZE)) {
            AssertUtil.isTrue(userRoleRelevanceMapper.insertBatchSomeColumn(batch) == batch.size(), "保存用户角色失败");
        }
    }

    private Map<Long, List<UserRoleVO>> getUserRoleMap(Collection<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return Map.of();
        }
        List<UserRoleRelevance> links = userRoleRelevanceMapper.selectList(Wrappers.<UserRoleRelevance>lambdaQuery()
                .in(UserRoleRelevance::getUserId, new LinkedHashSet<>(userIds))
                .select(UserRoleRelevance::getUserId, UserRoleRelevance::getRoleId)
                .orderByAsc(UserRoleRelevance::getRoleId));
        if (links.isEmpty()) {
            return Map.of();
        }
        List<Long> roleIds = links.stream().map(UserRoleRelevance::getRoleId).distinct().toList();
        Map<Long, UserRoleVO> roles = new HashMap<>();
        for (Role role : roleMapper.selectList(Wrappers.<Role>lambdaQuery()
                .in(Role::getId, roleIds)
                .select(Role::getId, Role::getCode, Role::getName))) {
            roles.put(role.getId(), userConverter.convertUserRoleVO(role));
        }
        Map<Long, LinkedHashSet<Long>> groupedRoleIds = new HashMap<>();
        for (UserRoleRelevance link : links) {
            groupedRoleIds.computeIfAbsent(link.getUserId(), ignored -> new LinkedHashSet<>()).add(link.getRoleId());
        }
        Map<Long, List<UserRoleVO>> result = new HashMap<>();
        groupedRoleIds.forEach((userId, ids) -> result.put(userId,
                ids.stream().map(roles::get).filter(Objects::nonNull).toList()));
        return result;
    }

    @Override
    public boolean updateStatus(UserUpdateStatusDTO updateStatusDTO) {
        log.info("更新状态,参数:{}", JSON.toJSONString(updateStatusDTO));

        Long userId = updateStatusDTO.getId();
        String status = updateStatusDTO.getStatus();

        User user = getUserByIdAssertExist(userId);
        AssertUtil.isTrue(UserTypeEnum.NORMAL.getCode().equals(user.getType()), "该用户不能修改状态");

        boolean success = true;

        if (!Objects.equals(status, user.getStatus())) {
            success = baseMapper.update(Wrappers.<User>lambdaUpdate()
                    .set(User::getStatus, status)
                    .eq(User::getId, userId)) > 0;

            // 添加日志
            addLog(userId, "编辑", CodeDescEnumUtil.getDescByCode(UserStatusEnum.class, status) + "用户");
        }

        if (!UserStatusEnum.ENABLED.getCode().equals(status)) {
            // 登出系统
            StpUtil.logout(userId);
        }

        // 清除缓存
        clearUserCache();

        return success;
    }

    @Override
    public boolean updatePassword(UserUpdatePasswordDTO userUpdatePasswordDTO) {
        Long userId = userUpdatePasswordDTO.getId();
        log.info("修改密码,用户ID:{}", userId);

        String oldPassword = userUpdatePasswordDTO.getOldPassword();
        String password = userUpdatePasswordDTO.getPassword();
        String confirmPassword = userUpdatePasswordDTO.getConfirmPassword();

        AssertUtil.isTrue(Objects.equals(password, confirmPassword), "两次密码输入不一致");

        User user = getUserByIdAssertExist(userId);
        AssertUtil.isTrue(UserHelper.checkPassword(oldPassword, user.getPassword()), "旧密码不正确");

        baseMapper.update(Wrappers.<User>lambdaUpdate()
                .set(User::getPassword, UserHelper.encryptPassword(password))
                .eq(User::getId, userId));

        // 添加日志
        addLog(userId, "修改密码", "修改密码成功");

        // 登出系统
        StpUtil.logout(userId);

        // 清除缓存
        clearUserCache();

        return true;
    }

    @Override
    public boolean resetPassword(UserResetPasswordDTO userResetPasswordDTO) {
        Long userId = userResetPasswordDTO.getId();
        log.info("重置密码,用户ID:{}", userId);

        String password = userResetPasswordDTO.getPassword();
        String confirmPassword = userResetPasswordDTO.getConfirmPassword();

        AssertUtil.isTrue(Objects.equals(password, confirmPassword), "两次密码输入不一致");

        baseMapper.update(Wrappers.<User>lambdaUpdate()
                .set(User::getPassword, UserHelper.encryptPassword(password))
                .eq(User::getId, userId));

        // 添加日志
        addLog(userId, "重置密码", "重置密码成功");

        // 登出系统
        StpUtil.logout(userId);

        // 清除缓存
        clearUserCache();

        return true;
    }

    @Override
    public UserDetailVO detail(IdDTO idDTO) {
        User user = getUserById(idDTO.getId());
        if (Objects.isNull(user)) {
            return null;
        }

        UserDetailVO vo = userConverter.convertUserDetailVO(user);
        vo.setStatusDesc(CodeDescEnumUtil.getDescByCode(UserStatusEnum.class, vo.getStatus()));
        vo.setTypeDesc(CodeDescEnumUtil.getDescByCode(UserTypeEnum.class, vo.getType()));

        Map<Long, String> userIdNameMap = getUserIdNameMap();

        vo.setCreateUserName(userIdNameMap.get(vo.getCreateUserId()));
        vo.setUpdateUserName(userIdNameMap.get(vo.getUpdateUserId()));
        vo.setRoles(getUserRoleMap(List.of(vo.getId())).getOrDefault(vo.getId(), List.of()));

        return vo;
    }

    @Override
    public UserInfoVO currentUserInfo() {
        UserPermissionDTO userPermission = permissionService.getCurrentUserPermission();

        UserInfoVO vo = userConverter.convertUserInfoVO(userPermission);
        vo.setStatusDesc(CodeDescEnumUtil.getDescByCode(UserStatusEnum.class, vo.getStatus()));
        vo.setTypeDesc(CodeDescEnumUtil.getDescByCode(UserTypeEnum.class, vo.getType()));

        vo.setAuthMenuList(menuService.userPermissionMenuTree());

        return vo;
    }

    @Override
    public List<SelectCombobox> userCombobox(String status) {

        // 用户ID和姓名
        return baseMapper
                .selectList(Wrappers.<User>lambdaQuery()
                        .eq(StringUtils.isNotBlank(status), User::getStatus, status)
                        .select(User::getId, User::getName))
                .stream()
                .map(v -> new SelectCombobox(String.valueOf(v.getId()), v.getName()))
                .sorted(Comparator.comparing(SelectCombobox::getLabel))
                .toList();
    }
}
