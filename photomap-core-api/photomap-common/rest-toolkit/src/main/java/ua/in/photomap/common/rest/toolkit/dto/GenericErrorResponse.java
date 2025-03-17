package ua.in.photomap.common.rest.toolkit.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GenericErrorResponse {
    private String timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
}