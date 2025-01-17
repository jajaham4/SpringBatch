package com.example.sample.batch;

import com.example.sample.entity.WinEntity;
import com.example.sample.repository.WinRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;
import java.util.Map;

@Configuration
public class SecondBatch {
    // 작업을 관리
    private final JobRepository jobRepository;
    // 트랜잭션 관리
    private final PlatformTransactionManager platformTransactionManager;
    // 엔티티 접근
    private final WinRepository winRepository;

    public SecondBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, WinRepository winRepository) {

        this.jobRepository = jobRepository;

        this.platformTransactionManager = platformTransactionManager;

        this.winRepository = winRepository;
    }

    @Bean
    public Job secondJob() {

        return new JobBuilder("secondJob", jobRepository)
                .start(secondStep())
                .build();
    }

    @Bean
    public Step secondStep() {

        return new StepBuilder("secondStep", jobRepository)
                .<WinEntity, WinEntity> chunk(10, platformTransactionManager)
                .reader(winReader())
                .processor(trueProcessor())
                .writer(winWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<WinEntity> winReader() {
        // JPA를 이용하기떄문에 RepositoryItemReaderBuilder 사용
        return new RepositoryItemReaderBuilder<WinEntity>()
                .name("winReader")
                .pageSize(10)
                .methodName("findByWinGreaterThanEqual")
                // 정수 10의 크기를 비교하기위해 arguments를 사용
                .arguments(Collections.singletonList(10L))
                .repository(winRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<WinEntity, WinEntity> trueProcessor() {

        return item -> {
            // 10이 이상일경우 맞기 때문에 true로 설정
            item.setReward(true);
            return item;
        };
    }

    @Bean
    public RepositoryItemWriter<WinEntity> winWriter() {

        return new RepositoryItemWriterBuilder<WinEntity>()
                .repository(winRepository)
                .methodName("save")
                .build();
    }


}
