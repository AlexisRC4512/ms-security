package com.project.mssecurity.repository;

import com.project.mssecurity.entity.DocumentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentTypeRepository extends JpaRepository<DocumentTypeEntity,Long> {
}
