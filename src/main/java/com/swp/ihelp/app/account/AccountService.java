package com.swp.ihelp.app.account;

import com.swp.ihelp.app.account.request.ProfileUpdateRequest;
import com.swp.ihelp.app.account.request.SignUpRequest;
import com.swp.ihelp.app.account.response.AccountGeneralResponse;
import com.swp.ihelp.app.account.response.ProfileResponse;
import com.swp.ihelp.app.entity.AccountStatusEntity;

import java.util.List;
import java.util.Map;

public interface AccountService {

    void insert(SignUpRequest signUpRequest) throws Exception;

    ProfileResponse findById(String email) throws Exception;

    List<AccountGeneralResponse> findAll() throws Exception;

    String findRoleById(String email) throws Exception;

    ProfileResponse update(ProfileUpdateRequest request) throws Exception;

    boolean delete(String email) throws Exception;

    List<Map<String, Object>> findByEventId(String eventId) throws Exception;

    List<Map<String, Object>> findByServiceId(String serviceId) throws Exception;

    void updateStatus(String email, String statusId) throws Exception;

    void updatePassword(String email, String password) throws Exception;

    void updateRole(String email, String roleId) throws Exception;

    void updateDeviceToken(String email, String deviceToken) throws Exception;

}
