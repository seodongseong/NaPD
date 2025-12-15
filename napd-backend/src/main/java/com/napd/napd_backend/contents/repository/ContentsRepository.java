package com.napd.napd_backend.contents.repository;

import com.napd.napd_backend.contents.entity.Contents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentsRepository extends JpaRepository<Contents, Long> {
    // 향후 특정 사용자(User)가 작성한 게시물 목록 조회 등 추가 예정
}
