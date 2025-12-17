package com.napd.napd_backend.contents.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.napd.napd_backend.contents.entity.Contents;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ContentsResponseDto {
    private Long id;
    private String title;
    private String body;

    // 유저 정보는 게시물의 작성자 ID만 노출
    private Long userId;
    // (선택사항) 만약 User 엔티티에서 유저 이름을 가져올 수 있다면 노출 가능
    // private String username;

    // 날짜 포맷은 DTO에서 다시 지정해주는 것이 유연합니다.
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;

    // 엔티티를 받아서 DTO를 생성하는 생성자
    public ContentsResponseDto(Contents contents){
        this.id = contents.getId();
        this.title = contents.getTitle();
        this.body = contents.getBody();
        this.createdAt = contents.getCreatedAt();
        this.updatedAt = contents.getUpdatedAt();

        // Contents 엔티티의 user 필드에서 ID를 가져옵니다.
        // 이 때, LAZY 로딩 문제가 발생하지 않도록 주의해야 합니다.
        // 현재 user 필드에는 @JsonIgnore가 있지만, DTO에서 접근 시 트랜잭션이 살아있어야 합니다.
        this.userId = contents.getUser().getId();

    }

}
