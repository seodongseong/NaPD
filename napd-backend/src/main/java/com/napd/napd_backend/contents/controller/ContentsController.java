package com.napd.napd_backend.contents.controller;

import com.napd.napd_backend.contents.dto.ContentsCreateRequestDto;
import com.napd.napd_backend.contents.dto.ContentsListResponseDto;
import com.napd.napd_backend.contents.dto.ContentsResponseDto;
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

import java.util.List;
import java.util.stream.Collectors;

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
        // 1. SecurityContext에서 현재 로그인한 사용자의 ID(Principal)를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // principal은 String 타입의 User ID입니다.
        Long userId = Long.valueOf((String) authentication.getPrincipal());
        // 2. Service 로직 실행
        Contents createdContents = contentsService.createContents(userId, requestDto);
        // 3. 성공 응답 (201 Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(new ContentsResponseDto(createdContents));
    }

    /*
    * 게시물 목록 조회 API (페이지네이션 적용)
    * GET /api/contents
    * - 인증 불필요 (누구나 조회 가능)
    */

    // 단건 조회 (GET /api/contents/{contentsId})
    @GetMapping("/{contentsId}")
    public ResponseEntity<?> getContents(@PathVariable Long contentsId) {
        Contents contents = contentsService.getContents(contentsId);
        return ResponseEntity.ok(new ContentsResponseDto(contents));
    }

    // 전체 목록 조회 (GET /api/contents)
    @GetMapping
    public ResponseEntity<?> getAllContents() {
        List<Contents> contentsList = contentsService.getAllContents();
        List<ContentsResponseDto> responseDtos = contentsList.stream()
                .map(ContentsResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }



    /*
    * 게시물 수정 API
    * PUT /api/contents/{contentsId}
    * - 인증 및 인가(작성자) 필수
    */
    @PutMapping("/{contentsId}")
    public ResponseEntity<?> updateContents(@PathVariable Long contentsId,
                                            @Valid @RequestBody ContentsUpdateRequestDto requestDto){
        Long userId = Long.valueOf((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Contents updatedContents = contentsService.updateContents(userId, contentsId, requestDto);
        return ResponseEntity.ok(new ContentsResponseDto(updatedContents));
    }

    /*
    * 게시물 삭제 API
    * DELETE /api/contents/{contentsId}
    * - 인증 및 인가(작성자) 필수
    * */
    @DeleteMapping("/{contentsId}")
    public ResponseEntity<?> deleteContents(@PathVariable Long contentsId){
        Long userId = Long.valueOf((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        contentsService.deleteContents(userId, contentsId);
        // 성공 시 204 No contents 반환
        return ResponseEntity.noContent().build();
    }



}
