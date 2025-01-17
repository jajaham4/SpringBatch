package com.example.sample.controller;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
// 특정한 페이지가아닌 문자를 반음
@ResponseBody 
// 배체프로그램 시간 설정
public class MainController {

    private final JobLauncher jobLauncher;

    private final JobRegistry jobRegistry;

    public MainController(JobLauncher jobLauncher, JobRegistry jobRegistry) {
        this.jobLauncher = jobLauncher;
        this.jobRegistry = jobRegistry;
    }

    @GetMapping("/first")
    // Api기반, 예외처리까지 함 (값을 받아 실행)
    public String firstApi(@RequestParam("value") String value) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("firstJob"), jobParameters);

        return "OK";
    }
    //
    @GetMapping("/second")
    public String SecondApi(@RequestParam("value") String value) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("secondJob"), jobParameters);

        return "OK";
    }
}
