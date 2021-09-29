package com.ezpay.main.connection.repository;

import com.ezpay.core.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
}
