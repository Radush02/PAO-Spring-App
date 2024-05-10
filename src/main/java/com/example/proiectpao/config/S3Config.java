package com.example.proiectpao.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clasa S3Config este o clasa de configurare pentru Amazon S3.<br>
 * Aceasta clasa contine metoda s3client care returneaza un obiect de tip AmazonS3.
 * @see <a href=" https://medium.com/@mertcakmak2/object-storage-with-spring-boot-and-aws-s3-64448c91018f">Stocarea unui obiect in Spring folosind AWS S3</a>
 */
@Configuration
public class S3Config {

    @Value("${aws.access.key}")
    private String awsAccessKey;

    @Value("${aws.secret.key}")
    private String awsSecretKey;

    @Bean
    public AmazonS3 s3client() {

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsAccessKey, awsSecretKey);

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(Regions.EU_NORTH_1)
                .build();
    }
}
