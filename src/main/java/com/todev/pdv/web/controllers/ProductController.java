package com.todev.pdv.web.controllers;

import com.todev.pdv.common.dtos.ProductRequest;
import com.todev.pdv.common.dtos.ProductResponse;
import com.todev.pdv.core.services.contracts.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<ProductResponse> save(@RequestBody @Valid ProductRequest requestDTO) {
        var product = productService.save(requestDTO);
        return new ResponseEntity<>(product, CREATED);
    }

    @GetMapping("active")
    public ResponseEntity<Page<ProductResponse>> findActive(Pageable pageable) {
        var products = productService.findActive(pageable);
        return new ResponseEntity<>(products, OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("inactive")
    public ResponseEntity<Page<ProductResponse>> findInactive(Pageable pageable) {
        var products = productService.findInactive(pageable);
        return new ResponseEntity<>(products, OK);
    }

    @GetMapping("active/search")
    public ResponseEntity<Page<ProductResponse>> findActiveByDescriptionContaining(@RequestParam String description,
                                                                                   Pageable pageable) {
        var products = productService.findActiveByDescriptionContaining(description, pageable);
        return new ResponseEntity<>(products, OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("inactive/search")
    public ResponseEntity<Page<ProductResponse>> findInactiveByDescriptionContaining(@RequestParam String description,
                                                                                     Pageable pageable) {
        var products = productService.findInactiveByDescriptionContaining(description, pageable);
        return new ResponseEntity<>(products, OK);
    }

    @GetMapping("active/{id}")
    public ResponseEntity<ProductResponse> findActiveById(@PathVariable Integer id) {
        var product = productService.findActiveById(id);
        return new ResponseEntity<>(product, OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("inactive/{id}")
    public ResponseEntity<ProductResponse> findInactiveById(@PathVariable Integer id) {
        var product = productService.findInactiveById(id);
        return new ResponseEntity<>(product, OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PutMapping("{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Integer id, @RequestBody @Valid ProductRequest requestDTO) {
        var product = productService.update(id, requestDTO);
        return new ResponseEntity<>(product, OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productService.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PatchMapping("{id}")
    public ResponseEntity<Void> reactivate(@PathVariable Integer id) {
        productService.reactivate(id);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
