package org.oauth.resource.dto.res;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * packageName    : org.oauth.resource.dto.res
 * fileName       : ResUserDto
 * author         : sinuk
 * date           : 2025-09-03
 * description    : 사용자 정보 응답 Dto
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-03         sinuk       최초 생성
 **/

@Data
@Builder
public class ResUserDto {

    private String userId;
    private String email;
    private String userName;
    private Byte gender;
    private LocalDateTime createDateTime;
}