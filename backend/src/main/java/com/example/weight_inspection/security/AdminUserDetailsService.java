package com.example.weight_inspection.security;

import com.example.weight_inspection.models.Admin;
import com.example.weight_inspection.repositories.AdminRepository;
import com.example.weight_inspection.repositories.PaletteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {
    private final AdminRepository adminRepository;

    @Autowired
    public AdminUserDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username);
        if (admin == null) {
            throw new UsernameNotFoundException(username);
        }
        UserDetails user = User.withUsername(admin.getUsername()).password(admin.getPassword()).authorities("USER").build();
        return user;
    }
}