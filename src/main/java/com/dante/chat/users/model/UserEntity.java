package com.dante.chat.users.model;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@ToString
@Entity
@Table(name="users")
public class UserEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name ="id")
    private Long id;

    @Column(name ="email_id")
    @NotEmpty(message = "Email id cannot be blank")
    @Email(message = "Not properly formatted email id")
    private String emailId;

    @NotEmpty(message = "Password cannot be empty")
    @Column(name="password")
    private String password;

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
