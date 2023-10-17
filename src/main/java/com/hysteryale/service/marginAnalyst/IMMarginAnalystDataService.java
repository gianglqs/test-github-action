package com.hysteryale.service.marginAnalyst;

import com.hysteryale.model.marginAnalyst.inmemory.IMMarginAnalystData;
import com.hysteryale.model.marginAnalyst.inmemory.IMMarginAnalystSummary;
import com.hysteryale.repository.marginAnalyst.inmemory.IMMarginAnalystDataRepository;
import com.hysteryale.repository.marginAnalyst.inmemory.IMMarginAnalystSummaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class IMMarginAnalystDataService {
    @Resource
    IMMarginAnalystDataRepository imMarginAnalystDataRepository;
    @Resource
    IMMarginAnalystSummaryRepository imMarginAnalystSummaryRepository;

    //TODO: implement 2 below methods when having Excel file template from HYG


    /* @Transactional("transactionManager") annotate for using DataSource of H2 Database*/
    /**
     * Calculate MarginAnalystData and save into In-memory database
     * @param fileUUID identifier of uploaded file
     * @return calculated MarginAnalystData based on uploaded file
     */
    @Transactional("transactionManager")
    public IMMarginAnalystData calculateMarginAnalystData(String fileUUID) {
        return new IMMarginAnalystData();
    }

    /**
     * Calculate MarginAnalystSummary and save into In-memory database
     * @param fileUUID identifier of uploaded file
     * @return calculated MarginAnalystSummary based on uploaded file
     */
    @Transactional("transactionManager")
    public IMMarginAnalystSummary calculateMarginAnalystSummary(String fileUUID) {
        return new IMMarginAnalystSummary();
    }


}
