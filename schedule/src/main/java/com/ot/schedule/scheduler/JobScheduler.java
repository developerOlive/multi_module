package com.ot.schedule.scheduler;

import com.ot.schedule.job.InactiveUserJobConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobScheduler {
    private final JobLauncher jobLauncher;
    private final InactiveUserJobConfig inactiveUserJobConfig;

    @Scheduled(cron = "0 0 1 * * *") // 매일 오전 1시
    public void runJob() {

        Map<String, JobParameter> confMap = new HashMap<>();
        confMap.put("date", new JobParameter(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH:mm:ss"))));
        JobParameters jobParameters = new JobParameters(confMap);

        try {
            jobLauncher.run(inactiveUserJobConfig.inactiveUserJob(), jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.error(e.getMessage());
        } catch (JobRestartException e) {
            throw new RuntimeException(e);
        }
    }
}
