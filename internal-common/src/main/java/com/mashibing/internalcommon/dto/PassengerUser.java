package com.mashibing.internalcommon.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PassengerUser {

    private long passengerId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String passengerPhone;

    private String passengerName;

    private String profilePhoto;

    private byte passengerGender;

    private byte state;
}
