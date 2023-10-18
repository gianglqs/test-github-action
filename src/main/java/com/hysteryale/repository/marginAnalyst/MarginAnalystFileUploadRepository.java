package com.hysteryale.repository.marginAnalyst;

import com.hysteryale.model.marginAnalyst.MarginAnalystFileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MarginAnalystFileUploadRepository extends JpaRepository<MarginAnalystFileUpload, Integer> {
    @Query("SELECT m.fileName FROM MarginAnalystFileUpload m WHERE m.uuid = ?1")
    String getFileNameByUUID(String uuid);
}
