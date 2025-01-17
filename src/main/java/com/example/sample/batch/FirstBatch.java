package com.example.sample.batch;

import com.example.sample.entity.AfterEntity;
import com.example.sample.entity.BeforeEntity;
import com.example.sample.repository.AfterRepository;
import com.example.sample.repository.BeforeRepository;
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

import java.util.Map;

@Configuration
// DB를 복사하는 기능
public class FirstBatch {
    // 생성자 주입
    // 트래킹 작업 (기록)
    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final BeforeRepository beforeRepository;

    private final AfterRepository afterRepository;

    public FirstBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, BeforeRepository beforeRepository, AfterRepository afterRepository) {
      
        this.jobRepository = jobRepository;

        this.platformTransactionManager = platformTransactionManager;

        this.beforeRepository = beforeRepository;

        this.afterRepository = afterRepository;
    }

    @Bean
    // 배치 첫번째 작업
    public Job firstJob() {
        
        return new JobBuilder("firstJob", jobRepository)
                .start(firstStep())
//              .next() 스탭이 추가되면 메서드 사용해서 추가하기
                .build();
    }

    @Bean
    // 1.step
    public Step firstStep() {

        return new StepBuilder("firstStep", jobRepository)
                // 청크 단위로 진행 -> 얼만큼 끊어서 사용할지
                .<BeforeEntity, AfterEntity> chunk(10, platformTransactionManager) // 실패시 다시처리하게 만들어줌 -> platformTransactionManager
                // 읽기
                .reader(beforeReader())
                // 처리 메소드자리
                .processor(middleProcessor())
                // 쓰기
                .writer(afterWriter())
                .build();
    }

    @Bean
    // 2. 동작 - JPA기반 반환타입
    public RepositoryItemReader<BeforeEntity> beforeReader() {
        // 읽는 부분 정의
        return new RepositoryItemReaderBuilder<BeforeEntity>()
                .name("beforeReader")
                .pageSize(10)
                .methodName("findAll")
                .repository(beforeRepository)
                // 아이디가 작은 순서로 부터
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    // 2. 동작
    public ItemProcessor<BeforeEntity, AfterEntity> middleProcessor() {

        return new ItemProcessor<BeforeEntity, AfterEntity>() {
            // 처리 부분 정의
            @Override
            public AfterEntity process(BeforeEntity item) throws Exception {

                AfterEntity afterEntity = new AfterEntity();
                afterEntity.setUsername(item.getUsername());

                return afterEntity;
            }
        };
    }

     @Bean
    // 2. 동작
    public RepositoryItemWriter<AfterEntity> afterWriter() {
        // 쓰는 부분 정의
        return new RepositoryItemWriterBuilder<AfterEntity>()
                .repository(afterRepository)
                .methodName("save")
                .build();
    }
}
