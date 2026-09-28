package com.duoc.ms_correo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms 
public class MsCorreoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCorreoApplication.class, args);
	}

}
