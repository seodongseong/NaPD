package com.napd.napd_backend.contents.service;

import com.napd.napd_backend.contents.dto.ContentsCreateRequestDto;
import com.napd.napd_backend.contents.dto.ContentsListResponseDto;
import com.napd.napd_backend.contents.dto.ContentsUpdateRequestDto;
import com.napd.napd_backend.contents.entity.Contents;
import com.napd.napd_backend.contents.repository.ContentsRepository;
import com.napd.napd_backend.user.entity.User;
import com.napd.napd_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentsService {

    private final ContentsRepository contentsRepository;
    private final UserRepository userRepository; // User 엔티티를 찾기 위해 필요

    @Transactional
    public Contents createContents(Long userId, ContentsCreateRequestDto requestDto){

        // 1. JWT 토큰에서 추출된 UserId로 User 엔티티를 조회
        //      (인증된 사용자만 게시물을 작성할 수 있습니다.)
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. Contents 엔티티 생성
        Contents contents = new Contents();
        contents.setTitle(requestDto.getTitle());
        contents.setBody(requestDto.getBody());

        // 3. User 연관 관계 생성
        contents.setUser(user);

        // 4. DB 저장
        return contentsRepository.save(contents);

    }

    @Transactional(readOnly = true)
    public Page<ContentsListResponseDto> getAllContents(Pageable pageable){

        // 1. ContentsRepository를 사용하여 페이지네이션된 Contents 목록 조회
        Page<Contents> contentsPage = contentsRepository.findAll(pageable);

        // 2. 조회된 Contents 엔티티 Page를 ContentsListResponseDto Page로 변환
        return contentsPage.map(ContentsListResponseDto::new);

        // 참고 : N+1 문제 최적화는 추후에 진행됩니다.
    }

    @Transactional
    public Contents updateContents(Long userId, Long contentsId, ContentsUpdateRequestDto requestDto) {

        // 1. 게시물 조회
        Contents contents = contentsRepository.findById(contentsId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        // 2. 인가 확인 (Authorization)
        // 게시물의 작성자 ID와 현재 로그인한 사용자의 ID를 비교
        if (!contents.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("게시물을 수정할 권한이 없습니다.");
        }

        // 3. 내용 수정 (더티 체킹 발생)
        contents.setTitle(requestDto.getTitle());
        contents.setBody(requestDto.getBody());

        // 4. 수정 시간 업데이트 : @PreUpdate 어노테이션을 Contents 엔티티에 추가해야 함.

        // save()를 명시적으로 호출하지 않아도 @Transactional에 의해 변경 내용이 DB에 반영됨 (더티 체킹).
        return contents;
    }

    @Transactional
    public void deleteContents(Long userId, Long contentsId){

        // 1. 게시물 조회
        Contents contents = contentsRepository.findById(contentsId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        // 2. 인가 확인 (Authorization)
        if (!contents.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("게시물을 삭제할 권한이 없습니다.");
        }

        // 3. 삭제 실행
        contentsRepository.delete(contents);
    }
}
