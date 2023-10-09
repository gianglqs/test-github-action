package com.hysteryale.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Region {
    @Id
    private String id;
    private String region;
}
