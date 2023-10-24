package com.hysteryale.service.marginAnalyst;

import com.hysteryale.model.User;
import com.hysteryale.model.marginAnalyst.MarginAnalystFileUpload;
import com.hysteryale.repository.marginAnalyst.MarginAnalystFileUploadRepository;
import com.hysteryale.service.UserService;
import com.hysteryale.utils.EnvironmentUtils;
import com.hysteryale.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@EnableTransactionManagement
public class MarginAnalystFileUploadService {
    @Resource
    MarginAnalystFileUploadRepository marginAnalystFileUploadRepository;
    @Resource
    UserService userService;

    /**
     * Save uploaded excel into disk and file information into db
     * @param excelFile
     * @param authentication contains upload person's email
     * @return UUID string
     */
    public String saveMarginAnalystFileUpload(MultipartFile excelFile, Authentication authentication) throws IOException {
        // Find who uploads this file
        String uploadedByEmail = authentication.getName();
        Optional<User> optionalUploadedBy = userService.getActiveUserByEmail(uploadedByEmail);

        if(optionalUploadedBy.isPresent())
        {
            User uploadedBy = optionalUploadedBy.get();
            MarginAnalystFileUpload marginAnalystFileUpload = new MarginAnalystFileUpload();
            Date uploadedTime = new Date();
            String strUploadedTime = (new SimpleDateFormat("ddMMyyyyHHmmss").format(uploadedTime));

            // generate random UUID
            marginAnalystFileUpload.setUuid(UUID.randomUUID().toString());
            marginAnalystFileUpload.setUploadedBy(uploadedBy);
            marginAnalystFileUpload.setUploadedTime(uploadedTime);

            // append suffix into fileName
            marginAnalystFileUpload.setFileName(FileUtils.hashFileName(excelFile.getOriginalFilename()) + "_" + strUploadedTime + ".xlsx");

            // save information to db
            marginAnalystFileUploadRepository.save(marginAnalystFileUpload);

            // save file into disk
            String baseFolder = EnvironmentUtils.getEnvironmentValue("upload_files.base-folder");
            File file = new File(baseFolder + "/" + marginAnalystFileUpload.getFileName());

            if(file.createNewFile()){
                log.info("File " + marginAnalystFileUpload.getFileName() + " created");
                excelFile.transferTo(file);
            }
            else {
                log.info("Can not create new file: " + marginAnalystFileUpload.getFileName());
            }
            return marginAnalystFileUpload.getUuid();
        }
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find user with email: " + uploadedByEmail);
    }

    /**
     * Getting the fileName by UUID String for reading from disk
     */
    public String getFileNameByUUID(String uuid) {
        String fileName = marginAnalystFileUploadRepository.getFileNameByUUID(uuid);
        if(fileName.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find file name with uuid: " + uuid);
        return fileName;
    }
}