package com.project.mssecurity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "document_type")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Document_type_id")
    private Long id;
    @NotNull(message = "la codeType no puede ser vacia")
    @Column(name = "Code_type", nullable = false, length = 150)
    private String codeType;
    @Column(name = "State", nullable = false)
    private Integer State;
    @Column(name = "Created_by", length = 150 , nullable = true)
    private String createdUser;
    @Column(name = "Date_created" , nullable = true)
    private Timestamp dateCreate;
    @Column(name = "Modified_by", length = 150 , nullable = true)
    private String usuaModif;
    @Column(name = "Date_modified" , nullable = true)
    private Timestamp dateModif;
    @Column(name = "Deleted_by", length = 150 , nullable = true)
    private String usuaDelet;
    @Column(name = "Date_deleted" , nullable = true)
    private Timestamp dateDelet;
}