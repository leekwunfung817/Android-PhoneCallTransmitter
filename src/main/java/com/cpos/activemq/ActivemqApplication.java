package com.cpos.activemq;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ActivemqApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ActivemqApplication.class, args);
	}
    @Override
    public void run(String...args) throws Exception {
    	
    }

}
