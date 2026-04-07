package com.bank.producer;

import com.bank.model.KafkaConstants;
import com.bank.dto.TransferMessage;
import com.bank.model.Account;
import com.bank.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ProducerService {
    private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);
    private static final int ACCOUNTS_COUNT = 1000;
    private static final BigDecimal INITIAL_BALANCE = new BigDecimal("10000.00");
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    private Map<String, Account> accountsMap = new ConcurrentHashMap<>();
    private List<String> accountIdsList = new ArrayList<>();
    private AtomicLong transferCounter = new AtomicLong(0);
    private ReentrantLock scheduledLock = new ReentrantLock();
    
    @PostConstruct
    @Transactional
    public void initializeAccounts() {
        logger.info("Initializing accounts...");
        
        if (accountRepository.count() == 0) {
            List<Account> accounts = new ArrayList<>();
            for (int i = 1; i <= ACCOUNTS_COUNT; i++) {
                String accountId = String.format("ACC-%04d", i);
                Account account = new Account(accountId, INITIAL_BALANCE);
                accounts.add(account);
                accountsMap.put(accountId, account);
                accountIdsList.add(accountId);
            }
            
            accountRepository.saveAll(accounts);
            logger.info("Generated and saved {} new accounts", ACCOUNTS_COUNT);
        } else {
            List<Account> accounts = accountRepository.findAll();
            for (Account account : accounts) {
                accountsMap.put(account.getAccountId(), account);
                accountIdsList.add(account.getAccountId());
            }
            logger.info("Loaded {} existing accounts from database", accounts.size());
        }
    }
    
    @Scheduled(fixedDelay = 200)
    public void generateTransferMessage() {
        if (!scheduledLock.tryLock()) {
            logger.debug("Previous generation still in progress, skipping...");
            return;
        }
        
        try {
            TransferMessage message = createRandomTransfer();
            sendMessageWithTransaction(message);
        } catch (Exception e) {
            logger.error("Error generating transfer message", e);
        } finally {
            scheduledLock.unlock();
        }
    }
    
    private TransferMessage createRandomTransfer() {
        Random random = new Random();
        
        String fromAccountId;
        String toAccountId;
        do {
            fromAccountId = accountIdsList.get(random.nextInt(accountIdsList.size()));
            toAccountId = accountIdsList.get(random.nextInt(accountIdsList.size()));
        } while (fromAccountId.equals(toAccountId));
        
        BigDecimal amount = BigDecimal.valueOf(1 + random.nextDouble() * 999)
                .setScale(2, RoundingMode.HALF_UP);
        
        String transferId = String.format("TRF-%d-%d", 
                System.currentTimeMillis(), 
                transferCounter.incrementAndGet());
        
        return new TransferMessage(transferId, fromAccountId, toAccountId, amount);
    }
    
    private void sendMessageWithTransaction(TransferMessage message) {
        String key = message.getFromAccountId();
        
        try {
            CompletableFuture<SendResult<String, Object>> future = 
                    kafkaTemplate.executeInTransaction(operations -> 
                        operations.send(KafkaConstants.TRANSFER_TOPIC, key, message)
                    );
            
            SendResult<String, Object> result = future.join();
            
            logger.info("Message sent successfully in transaction: id={}, partition={}, offset={}", 
                    message.getId(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
                    
        } catch (Exception e) {
            logger.error("Failed to send message in transaction: id={}, error={}", 
                    message.getId(), e.getMessage(), e);
        }
    }
}