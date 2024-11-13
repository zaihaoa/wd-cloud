package ${package.Controller};

import org.springframework.web.bind.annotation.*;
<#if restControllerStyle>
    import org.springframework.web.bind.annotation.RestController;
<#else>
    import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
    import ${superControllerClassPackage};
</#if>
<#if swagger>
    import io.swagger.annotations.Api;
    import io.swagger.annotations.ApiOperation;
</#if>
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import javax.validation.Valid;
import ${package.Parent}.aop.EnableLog;
import ${package.Parent}.pojo.query.${entity}Query;
import ${package.Parent}.pojo.dto.${entity}DTO;
import ${package.Parent}.pojo.vo.${entity}VO;
import ${package.Parent}.base.Result;
import ${package.Service}.${table.serviceName};

import java.util.List;
/**
* ${table.comment!} 前端控制器
*
* @author ${author}
* @since ${date}
*/
<#if swagger>
    @Api(value = "${table.comment!} API", tags = "${table.comment!}")
</#if>
@RequiredArgsConstructor
<#if restControllerStyle>
    @RestController
<#else>
    @Controller
</#if>
@RequestMapping("<#if package.ModuleName?? && package.ModuleName != "">/${package.ModuleName}</#if>/<#if controllerMappingHyphenStyle>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
<#if kotlin>
class ${table.controllerName}<#if superControllerClass??> : ${superControllerClass}()</#if>
<#else>
<#if superControllerClass??>
    public class ${table.controllerName} extends ${superControllerClass} {
<#else>
    public class ${table.controllerName} {
</#if>
private final ${table.serviceName} service;

<#if swagger>
    @ApiOperation("新增 ${table.comment!}")
<#else>
    /**
    * 新增 ${table.comment!}
    *
    * @param dto 参数
    */
</#if>
@PostMapping
@EnableLog("新增 ${table.comment!}")
public Result
<Void> add${entity}(@Valid @RequestBody ${entity}DTO dto) {
    service.add${entity}(dto);
    return Result.success();
    }

    <#if swagger>
        @ApiOperation("修改 ${table.comment!}")
    <#else>
        /**
        * 修改 ${table.comment!}
        *
        * @param id 主键
        * @param dto 参数
        */
    </#if>
    @PutMapping("/{id}")
    @EnableLog("修改 ${table.comment!}")
    public Result
    <Void> modify${entity}(@PathVariable Long id, @Valid @RequestBody ${entity}DTO dto) {
        dto.setId(id);
        service.modify${entity}(dto);
        return Result.success();
        }

        <#if swagger>
            @ApiOperation("删除 ${table.comment!}")
        <#else>
            /**
            * 删除 ${table.comment!}
            *
            * @param id 主键
            */
        </#if>
        @DeleteMapping("/{id}")
        @EnableLog("删除 ${table.comment!}")
        public Result
        <Void> delete${entity}(@PathVariable Long id) {
            service.delete${entity}(id);
            return Result.success();
            }


            <#if swagger>
                @ApiOperation("根据id获取 ${table.comment!} 详情")
            <#else>
                /**
                * 根据id获取 ${table.comment!} 详情
                *
                * @param id 主键
                */
            </#if>
            @GetMapping("/{id}")
            @EnableLog("根据id获取 ${table.comment!} 详情")
            public Result
            <${entity}VO> query${entity}ById(@PathVariable Long id) {
                ${entity}VO vo = service.query${entity}ById(id);
                return Result.success(vo);
                }


                <#if swagger>
                    @ApiOperation("分页查询 ${table.comment!}")
                <#else>
                    /**
                    * 分页查询 ${table.comment!}
                    *
                    * @param dto 参数
                    * @return
                    */
                </#if>
                @PostMapping("/page")
                @EnableLog("分页查询 ${table.comment!} ")
                public Result
                <List
                <${entity}VO>> pagingList(@Valid @RequestBody ${entity}Query query) {
                    Page
                    <${entity}VO> page = service.pageList(query);
                        return Result.from(page);
                        }
                        }
                        </#if>
