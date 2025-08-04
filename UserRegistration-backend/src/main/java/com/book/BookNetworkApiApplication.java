package com.book;

import com.book.role.Role;
import com.book.role.RoleRepo;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")//we have to add the bean name of the auditorAware here so that now it will also genrate the values for the created by and the modifiedBy fields
@EnableAsync
public class BookNetworkApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookNetworkApiApplication.class,args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepo roleRepo){
		 return args -> {
	          if(roleRepo.findByName("USER").isEmpty()){
				     roleRepo.save(
							 Role.builder()
									 .name("USER").
									 createdDate(LocalDateTime.now()).
									 build()
					 );
			  }
		 };
	}

}
