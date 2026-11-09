package com.wd.system.basic.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.model.IdDTO;
import com.wd.common.core.objectcompare.ObjectCompareUtil;
import com.wd.common.core.util.AssertUtil;
import com.wd.common.core.util.CodeDescEnumUtil;
import com.wd.common.redis.CacheRegion;
import com.wd.common.redis.RedisKey;
import com.wd.common.redis.util.RedisHelper;
import com.wd.system.basic.convert.MenuConverter;
import com.wd.system.basic.domain.dto.CommonLogAddDTO;
import com.wd.system.basic.domain.dto.MenuAddDTO;
import com.wd.system.basic.domain.dto.MenuUpdateDTO;
import com.wd.system.basic.domain.entity.Menu;
import com.wd.system.basic.domain.enums.CommonLogBusinessTypeEnum;
import com.wd.system.basic.domain.enums.MenuStatusEnum;
import com.wd.system.basic.domain.enums.MenuTypeEnum;
import com.wd.system.basic.domain.vo.MenuDetailVO;
import com.wd.system.basic.domain.vo.MenuTreeVO;
import com.wd.system.basic.mapper.MenuMapper;
import com.wd.system.basic.service.CommonLogService;
import com.wd.system.basic.service.MenuService;
import com.wd.system.basic.service.PermissionService;
import com.wd.system.basic.service.UserService;
import com.wd.system.system.api.basic.dto.UserPermissionDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import tools.jackson.core.type.TypeReference;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Slf4j
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    private static final Pattern MENU_GROUP_LINE_BREAK = Pattern.compile("\\R");

    @Resource
    private RedisHelper redisHelper;
    @Resource
    private MenuConverter menuConverter;
    @Resource
    private UserService userService;
    @Resource
    private CommonLogService commonLogService;
    @Resource
    private PermissionService permissionService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long addMenu(MenuAddDTO menuAddDTO) {
        log.info("添加菜单,参数:{}", JSON.toJSONString(menuAddDTO));

        Long parentId = menuAddDTO.getParentId();
        Menu parent = getParentMenu(parentId, StringUtils.isNotBlank(menuAddDTO.getMenuGroupName()));
        String menuGroupName = normalizeMenuGroupName(menuAddDTO.getMenuGroupName(), parent);

        String routePath = Optional.ofNullable(menuAddDTO.getRoutePath()).orElse("");
        if (StringUtils.isNotBlank(routePath)) {
            long routePathCount = baseMapper.selectCount(Wrappers.<Menu>lambdaQuery().eq(Menu::getRoutePath, routePath));
            AssertUtil.isTrue(routePathCount <= 0, "路由路径已存在");
        }

        Menu menu = new Menu();
        long id = IdWorker.getId();
        menu.setId(id);
        menu.setMenuName(menuAddDTO.getMenuName());
        menu.setMenuGroupName(menuGroupName);
        menu.setParentId(Optional.ofNullable(parentId).orElse(CommonConstant.ZERO_LONG));
        menu.setSortNumber(Optional.ofNullable(menuAddDTO.getSortNumber()).orElse(0));
        menu.setMenuType(menuAddDTO.getMenuType());
        menu.setRoutePath(routePath);
        menu.setPermission(Optional.ofNullable(menuAddDTO.getPermission()).orElse(""));
        menu.setIcon(Optional.ofNullable(menuAddDTO.getIcon()).orElse(""));
        menu.setStatus(StringUtils.isBlank(menuAddDTO.getStatus()) ? MenuStatusEnum.ENABLED.getCode() : menuAddDTO.getStatus());
        menu.setRouteName(menuAddDTO.getRouteName());
        menu.setRoutePathParam(menuAddDTO.getRoutePathParam());
        menu.setLayout(menuAddDTO.getLayout());
        menu.setComponent(Optional.ofNullable(menuAddDTO.getComponent()).orElse(""));
        menu.setI18nKey(Optional.ofNullable(menuAddDTO.getI18nKey()).orElse(""));
        menu.setIconType(Optional.ofNullable(menuAddDTO.getIconType()).orElse(""));
        menu.setCacheRoute(Optional.ofNullable(menuAddDTO.getCacheRoute()).orElse(false));
        menu.setConstantRoute(Optional.ofNullable(menuAddDTO.getConstantRoute()).orElse(false));
        menu.setHideRoute(Optional.ofNullable(menuAddDTO.getHideRoute()).orElse(false));
        menu.setHighlightRouteName(menuAddDTO.getHighlightRouteName());
        menu.setExternalLink(Optional.ofNullable(menuAddDTO.getExternalLink()).orElse(""));
        menu.setRouteParam(menuAddDTO.getRouteParam());
        menu.setButton(menuAddDTO.getButton());

        baseMapper.insert(menu);

        // 添加日志
        addLog(id, "新增", "新增菜单");

        // 清除缓存
        clearMenuCacheAfterCommit();

        return id;
    }

    private void addLog(Long businessKey, String operationType, String content) {
        CommonLogAddDTO commonLog = CommonLogAddDTO.builder()
                .businessType(CommonLogBusinessTypeEnum.MENU.getCode())
                .businessKey(String.valueOf(businessKey))
                .operationType(operationType)
                .content(content)
                .build();
        commonLogService.addLog(commonLog);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateMenu(MenuUpdateDTO menuUpdateDTO) {
        log.info("更新菜单,参数:{}", JSON.toJSONString(menuUpdateDTO));

        Long menuId = menuUpdateDTO.getId();
        Menu menu = getMenuForUpdate(menuId);
        AssertUtil.notNull(menu, "菜单不存在");
        String menuName = menu.getMenuName();

        Long parentId = Optional.ofNullable(menuUpdateDTO.getParentId()).orElse(CommonConstant.ZERO_LONG);
        boolean preserveGroup = !menuUpdateDTO.hasMenuGroupName() && Objects.equals(menu.getParentId(), parentId);
        String requestedGroup = preserveGroup ? menu.getMenuGroupName() : menuUpdateDTO.getMenuGroupName();
        Menu parent = getParentMenu(parentId, StringUtils.isNotBlank(requestedGroup));
        String menuGroupName = menuUpdateDTO.hasMenuGroupName()
                ? normalizeMenuGroupName(requestedGroup, parent)
                : isTopLevelDirectory(parent) ? requestedGroup : null;
        if (!CommonConstant.ZERO_LONG.equals(parentId)
                || !MenuTypeEnum.DIRECTORY.getCode().equals(menuUpdateDTO.getMenuType())) {
            assertNoGroupedChildren(menu);
        }

        String routePath = StringUtils.isBlank(menuUpdateDTO.getRoutePath()) ? null : menuUpdateDTO.getRoutePath();
        if (StringUtils.isNotBlank(routePath)) {
            long routePathCount = baseMapper.selectCount(Wrappers.<Menu>lambdaQuery()
                    .eq(Menu::getRoutePath, routePath)
                    .ne(Menu::getId, menuId));
            AssertUtil.isTrue(routePathCount <= 0, "路由路径已存在");
        }

        // 先应用实际写入的默认值，确保变更日志与落库结果一致。
        menuUpdateDTO.setParentId(parentId);
        menuUpdateDTO.setMenuGroupName(menuGroupName);
        menuUpdateDTO.setSortNumber(Optional.ofNullable(menuUpdateDTO.getSortNumber()).orElse(0));
        menuUpdateDTO.setRoutePath(routePath);
        menuUpdateDTO.setPermission(Optional.ofNullable(menuUpdateDTO.getPermission()).orElse(""));
        menuUpdateDTO.setIcon(Optional.ofNullable(menuUpdateDTO.getIcon()).orElse(""));
        menuUpdateDTO.setStatus(StringUtils.isBlank(menuUpdateDTO.getStatus()) ? MenuStatusEnum.ENABLED.getCode() : menuUpdateDTO.getStatus());
        menuUpdateDTO.setComponent(Optional.ofNullable(menuUpdateDTO.getComponent()).orElse(""));
        menuUpdateDTO.setI18nKey(Optional.ofNullable(menuUpdateDTO.getI18nKey()).orElse(""));
        menuUpdateDTO.setIconType(Optional.ofNullable(menuUpdateDTO.getIconType()).orElse(""));
        menuUpdateDTO.setCacheRoute(Optional.ofNullable(menuUpdateDTO.getCacheRoute()).orElse(false));
        menuUpdateDTO.setConstantRoute(Optional.ofNullable(menuUpdateDTO.getConstantRoute()).orElse(false));
        menuUpdateDTO.setHideRoute(Optional.ofNullable(menuUpdateDTO.getHideRoute()).orElse(false));
        menuUpdateDTO.setExternalLink(Optional.ofNullable(menuUpdateDTO.getExternalLink()).orElse(""));

        String compareLog = ObjectCompareUtil.compareLog(menu, menuUpdateDTO);

        menu.setMenuName(menuUpdateDTO.getMenuName());
        menu.setMenuGroupName(menuGroupName);
        menu.setParentId(menuUpdateDTO.getParentId());
        menu.setSortNumber(menuUpdateDTO.getSortNumber());
        menu.setMenuType(menuUpdateDTO.getMenuType());
        menu.setRoutePath(routePath);
        menu.setPermission(menuUpdateDTO.getPermission());
        menu.setIcon(menuUpdateDTO.getIcon());
        menu.setStatus(menuUpdateDTO.getStatus());
        menu.setRouteName(menuUpdateDTO.getRouteName());
        menu.setRoutePathParam(menuUpdateDTO.getRoutePathParam());
        menu.setLayout(menuUpdateDTO.getLayout());
        menu.setComponent(menuUpdateDTO.getComponent());
        menu.setI18nKey(menuUpdateDTO.getI18nKey());
        menu.setIconType(menuUpdateDTO.getIconType());
        menu.setCacheRoute(menuUpdateDTO.getCacheRoute());
        menu.setConstantRoute(menuUpdateDTO.getConstantRoute());
        menu.setHideRoute(menuUpdateDTO.getHideRoute());
        menu.setHighlightRouteName(menuUpdateDTO.getHighlightRouteName());
        menu.setExternalLink(menuUpdateDTO.getExternalLink());
        menu.setRouteParam(menuUpdateDTO.getRouteParam());
        menu.setButton(menuUpdateDTO.getButton());

        boolean success = baseMapper.alwaysUpdateSomeColumnById(menu) > 0;

        // 添加日志
        if (StringUtils.isNotBlank(compareLog)) {
            addLog(menuId, "编辑", compareLog);
        }

        // 清除缓存
        clearMenuCacheAfterCommit();

        return success;
    }

    private Menu getMenuForUpdate(Long menuId) {
        return baseMapper.selectOne(Wrappers.<Menu>lambdaQuery().eq(Menu::getId, menuId).last("FOR UPDATE"));
    }

    private Menu getParentMenu(Long parentId, boolean lockParent) {
        if (parentId == null || CommonConstant.ZERO_LONG.equals(parentId)) {
            return null;
        }
        Menu parent = lockParent ? getMenuForUpdate(parentId) : baseMapper.selectById(parentId);
        AssertUtil.notNull(parent, "父级菜单不存在");
        return parent;
    }

    private static boolean isTopLevelDirectory(Menu menu) {
        return menu != null && CommonConstant.ZERO_LONG.equals(menu.getParentId())
                && MenuTypeEnum.DIRECTORY.getCode().equals(menu.getMenuType());
    }

    private static String normalizeMenuGroupName(String value, Menu parent) {
        String name = StringUtils.stripToNull(value);
        if (name == null) {
            return null;
        }
        AssertUtil.isTrue(name.codePointCount(0, name.length()) <= 100, "所属分类长度不能超过100");
        AssertUtil.isTrue(!MENU_GROUP_LINE_BREAK.matcher(name).find(), "所属分类只能填写一行");
        AssertUtil.isTrue(isTopLevelDirectory(parent), "仅一级目录的直接子菜单可设置所属分类");
        return name;
    }

    private void assertNoGroupedChildren(Menu menu) {
        if (!isTopLevelDirectory(menu)) {
            return;
        }
        boolean hasGroupedChildren = !baseMapper.selectList(Wrappers.<Menu>lambdaQuery()
                .eq(Menu::getParentId, menu.getId())
                .isNotNull(Menu::getMenuGroupName)
                .last("LIMIT 1 FOR UPDATE")).isEmpty();
        AssertUtil.isTrue(!hasGroupedChildren, "请先清空子菜单的所属分类");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteMenuById(IdDTO idParam) {
        log.info("删除菜单,参数:{}", JSON.toJSONString(idParam));

        Long id = idParam.getId();
        Menu menu = getMenuForUpdate(id);
        AssertUtil.notNull(menu, "菜单不存在");
        assertNoGroupedChildren(menu);

        int deleted = baseMapper.deleteById(id);

        // 添加日志
        addLog(id, "删除", "删除菜单：" + menu.getMenuName());

        // 清除缓存
        clearMenuCacheAfterCommit();

        return deleted > 0;
    }

    @Override
    public List<MenuTreeVO> userPermissionMenuTree() {
        return userPermissionMenuTree(permissionService.getCurrentUserPermission());
    }

    @Override
    public List<MenuTreeVO> userPermissionMenuTree(UserPermissionDTO userPermission) {
        List<Long> authMenuIds = userPermission.getAuthMenuIds();

        // 没有菜单权限
        if (CollUtil.isEmpty(authMenuIds)) {
            return Lists.newArrayList();
        }

        // 菜单列表沿用现有查询规则
        List<Menu> menuList = menuList();

        // 过滤拥有的权限
        menuList = menuList
                .stream()
                .filter(v -> authMenuIds.contains(v.getId()))
                .toList();

        return getNormalMenuTree(menuList);
    }

    @Override
    public List<MenuTreeVO> menuTree() {
        // 所有菜单
        List<Menu> menuList = menuList();

        return getNormalMenuTree(menuList);
    }

    private List<MenuTreeVO> getNormalMenuTree(List<Menu> menusList) {
        Map<String, String> menyTypeMap = CodeDescEnumUtil.codeDescMap(MenuTypeEnum.class);

        // 转化为vo对象
        List<MenuTreeVO> menuTreeList = menusList
                .stream()
                .map(menu -> {
                    MenuTreeVO vo = menuConverter.convertMenuTreeVO(menu);
                    vo.setMenuTypeDesc(menyTypeMap.get(vo.getMenuType()));
                    return vo;
                })
                .toList();

        // 根据父级id分组
        Map<Long, List<MenuTreeVO>> parentIdGroupMap = menuTreeList
                .stream()
                .collect(Collectors.groupingBy(MenuTreeVO::getParentId));

        // 第一级
        List<MenuTreeVO> levelOneTreeList = menuTreeList
                .stream()
                .filter(v -> CommonConstant.ZERO_LONG.equals(v.getParentId()))
                .sorted(Comparator.comparing(MenuTreeVO::getSortNumber))
                .toList();

        // 循环补充下级列表
        loopMenuChildren(levelOneTreeList, parentIdGroupMap);

        return levelOneTreeList;
    }

    private void loopMenuChildren(List<MenuTreeVO> voList, Map<Long, List<MenuTreeVO>> parentIdGroupMap) {
        for (MenuTreeVO vo : voList) {
            List<MenuTreeVO> childrenMenu = getChildrenMenu(vo.getId(), parentIdGroupMap);
            if (CollUtil.isNotEmpty(childrenMenu)) {
                loopMenuChildren(childrenMenu, parentIdGroupMap);

                vo.setChildren(childrenMenu);
            }
        }
    }

    private List<MenuTreeVO> getChildrenMenu(Long parentId, Map<Long, List<MenuTreeVO>> parentIdGroupMap) {
        List<MenuTreeVO> menus = parentIdGroupMap.get(parentId);
        if (CollUtil.isNotEmpty(menus)) {
            menus.sort(Comparator.comparing(MenuTreeVO::getSortNumber));
            return menus;
        }
        return null;
    }

    @Override
    public List<Menu> menuList() {
        String key = RedisKey.MENU_LIST;

        List<Menu> cache = redisHelper.get(CacheRegion.MENU, key, new TypeReference<>() {
        });
        if (Objects.nonNull(cache)) {
            return cache;
        }

        List<Menu> menus = baseMapper.selectList(Wrappers.<Menu>lambdaQuery());

        redisHelper.setEx(CacheRegion.MENU, key, menus, 5, TimeUnit.DAYS);

        return menus;
    }

    @Override
    public List<Long> allMenuIds() {
        return baseMapper.selectList(Wrappers.<Menu>lambdaQuery().select(Menu::getId))
                .stream()
                .map(Menu::getId)
                .toList();
    }

    private void clearMenuCache() {
        redisHelper.deleteByCacheRegion(CacheRegion.MENU);
        redisHelper.deleteByCacheRegion(CacheRegion.PERMISSION);
    }

    private void clearMenuCacheAfterCommit() {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            clearMenuCache();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                clearMenuCache();
            }
        });
    }

    @Override
    public MenuDetailVO detail(IdDTO idParam) {
        Long menuId = idParam.getId();

        Menu menu = getMenuById(menuId);
        if (Objects.isNull(menu)) {
            return null;
        }

        MenuDetailVO vo = menuConverter.convertMenuDetailVO(menu);
        vo.setMenuTypeDesc(CodeDescEnumUtil.getDescByCode(MenuTypeEnum.class, vo.getMenuType()));

        Map<Long, String> userIdNameMap = userService.getUserIdNameMap();
        vo.setCreateUserName(userIdNameMap.get(vo.getCreateUserId()));
        vo.setUpdateUserName(userIdNameMap.get(vo.getUpdateUserId()));

        return vo;
    }

    @Override
    public List<String> getPermissionByIds(Collection<Long> menuIds) {
        if (CollUtil.isEmpty(menuIds)) {
            return Lists.newArrayList();
        }

        return baseMapper
                .selectList(Wrappers.<Menu>lambdaQuery()
                        .in(Menu::getId, menuIds)
                        .select(Menu::getPermission))
                .stream()
                .map(Menu::getPermission)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }

    public Menu getMenuById(Long id) {
        if (Objects.isNull(id)) {
            return null;
        }
        return baseMapper.selectById(id);
    }
}
