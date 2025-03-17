package ua.in.photomap.common.photo.model.dto.batch;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUsersByIdsRequest {
    @NotNull
    @Size(min = 1, max = 500)
    private List<Long> ids;
}
