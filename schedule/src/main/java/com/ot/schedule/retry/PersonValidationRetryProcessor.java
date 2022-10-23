package com.ot.schedule.retry;

import com.ot.schedule.core.domain.Person;
import com.ot.schedule.exception.NotFoundNameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;

@Slf4j
public class PersonValidationRetryProcessor implements ItemProcessor<Person, Person> {

    private final RetryTemplate retryTemplate;

    // 생성자에서 retryTemplate을 생성하고, 예외를 3번까지 재시도 함
    public PersonValidationRetryProcessor() {
        this.retryTemplate = new RetryTemplateBuilder()
                .maxAttempts(1)
                .retryOn(NotFoundNameException.class)
                .withListener(new SavePersonRetryListener())
                .build();
    }

    @Override
    public Person process(Person item) throws Exception {
        // ececute 메소드 호출 시 2개의 객체를 생성함
        return this.retryTemplate.execute(context -> {
            // RetryCallback 지점이며, retryTemplate의 첫 시작점
            // 예외가 n번 발생하면 RecoveryCallback을 호출함
            log.info("savePersonRetry (2) : RetryCallback");
            if (item.isNotEmptyName()) {
                return item;
            }
            throw new NotFoundNameException();
        }, context -> {
            // RecoveryCallback
            log.info("savePersonRetry (4) : RecoveryCallback");
            return item.unknownName();
        });
    }

    public static class SavePersonRetryListener implements RetryListener {
        @Override
        public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
            log.info("savePersonRetry (1) : open");
            // retry를 시작하는 설정이며, true여야 retry가 실행됨
            return true;
        }

        @Override
        public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
            log.info("savePersonRetry : close");
        }

        @Override
        public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
            log.info("savePersonRetry (3) : onError");
        }
    }
}
