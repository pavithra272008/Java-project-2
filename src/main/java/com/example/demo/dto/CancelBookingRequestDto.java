package com.example.demo.dto;

public class CancelBookingRequestDto {

    private String reason;

    public CancelBookingRequestDto() {
    }

    public CancelBookingRequestDto(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
