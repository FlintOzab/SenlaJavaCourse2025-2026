package com.bank.dto;

import java.math.BigDecimal;
import java.io.Serializable;

public class TransferMessage implements Serializable {
    private String id;
    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;
    
    public TransferMessage() {}
    
    public TransferMessage(String id, String fromAccountId, String toAccountId, BigDecimal amount) {
        this.id = id;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(String fromAccountId) { this.fromAccountId = fromAccountId; }
    
    public String getToAccountId() { return toAccountId; }
    public void setToAccountId(String toAccountId) { this.toAccountId = toAccountId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    @Override
    public String toString() {
        return String.format("TransferMessage{id='%s', from='%s', to='%s', amount=%s}", 
                            id, fromAccountId, toAccountId, amount);
    }
}