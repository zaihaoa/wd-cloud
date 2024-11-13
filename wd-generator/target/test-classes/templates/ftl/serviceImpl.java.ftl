package ${package.ServiceImpl};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
<#if table.serviceInterface>
    import ${package.Service}.${table.serviceName};
</#if>
import ${superServiceImplClassPackage};
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import ${package.Parent}.pojo.query.${entity}Query;
import ${package.Parent}.pojo.dto.${entity}DTO;
import ${package.Parent}.pojo.vo.${entity}VO;
import ${package.Parent}.component.convert.${entity}Convert;

import java.util.List;


/**
* <p>
    * ${table.comment!} 服务实现类
    * </p>
*
* @author ${author}
* @since ${date}
*/
@Slf4j
@Service
@AllArgsConstructor
<#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>()<#if table.serviceInterface>, ${table.serviceName}</#if> {

}
<#else>
public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}><#if table.serviceInterface> implements ${table.serviceName}</#if> {

private final ${entity}Convert INSTANCE = ${entity}Convert.INSTANCE;

/**
* 新增 ${table.comment!}
*
* @param dto 参数
*/
@Override
@Transactional(rollbackFor = Exception.class)
public void add${entity}(${entity}DTO dto){
${entity} entity = INSTANCE.toEntity(dto);
super.save(entity);
}

/**
* 修改 ${table.comment!}
*
* @param dto 参数
*/
@Override
@Transactional(rollbackFor = Exception.class)
public void modify${entity}(${entity}DTO dto){
${entity} entity = INSTANCE.toEntity(dto);
super.updateById(entity);
}

/**
* 删除 ${table.comment!}
*
* @param id 主键
*/
@Override
@Transactional(rollbackFor = Exception.class)
public void delete${entity}(Long id){
super.removeById(id);
}

/**
* 根据id获取 ${table.comment!} 详情
*
* @param id 主键
*/
@Override
public ${entity}VO query${entity}ById(Long id){
${entity} entity = super.getById(id);
return INSTANCE.toVO(entity);
}


/**
* 分页查询 ${table.comment!}
*
* @param dto 参数
* @return
*/
@Override
public Page<${entity}VO> pageList(${entity}Query query) {
    Page<${entity}> page = page(new Page<>(query.getPageNum(), query.getPageSize()),
    new LambdaQueryWrapper<${entity}>());
    List<${entity}VO> resultList = INSTANCE.toVOS(page.getRecords());
        return new Page<${entity}VO>().setTotal(page.getTotal()).setRecords(resultList);
            }
            }
            </#if>
