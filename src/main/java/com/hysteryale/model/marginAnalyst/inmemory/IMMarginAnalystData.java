package com.hysteryale.model.marginAnalyst.inmemory;

import com.hysteryale.model.marginAnalyst.MarginAnalystData;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class IMMarginAnalystData extends MarginAnalystData {
    private String fileUUID;
}
