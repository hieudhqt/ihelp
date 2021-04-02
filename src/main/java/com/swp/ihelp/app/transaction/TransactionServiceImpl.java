package com.swp.ihelp.app.transaction;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.transaction.request.TransactionRequest;
import com.swp.ihelp.app.transaction.response.TransactionResponse;
import com.swp.ihelp.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Value("${paging.page-size}")
    private int pageSize;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public void save(TransactionRequest request) throws Exception {
        String senderEmail = request.getSenderEmail();
        String receiverEmail = request.getReceiverEmail();
        int point = request.getPoint();

        AccountEntity senderAccount = accountRepository.getOne(senderEmail);
        AccountEntity receiverAccount = accountRepository.getOne(receiverEmail);

        if (senderAccount.getBalancePoint() >= point) {
            senderAccount.decreaseBalancePoint(point);
            receiverAccount.addBalancePoint(point);

            TransactionEntity transactionEntity = TransactionRequest.convertToEntity(request);
            transactionEntity.setDate(new Timestamp(System.currentTimeMillis()));

            transactionRepository.save(transactionEntity);
            accountRepository.save(senderAccount);
            accountRepository.save(receiverAccount);
        } else {
            throw new RuntimeException("Account " + senderEmail + " does not have enough point.");
        }
    }

    @Override
    public Map<String, Object> findByEmail(String email, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<TransactionEntity> pageTransactions = transactionRepository.findAllByEmail(email, paging);
        if (pageTransactions.isEmpty()) {
            throw new EntityNotFoundException("Transaction not found.");
        }

        List<TransactionEntity> listTransactions = pageTransactions.getContent();
        List<TransactionResponse> transactionResponses
                = TransactionResponse.convertToResponseList(listTransactions);

        Map<String, Object> response = new HashMap<>();
        response.put("transactions", transactionResponses);
        response.put("currentPage", pageTransactions.getNumber());
        response.put("totalItems", pageTransactions.getTotalElements());
        response.put("totalPages", pageTransactions.getTotalPages());
        return response;
    }

}
