package com.napd.napd_backend.contents.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.napd.napd_backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Contents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd HH:mm",
                timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm",
            timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;

    // User와의 ManyToOne 관계 설정
    @JsonIgnore // 추가 : JSON 변환 시 이 필드를 무시합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 게시물을 작성한 사용자

    // 게시물 업데이트 시간은 @PreUpDate 어노테이션으로 처리 예정

    @PrePersist // 엔티티가 영속화되기 전에 실행 (최초 저장 시)
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
        // 최초 저장 시 updatedAt도 createdAt과 동일하게 설정
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate // 엔티티가 수정되어 flush되기 전에 실행
    protected void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

}
