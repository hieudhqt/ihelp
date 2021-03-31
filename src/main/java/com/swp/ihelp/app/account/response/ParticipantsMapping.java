package com.swp.ihelp.app.account.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

public interface ParticipantsMapping {

    String getEmail();
    String getFullname();
    Boolean getGender();
    String getPhone();
    Integer getBalancePoint();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    Timestamp getJoinDate();

    String getImageUrl();

}
