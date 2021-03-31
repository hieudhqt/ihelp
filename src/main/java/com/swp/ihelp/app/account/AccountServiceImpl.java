package com.swp.ihelp.app.account;

import com.swp.ihelp.app.account.request.ProfileUpdateRequest;
import com.swp.ihelp.app.account.request.SignUpRequest;
import com.swp.ihelp.app.account.response.AccountGeneralResponse;
import com.swp.ihelp.app.account.response.ParticipantsMapping;
import com.swp.ihelp.app.account.response.ProfileResponse;
import com.swp.ihelp.app.event.EventRepository;
import com.swp.ihelp.app.image.ImageRepository;
import com.swp.ihelp.app.service.ServiceRepository;
import com.swp.ihelp.exception.EntityExistedException;
import com.swp.ihelp.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

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
        String errorMessage = "";
        boolean isExisted = false;
        if (accountRepository.existsById(signUpRequest.getEmail())) {
            errorMessage = errorMessage + "Email: " + signUpRequest.getEmail() + " is used\n";
            isExisted = true;
        }
        if (accountRepository.existsByPhone(signUpRequest.getPhone())) {
            errorMessage = errorMessage + "Phone number: " + signUpRequest.getPhone() + " is used\n";
            isExisted = true;
        }
        if (!isExisted) {
            AccountEntity accountEntity = SignUpRequest.convertToEntity(signUpRequest);
            accountRepository.save(accountEntity);
        } else {
            throw new EntityExistedException(errorMessage);
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
        } else {
            throw new RuntimeException("Do not find account: " + email);
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
    public ProfileResponse update(ProfileUpdateRequest request) throws Exception {
        if (!accountRepository.existsById(request.getEmail())) {
            throw new EntityNotFoundException("Account " + request.getEmail() + " does not exist");
        }
        AccountEntity accountEntity = accountRepository.getOne(request.getEmail());
        accountEntity.setFullName(request.getFullname());
        accountEntity.setPhone(request.getPhone());
        accountEntity.setGender(request.getGender());
        accountEntity.setDateOfBirth(new Date(request.getDateOfBirth().getTime()));

        int totalJoinedEvents = eventRepository.getTotalJoinedEvents(request.getEmail());
        int totalHostEvents = eventRepository.getTotalHostEvents(request.getEmail());
        int totalUsedServices = serviceRepository.getTotalUsedServices(request.getEmail());
        int totalHostServices = serviceRepository.getTotalHostServices(request.getEmail());
        String imageUrl = imageRepository.findAvatarByEmail(request.getEmail());

        return new ProfileResponse(accountRepository.save(accountEntity))
                .setTotalJoinedEvents(totalJoinedEvents)
                .setTotalHostEvents(totalHostEvents)
                .setTotalUsedServices(totalUsedServices)
                .setTotalHostServices(totalHostServices)
                .setImageUrl(imageUrl);
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
    public List<Map<String, Object>> findByEventId(String eventId) throws Exception {
        List<ParticipantsMapping> joinedAccount = accountRepository.findByEventId(eventId);
        return convertMappingToParticipantResponse(joinedAccount);
    }

    @Override
    public List<Map<String, Object>> findByServiceId(String serviceId) throws Exception {
        List<ParticipantsMapping> usedAccount = accountRepository.findByServiceId(serviceId);
        return convertMappingToParticipantResponse(usedAccount);
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

    @Override
    public void updateRole(String email, String roleId) throws Exception {
        accountRepository.updateRole(email, roleId);
    }

    @Override
    public void updateDeviceToken(String email, String deviceToken) throws Exception {
        accountRepository.updateDeviceToken(email, deviceToken);
    }

//    private List<Map<String, Object>> convertObjectToParticipantResponse(List<Object[]> participants) {
//        List<Map<String, Object>> result = new ArrayList<>();
//        if (!participants.isEmpty()) {
//            for (Object obj[] : participants) {
//                String email = (String) obj[0];
//                String imageUrl = (String) obj[1];
//                Map<String, Object> map = new HashMap<>();
//                map.put("email", email);
//                map.put("imageUrl", imageUrl);
//                result.add(map);
//            }
//        }
//        return result;
//    }

    private List<Map<String, Object>> convertMappingToParticipantResponse(List<ParticipantsMapping> participants) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (!participants.isEmpty()) {
            for (ParticipantsMapping obj : participants) {
                String email = obj.getEmail();
                String fullname = obj.getFullname();
                Boolean gender = obj.getGender();
                String phone = obj.getPhone();
                Integer balancePoint = obj.getBalancePoint();
                Timestamp joinDate = obj.getJoinDate();
                String imageUrl = obj.getImageUrl();
                Map<String, Object> map = new HashMap<>();
                map.put("email", email);
                map.put("fullname", fullname);
                map.put("gender", gender);
                map.put("phone", phone);
                map.put("balancePoint", balancePoint);
                map.put("joinDate", joinDate);
                map.put("imageUrl", imageUrl);
                result.add(map);
            }
        }
        return result;
    }

}
