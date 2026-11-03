package com.wd.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Id集合")
public class IdsDTO {

    @Schema(description = "ID集合", example = "[123,124]")
    @NotEmpty(message = "ids not null")
    private List<Long> ids;
}
