package com.ot.schedule.core.repository;

import com.ot.schedule.core.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Integer> {
}
