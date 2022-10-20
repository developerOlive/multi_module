package com.ot.schedule.core.repository;

import com.ot.schedule.core.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByLastLoginDateBeforeAndStatusEquals(LocalDateTime lastLoginDate, User.Status status);
    List<User> findAllByUpdateDateBetweenAndStatus(LocalDateTime before, LocalDateTime now, User.Status status);
}
