package com.example.just;

import com.example.just.Repository.HashTagESRepository;
import com.example.just.Repository.PostContentESRespository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableElasticsearchRepositories(
		includeFilters = {
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {PostContentESRespository.class,
						HashTagESRepository.class}),
		}
)
