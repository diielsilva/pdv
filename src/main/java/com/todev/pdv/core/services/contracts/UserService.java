package com.todev.pdv.core.services.contracts;

import com.todev.pdv.common.dtos.UserRequestBody;
import com.todev.pdv.common.dtos.UserResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseBody save(UserRequestBody requestDTO);

    Page<UserResponseBody> findActive(Pageable pageable);

    Page<UserResponseBody> findInactive(Pageable pageable);

    Page<UserResponseBody> findActiveByNameContaining(String name, Pageable pageable);

    Page<UserResponseBody> findInactiveByNameContaining(String name, Pageable pageable);

    UserResponseBody findActiveById(Integer id);

    UserResponseBody findInactiveById(Integer id);

    UserResponseBody update(String onlineLogin, UserRequestBody requestDTO);

    void delete(String onlineLogin, Integer id);

    void reactivate(Integer id);
}
