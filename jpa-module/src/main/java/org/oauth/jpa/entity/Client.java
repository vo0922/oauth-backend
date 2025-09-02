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
 * fileName       : Client
 * author         : sinuk
 * date           : 2025-09-02
 * description    : 클라이언트 정의
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@Entity
@Table(
        name = "tbl_client",
        indexes = {
                @Index(name = "idx_client_id", columnList = "client_id", unique = true)
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(length = 50, nullable = false, unique = true)
    @Comment("클라이언트 아이디")
    private String clientId;

    @Column(length = 200, nullable = false)
    @Comment("클라이언트 Secret")
    private String clientSecret;

    @Column(length = 300, nullable = false)
    @Comment("redirect uri")
    private String redirectUri;

    @Column(length = 200)
    private String scope;

    @Column(length = 200)
    @Comment("권한 e.g. authorization_code")
    private String grantTypes;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean publicClient;

    @CreatedDate
    @Comment("생성일")
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    @Comment("수정일")
    private LocalDateTime updateDateTime;
}