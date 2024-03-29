package com.example.weight_inspection.controllers;

import com.example.weight_inspection.models.Admin;
import com.example.weight_inspection.models.Email;
import com.example.weight_inspection.repositories.AdminRepository;
import com.example.weight_inspection.repositories.EmailRepository;
import com.example.weight_inspection.transfer.AddAdminDTO;
import com.example.weight_inspection.transfer.GetAdminDTO;
import com.example.weight_inspection.transfer.ListResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    private final AdminRepository adminRepository;
    private final EmailRepository emailRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public AdminController(AdminRepository adminRepository, EmailRepository emailRepository) {
        this.adminRepository = adminRepository;
        this.emailRepository = emailRepository;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        this.modelMapper = new ModelMapper();
    }

    @GetMapping
    public ResponseEntity<ListResponse<GetAdminDTO>> GetAdmins(
            @RequestParam(value = "page", defaultValue = "0") int currentPage,
            @RequestParam(value = "page_size", defaultValue = "100") int pageSize) {

        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("id").descending());
        Page<Admin> page = adminRepository.findAll(pageable);

        ListResponse<GetAdminDTO> listResponse = new ListResponse<>();
        listResponse.setPage(page.getNumber());
        listResponse.setTotalItems(page.getTotalElements());
        listResponse.setTotalPages(page.getTotalPages());
        listResponse.setItems(
                page.getContent().stream()
                        .map(item -> modelMapper.map(item, GetAdminDTO.class))
                        .collect(Collectors.toList())
        );

        return new ResponseEntity<>(listResponse, HttpStatus.OK);
    }

    @GetMapping("{adminId}")
    public ResponseEntity<GetAdminDTO> getAdminById(@PathVariable Long adminId) {

        Optional<Admin> admin = adminRepository.findById(adminId);
        if (!admin.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        GetAdminDTO adminDTO = modelMapper.map(admin, GetAdminDTO.class);
        return new ResponseEntity<>(adminDTO, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Admin> saveAdmin(@RequestBody @Valid AddAdminDTO adminDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors() || adminDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Admin admin = modelMapper.map(adminDTO, Admin.class);

        if(adminDTO.getEmailId() != null) {
            Optional<Email> email = emailRepository.findById(adminDTO.getEmailId());
            if (!email.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            admin.setEmail(email.get());
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminRepository.save(admin);
        return new ResponseEntity<>(admin, HttpStatus.CREATED);
    }

    @PutMapping("{adminId}")
    public ResponseEntity<Admin> replaceAdmin(@RequestBody @Valid AddAdminDTO adminDTO,
                                                            BindingResult bindingResult,
                                                            @PathVariable Long adminId) {

        if (bindingResult.hasErrors() || adminDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Admin admin = modelMapper.map(adminDTO, Admin.class);

        if(adminDTO.getEmailId() != null) {
            Optional<Email> email = emailRepository.findById(adminDTO.getEmailId());
            if (!email.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            admin.setEmail(email.get());
        }

        Optional<Admin> replacedAdmin = adminRepository.findById(adminId);

        String password = admin.getPassword();
        if(replacedAdmin.isPresent() && Objects.equals(password, replacedAdmin.get().getPassword())) {
            admin.setPassword(password);
        }
        else {
            admin.setPassword(passwordEncoder.encode(password));
        }

        if (!replacedAdmin.isPresent()) {
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