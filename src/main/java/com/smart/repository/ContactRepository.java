package com.smart.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@Repository
public interface ContactRepository extends MongoRepository<Contact, String> {
    
    public Page<Contact> findByUserId(String userId, Pageable pageable);

    @Query("{ 'name' : { $regex: ?0, $options: 'i' }, 'user' : ?1 }")
    public List<Contact> searchContacts(@Param("query") String query, @Param("user") User user);
}
