package com.example.just.Config;

import com.example.just.Repository.HashTagESRepository;

import com.example.just.Repository.PostContentESRespository;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;

@Configuration
@EnableReactiveElasticsearchRepositories(basePackageClasses = {PostContentESRespository.class, HashTagESRepository.class})
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {

    //서버에 있는 ELK연결 설정
    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("34.22.67.43:9200")
                .build();
        return RestClients.create(clientConfiguration).rest();
    }

    //ELK에 연결하는 설정 Bean등록
    @Bean
    public ElasticsearchOperations elasticsearchOperations(){
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }

    //ELK연결 템플릿 Bean등록
    @Bean
    public ElasticsearchRestTemplate elasticsearchRestTemplate() {
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }
}
