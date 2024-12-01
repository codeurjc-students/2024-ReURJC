package com.example.services;

import java.util.List;

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

	public void save(User user) {
		repository.save(user);
	}

	public User findById(Long voted) {
		return repository.findById(voted).get();
	}

	public List<User> getCandidatesExcludingUser(String email) {
		return repository.findByIsCandidateTrueExcludingUser(email);
	}

	public List<User> getAll() {
		return repository.findAll();
	}

	public void setToken(User user, String fcmToken) {
		if (fcmToken != null && !fcmToken.isEmpty()) { 
			System.out.println("El token es: "+ fcmToken );
			user.addFcmToken(fcmToken);
			repository.save(user);
		}
	}

	public List<String> getToken(User user) {
		return user.getFcmToken();
	}
}
