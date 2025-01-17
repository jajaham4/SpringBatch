package com.example.sample.schedule;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;

// schedule config 설정
@Configuration
public class FirstSchedule {
    // 실행시키기 위한 시작지점 시작버튼
    private final JobLauncher jobLauncher;
    // 특정 배치를 가져오는 변수
    private final JobRegistry jobRegistry;

    public FirstSchedule(JobLauncher jobLauncher,JobRegistry jobRegistry) {

        this.jobLauncher = jobLauncher;

        this.jobRegistry = jobRegistry;
    }

//  cron 식 -> 해당 주기마다 실행시키는 메소드
/*    @Scheduled(cron = "10 * * * * *", zone = "Asia/Seoul")
    public void runFirstJob() throws Exception{

        // simpledataformat 현재 시간
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        String date = dateFormat.format(new Date());

        // 파라미터 값으로 넘겨줌
        JobParameters jobParameters = new JobParametersBuilder()
                // 지정 파라미터값을 줌 (integer)
                .addString("date", date)
                // 실행을 막음
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("firstJob"), jobParameters);

    }*/
}
