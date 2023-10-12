package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(uniqueConstraints =
@UniqueConstraint(columnNames = {"model", "series"}))
public class APACSerial implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String model;
    //    @ManyToOne(fetch = FetchType.EAGER)
    //    private MetaSeries metaSeries;
    private String series;
    private String brand;       // Hyster or Yale
    // private String line;
    private String quoteReference;
    private String plant;
    private String remarks;
}
