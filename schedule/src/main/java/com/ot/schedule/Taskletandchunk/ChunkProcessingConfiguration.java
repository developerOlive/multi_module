package com.ot.schedule.Taskletandchunk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration // 스프링배치의 모든 Job은 @Configuration으로 등록해서 사용
@Slf4j
public class ChunkProcessingConfiguration {

    // Job은 배치의 실행단위. Job을 만들 수 있는 팩토리 클래스는 스프링 배치 설정에 의해 빈으로 생성되어 있음
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public ChunkProcessingConfiguration(JobBuilderFactory jobBuilderFactory,
                                        StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }


    @Bean
    public Job chunkProcessingJob() {
        // JobBuilder를 생성할 수 있는 get() 메서드를 포함. get()메서드는 새로운 JobBuilder를 생성해서 반환.
        return jobBuilderFactory.get("chunkProcessingJob")
                .incrementer(new RunIdIncrementer()) // Job이 실행될 때마다 파라미터 아이디를 자동으로 생성해주는 클래스
                .start(this.taskBaseStep())// job 실행 시 최초로 실행될 step 지정
                .start(this.chunkBaseStep())
                .build();
    }


    // =============================== task base step ===============================

    @Bean
    public Step taskBaseStep() {
        return stepBuilderFactory.get("taskBaseStep")
                .tasklet(this.tasklet())
                .build();
    }

    private Tasklet tasklet() {
        return (stepContribution, chunkContext) -> {
            List<String> itmes = getItems();
            log.info("task item size : {}", itmes.size());

            return RepeatStatus.FINISHED;
        };
    }


    // =============================== chunk base step ===============================

    @Bean
    public Step chunkBaseStep() {
        return stepBuilderFactory.get("chunkBaseStep")
                .<String, String>chunk(10)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    private ItemReader<String> itemReader() {
        return new ListItemReader<>(getItems());
    }

    private ItemProcessor<String, String> itemProcessor() {
        return item -> item + ", Spring Batch"; // item : ItemReader 읽어온 item
    }

    private ItemWriter<String> itemWriter() {
//        return items -> log.info("chunk item size : {}", items.size());
        return items -> items.forEach(log::info);
    }


    private List<String> getItems() {
        List<String> items = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            items.add(i + "Hello");
        }
        return items;
    }
}
