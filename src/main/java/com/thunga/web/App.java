package com.thunga.web;

import com.thunga.web.repository.UserRepository;
import com.thunga.web.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.transaction.Transactional;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class App implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    private final UserRepository userRepository;
    private final OrderService orderService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {


    }

}
