package com.dante.chat.users.model;

import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="auth")
public class AuthEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name ="id")
    private Long id;

    @Column(name ="user_id")
    private Long userId;

    @Column(name ="token")
    private String token;


}
