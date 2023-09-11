package com.example.just.Config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class AmazonS3Config {

    @Value("${cloud.aws.credentials.accessKey}")
    public String accessKey;
    @Value("${cloud.aws.credentials.secretKey}")
    public String secretKey;
    @Value("${cloud.aws.credentials.region}")
    public String region;


}
