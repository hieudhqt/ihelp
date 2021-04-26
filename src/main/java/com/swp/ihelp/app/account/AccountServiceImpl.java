package com.swp.ihelp.app.account;

import com.swp.ihelp.app.account.request.ProfileUpdateRequest;
import com.swp.ihelp.app.account.request.SignUpRequest;
import com.swp.ihelp.app.account.response.AccountGeneralResponse;
import com.swp.ihelp.app.account.response.NotEvaluatedParticipantsMapping;
import com.swp.ihelp.app.account.response.ParticipantsMapping;
import com.swp.ihelp.app.account.response.ProfileResponse;
import com.swp.ihelp.app.event.EventRepository;
import com.swp.ihelp.app.image.ImageEntity;
import com.swp.ihelp.app.image.ImageRepository;
import com.swp.ihelp.app.reward.RewardRepository;
import com.swp.ihelp.app.service.ServiceRepository;
import com.swp.ihelp.exception.EntityExistedException;
import com.swp.ihelp.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Service
public class AccountServiceImpl implements AccountService {

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final AccountRepository accountRepository;
    private final ServiceRepository serviceRepository;
    private final EventRepository eventRepository;
    private final ImageRepository imageRepository;
    private final RewardRepository rewardRepository;

    @Value("${paging.page-size}")
    private int pageSize;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, ServiceRepository serviceRepository, EventRepository eventRepository, ImageRepository imageRepository, RewardRepository rewardRepository) {
        this.accountRepository = accountRepository;
        this.serviceRepository = serviceRepository;
        this.eventRepository = eventRepository;
        this.imageRepository = imageRepository;
        this.rewardRepository = rewardRepository;
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
    public Map<String, Object> findAll(int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize,
                Sort.by("email").ascending());
        Page<AccountEntity> pageAccounts = accountRepository.findAll(paging);
        if (pageAccounts.isEmpty()) {
            throw new EntityNotFoundException("Account not found.");
        }

        return getAccountResponseMap(pageAccounts);
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
    public Map<String, Object> getTopContributor(int page, int pageSize) throws Exception {
        //Get start and end date of quarter in a year.
        LocalDate currentDate = LocalDate.now();
        String startDate = "", endDate = "";
        switch (currentDate.getMonthValue()) {
            case 1:
            case 2:
            case 3:
                startDate = currentDate.getYear() + "-01-01";
                endDate = currentDate.getYear() + "-03-31";
                break;
            case 4:
            case 5:
            case 6:
                startDate = currentDate.getYear() + "-04-01";
                endDate = currentDate.getYear() + "-06-30";
                break;
            case 7:
            case 8:
            case 9:
                startDate = currentDate.getYear() + "-07-01";
                endDate = currentDate.getYear() + "-09-30";
                break;
            case 10:
            case 11:
            case 12:
                startDate = currentDate.getYear() + "-10-01";
                endDate = currentDate.getYear() + "-12-31";
                break;
        }
        Pageable paging = PageRequest.of(page, pageSize);
        Page<AccountEntity> pageAccounts = accountRepository
                .getTopAccountsByContributionAndDateWithPaging(startDate, endDate, paging);
        if (pageAccounts.isEmpty()) {
            throw new EntityNotFoundException("Unable to get top contributors.");
        }

        return getAccountResponseMapWithContributionInQuarter(pageAccounts, startDate, endDate);
    }

    @Override
    public ProfileResponse update(ProfileUpdateRequest request) throws Exception {
        if (!accountRepository.existsById(request.getEmail())) {
            throw new EntityNotFoundException("Account " + request.getEmail() + " does not exist");
        }
        AccountEntity accountEntity = accountRepository.getOne(request.getEmail());
        accountEntity.setFullName(request.getFullName());
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
        List<ParticipantsMapping> joinedAccounts = accountRepository.findByEventId(eventId);
        return convertMappingToParticipantResponse(joinedAccounts);
    }

    @Override
    public List<Map<String, Object>> findByServiceId(String serviceId) throws Exception {
        List<ParticipantsMapping> usedAccounts = accountRepository.findByServiceId(serviceId);
        return convertMappingToParticipantResponse(usedAccounts);
    }

    @Override
    public List<Map<String, Object>> findNotEvaluatedAccountsByEventId(String eventId) throws Exception {
        List<NotEvaluatedParticipantsMapping> notEvaluatedAccounts = accountRepository.findNotEvaluatedAccountsByEventId(eventId);
        return convertMappingToNotEvaluatedParticipantResponse(notEvaluatedAccounts);
    }

    @Override
    public Map<String, Object> existsByEmailAndPhone(String email, String phone) throws Exception {
        boolean existsByEmail = accountRepository.existsById(email);
        boolean existsByPhone = accountRepository.existsByPhone(phone);
        Map<String, Object> map = new HashMap<>();
        map.put("existsByEmail", existsByEmail);
        map.put("existsByPhone", existsByPhone);
        return map;
    }

    @Transactional
    @Override
    public void updateStatus(String email, String statusId) throws Exception {
        existsByEmail(email);
        accountRepository.updateStatus(email, statusId);
    }

    @Transactional
    @Override
    public void updatePassword(String email, String password) throws Exception {
        existsByEmail(email);
        accountRepository.updatePassword(email, encoder.encode(password));
    }

    @Transactional
    @Override
    public void updateRole(String email, String roleId) throws Exception {
        existsByEmail(email);
        accountRepository.updateRole(email, roleId);
    }

    @Transactional
    @Override
    public void updateAvatar(String email, String avatarUrl) throws Exception {
        existsByEmail(email);
        imageRepository.updateAvatar(email, avatarUrl);
    }

    @Override
    public void insertAvatar(String email, String avatarUrl) throws Exception {
        ImageEntity avatarEntity = new ImageEntity()
                .setImageUrl(avatarUrl)
                .setAuthorAccount(new AccountEntity().setEmail(email))
                .setType("avatar");
        imageRepository.save(avatarEntity);
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
            for (ParticipantsMapping mapping : participants) {
                String email = mapping.getEmail();
                String fullName = mapping.getFullName();
                Boolean gender = mapping.getGender();
                String phone = mapping.getPhone();
                Integer balancePoint = mapping.getBalancePoint();
                Timestamp joinDate = mapping.getJoinDate();
                String imageUrl = mapping.getImageUrl();
                Map<String, Object> map = new HashMap<>();
                map.put("email", email);
                map.put("fullName", fullName);
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

    private List<Map<String, Object>> convertMappingToNotEvaluatedParticipantResponse(List<NotEvaluatedParticipantsMapping> participants) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (!participants.isEmpty()) {
            for (NotEvaluatedParticipantsMapping mapping : participants) {
                String email = mapping.getEmail();
                String fullName = mapping.getFullName();
                String phone = mapping.getPhone();
                String imageUrl = mapping.getImageUrl();
                Map<String, Object> map = new HashMap<>();
                map.put("email", email);
                map.put("fullName", fullName);
                map.put("phone", phone);
                map.put("imageUrl", imageUrl);
                result.add(map);
            }
        }
        return result;
    }

    private void existsByEmail(String email) {
        if (!accountRepository.existsById(email)) {
            throw new RuntimeException("Account: " + email + " not found.");
        }
    }

    private Map<String, Object> getAccountResponseMap(Page<AccountEntity> pageAccounts) throws Exception {
        List<AccountEntity> accountEntities = pageAccounts.getContent();
        List<AccountGeneralResponse> accountResponses = AccountGeneralResponse.convertToListResponse(accountEntities);

        Map<String, Object> response = new HashMap<>();
        response.put("accounts", accountResponses);
        response.put("currentPage", pageAccounts.getNumber());
        response.put("totalItems", pageAccounts.getTotalElements());
        response.put("totalPages", pageAccounts.getTotalPages());

        return response;
    }

    private Map<String, Object> getAccountResponseMapWithContributionInQuarter(Page<AccountEntity> pageAccounts,
                                                                               String startDate, String endDate) throws Exception {
        List<AccountEntity> accountEntities = pageAccounts.getContent();
        List<AccountGeneralResponse> accountResponses = AccountGeneralResponse.convertToListResponse(accountEntities);

        for (AccountGeneralResponse accountResponse : accountResponses) {
            int contributionPoint = rewardRepository.getTotalPointByDate(accountResponse.getEmail(), startDate, endDate);
            accountResponse.setContributionPoint(contributionPoint);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("accounts", accountResponses);
        response.put("currentPage", pageAccounts.getNumber());
        response.put("totalItems", pageAccounts.getTotalElements());
        response.put("totalPages", pageAccounts.getTotalPages());

        return response;
    }
}
