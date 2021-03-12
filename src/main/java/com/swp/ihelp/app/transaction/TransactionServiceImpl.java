package com.swp.ihelp.app.transaction;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.transaction.request.TransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

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
            senderAccount.setBalancePoint(senderAccount.getBalancePoint() - point);
            receiverAccount.setBalancePoint(receiverAccount.getBalancePoint() + point);

            TransactionEntity transactionEntity = TransactionRequest.convertToEntity(request);
            transactionEntity.setDate(System.currentTimeMillis());

            transactionRepository.save(transactionEntity);
            accountRepository.save(senderAccount);
            accountRepository.save(receiverAccount);
        } else {
            throw new RuntimeException("Account " + senderEmail + " does not have enough point.");
        }
    }
}
