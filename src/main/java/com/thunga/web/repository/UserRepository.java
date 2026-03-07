package com.thunga.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thunga.web.entity.Account;
import com.thunga.web.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	User findByName(String name);
	User findByAccount(Account account);
}
