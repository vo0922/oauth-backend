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
 * fileName       : RefreshToken
 * author         : sinuk
 * date           : 2025-09-02
 * description    : RefreshToken 정의
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@Entity
@Table(name = "tbl_refresh_token", indexes = {
        @Index(name = "idx_rtoken_user", columnList = "user_id"),
        @Index(name = "idx_rtoken_client", columnList = "client_id"),
        @Index(name = "idx_rtoken_expires", columnList = "expire_time"),
        @Index(name = "idx_rtoken_revoked", columnList = "revoked")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(name = "fk_refresh_user"),
            nullable = false
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "client_id",
            foreignKey = @ForeignKey(name = "fk_refresh_client"),
            nullable = false
    )
    private Client client;

    @Column(length = 200, nullable = false)
    @Comment("토큰 해쉬 값")
    private String hashValue;

    @Column(nullable = false)
    @Comment("만료 시간")
    private Instant expireTime;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean revoked;

    @Column(length = 500)
    @Comment("IP, UA 등 감사 로그용(선택)")
    private String metadata;

    @CreatedDate
    @Comment("생성일")
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    @Comment("수정일")
    private LocalDateTime updateDateTime;
}