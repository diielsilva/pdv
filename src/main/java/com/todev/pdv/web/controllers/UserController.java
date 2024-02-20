package com.todev.pdv.web.controllers;

import com.todev.pdv.common.dtos.UserRequest;
import com.todev.pdv.common.dtos.UserResponse;
import com.todev.pdv.core.services.contracts.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<UserResponse> save(@RequestBody @Valid UserRequest requestDTO) {
        var user = userService.save(requestDTO);
        return new ResponseEntity<>(user, CREATED);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("active")
    public ResponseEntity<Page<UserResponse>> findActive(Pageable pageable) {
        var users = userService.findActive(pageable);
        return new ResponseEntity<>(users, OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("inactive")
    public ResponseEntity<Page<UserResponse>> findInactive(Pageable pageable) {
        var users = userService.findInactive(pageable);
        return new ResponseEntity<>(users, OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("active/search")
    public ResponseEntity<Page<UserResponse>> findActiveByNameContaining(@RequestParam String name, Pageable pageable) {
        var users = userService.findActiveByNameContaining(name, pageable);
        return new ResponseEntity<>(users, OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("inactive/search")
    public ResponseEntity<Page<UserResponse>> findInactiveByNameContaining(@RequestParam String name, Pageable pageable) {
        var users = userService.findInactiveByNameContaining(name, pageable);
        return new ResponseEntity<>(users, OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("active/{id}")
    public ResponseEntity<UserResponse> findActiveById(@PathVariable Integer id) {
        var user = userService.findActiveById(id);
        return new ResponseEntity<>(user, OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("inactive/{id}")
    public ResponseEntity<UserResponse> findInactiveById(@PathVariable Integer id) {
        var user = userService.findInactiveById(id);
        return new ResponseEntity<>(user, OK);
    }

    @PutMapping
    public ResponseEntity<UserResponse> update(Authentication onlineUser, @RequestBody @Valid UserRequest requestDTO) {
        var user = userService.update(onlineUser.getName(), requestDTO);
        return new ResponseEntity<>(user, OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(Authentication onlineUser, @PathVariable Integer id) {
        userService.delete(onlineUser.getName(), id);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PatchMapping("{id}")
    public ResponseEntity<Void> reactivate(@PathVariable Integer id) {
        userService.reactivate(id);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
