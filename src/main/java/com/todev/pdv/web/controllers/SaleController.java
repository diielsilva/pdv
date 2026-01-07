package com.todev.pdv.web.controllers;

import com.todev.pdv.common.dtos.SaleDetailsResponseBody;
import com.todev.pdv.common.dtos.SaleRequestBody;
import com.todev.pdv.common.dtos.SaleResponseBody;
import com.todev.pdv.core.services.contracts.SaleService;
import jakarta.validation.Valid;
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
public class SaleController {
    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<SaleResponseBody> save(Authentication authentication, @RequestBody @Valid SaleRequestBody requestDTO) {
        var sale = saleService.save(authentication.getName(), requestDTO);
        return new ResponseEntity<>(sale, CREATED);
    }

    @PostMapping("online")
    public ResponseEntity<SaleResponseBody> createOnline(
            Authentication authentication,
            @RequestParam boolean hasDelivery,
            @RequestBody @Valid SaleRequestBody body
    ) {
        SaleResponseBody sale = saleService.saveOnline(authentication.getName(), hasDelivery, body);
        return new ResponseEntity<>(sale, CREATED);
    }

    @GetMapping("active")
    public ResponseEntity<Page<SaleResponseBody>> findActive(Pageable pageable) {
        var sales = saleService.findActive(pageable);
        return new ResponseEntity<>(sales, OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("inactive")
    public ResponseEntity<Page<SaleResponseBody>> findInactive(Pageable pageable) {
        var sales = saleService.findInactive(pageable);
        return new ResponseEntity<>(sales, OK);
    }

    @GetMapping("details/{id}")
    public ResponseEntity<SaleDetailsResponseBody> details(@PathVariable Integer id) {
        var details = saleService.details(id);
        return new ResponseEntity<>(details, OK);
    }

    @GetMapping("active/search")
    public ResponseEntity<List<SaleResponseBody>> findActiveByDate(@RequestParam LocalDateTime date) {
        var sales = saleService.findActiveByDate(date);
        return new ResponseEntity<>(sales, OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("inactive/search")
    public ResponseEntity<List<SaleResponseBody>> findInactiveByDate(@RequestParam LocalDateTime date) {
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
