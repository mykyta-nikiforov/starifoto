package ua.in.photomap.common.photo.model.dto.batch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.in.photomap.common.photo.model.dto.UserBasicDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUsersByIdsResponse {
    private List<UserBasicDTO> users;
}
