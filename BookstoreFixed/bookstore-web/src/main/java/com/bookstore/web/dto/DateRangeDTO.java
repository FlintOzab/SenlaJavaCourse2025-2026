package com.bookstore.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class DateRangeDTO {
    @JsonFormat(pattern = "dd.MM.yyyy")
    @NotNull(message = "Start date is required")
    private Date startDate;
    
    @JsonFormat(pattern = "dd.MM.yyyy")
    @NotNull(message = "End date is required")
    private Date endDate;
    
    public DateRangeDTO() {}
    
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
}