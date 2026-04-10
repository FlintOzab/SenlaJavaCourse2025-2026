package com.bank.model;

public enum TransferStatus {
    SUCCESS("готово"),
    FAILED("завершилось с ошибкой");
    
    private final String description;
    
    TransferStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}