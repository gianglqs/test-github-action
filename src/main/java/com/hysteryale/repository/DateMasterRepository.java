package com.hysteryale.repository;

import com.hysteryale.model.DateMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.GregorianCalendar;

public interface DateMasterRepository extends JpaRepository<DateMaster, GregorianCalendar> {
}
