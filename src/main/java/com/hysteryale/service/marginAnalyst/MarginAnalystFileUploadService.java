package com.hysteryale.service.marginAnalyst;

import com.hysteryale.model.User;
import com.hysteryale.model.marginAnalyst.MarginAnalystFileUpload;
import com.hysteryale.repository.marginAnalyst.MarginAnalystFileUploadRepository;
import com.hysteryale.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class MarginAnalystFileUploadService {
    @Resource
    MarginAnalystFileUploadRepository marginAnalystFileUploadRepository;
    @Resource
    UserService userService;

    public void saveMarginAnalystFileUpload(MultipartFile excelFile, Authentication authentication) throws IOException {
        // Find who uploads this file
        String uploadedByEmail = authentication.getName();
        Optional<User> optionalUploadedBy = userService.getActiveUserByEmail(uploadedByEmail);

        if(optionalUploadedBy.isPresent())
        {
            User uploadedBy = optionalUploadedBy.get();

            MarginAnalystFileUpload marginAnalystFileUpload = new MarginAnalystFileUpload();
            marginAnalystFileUpload.setFileName(excelFile.getOriginalFilename());
            marginAnalystFileUpload.setUuid(UUID.nameUUIDFromBytes(excelFile.getBytes()).toString());
            marginAnalystFileUpload.setUploadedBy(uploadedBy);
            marginAnalystFileUpload.setUploadedTime(new Date());

            marginAnalystFileUploadRepository.save(marginAnalystFileUpload);
        }
    }
}
