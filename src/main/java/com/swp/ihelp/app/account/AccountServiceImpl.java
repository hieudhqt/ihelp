package com.swp.ihelp.app.account;

import com.swp.ihelp.app.account.request.SignUpRequest;
import com.swp.ihelp.app.account.response.AccountGeneralResponse;
import com.swp.ihelp.app.account.response.ProfileResponse;
import com.swp.ihelp.app.event.EventRepository;
import com.swp.ihelp.app.image.ImageRepository;
import com.swp.ihelp.app.service.ServiceRepository;
import com.swp.ihelp.exception.EntityExistedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private ServiceRepository serviceRepository;
    private EventRepository eventRepository;
    private ImageRepository imageRepository;

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, ServiceRepository serviceRepository, EventRepository eventRepository, ImageRepository imageRepository) {
        this.accountRepository = accountRepository;
        this.serviceRepository = serviceRepository;
        this.eventRepository = eventRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    public void insert(SignUpRequest signUpRequest) throws Exception {
        if (!accountRepository.existsById(signUpRequest.getEmail())) {
            AccountEntity accountEntity = SignUpRequest.convertToEntity(signUpRequest);
            accountRepository.save(accountEntity);
        } else {
            throw new EntityExistedException("Email " + signUpRequest.getEmail() + " is existed");
        }
    }

    @Override
    public ProfileResponse findById(String email) throws Exception {
        Optional<AccountEntity> result = accountRepository.findById(email);
        AccountEntity accountEntity = null;
        int totalJoinedEvents;
        int totalHostEvents;
        int totalUsedServices;
        int totalHostServices;
        String imageUrl;
        if (result.isPresent()) {
            accountEntity = result.get();
            totalJoinedEvents = eventRepository.getTotalJoinedEvents(email);
            totalHostEvents = eventRepository.getTotalHostEvents(email);
            totalUsedServices = serviceRepository.getTotalUsedServices(email);
            totalHostServices = serviceRepository.getTotalHostServices(email);
            imageUrl = imageRepository.findAvatarByEmail(email);
            System.out.println(totalJoinedEvents + " " + totalHostEvents + " " + totalUsedServices + " " + totalHostServices + " " + imageUrl);
        } else {
            throw new RuntimeException("Do not find account!");
        }
        return new ProfileResponse(accountEntity)
                .setTotalJoinedEvents(totalJoinedEvents)
                .setTotalHostEvents(totalHostEvents)
                .setTotalUsedServices(totalUsedServices)
                .setTotalHostServices(totalHostServices)
                .setImageUrl(imageUrl);
    }

    @Override
    public List<AccountGeneralResponse> findAll() throws Exception {
        List<AccountEntity> result = accountRepository.findAll();
        return AccountGeneralResponse.convertToListResponse(result);
    }

    @Override
    public String findRoleById(String email) throws Exception {
        boolean isExisted = accountRepository.existsById(email);
        String role = null;
        if (isExisted) {
            role = accountRepository.findRoleByEmail(email);
        }
        return role;
    }

    @Override
    public boolean delete(String email) throws Exception {
        boolean isExisted = accountRepository.existsById(email);
        boolean isDeleted = false;
        if (isExisted) {
            accountRepository.deleteById(email);
            isDeleted = true;
        }
        return isDeleted;
    }

    @Override
    public List<AccountGeneralResponse> findByEventId(String eventId) throws Exception {
        List<AccountEntity> result = accountRepository.findByEventId(eventId);
        return AccountGeneralResponse.convertToListResponse(result);
    }

    @Override
    public List<AccountGeneralResponse> findByServiceId(String serviceId) throws Exception {
        List<AccountEntity> result = accountRepository.findByServiceId(serviceId);
        return AccountGeneralResponse.convertToListResponse(result);
    }

    @Override
    public void updateStatus(String email, String statusId) throws Exception {
        accountRepository.updateStatus(email, statusId);
    }

    @Transactional
    @Override
    public void updatePassword(String email, String password) throws Exception {
        accountRepository.updatePassword(email, encoder.encode(password));
    }

}
