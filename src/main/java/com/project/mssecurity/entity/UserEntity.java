package com.project.mssecurity.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "user_tb")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_ID")
    private Long id;
    @NotEmpty(message = "El Password no puede ser vacío")
    @Column(name = "Num_Doc", length = 10)
    private String numDoc;
    @NotEmpty(message = "El Password no puede ser vacío")
    @Size(min = 6, max = 150, message = "El Password debe tener al menos 6 caracteres")
    @Column(name = "Password", length = 150)
    private String password;

    @Column(name = "First_name", length = 150)
    private String firstName;

    @Column(name = "Last_name", length = 150)
    private String lastName;

    @Column(name = "Middle_name", length = 150)
    private String middleName;

    @Column(name = "State")
    private Integer state;

    @Email(message = "No es una dirección de correo bien formada")
    @NotEmpty(message = "El email no puede ser vacío")
    @Column(name = "Email", length = 150)
    private String email;

    @ManyToOne(optional = false)
    @JoinColumn(name = "Document_type_id", nullable = false)
    private DocumentTypeEntity documentType;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_user_type",
            joinColumns = @JoinColumn(name = "User_ID"),
            inverseJoinColumns = @JoinColumn(name = "User_type_id"))
    @JsonManagedReference
    private Set<UserTypeEntity> userTypeId = new HashSet<>();

    @Column(name = "Created_by", length = 150, nullable = true)
    private String createdUser;

    @Column(name = "Date_created", nullable = true)
    private Timestamp dateCreate;

    @Column(name = "Modified_by", length = 150, nullable = true)
    private String usuaModif;

    @Column(name = "Date_modified")
    private Timestamp dateModif;

    @Column(name = "Deleted_by", length = 150, nullable = true)
    private String usuaDelet;

    @Column(name = "Date_deleted", nullable = true)
    private Timestamp dateDelet;

    @Column(name = "Enable_user", nullable = false)
    private boolean enabled = false;

    @Column(name = "Account_noexpire", nullable = false)
    private boolean accountNonExpire = false;

    @Column(name = "Accountno_locked", nullable = false)
    private boolean accountNonLocked = false;

    @Column(name = "Credentials_noexpired", nullable = false)
    private boolean credentialsNonExpired = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userTypeId.stream().map(type -> new SimpleGrantedAuthority(type.getRoleUser())).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpire;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}