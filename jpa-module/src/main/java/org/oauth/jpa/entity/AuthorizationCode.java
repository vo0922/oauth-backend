package org.oauth.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * packageName    : org.oauth.jpamodule.entity
 * fileName       : AuthorizationCode
 * author         : sinuk
 * date           : 2025-09-02
 * description    : 인증 코드 정의
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@Entity
@Table(name = "tbl_authorization_code", indexes = {
        @Index(name = "idx_authcode_used", columnList = "used"),
        @Index(name = "idx_authcode_expires", columnList = "expire_time")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class AuthorizationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(length = 200, nullable = false)
    @Comment("해쉬 값")
    private String hashValue;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "client_id",
            foreignKey = @ForeignKey(name = "fk_authcode_client"),
            nullable = false
    )
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(name = "fk_authcode_user"),
            nullable = false
    )
    private User user;

    @Column(length = 300, nullable = false)
    @Comment("redirect uri")
    private String redirectUri;

    // PKCE
    @Column(length = 256)
    private String codeChallenge;        // base64url(SHA256(verifier))

    @Column(length = 10)
    private String codeChallengeMethod;  // "S256" or "plain"

    @Column(length = 200)
    @Comment("영역")
    private String scope;

    @Column(nullable = false)
    @Comment("만료 시간")
    private Instant expireTime;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean used;

    @CreatedDate
    @Comment("생성일")
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    @Comment("수정일")
    private LocalDateTime updateDateTime;
}