package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SAPCustomer {
    @Id
    private String customer;
    private String customerCountry;
    private String customerName;
    private String legacySystemId;
    private int legacySourceCompanyCode;
    private int legacyFieldValue;
    private int sapFieldValue;

}
