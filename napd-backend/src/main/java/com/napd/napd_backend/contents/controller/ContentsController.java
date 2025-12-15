package com.napd.napd_backend.contents.controller;

import com.napd.napd_backend.contents.dto.ContentsCreateRequestDto;
import com.napd.napd_backend.contents.dto.ContentsListResponseDto;
import com.napd.napd_backend.contents.dto.ContentsUpdateRequestDto;
import com.napd.napd_backend.contents.entity.Contents;
import com.napd.napd_backend.contents.service.ContentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentsController {

    private  final ContentsService contentsService;
    /*
    * 게시물 생성 API
    * POST /api/contents
    * -인증(JWT) 필수
    */
    @PostMapping
    public ResponseEntity<?> createContents(@Valid @RequestBody ContentsCreateRequestDto requestDto) {
        try{
            // 1. SecurityContext에서 현재 로그인한 사용자의 ID(Principal)를 가져옵니다.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // principal은 String 타입의 User ID입니다.
            Long userId = Long.valueOf((String) authentication.getPrincipal());

            // 2. Service 로직 실행
            Contents createdContents = contentsService.createContents(userId, requestDto);

            // 3. 성공 응답 (201 Created)
            return ResponseEntity.status(HttpStatus.CREATED).body(createdContents);

        } catch (IllegalArgumentException e){
            // 사용자 ID가 유효하지 않을 경우 (발생 가능성은 낮음)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // 기타 서버 오류
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시물 생성 중 서버 오류가 발생했습니다.");
        }
    }

    /*
    * 게시물 목록 조회 API (페이지네이션 적용)
    * GET /api/contents
    * - 인증 불필요 (누구나 조회 가능)
    */
    @GetMapping
    public ResponseEntity<Page<ContentsListResponseDto>> getAllContents(
            // URL 쿼리 파라미터가 없으면 기본값 설정 (createdAt 기준 내림차순)
            @PageableDefault(size = 20, sort = "createAt", direction = Sort.Direction.DESC)
            Pageable pageable){
        Page<ContentsListResponseDto> contentsList = contentsService.getAllContents(pageable);
        return ResponseEntity.ok(contentsList);

    }

    /*
    * 게시물 수정 API
    * PUT /api/contents/{contentsId}
    * - 인증 및 인가(작성자) 필수
    */
    @PutMapping("/{contentsId}")
    public ResponseEntity<?> updateContents(@PathVariable Long contentsId,
                                            @Valid @RequestBody ContentsUpdateRequestDto requestDto){
        try{
            Long userId = Long.valueOf((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

            Contents updatedContents = contentsService.updateContents(userId, contentsId, requestDto);

            return ResponseEntity.ok(updatedContents);

        } catch (IllegalArgumentException e) {
            // 인가 실패 또는 게시물 없음
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403 Forbidden
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시물 수정 중 서버 오류가 발생했습니다.");
        }
    }

    /*
    * 게시물 삭제 API
    * DELETE /api/contents/{contentsId}
    * - 인증 및 인가(작성자) 필수
    * */
    @DeleteMapping("/{contentsId}")
    public ResponseEntity<?> deleteContents(@PathVariable Long contentsId){
        try{
            Long userId = Long.valueOf((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

            contentsService.deleteContents(userId, contentsId);

            // 성공 시 204 No contents 반환
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403 Forbidden
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시물 삭제 중 서버 오류가 발생했습니다.");
        }
    }




}
