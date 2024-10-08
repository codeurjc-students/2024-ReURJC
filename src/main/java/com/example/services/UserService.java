package com.example.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.User;
import com.example.repository.UserRepository;

@Service
public class UserService {

    @Autowired
	private UserRepository repository;
    

    public User findByEmail(String email) {
		return repository.findByEmail(email);
	}

    public boolean existsByEmail(String email) {
		return repository.existsByEmail(email);
	}
}
