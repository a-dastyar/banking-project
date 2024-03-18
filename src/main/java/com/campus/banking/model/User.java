package com.campus.banking.model;

import java.util.Set;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@Builder
@With
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "users", indexes = {
        @Index(name = "user_email_idx", columnList = "email", unique = true),
        @Index(name = "user_username_idx", columnList = "username", unique = true) })
public class User extends BaseModel<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Length(min = 3)
    @Column(name = "username")
    private String username;

    @Length(min = 4)
    @Column(name = "password")
    private String password;

    @Email(regexp = ".+@.+\\..+")
    @Column(name = "email")
    private String email;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "username", referencedColumnName = "username"), indexes = {
            @Index(name = "", columnList = "username, name") })
    @Column(name = "name")
    private Set<Role> roles;

}