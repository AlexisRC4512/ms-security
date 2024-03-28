package com.project.mssecurity.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_type")
public class UserTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_type_id")
    private Long id;
    @Column(name = "Role_user", length = 150 ,nullable = false)
    private String roleUser;
    @Column(name = "State", nullable = false)
    private Integer State;
    @Column(name = "Created_by", length = 150 , nullable = true)
    private String usuaCrea;
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
    @ManyToMany
    @JoinTable(
            name = "user_user_type",
            joinColumns = @JoinColumn(name = "User_type_id"),
            inverseJoinColumns = @JoinColumn(name = "User_ID")
    )
    @JsonBackReference
    private List<UserEntity> userEntityList = new ArrayList<>();

}
