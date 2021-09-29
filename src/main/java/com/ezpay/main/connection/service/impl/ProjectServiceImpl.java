package com.ezpay.main.connection.service.impl;

import com.ezpay.core.entity.Project;
import com.ezpay.core.service.impl.BaseServiceImpl;
import com.ezpay.main.connection.repository.ProjectRepository;
import com.ezpay.main.connection.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl extends BaseServiceImpl<Project, String> implements ProjectService {
    private ProjectRepository repository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository repository) {
        super(repository);
        this.repository = repository;
    }
}
