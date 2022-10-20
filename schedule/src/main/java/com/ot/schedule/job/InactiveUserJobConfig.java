package com.ot.schedule.job;

import com.ot.schedule.config.QueueItemReader;
import com.ot.schedule.core.domain.User;
import com.ot.schedule.core.repository.UserRepository;
import com.ot.schedule.listener.InactiveUserJobExecutionListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class InactiveUserJobConfig {
    private final UserRepository userRepository;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean("inactiveUserJob")
    public Job inactiveUserJob() {
        return jobBuilderFactory.get("inactiveUserJob")
                .incrementer(new RunIdIncrementer())
                .start(inactiveJobStep())
                .listener(new InactiveUserJobExecutionListener(userRepository))
                .build();
    }

    @JobScope
    @Bean("inactiveJobStep")
    public Step inactiveJobStep() {
        return stepBuilderFactory.get("inactiveJobStep")
                .<User, User>chunk(5)
                .reader(inactiveUserReader())
                .processor(inactiveUserProcessor())
                .writer(inactiveUserWriter())
                .build();
    }

    @Bean
    @StepScope
    public QueueItemReader<User> inactiveUserReader() {
        List<User> oldUsers = userRepository.findAllByLastLoginDateBeforeAndStatusEquals(LocalDateTime.now().minusYears(1), User.Status.ACTIVE);
        return new QueueItemReader<>(oldUsers);
    }

    @Bean
    public ItemProcessor<User, User> inactiveUserProcessor() {
        return User::setInactive;
    }

    @Bean
    public ItemWriter<User> inactiveUserWriter() {
        return (userRepository::saveAll);
    }
}
