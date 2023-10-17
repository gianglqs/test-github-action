package com.hysteryale.model.marginAnalyst.inmemory;

import com.hysteryale.model.marginAnalyst.MarginAnalystSummary;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class IMMarginAnalystSummary extends MarginAnalystSummary {
    private String fileUUID;
}
