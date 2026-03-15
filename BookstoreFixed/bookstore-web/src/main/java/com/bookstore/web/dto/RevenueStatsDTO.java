package com.bookstore.web.dto;

public class RevenueStatsDTO {
    private Long totalRevenue;
    private Integer completedOrdersCount;
    private DateRangeDTO period;
    
    public RevenueStatsDTO() {}
    
    public RevenueStatsDTO(Long totalRevenue, Integer completedOrdersCount, DateRangeDTO period) {
        this.totalRevenue = totalRevenue;
        this.completedOrdersCount = completedOrdersCount;
        this.period = period;
    }
    
    public Long getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Long totalRevenue) { this.totalRevenue = totalRevenue; }
    
    public Integer getCompletedOrdersCount() { return completedOrdersCount; }
    public void setCompletedOrdersCount(Integer completedOrdersCount) { this.completedOrdersCount = completedOrdersCount; }
    
    public DateRangeDTO getPeriod() { return period; }
    public void setPeriod(DateRangeDTO period) { this.period = period; }
}