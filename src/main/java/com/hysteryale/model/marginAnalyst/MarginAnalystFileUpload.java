package com.hysteryale.model.marginAnalyst;

import com.hysteryale.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MarginAnalystFileUpload {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fileUploadedSeq")
    private int id;
    private String uuid;
    private String fileName;
    @ManyToOne(fetch = FetchType.EAGER)
    private User uploadedBy;
    private Date uploadedTime;
}
