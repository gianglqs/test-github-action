package com.hysteryale.service;

import com.hysteryale.model.UnitFlags;
import com.hysteryale.repository.UnitFlagsRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UnitFlagsService {
    @Autowired
    private UnitFlagsRepository unitFlagsRepository;

    public UnitFlagsService(UnitFlagsRepository unitFlagsRepository) {
        this.unitFlagsRepository = unitFlagsRepository;
    }

    public void addUnitFlags(UnitFlags unitFlags) {
        unitFlagsRepository.save(unitFlags);
    }
    public List<UnitFlags> getAllUnitFlags() {
        return unitFlagsRepository.findAll();
    }
    public void addListOfUnitFlags(List<UnitFlags> unitFlagsList) {
        unitFlagsRepository.saveAll(unitFlagsList);
    }
    public Optional<UnitFlags> getUnitFlagsByUnit(String unit) {
        return unitFlagsRepository.findById(unit);
    }
    public void saveUnitFlagsChanges(UnitFlags dbUnitFlags, UnitFlags tempUnitFlags) {
        //setting all changes
        dbUnitFlags.setUnit(tempUnitFlags.getUnit());
        dbUnitFlags.setDescription(tempUnitFlags.getDescription());
        dbUnitFlags.setUClass(tempUnitFlags.getUClass());
        dbUnitFlags.setReadyForDistribution(tempUnitFlags.getReadyForDistribution());
        dbUnitFlags.setEnableGLReadiness(tempUnitFlags.getEnableGLReadiness());
        dbUnitFlags.setFullyAttributed(tempUnitFlags.getFullyAttributed());
        dbUnitFlags.setReadyForPartsCosting(tempUnitFlags.getReadyForPartsCosting());
        dbUnitFlags.setCreatedDate(tempUnitFlags.getCreatedDate());
        dbUnitFlags.setCancelled(tempUnitFlags.getCancelled());
    }
}
