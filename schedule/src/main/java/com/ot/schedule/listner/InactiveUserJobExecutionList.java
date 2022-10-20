package com.ot.schedule.listener;

import com.ot.schedule.core.domain.User;
import com.ot.schedule.core.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/*
    Job 실행 전/후에 실행되는 Interceptor 개념의 클래스 입니다.
 */
@Slf4j
public class InactiveUserJobExecutionListener implements JobExecutionListener {

    private final UserRepository userRepository;


    public InactiveUserJobExecutionListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        List<User> users = userRepository.findAllByUpdateDateBetweenAndStatus(LocalDateTime.now().minusSeconds(1), LocalDateTime.now(), User.Status.INACTIVE);

        long time = Objects.requireNonNull(jobExecution.getEndTime()).getTime() - Objects.requireNonNull(jobExecution.getStartTime()).getTime();

        log.info("휴면회원 업데이트 배치 프로그램");
        log.info("----------------------------------");
        log.info("총 데이터 처리 {}건, 처리시간{}millis", users.size(), time);
    }
}
