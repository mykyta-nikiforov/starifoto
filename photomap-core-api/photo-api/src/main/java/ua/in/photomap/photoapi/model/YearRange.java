package ua.in.photomap.photoapi.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YearRange {
    private Short start;
    private Short end;
}