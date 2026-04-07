package com.bank.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfers")
public class Transfer {
    @Id
    private String id;
    
    @Column(name = "from_account_id", nullable = false)
    private String fromAccountId;
    
    @Column(name = "to_account_id", nullable = false)
    private String toAccountId;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public Transfer() {}
    
    public Transfer(String id, String fromAccountId, String toAccountId, 
                   BigDecimal amount, TransferStatus status) {
        this.id = id;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(String fromAccountId) { this.fromAccountId = fromAccountId; }
    
    public String getToAccountId() { return toAccountId; }
    public void setToAccountId(String toAccountId) { this.toAccountId = toAccountId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public TransferStatus getStatus() { return status; }
    public void setStatus(TransferStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}