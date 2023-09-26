package com.hysteryale.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NovoQuote {

    @Id
    private String quoteNumber;
    private String description;
    @OneToMany(fetch = FetchType.EAGER)
    private List<BookingOrder> bookingOrders;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Part> parts;
    private String opportunityName;

}
