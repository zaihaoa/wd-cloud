package com.wd.common.excel.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.wd.common.core.enums.ExportTypeEnum;
import com.wd.common.core.exception.PromptException;
import com.wd.common.core.model.BaseExportQuery;
import com.wd.common.core.model.ExportResult;
import com.wd.common.core.model.ImportParam;
import com.wd.common.core.util.ExportUtil;
import com.wd.common.excel.dto.ExcelSheetExport;
import com.wd.common.excel.handler.CellSelector;
import com.wd.common.excel.handler.CellSelectorSheetWriteHandler;
import com.wd.common.excel.listener.ExcelReadListener;
import com.wd.common.oss.service.OssClientService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.write.builder.ExcelWriterBuilder;
import org.apache.fesod.sheet.write.builder.ExcelWriterSheetBuilder;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/**
 *
 *
 * @author huangwenda
 * @date 2025/10/30 14:35
 **/
@Slf4j
@Component
public class EasyExcelHelper {

    @Resource
    private OssClientService ossClientService;

    /**
     * 获取Excel属性字段
     *
     * @param clazz          实体类
     * @return 属性名称列表
     */
    public static List<String> getExcelPropertyNames(Class<?> clazz) {
        return getExcelPropertyNames(clazz, null);
    }

    /**
     * 获取Excel属性字段
     *
     * @param clazz          实体类
     * @param excludeFields  需要排除的字段
     * @return 属性名称列表
     */
    public static List<String> getExcelPropertyNames(Class<?> clazz, Set<String> excludeFields) {
        // excel属性名称
        List<String> excelPropertyList = Lists.newArrayList();
        boolean hasExcludeFields = CollUtil.isNotEmpty(excludeFields);

        Field[] declaredFields = clazz.getDeclaredFields();

        for (Field field : declaredFields) {
            ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
            if (annotation != null) {
                if (hasExcludeFields && excludeFields.contains(field.getName())) {
                    continue;
                }
                excelPropertyList.add(annotation.value()[0]);
            }
        }
        return excelPropertyList;
    }


    /**
     * 读取Excel数据
     *
     * @param importParam    导入参数
     * @param cls            实体类
     * @param excludeFields  需要排除的字段
     * @return 数据列表
     */
    public <T> List<T> readExcelData(ImportParam importParam, Class<T> cls, Set<String> excludeFields) {
        log.info("导入参数:{}", importParam);

        String filePath = importParam.getFilePath();
        // 下载文件到临时文件
        File tempFile = FileUtil.createTempFile(".xlsx", true);
        ossClientService.download(tempFile, filePath);

        ExcelReadListener<T> listener = new ExcelReadListener<>(getExcelPropertyNames(cls, excludeFields));

        try (InputStream inputStream = Files.newInputStream(tempFile.toPath())) {
            //读取文件
            FesodSheet.read(inputStream, cls, listener).sheet().doReadSync();
            // 验证表头
            listener.validateTitle();

            List<T> excelDataList = listener.getDataList();
            log.info("导入数量:{},数据:{}", excelDataList.size(), JSON.toJSONString(excelDataList));

            return excelDataList;
        } catch (PromptException e) {
            throw e;
        } catch (Exception e) {
            log.error("文件解析失败", e);
            throw new PromptException("文件解析失败");
        } finally {
            tempFile.deleteOnExit();
        }
    }


    private <E> ExcelWriter buildExcelWriter(ByteArrayOutputStream out,
                                             Class<E> excelClass,
                                             List<CellSelector> cellSelectors) {
        ExcelWriterBuilder excelWriterBuilder = FesodSheet.write(out, excelClass);
        if (CollUtil.isNotEmpty(cellSelectors)) {
            excelWriterBuilder.registerWriteHandler(new CellSelectorSheetWriteHandler(cellSelectors));
        }
        return excelWriterBuilder.build();
    }

    private WriteSheet createWriteSheet(String sheetName,
                                        List<String> excludeColumnFieldNames) {
        WriteSheet writeSheet = FesodSheet.writerSheet().sheetName(sheetName).build();
        if (CollUtil.isNotEmpty(excludeColumnFieldNames)) {
            writeSheet.setExcludeColumnFieldNames(excludeColumnFieldNames);
        }
        return writeSheet;
    }

    private <E> WriteSheet createWriteSheet(String sheetName,
                                            Class<E> excelClass,
                                            List<String> excludeColumnFieldNames,
                                            List<CellSelector> cellSelectors) {
        ExcelWriterSheetBuilder writerSheetBuilder = FesodSheet.writerSheet().sheetName(sheetName).head(excelClass);
        if (CollUtil.isNotEmpty(cellSelectors)) {
            writerSheetBuilder.registerWriteHandler(new CellSelectorSheetWriteHandler(cellSelectors));
        }
        if (CollUtil.isNotEmpty(excludeColumnFieldNames)) {
            writerSheetBuilder.excludeColumnFieldNames(excludeColumnFieldNames);
        }
        return writerSheetBuilder.build();
    }

    private String generateFileName(String sheetName) {
        return sheetName + "-" + LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
    }


    private String generateOssPath() {
        return "/excel/" + UUID.randomUUID().toString().replace("-", "") + ".xlsx";
    }

    public static int getTotalPage(int total, int size) {
        if (size == 0) {
            return 0;
        } else {
            return total % size == 0 ? total / size : total / size + 1;
        }
    }

    public <T extends BaseExportQuery, E> List<E> pageData(List<E> allDataList, T pageParam) {
        if (CollUtil.isEmpty(allDataList)) {
            return Collections.emptyList();
        }
        if (!ExportTypeEnum.QUERY_RESULT.getCode().equals(pageParam.getExportType())) {
            return allDataList;
        }

        int current = Optional.ofNullable(pageParam.getCurrent()).orElse(1);
        int size = Optional.ofNullable(pageParam.getSize()).orElse(allDataList.size());
        int fromIndex = Math.max((current - 1) * size, 0);
        if (fromIndex >= allDataList.size()) {
            return Collections.emptyList();
        }
        int toIndex = Math.min(fromIndex + size, allDataList.size());
        return allDataList.subList(fromIndex, toIndex);
    }

    /**
     * 模版导出执行器
     *
     * @param fileName 文件名称
     * @param sheetName sheet名称
     * @param excelClass Excel数据类
     * @return 导出结果
     */
    public <T extends BaseExportQuery, R, E> ExportResult exportTemplate(String fileName,
                                                                         String sheetName,
                                                                         Class<E> excelClass) {
        return exportTemplate(fileName, sheetName, excelClass, null, null, null);
    }

    /**
     * 模版导出执行器
     *
     * @param fileName 文件名称
     * @param sheetName sheet名称
     * @param excelClass Excel数据类
     * @param dataList 数据列表
     * @return 导出结果
     */
    public <T extends BaseExportQuery, R, E> ExportResult exportTemplate(String fileName,
                                                                         String sheetName,
                                                                         Class<E> excelClass,
                                                                         List<E> dataList) {
        return exportTemplate(fileName, sheetName, excelClass, dataList, null, null);
    }

    /**
     * 模版导出执行器
     *
     * @param fileName 文件名称
     * @param sheetName sheet名称
     * @param excelClass Excel数据类
     * @param dataList 数据列表
     * @param excludeColumnFieldNames 需要排除的列字段名
     * @return 导出结果
     */
    public <T extends BaseExportQuery, R, E> ExportResult exportTemplate(String fileName,
                                                                         String sheetName,
                                                                         Class<E> excelClass,
                                                                         List<E> dataList,
                                                                         List<String> excludeColumnFieldNames) {
        return exportTemplate(fileName, sheetName, excelClass, dataList, excludeColumnFieldNames, null);
    }

    /**
     * 模版导出执行器
     *
     * @param fileName 文件名称
     * @param sheetName sheet名称
     * @param excelClass Excel数据类
     * @param dataList 数据列表
     * @param excludeColumnFieldNames 需要排除的列字段名
     * @param cellSelectors 列选择器
     * @return 导出结果
     */
    public <T extends BaseExportQuery, R, E> ExportResult exportTemplate(String fileName,
                                                                         String sheetName,
                                                                         Class<E> excelClass,
                                                                         List<E> dataList,
                                                                         List<String> excludeColumnFieldNames,
                                                                         List<CellSelector> cellSelectors) {
        // 参数校验
        if (sheetName == null || excelClass == null) {
            throw new IllegalArgumentException("关键参数不能为空");
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ExcelWriter excelWriter = buildExcelWriter(out, excelClass, cellSelectors)) {

            // 创建sheet
            WriteSheet writeSheet = createWriteSheet(sheetName, excludeColumnFieldNames);

            excelWriter.write(CollUtil.isEmpty(dataList) ? List.of() : dataList, writeSheet);

            excelWriter.finish();

            String finalFileName = fileName + ".xlsx";
            String filePath = ossClientService.uploadTemporary(new ByteArrayInputStream(out.toByteArray()), generateOssPath());
            log.info("生成文件成功,fileName:{},filePath:{}", finalFileName, filePath);
            return new ExportResult(finalFileName, filePath);
        } catch (Exception e) {
            log.error("导出报错", e);
            throw new PromptException("导出失败");
        }
    }

    /**
     * 分页导出执行器,单sheet页
     *
     * @param sheetName sheet名称
     * @param pageParam 分页参数
     * @param pageCountFunction 分页统计函数
     * @param pageListFunction 分页数据函数
     * @param dataConvertFunction 数据转换函数
     * @param excelClass Excel数据类
     * @return 导出结果
     */
    public <T extends BaseExportQuery, R, E> ExportResult exportPageSingleSheet(String sheetName,
                                                                                T pageParam,
                                                                                Function<T, Integer> pageCountFunction,
                                                                                Function<T, List<R>> pageListFunction,
                                                                                Function<List<R>, List<E>> dataConvertFunction,
                                                                                Class<E> excelClass) {
        return exportPageSingleSheet(sheetName, sheetName, pageParam, pageCountFunction, pageListFunction, dataConvertFunction, excelClass, null, null);
    }

    /**
     * 分页导出执行器,单sheet页
     *
     * @param sheetName sheet名称
     * @param pageParam 分页参数
     * @param pageCountFunction 分页统计函数
     * @param pageListFunction 分页数据函数
     * @param dataConvertFunction 数据转换函数
     * @param excelClass Excel数据类
     * @param excludeColumnFieldNames 需要排除的列字段名
     * @return 导出结果
     */
    public <T extends BaseExportQuery, R, E> ExportResult exportPageSingleSheet(String sheetName,
                                                                                T pageParam,
                                                                                Function<T, Integer> pageCountFunction,
                                                                                Function<T, List<R>> pageListFunction,
                                                                                Function<List<R>, List<E>> dataConvertFunction,
                                                                                Class<E> excelClass,
                                                                                List<String> excludeColumnFieldNames) {
        return exportPageSingleSheet(sheetName, sheetName, pageParam, pageCountFunction, pageListFunction, dataConvertFunction, excelClass, excludeColumnFieldNames, null);
    }

    /**
     * 分页导出执行器,单sheet页
     *
     * @param fileNamePrefix 文件名称前缀
     * @param sheetName sheet名称
     * @param pageParam 分页参数
     * @param pageCountFunction 分页统计函数
     * @param pageListFunction 分页数据函数
     * @param dataConvertFunction 数据转换函数
     * @param excelClass Excel数据类
     * @param excludeColumnFieldNames 需要排除的列字段名
     * @return 导出结果
     */
    public <T extends BaseExportQuery, R, E> ExportResult exportPageSingleSheet(String fileNamePrefix,
                                                                                String sheetName,
                                                                                T pageParam,
                                                                                Function<T, Integer> pageCountFunction,
                                                                                Function<T, List<R>> pageListFunction,
                                                                                Function<List<R>, List<E>> dataConvertFunction,
                                                                                Class<E> excelClass,
                                                                                List<String> excludeColumnFieldNames) {
        return exportPageSingleSheet(fileNamePrefix, sheetName, pageParam, pageCountFunction, pageListFunction, dataConvertFunction, excelClass, excludeColumnFieldNames, null);
    }


    /**
     * 分页导出执行器,单sheet页
     *
     * @param fileNamePrefix 文件名称前缀
     * @param sheetName sheet名称
     * @param pageParam 分页参数
     * @param pageCountFunction 分页统计函数
     * @param pageListFunction 分页数据函数
     * @param dataConvertFunction 数据转换函数
     * @param excelClass Excel数据类
     * @param excludeColumnFieldNames 需要排除的列字段名
     * @param cellSelectors 列选择器
     * @return 导出结果
     */
    public <T extends BaseExportQuery, R, E> ExportResult exportPageSingleSheet(String fileNamePrefix,
                                                                                String sheetName,
                                                                                T pageParam,
                                                                                Function<T, Integer> pageCountFunction,
                                                                                Function<T, List<R>> pageListFunction,
                                                                                Function<List<R>, List<E>> dataConvertFunction,
                                                                                Class<E> excelClass,
                                                                                List<String> excludeColumnFieldNames,
                                                                                List<CellSelector> cellSelectors) {
        // 参数校验
        if (sheetName == null || pageParam == null || pageCountFunction == null || pageListFunction == null || dataConvertFunction == null || excelClass == null) {
            throw new IllegalArgumentException("关键参数不能为空");
        }

        log.info("导出,sheetName:{},查询参数:{},需要排除的列:{}", sheetName, JSON.toJSONString(pageParam), JSON.toJSONString(excludeColumnFieldNames));

        // 导出参数处理
        ExportUtil.pageExportParamValidAndDefaultValue(pageParam, 5000);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ExcelWriter excelWriter = buildExcelWriter(out, excelClass, cellSelectors)) {

            // 创建sheet
            WriteSheet writeSheet = createWriteSheet(sheetName, excludeColumnFieldNames);

            // 总记录数
            Integer pageCount = pageCountFunction.apply(pageParam);
            // 当前页码
            int current = pageParam.getCurrent();
            // 每页大小
            int size = pageParam.getSize();
            // 导出类型
            String exportType = pageParam.getExportType();
            // 总页数
            int totalPage = getTotalPage(pageCount, size);
            // 总处理记录数
            int totalRecords = 0;

            if (totalPage == 0) {
                excelWriter.write(new ArrayList<>(), writeSheet);
                log.info("处理进度 - 无数据");
            } else {
                for (int currentPage = current; currentPage <= totalPage; currentPage++) {
                    pageParam.setCurrent(currentPage);

                    // 分页查询
                    List<R> records = Optional.ofNullable(pageListFunction.apply(pageParam)).orElseGet(ArrayList::new);

                    // 转换数据
                    List<E> data = Optional.ofNullable(dataConvertFunction.apply(records)).orElseGet(ArrayList::new);
                    excelWriter.write(data, writeSheet);

                    totalRecords += records.size();

                    log.info("处理进度 - 当前处理完第{}页,共{}页,已处理{}条,一共{}条", currentPage, totalPage, totalRecords, pageCount);

                    if (!ExportTypeEnum.QUERY_RESULT.getCode().equals(exportType)) {
                        break;
                    }
                }
            }

            excelWriter.finish();

            String fileName = generateFileName(fileNamePrefix);
            String filePath = ossClientService.uploadTemporary(new ByteArrayInputStream(out.toByteArray()), generateOssPath());

            log.info("生成文件成功,总记录数:{},fileName:{},filePath:{}", totalRecords, fileName, filePath);
            return new ExportResult(fileName, filePath);
        } catch (Exception e) {
            log.error("导出报错", e);
            throw new PromptException("导出失败");
        }
    }

    public <T extends BaseExportQuery, E> ExportResult exportPageMultiSheet(String fileNamePrefix,
                                                                            ExcelSheetExport<T, ?>... excelSheetExportList) {
        log.info("多sheet导出,fileNamePrefix:{},参数:{}", fileNamePrefix, JSON.toJSONString(excelSheetExportList));

        for (ExcelSheetExport<T, ?> excelSheetExport : excelSheetExportList) {
            String sheetName = excelSheetExport.getSheetName();
            T pageParam = excelSheetExport.getPageParam();
            Function<T, Integer> pageCountFunction = excelSheetExport.getPageCountFunction();
            Function<T, ? extends List<?>> excelDataFunction = excelSheetExport.getExcelDataFunction();
            Class<?> excelClass = excelSheetExport.getExcelClass();

            // 参数校验
            if (sheetName == null || pageParam == null || pageCountFunction == null || excelDataFunction == null || excelClass == null) {
                throw new IllegalArgumentException("关键参数不能为空");
            }
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ExcelWriter excelWriter = FesodSheet.write(out).build()) {

            int sheetNum = 1;
            for (ExcelSheetExport<T, ?> excelSheetExport : excelSheetExportList) {
                String sheetName = excelSheetExport.getSheetName();
                T pageParam = excelSheetExport.getPageParam();
                Function<T, Integer> pageCountFunction = excelSheetExport.getPageCountFunction();
                Function<T, ? extends List<?>> excelDataFunction = excelSheetExport.getExcelDataFunction();
                Class<?> excelClass = excelSheetExport.getExcelClass();
                List<String> excludeColumnFieldNames = excelSheetExport.getExcludeColumnFieldNames();
                List<CellSelector> cellSelectors = excelSheetExport.getCellSelectors();

                // 导出参数处理
                ExportUtil.pageExportParamValidAndDefaultValue(pageParam, 5000);

                // 创建sheet
                WriteSheet writeSheet = createWriteSheet(sheetName, excelClass, excludeColumnFieldNames, cellSelectors);

                // 总记录数
                Integer pageCount = pageCountFunction.apply(pageParam);
                // 当前页码
                int current = pageParam.getCurrent();
                // 每页大小
                int size = pageParam.getSize();
                // 导出类型
                String exportType = pageParam.getExportType();
                // 总页数
                int totalPage = getTotalPage(pageCount, size);
                // 总处理记录数
                int totalRecords = 0;

                // 数据为空时也需要写入空列表以显示表头
                if (totalPage == 0) {
                    excelWriter.write(new ArrayList<>(), writeSheet);
                    log.info("处理进度 - 第{}个sheet页:{},数据为空,已写入表头", sheetNum, sheetName);
                } else {
                    for (int currentPage = current; currentPage <= totalPage; currentPage++) {
                        pageParam.setCurrent(currentPage);

                        // 分页查询
                        List<?> data = excelDataFunction.apply(pageParam);
                        if (CollUtil.isNotEmpty(data)) {
                            excelWriter.write(data, writeSheet);
                            totalRecords += data.size();
                        }

                        log.info("处理进度 - 第{}个sheet页:{},当前处理完第{}页,共{}页,已处理{}条,一共{}条", sheetNum, sheetName, currentPage, totalPage, totalRecords, pageCount);

                        if (!ExportTypeEnum.QUERY_RESULT.getCode().equals(exportType)) {
                            break;
                        }
                    }
                }

                sheetNum++;
            }

            excelWriter.finish();

            String fileName = generateFileName(fileNamePrefix);
            String filePath = ossClientService.uploadTemporary(new ByteArrayInputStream(out.toByteArray()), generateOssPath());

            log.info("生成文件成功,fileName:{},filePath:{}", fileName, filePath);
            return new ExportResult(fileName, filePath);
        } catch (Exception e) {
            log.error("导出报错", e);
            throw new PromptException("导出失败");
        }
    }
}
