package com.wd.system.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wd.common.core.ExportTypeEnum;
import com.wd.common.core.PageInfo;
import com.wd.common.core.R;
import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.exception.ExcelExportException;
import com.wd.common.core.util.CodeDescEnumUtil;
import com.wd.common.core.util.ExportUtil;
import com.wd.common.core.util.ResponseUtil;
import com.wd.common.mybatisplus.PageUtil;
import com.wd.file.feign.AttachmentFeign;
import com.wd.file.feign.DownloadCenterFeign;
import com.wd.file.feign.dto.AttachmentDTO;
import com.wd.file.feign.dto.DownloadCenterCreateDTO;
import com.wd.system.mapper.system.UserMapper;
import com.wd.system.system.convert.UserConverter;
import com.wd.system.system.dto.*;
import com.wd.system.system.entity.User;
import com.wd.system.system.enums.UserSexEnum;
import com.wd.system.system.enums.UserStatusEnum;
import com.wd.system.system.enums.UserTypeEnum;
import com.wd.system.system.service.IUserService;
import com.wd.system.system.vo.UserPageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author huangwenda
 * @since 2023-01-30
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private AttachmentFeign attachmentApi;
    @Autowired
    private DownloadCenterFeign downloadCenterApi;
    @Autowired
    private UserConverter userConverter;

    @Override
    public PageInfo<UserPageVO> page(UserPageParam pageParam) {
        long total = baseMapper.pageTotal(pageParam);
        if (total <= 0) {
            PageUtil.buildPageInfo(pageParam);
        }

        List<UserPageDTO> dbRecords = baseMapper.page(pageParam);
        List<UserPageVO> records = userConverter.convertUserPageVOList(dbRecords);
        records.forEach(record -> {
            record.setStatusDesc(CodeDescEnumUtil.getDescByCode(UserStatusEnum.class, record.getStatus()));
            record.setTypeDesc(CodeDescEnumUtil.getDescByCode(UserTypeEnum.class, record.getType()));
            record.setSexDesc(CodeDescEnumUtil.getDescByCode(UserSexEnum.class, record.getSex()));
        });

        return PageUtil.buildPageInfo(pageParam, total, records);
    }

    @Override
    public long exportPage(UserPageParam pageParam) {
        ExportUtil.pageExportParamValidAndDefaultValue(pageParam);

        R<Long> createResponse = downloadCenterApi.onCreate(new DownloadCenterCreateDTO("用户信息"));
        ResponseUtil.assertSuccess(createResponse, "创建失败");
        Long downloadId = createResponse.getData();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            // 导出类型
            String exportType = pageParam.getExportType();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (ExcelWriter excelWriter = EasyExcel.write(out, UserExportDTO.class).build()) {
                // 这里注意 如果同一个sheet只要创建一次
                WriteSheet writeSheet = EasyExcel.writerSheet().build();

                long current;
                long totalPage;
                do {
                    // 分页查询
                    PageInfo<UserPageVO> pageInfo = page(pageParam);
                    // 数据对象转化
                    List<UserExportDTO> data = userConverter.convertUserExportDTOList(pageInfo.getRecords());
                    // 写入
                    excelWriter.write(data, writeSheet);

                    current = pageInfo.getCurrent();
                    totalPage = PageUtil.getTotalPage(pageInfo);
                    log.info("导出类型:{},当前页数:{},一共页数:{}", exportType, current, totalPage);

                    // 设置下一页
                    pageParam.setCurrent(current + 1);
                    // 导出全部查询条件数据时才进行分页查询
                } while (current < totalPage && ExportTypeEnum.QUERY_RESULT.getCode().equals(exportType));
                excelWriter.finish();

                R<AttachmentDTO> saveResponse = attachmentApi.save("用户信息.xlsx", new ByteArrayInputStream(out.toByteArray()));
                ResponseUtil.assertSuccess(saveResponse, "");
                AttachmentDTO attachmentDTO = saveResponse.getData();
                log.info("生成文件成功,附件ID:{}", "");
                downloadCenterApi.onCompleted(downloadId, attachmentDTO.getAttachmentId());
            } catch (Exception e) {
                log.error("下载Excel失败", e);
                downloadCenterApi.onError(downloadId, e.getMessage());
                throw new ExcelExportException("用户信息下载失败");
            }
        });
        executorService.shutdown();
        return downloadId;
    }

    @Override
    public boolean add(UserAddDTO addUserParam) {
        // 参数校验
        addUserParamValidate(addUserParam);

        User user = new User();
        user.setLoginName(addUserParam.getLoginName());
        user.setRealName(addUserParam.getRealName());
        user.setPassword("");
        user.setStatus(UserStatusEnum.ENABLE.getCode());
        user.setType(addUserParam.getType());
        user.setAvatarImageUrl(addUserParam.getAvatarImageUrl());
        user.setSex(addUserParam.getSex());
        user.setTel(addUserParam.getTel());
        user.setEmail(addUserParam.getEmail());
        return baseMapper.insert(user) == 1;
    }

    private void addUserParamValidate(UserAddDTO addUserParam) {
        StringBuilder errorMessage = new StringBuilder();
        String loginName = addUserParam.getLoginName();
        Long loginNameCount = baseMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getLoginName, loginName));
        if (loginNameCount > 0) {
            errorMessage.append("登录名已存在;");
        }

        String tel = addUserParam.getTel();
        if (StringUtils.isNotBlank(tel)) {
            Long telCount = baseMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getTel, tel));
            if (telCount > 0) {
                errorMessage.append("手机号码已被使用;");
            }
        }

        String email = addUserParam.getEmail();
        if (StringUtils.isNotBlank(email)) {
            Long emailCount = baseMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getEmail, email));
            if (emailCount > 0) {
                errorMessage.append("邮箱已被使用;");
            }
        }
        Assert.isTrue(errorMessage.length() <= 0, errorMessage.toString());
    }

    @Override
    public boolean update(Long id, UserUpdateDTO updateUserParam) {
        Assert.notNull(id, "ID不能为空");

        User record = baseMapper.selectById(id);
        Assert.notNull(record, "ID[" + id + "]不存在");

        User updateUser = userConverter.convertUser(updateUserParam);

        return baseMapper.update(updateUser, Wrappers.<User>lambdaUpdate().eq(User::getId, id)) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        log.info("删除用户:{}", id);

        Assert.notNull(id, "ID不能为空");

        User record = baseMapper.selectById(id);
        Assert.notNull(record, "ID[" + id + "]不存在");

        String type = record.getType();
        Assert.isTrue(!UserTypeEnum.SYSTEM_MANAGER.getCode().equals(type), "系统管理员账号不允许删除");
        Assert.isTrue(!UserTypeEnum.SYSTEM_SUPER_ADMIN.getCode().equals(type), "系统超级管理员账号不允许删除");

        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public String getShowName(Long userId) {
        if (userId == null) {
            return null;
        }
        if (CommonConstant.SYSTEM_USER_ID.equals(userId)) {
            return CommonConstant.SYSTEM_USER_REAL_NAME;
        }
        return Optional.ofNullable(baseMapper.selectById(userId)).map(User::getRealName).orElse(null);
    }
}
