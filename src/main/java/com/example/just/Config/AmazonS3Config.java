package com.example.just.Config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class AmazonS3Config { // Amazon S3 설정

    @Value("${cloud.aws.credentials.accessKey}")
    public String accessKey; // AWS Access Key
    @Value("${cloud.aws.credentials.secretKey}")
    public String secretKey; // AWS Secret Key
    @Value("${cloud.aws.credentials.region}")
    public String region; // AWS Region


}
