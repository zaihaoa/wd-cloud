package com.wd.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Id")
public class Id {

    @Schema(description = "ID", example = "123")
    @NotNull(message = "id not null")
    private Long id;
}
