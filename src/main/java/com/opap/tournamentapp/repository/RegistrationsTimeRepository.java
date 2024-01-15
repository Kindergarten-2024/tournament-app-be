package com.opap.tournamentapp.repository;

import com.opap.tournamentapp.model.RegistrationsTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RegistrationsTimeRepository extends JpaRepository<RegistrationsTime, Long> {
    @Query("SELECT e FROM RegistrationsTime e")
    RegistrationsTime findFirstRecord();
}
