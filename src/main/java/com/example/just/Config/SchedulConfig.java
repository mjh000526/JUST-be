package com.example.just.Config;

import com.example.just.Service.MigrationService;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulConfig {

    @Autowired
    private MigrationService migrationService;

    //DB동기화 작업 프로젝트 실행시에 한번 실행
    @PostConstruct
    public void setMigrationService(){
        migrationService.migrationDB();
    }
}
