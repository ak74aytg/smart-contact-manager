package com.smart.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.smart.entities.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    @Query("{ 'email' : :#{#username} }")
    public User getUserByUsername(@Param("username") String username);
}
