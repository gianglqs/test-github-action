package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class APACSerial {
    @Id
    private String model;
    @ManyToOne(fetch = FetchType.EAGER)
    private MetaSeries metaSeries;
    private String brand;       // Hyster or Yale
    private String line;
    private String quoteReference;
    private String plant;
    private String remarks;
}
