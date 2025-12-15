package com.napd.napd_backend.contents.dto;

import com.napd.napd_backend.contents.entity.Contents;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ContentsListResponseDto {
    private Long id;
    private String title;
    private Long authorId;    // 작성자 식별을 위한 ID만 반환합니다.
    private LocalDateTime createAt;

    // Entity -> Dto 변환을 위한 생성자
    public ContentsListResponseDto(Contents contents) {
        this.id = contents.getId();
        this.title = contents.getTitle();
        // User 엔티티에서 ID를 가져옵니다. (연관 관계 사용)
        this.authorId = contents.getUser().getId();
        this.createAt = contents.getCreatedAt();
    }
}
