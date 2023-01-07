package com.example.weight_inspection.controllers;

import com.example.weight_inspection.models.Admin;
import com.example.weight_inspection.repositories.AdminRepository;
import com.example.weight_inspection.transfer.ListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @GetMapping
    public ResponseEntity<ListResponse<Admin>> GetAdmins(
            @RequestParam(value = "page", defaultValue = "0") int currentPage,
            @RequestParam(value = "page_size", defaultValue = "100") int pageSize) {

        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<Admin> page = adminRepository.findAll(pageable);
        ListResponse<Admin> listResponse = new ListResponse<>(page);
        return new ResponseEntity<>(listResponse, HttpStatus.OK);
    }

    @GetMapping("{adminId}")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long adminId) {

        Optional<Admin> admin = adminRepository.findById(adminId);
        if (!admin.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(admin.get(), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Admin> saveAdmin(@RequestBody @Valid Admin admin, BindingResult bindingResult) {

        if (bindingResult.hasErrors() || admin == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setId(null);

        adminRepository.save(admin);
        return new ResponseEntity<>(admin, HttpStatus.CREATED);
    }

    @PutMapping("{adminId}")
    public ResponseEntity<Admin> replaceAdmin(@RequestBody @Valid Admin admin,
                                                            BindingResult bindingResult,
                                                            @PathVariable Long adminId) {

        if (bindingResult.hasErrors() || admin == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Admin> replacedAdmin = adminRepository.findById(adminId);
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        if (!replacedAdmin.isPresent()) {
            admin.setId(null);
            adminRepository.save(admin);
            return new ResponseEntity<>(admin, HttpStatus.NO_CONTENT);
        }

        admin.setId(adminId);
        adminRepository.save(admin);
        return new ResponseEntity<>(admin, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("{adminId}")
    public ResponseEntity<Admin> deleteAdmin(@PathVariable Long adminId) {

        Optional<Admin> admin  = adminRepository.findById(adminId);
        if (!admin.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Admin delAdmin  = admin.get();
        delAdmin.setId(adminId);
        adminRepository.delete(delAdmin);
        return new ResponseEntity<>(delAdmin, HttpStatus.NO_CONTENT);
    }

    @GetMapping("login")
    public ResponseEntity<Admin> login() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}