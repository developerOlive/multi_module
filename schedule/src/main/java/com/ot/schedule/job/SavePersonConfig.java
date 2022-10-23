package com.ot.schedule.job;

import com.ot.schedule.core.domain.Person;
import com.ot.schedule.exception.NotFoundNameException;
import com.ot.schedule.listener.SavePersonListener;
import com.ot.schedule.processor.DuplicateValidationProcessor;
import com.ot.schedule.retry.PersonValidationRetryProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.persistence.EntityManagerFactory;

@Configuration
@Slf4j
public class SavePersonConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    public SavePersonConfig(JobBuilderFactory jobBuilderFactory,
                            StepBuilderFactory stepBuilderFactory,
                            EntityManagerFactory entityManagerFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public Job savePersonJob() throws Exception {
        return this.jobBuilderFactory.get("savePersonJob")
                .incrementer(new RunIdIncrementer())
                .start(this.savePersonStep(null))
                .listener(new SavePersonListener.SavePersonJobExecutionListener())
                .listener(new SavePersonListener.SavePersonAnnotationJobExecutionListener())
                .build();
    }

    @Bean
    @JobScope
    public Step savePersonStep(@Value("#{jobParameters[allow_duplicate]}") String allowDuplicate) throws Exception {
        return this.stepBuilderFactory.get("savePersonStep")
                .<Person, Person>chunk(10)
                .reader(itemReader())
                .processor(itemProcessor(allowDuplicate))
                .writer(itemWriter())
                .listener(new SavePersonListener.SavePersonStepExecutionListener())
                .faultTolerant()
                .skip(NotFoundNameException.class)
                .skipLimit(1)
                .build();
    }

    private ItemReader<? extends Person> itemReader() throws Exception {
        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>(); // csv 파일을 한 줄씩 읽을 수 있는 설정에 사용
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(); // csv 파일을 Person 객체에 맵핑하기 위해 Person 필드명 설정할 때 사용
        lineTokenizer.setNames("name", "age", "address");
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSet -> new Person(
                fieldSet.readString("name"),
                fieldSet.readString("age"),
                fieldSet.readString("address")));

        FlatFileItemReader<Person> itemReader = new FlatFileItemReaderBuilder<Person>()
                .name("savePersonItemReader")
                .encoding("UTF-8")
                .linesToSkip(1)
                .resource(new ClassPathResource("person.csv")) // resources 디렉토리 하위 파일을 읽음
                .lineMapper(lineMapper)
                .build();

        itemReader.afterPropertiesSet(); // itemReader에서 필요한 필드 설정값이 정상적으로 설정되었는지 검증

        return itemReader;
    }

    private ItemProcessor<? super Person, ? extends Person> itemProcessor(String allowDuplicate) throws Exception {

        ItemProcessor<Person, Person> validationProcessor = item -> {
            if (item.isNotEmptyName()) {
                return item;
            }
            // Name이 비어있다면 예외 발생
            throw new NotFoundNameException();
        };

        DuplicateValidationProcessor<Person> duplicateValidationProcessor =
                new DuplicateValidationProcessor<>(Person::getName, Boolean.parseBoolean(allowDuplicate));

        // ItemProcessor는 하나만 생성할 수 있기 때문에, 1개 이상 생성하려면 CompositeItemProcessor를 사용해서 Process를 묶는다.
        CompositeItemProcessor<Person, Person> itemProcessor = new CompositeItemProcessorBuilder<Person, Person>()
//                .delegates(validationProcessor, duplicateValidationProcessor)
                // PersonValidationRetryProcessor가 먼저 호출되어야, name이 비어있는 데이터를 먼저 처리할 수 있음
                .delegates(new PersonValidationRetryProcessor(), validationProcessor, duplicateValidationProcessor)
                .build();

        itemProcessor.afterPropertiesSet();

        return itemProcessor;
    }

    private ItemWriter<? super Person> itemWriter() throws Exception {
        JpaItemWriter<Person> jpaItemWriter = new JpaItemWriterBuilder<Person>()
                .entityManagerFactory(entityManagerFactory)
                .build();

        ItemWriter<Person> logItemWriter = items -> log.info("person.size : {}", items.size());

        CompositeItemWriter<Person> itemWriter = new CompositeItemWriterBuilder<Person>()
                .delegates(jpaItemWriter, logItemWriter)
                .build();

        itemWriter.afterPropertiesSet();

        return itemWriter;
    }
}
