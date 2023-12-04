package com.hysteryale.repository.upload;

import com.hysteryale.model.marginAnalyst.MarginAnalystFileUpload;
import com.hysteryale.model.upload.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FileUploadRepository extends JpaRepository<FileUpload, Integer> {
    @Query("SELECT m.fileName FROM FileUpload m WHERE m.uuid = ?1")
    String getFileNameByUUID(String uuid);
}
