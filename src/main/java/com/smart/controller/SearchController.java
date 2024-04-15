package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;

@RestController
public class SearchController {
	@Autowired
	ContactRepository contactRepository;
	@Autowired
	UserRepository userRepository;
	
	@GetMapping("search/contacts")
	public ResponseEntity<List<Contact>> searchHandler(@RequestParam("query") String query, Principal principal) {
		User user = userRepository.getUserByUsername(principal.getName());
		List<Contact> contacts = contactRepository.searchContacts(query, user);
		for(Contact contact : contacts) {
			contact.setUser(null);
		}
		return ResponseEntity.ok(contacts);
	}

}
