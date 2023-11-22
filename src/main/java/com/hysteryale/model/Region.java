package com.hysteryale.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "region_short_name")
    private String regionShortName;
    private String region;

    public Region(String region) {
        this.region = region;
    }
}
