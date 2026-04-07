package com.bank.consumer;

import com.bank.model.KafkaConstants;
import com.bank.dto.TransferMessage;
import com.bank.model.Account;
import com.bank.model.Transfer;
import com.bank.model.TransferStatus;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransferRepository transferRepository;
    
    @KafkaListener(
        topics = KafkaConstants.TRANSFER_TOPIC,
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void processTransfers(
            @Payload List<TransferMessage> messages,
            @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets,
            Acknowledgment acknowledgment) {
        
        logger.info("Received batch of {} messages from partitions: {}, offsets: {}", 
                messages.size(), partitions, offsets);
        
        for (int i = 0; i < messages.size(); i++) {
            TransferMessage message = messages.get(i);
            int partition = partitions.get(i);
            long offset = offsets.get(i);
            
            logger.info("Starting processing: id={}, partition={}, offset={}", 
                    message.getId(), partition, offset);
            
            try {
                processSingleTransfer(message);
                logger.info("Successfully processed: id={}, partition={}, offset={}", 
                        message.getId(), partition, offset);
            } catch (Exception e) {
                logger.error("Failed to process: id={}, partition={}, offset={}, error={}", 
                        message.getId(), partition, offset, e.getMessage(), e);
                saveFailedTransfer(message);
            }
        }
        
        acknowledgment.acknowledge();
        logger.info("Batch processing completed, acknowledged {} messages", messages.size());
    }
    
    @Transactional
    protected void processSingleTransfer(TransferMessage message) {
        Optional<Account> fromAccountOpt = accountRepository.findByAccountId(message.getFromAccountId());
        Optional<Account> toAccountOpt = accountRepository.findByAccountId(message.getToAccountId());
        
        if (!fromAccountOpt.isPresent() || !toAccountOpt.isPresent()) {
            String error = String.format("Validation failed - accounts not found: from=%s, to=%s", 
                    message.getFromAccountId(), message.getToAccountId());
            logger.error(error);
            throw new IllegalArgumentException(error);
        }
        
        Account fromAccount = fromAccountOpt.get();
        Account toAccount = toAccountOpt.get();
        
        if (fromAccount.getBalance().compareTo(message.getAmount()) < 0) {
            String error = String.format("Validation failed - insufficient funds: account %s balance=%s, required=%s",
                    fromAccount.getAccountId(), fromAccount.getBalance(), message.getAmount());
            logger.error(error);
            throw new IllegalArgumentException(error);
        }
        
        try {
            fromAccount.setBalance(fromAccount.getBalance().subtract(message.getAmount()));
            toAccount.setBalance(toAccount.getBalance().add(message.getAmount()));
            
            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);
            
            Transfer transfer = new Transfer(
                    message.getId(),
                    message.getFromAccountId(),
                    message.getToAccountId(),
                    message.getAmount(),
                    TransferStatus.SUCCESS
            );
            transferRepository.save(transfer);
            
            logger.info("Transfer executed successfully: id={}, from={}, to={}, amount={}", 
                    message.getId(), message.getFromAccountId(), 
                    message.getToAccountId(), message.getAmount());
            
        } catch (Exception e) {
            logger.error("Transaction failed for transfer: id={}, error={}", 
                    message.getId(), e.getMessage(), e);
            throw new RuntimeException("Transaction failed", e);
        }
    }
    
    @Transactional
    protected void saveFailedTransfer(TransferMessage message) {
        try {
            Transfer failedTransfer = new Transfer(
                    message.getId(),
                    message.getFromAccountId(),
                    message.getToAccountId(),
                    message.getAmount(),
                    TransferStatus.FAILED
            );
            transferRepository.save(failedTransfer);
            logger.info("Saved failed transfer record: id={}", message.getId());
        } catch (Exception e) {
            logger.error("Failed to save error record for transfer: id={}, error={}", 
                    message.getId(), e.getMessage(), e);
        }
    }
}