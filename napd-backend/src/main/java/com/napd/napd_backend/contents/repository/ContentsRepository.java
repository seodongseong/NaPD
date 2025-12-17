package com.napd.napd_backend.contents.repository;

import com.napd.napd_backend.contents.entity.Contents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContentsRepository extends JpaRepository<Contents, Long> {
    // 향후 특정 사용자(User)가 작성한 게시물 목록 조회 등 추가 예정
    // fetch join을 사용하여 Contents를 조회할 때 User 엔티티를 즉시(EAGER) 로딩합니다.
    @Query("SELECT c FROM Contents c JOIN FETCH c.user WHERE c.id = :id")
    Optional<Contents> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT c FROM Contents c JOIN FETCH c.user ORDER BY c.createdAt DESC")
    List<Contents> findAllWithUser();
}
