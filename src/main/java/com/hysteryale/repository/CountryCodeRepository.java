package com.hysteryale.repository;

import com.hysteryale.model.CountryCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryCodeRepository extends JpaRepository<CountryCode, String> {
}
