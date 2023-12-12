package com.hysteryale.model.competitor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CompetitorColor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String competitorName;
    private String colorCode;

    public CompetitorColor(String competitorName, String colorCode) {
        this.competitorName = competitorName;
        this.colorCode = colorCode;
    }
}
