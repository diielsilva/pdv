package com.todev.pdv.core.repositories;

import com.todev.pdv.core.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    Page<User> findByDeletedAtIsNull(Pageable pageable);

    Page<User> findByDeletedAtIsNotNull(Pageable pageable);

    Page<User> findByNameContainingAndDeletedAtIsNull(String name, Pageable pageable);

    Page<User> findByNameContainingAndDeletedAtIsNotNull(String name, Pageable pageable);

    Optional<User> findByIdAndDeletedAtIsNull(Integer id);

    Optional<User> findByIdAndDeletedAtIsNotNull(Integer id);

    Optional<User> findByLogin(String login);

    Optional<User> findByLoginAndDeletedAtIsNull(String login);
}
