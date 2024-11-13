package ${package.Service};

import ${superServiceClassPackage};
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${package.Entity}.${entity};
import ${package.Parent}.pojo.query.${entity}Query;
import ${package.Parent}.pojo.dto.${entity}DTO;
import ${package.Parent}.pojo.vo.${entity}VO;


/**
* <p>
    * ${table.comment!} 服务类
    * </p>
*
* @author ${author}
* @since ${date}
*/
<#if kotlin>
interface ${table.serviceName} : ${superServiceClass}<${entity}>
<#else>
public interface ${table.serviceName} extends ${superServiceClass}<${entity}> {

/**
* 新增 ${table.comment!}
*
* @param dto 参数
*/
void add${entity}(${entity}DTO dto);

/**
* 修改 ${table.comment!}
*
* @param dto 参数
*/
void modify${entity}(${entity}DTO dto);

/**
* 删除 ${table.comment!}
*
* @param id 主键
*/
void delete${entity}(Long id);

/**
* 根据id获取 ${table.comment!} 详情
*
* @param id 主键
*/
${entity}VO query${entity}ById(Long id);


/**
* 分页查询 ${table.comment!}
*
* @param dto 参数
* @return
*/
Page<${entity}VO> pageList(${entity}Query query);
    }
    </#if>
