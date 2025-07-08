package com.example2.demo.controller;

import com.example2.demo.model.AppUser;
import com.example2.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepo;

    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping
    public List<AppUser> getAllUsers() {
        return userRepo.findAll();
    }

    @PostMapping
    public AppUser createUser(@RequestBody AppUser user) {
    	System.out.println("Received: " + user);
        return userRepo.save(user);
    }
}
