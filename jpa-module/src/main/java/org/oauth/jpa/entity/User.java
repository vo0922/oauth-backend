package org.oauth.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * packageName    : org.oauth.jpamodule.entity
 * fileName       : User
 * author         : sinuk
 * date           : 2025-09-02
 * description    : 사용자 테이블 정의
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@Entity
@Table(
        name = "tbl_user",
        indexes = {
                @Index(name = "idx_user_id", columnList = "user_id", unique = true)
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(length = 50, nullable = false, unique = true)
    @Comment("사용자 아이디")
    private String userId;

    @Column(length = 200, nullable = false)
    @Comment("사용자 비밀번호")
    private String password;

    @Column(length = 100)
    @Comment("사용자 이메일")
    private String email;

    @Column(length = 1)
    @Comment("성별")
    private Byte gender;

    @Column(length = 11)
    @Comment("휴대폰 번호")
    private String mobileNo;

    @Column(nullable = false)
    @ColumnDefault("true")
    @Comment("사용 여부")
    private Boolean enabled;

    @CreatedDate
    @Comment("생성일")
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    @Comment("수정일")
    private LocalDateTime updateDateTime;

    @Column(length = 200)
    @Comment("영역 e.g. read write")
    private String defaultScopes;
}