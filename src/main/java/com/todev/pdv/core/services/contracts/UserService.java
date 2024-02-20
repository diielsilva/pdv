package com.todev.pdv.core.services.contracts;

import com.todev.pdv.common.dtos.UserRequest;
import com.todev.pdv.common.dtos.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse save(UserRequest requestDTO);

    Page<UserResponse> findActive(Pageable pageable);

    Page<UserResponse> findInactive(Pageable pageable);

    Page<UserResponse> findActiveByNameContaining(String name, Pageable pageable);

    Page<UserResponse> findInactiveByNameContaining(String name, Pageable pageable);

    UserResponse findActiveById(Integer id);

    UserResponse findInactiveById(Integer id);

    UserResponse update(String onlineLogin, UserRequest requestDTO);

    void delete(String onlineLogin, Integer id);

    void reactivate(Integer id);
}
