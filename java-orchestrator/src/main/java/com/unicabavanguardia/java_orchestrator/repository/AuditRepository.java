package com.unicabavanguardia.java_orchestrator.repository;

import com.unicabavanguardia.java_orchestrator.model.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {
    List<Audit> findByStudentId(Long studentId);
}