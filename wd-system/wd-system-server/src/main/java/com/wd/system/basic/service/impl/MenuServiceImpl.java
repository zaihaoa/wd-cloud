package com.wd.system.basic.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.model.Id;
import com.wd.common.core.util.AssertUtil;
import com.wd.common.core.util.CodeDescEnumUtil;
import com.wd.common.core.util.ObjectCompareUtil;
import com.wd.common.redis.CacheRegion;
import com.wd.common.redis.RedisKey;
import com.wd.common.redis.util.RedisHelper;
import com.wd.system.basic.convert.MenuConverter;
import com.wd.system.basic.domain.dto.CommonLogAddDTO;
import com.wd.system.basic.domain.dto.MenuAddDTO;
import com.wd.system.basic.domain.dto.MenuUpdateDTO;
import com.wd.system.basic.domain.entity.Menu;
import com.wd.system.basic.domain.enums.CommonLogBusinessTypeEnum;
import com.wd.system.basic.domain.enums.MenuTypeEnum;
import com.wd.system.basic.domain.vo.MenuDetailVO;
import com.wd.system.basic.domain.vo.MenuTreeVO;
import com.wd.system.basic.mapper.MenuMapper;
import com.wd.system.basic.service.CommonLogService;
import com.wd.system.basic.service.MenuService;
import com.wd.system.basic.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
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

    @Resource
    private RedisHelper redisHelper;
    @Resource
    private MenuConverter menuConverter;
    @Resource
    private UserService userService;
    @Resource
    private CommonLogService commonLogService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long addMenu(MenuAddDTO menuAddDTO) {
        log.info("添加菜单,参数:{}", JSON.toJSONString(menuAddDTO));

        Long parentId = menuAddDTO.getParentId();
        if (Objects.nonNull(parentId) && !Objects.equals(parentId, CommonConstant.LONG_ZERO)) {
            long parentIdCount = baseMapper.selectCount(Wrappers.<Menu>lambdaQuery().eq(Menu::getId, parentId));
            AssertUtil.isTrue(parentIdCount > 0, "父级菜单不存在");
        }

        String path = menuAddDTO.getPath();
        if (StringUtils.isNotBlank(path)) {
            long pathCount = baseMapper.selectCount(Wrappers.<Menu>lambdaQuery().eq(Menu::getPath, path));
            AssertUtil.isTrue(pathCount <= 0, "路由地址已存在");
        }

        Menu menu = new Menu();
        long id = IdWorker.getId();
        menu.setId(id);
        String name = menuAddDTO.getName();
        menu.setName(name);
        menu.setParentId(Optional.ofNullable(parentId).orElse(CommonConstant.LONG_ZERO));
        menu.setSortNumber(Optional.ofNullable(menuAddDTO.getSortNumber()).orElse(0));
        menu.setMenuType(menuAddDTO.getMenuType());
        menu.setPath(path);
        menu.setPermission(Optional.ofNullable(menuAddDTO.getPermission()).orElse(""));
        menu.setIcon(Optional.ofNullable(menuAddDTO.getIcon()).orElse(""));

        baseMapper.insert(menu);

        // 清除缓存
        clearMenuCache();

        // 添加日志
        addLog(id, "新增", "新增菜单");

        return id;
    }

    private void addLog(Long businessId, String operationType, String content) {
        CommonLogAddDTO commonLog = CommonLogAddDTO.builder()
                .businessType(CommonLogBusinessTypeEnum.MENU.getCode())
                .businessId(businessId)
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
        Menu menu = baseMapper.selectById(menuId);
        AssertUtil.notNull(menu, "菜单不存在");
        String menuName = menu.getName();

        Long parentId = menuUpdateDTO.getParentId();
        if (Objects.nonNull(parentId) && !Objects.equals(parentId, CommonConstant.LONG_ZERO)) {
            long parentIdCount = baseMapper.selectCount(Wrappers.<Menu>lambdaQuery().eq(Menu::getId, parentId));
            AssertUtil.isTrue(parentIdCount > 0, "父级菜单不存在");
        }

        String path = menuUpdateDTO.getPath();
        if (StringUtils.isNotBlank(path)) {
            long pathCount = baseMapper.selectCount(Wrappers.<Menu>lambdaQuery()
                    .eq(Menu::getPath, path)
                    .ne(Menu::getId, menuId));
            AssertUtil.isTrue(pathCount <= 0, "路由地址已存在");
        }

        String compareLog = ObjectCompareUtil.compareLog(menu, menuUpdateDTO);

        menu.setName(menuUpdateDTO.getName());
        menu.setParentId(Optional.ofNullable(parentId).orElse(CommonConstant.LONG_ZERO));
        menu.setSortNumber(Optional.ofNullable(menuUpdateDTO.getSortNumber()).orElse(0));
        menu.setMenuType(menuUpdateDTO.getMenuType());
        menu.setPath(path);
        menu.setPermission(Optional.ofNullable(menuUpdateDTO.getPermission()).orElse(""));
        menu.setIcon(Optional.ofNullable(menuUpdateDTO.getIcon()).orElse(""));

        boolean success = baseMapper.updateById(menu) > 0;

        // 清除缓存
        clearMenuCache();

        // 添加日志
        addLog(menuId, "编辑", "编辑菜单：" + menuName + (StringUtils.isNotBlank(compareLog) ? ("，" + compareLog) : ""));

        return success;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteMenuById(Id idParam) {
        log.info("删除菜单,参数:{}", JSON.toJSONString(idParam));

        Long id = idParam.getId();
        Menu menu = baseMapper.selectById(id);
        AssertUtil.notNull(menu, "菜单不存在");

        int deleted = baseMapper.deleteById(id);

        // 清除缓存
        clearMenuCache();

        // 添加日志
        addLog(id, "删除", "删除菜单：" + menu.getName());

        return deleted > 0;
    }

    @Override
    public List<MenuTreeVO> userPermissionMenuTree() {

        // 正常状态的菜单,排除按钮
        List<Menu> menuList = menuList();

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
                .filter(v -> CommonConstant.LONG_ZERO.equals(v.getParentId()))
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
    }

    @Override
    public MenuDetailVO detail(Id idParam) {
        Long menuId = idParam.getId();

        Menu menu = getMenuById(menuId);
        if (Objects.isNull(menu)) {
            return null;
        }

        MenuDetailVO vo = menuConverter.convertMenuDetailVO(menu);
        vo.setMenuTypeDesc(CodeDescEnumUtil.getDescByCode(MenuTypeEnum.class, vo.getMenuType()));

        Map<Long, String> userIdNameMap = userService.getUserIdNameMap(List.of(vo.getCreateUserId(), vo.getUpdateUserId()));
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
