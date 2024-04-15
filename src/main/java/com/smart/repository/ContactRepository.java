package com.smart.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smart.entities.Contact;
import com.smart.entities.User;


@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
	public Page<Contact> findByUserId(int userId, Pageable pageable);
	
	@Query("SELECT c FROM Contact c WHERE c.name LIKE %:query% AND c.user = :user")
	public List<Contact> searchContacts(@Param("query") String query, @Param("user") User user);

}
