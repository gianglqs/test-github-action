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

    //TODO: we need to consider to use Set here as I encountered https://www.baeldung.com/java-hibernate-multiplebagfetchexception
    // in another post that I read it may cause performance exception https://vladmihalcea.com/spring-data-jpa-multiplebagfetchexception/
    @OneToMany(fetch = FetchType.EAGER)
    private Set<BookingOrder> bookingOrders;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Part> parts;

    private String opportunityName;

}
