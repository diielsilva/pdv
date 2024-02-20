package com.todev.pdv.web.controllers;

import com.todev.pdv.common.dtos.SaleDetailsResponse;
import com.todev.pdv.common.dtos.SaleRequest;
import com.todev.pdv.common.dtos.SaleResponse;
import com.todev.pdv.core.services.contracts.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("sales")
@RequiredArgsConstructor
public class SaleController {
    private final SaleService saleService;

    @PostMapping
    public ResponseEntity<SaleResponse> save(Authentication authentication, @RequestBody @Valid SaleRequest requestDTO) {
        var sale = saleService.save(authentication.getName(), requestDTO);
        return new ResponseEntity<>(sale, CREATED);
    }

    @GetMapping("active")
    public ResponseEntity<Page<SaleResponse>> findActive(Pageable pageable) {
        var sales = saleService.findActive(pageable);
        return new ResponseEntity<>(sales, OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("inactive")
    public ResponseEntity<Page<SaleResponse>> findInactive(Pageable pageable) {
        var sales = saleService.findInactive(pageable);
        return new ResponseEntity<>(sales, OK);
    }

    @GetMapping("details/{id}")
    public ResponseEntity<SaleDetailsResponse> details(@PathVariable Integer id) {
        var details = saleService.details(id);
        return new ResponseEntity<>(details, OK);
    }

    @GetMapping("active/search")
    public ResponseEntity<List<SaleResponse>> findActiveByDate(@RequestParam LocalDateTime date) {
        var sales = saleService.findActiveByDate(date);
        return new ResponseEntity<>(sales, OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("inactive/search")
    public ResponseEntity<List<SaleResponse>> findInactiveByDate(@RequestParam LocalDateTime date) {
        var sales = saleService.findInactiveByDate(date);
        return new ResponseEntity<>(sales, OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        saleService.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PatchMapping("{id}")
    public ResponseEntity<Void> reactivate(@PathVariable Integer id) {
        saleService.reactivate(id);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
