package ${package.Parent}.pojo.query;

<#if springdoc>
    import io.swagger.v3.oas.annotations.media.Schema;
<#elseif swagger>
    import io.swagger.annotations.ApiModel;
    import io.swagger.annotations.ApiModelProperty;
</#if>
<#if entityLombokModel>
    import lombok.Getter;
    import lombok.Setter;
    <#if chainModel>
        import lombok.experimental.Accessors;
    </#if>
</#if>
import ${package.Parent}.base.BasePageQuery;

/**
* <p>
    * ${table.comment!}
    * </p>
*
* @author ${author}
* @since ${date}
*/
<#if entityLombokModel>
    @Getter
    @Setter
    <#if chainModel>
        @Accessors(chain = true)
    </#if>
</#if>
<#if springdoc>
    @Schema(name = "${entity}", description = "$!{table.comment}")
<#elseif swagger>
    @ApiModel(value = "${entity} 分页参数", description = "${table.comment!}")
</#if>
public class ${entity}Query extends BasePageQuery{


}
