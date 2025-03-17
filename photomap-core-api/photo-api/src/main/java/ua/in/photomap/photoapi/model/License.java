package ua.in.photomap.photoapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "license")
@Getter
@Setter
public class License extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "details_url")
    private String detailsUrl;

    @Column(name = "sequence_number")
    private Integer sequenceNumber;

    @Column(name = "active")
    private Boolean active;
}
