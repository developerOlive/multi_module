package com.ot.schedule.core.domain;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "ROLES")
    private String roles;

    @Column(name = "USER_TYPE")
    private String userType;

    @Column(name = "REG_DATE")
    private LocalDateTime regDate;

    @Column(name = "LAST_LOGIN_DATE")
    private LocalDateTime lastLoginDate;

    @Column(name = "UPDATE_DATE")
    private LocalDateTime updateDate;
    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "REG_USER_SEQ")
    private Long regUserSeq;

    @Column(name = "UPD_USER_SEQ")
    private Long updUserSeq;

    public User setInactive() {
        status = Status.INACTIVE;
        updateDate = LocalDateTime.now();
        return this;
    }

    @Getter
    @AllArgsConstructor
    public enum Status {
        ACTIVE(0, "가입"),
        INACTIVE(1, "휴면"),
        WITHDRAWAL(2, "탈퇴");

        private final Integer Id;
        private final String title;
    }
}
