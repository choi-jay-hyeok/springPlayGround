package com.codingrecipe.board.service;

import com.codingrecipe.board.dto.BoardDTO;
import com.codingrecipe.board.entity.BoardEntity;
import com.codingrecipe.board.entity.BoardFileEntity;
import com.codingrecipe.board.repository.BoardFileRepository;
import com.codingrecipe.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//Repository의 JpaRepository에서 상속받은 save() 메서드는 매개변수를 entity 타입으로 받도록 되어있고, 리턴도 entity 타입으로 반환하게끔 되어있음
//따라서 DTO -> Entity로 변환(controller로부터 DTO 타입으로 받으므로 / Entity 클래스에서 변환) 및,
// Entity -> DTO로 변환(데이터 조회 시, repository로부터 Entity 타입으로 받아서 controller로 반환할 때는 DTO 타입으로 줘야 하므로 / DTO 클래스에서 변환)하는 작업을 해야함

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;

    //게시글 작성
    public void save(BoardDTO boardDTO) throws IOException {
        //파일 첨부 여부에 따라 로직 분리
        if (boardDTO.getBoardFile().isEmpty()) {
            //첨부파일 없을 경우
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO); //DTO객체를 Entity객체로 옮겨담음
            boardRepository.save(boardEntity);
        } else {
            //첨부파일 있을 경우
            /*
                1. DTO에 담긴 파일을 꺼냄
                2. 파일의 이름 가져옴
                3. 서버 저장용 이름을 생성(ex. 내사진.jps => 986745_내사진.jpa
                4. 저장 경로 설정
                5. 해당 경로에 파일 저장
                6. board_table에 해당 데이터 save 처리
                7. board_file_table에 해당 데이터 save 처리
             */
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
            Long saveId = boardRepository.save(boardEntity).getId();
            BoardEntity board = boardRepository.findById(saveId).get();
            for (MultipartFile boardFile : boardDTO.getBoardFile()) {
//                MultipartFile boardFile = boardDTO.getBoardFile(); //1번 , 단일 파일첨부에서는 사용했지만, 다중파일 첨부에서는 for문에서 돌리기 때문에 필요없음
                String originalFilename = boardFile.getOriginalFilename(); //2번
                String storedFileName = System.currentTimeMillis() + "_" + originalFilename; //3번
                String savePath = "C:/Dev/coding_recipe/springboot_board/springboot_image/" + storedFileName; //C:\Dev\coding_recipe\springboot_board\springboot_img/923847657_내사진.jpg
                boardFile.transferTo(new File(savePath)); //5번
                BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(board, originalFilename, storedFileName);
                boardFileRepository.save(boardFileEntity);
            }
        }
    }

    //게시글 전체 목록 출력
    @Transactional
    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();
        for (BoardEntity boardEntity : boardEntityList) {
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }
        return boardDTOList;
    }

    //조회수 증가
    @Transactional
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

    @Transactional
    public BoardDTO findById(Long id) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
        if ((optionalBoardEntity.isPresent())) {
            BoardEntity boardEntity = optionalBoardEntity.get();
            BoardDTO boardDTO = BoardDTO.toBoardDTO(boardEntity);
            return boardDTO;
        } else {
            return null;
        }
    }

    //게시글 수정
    public BoardDTO update(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);
        boardRepository.save(boardEntity);
        return findById(boardDTO.getId());
    }

    //게시글 삭제
    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    //검색어 없을 시 페이징
    public Page<BoardDTO> paging(Pageable pageable) {
        return pagingWithSearch(pageable, null);
    }

    //검색어 있을 시 페이징
    public Page<BoardDTO> searchByTitle(String title, Pageable pageable) {
        return pagingWithSearch(pageable, title);
    }

    //페이징 처리 및 검색 기능
    public Page<BoardDTO> pagingWithSearch(Pageable pageable, String title) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 3; //한 페이지에 보여줄 글의 개수
        //한 페이지 당 3개씩 글을 보여주고, id(엔티티에 작성한 이름 기준으로 써야 함.(DB말고)) 기준으로 내림차순 정렬
        //아래에서 PageRequest.of()안의 매개변수로 넘겨주는 page의 값은 0부터 시작하기 때문에 위의 int page 선언하는 부분에 -1을 빼는 것임

        Page<BoardEntity> boardEntities;
        if (title != null && !title.isEmpty()) {
            //레포지토리 메서드를 사용하여 검색 수행
            boardEntities = boardRepository.findByBoardTitleContainingIgnoreCase(title, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
        } else {
            // 페이징 수행
            boardEntities = boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
        }

        //페이지 목록에서 보여줄 데이터: id, writer, title, hit, createdTime
        //Entity -> DTO로 변환(이 코드의 board가 엔티티이기 때문에 DTO로 변환해줘야 함)
        // Entity -> DTO 변환
        Page<BoardDTO> boardDTOS = boardEntities.map(board -> new BoardDTO(board.getId(), board.getBoardWriter(), board.getBoardTitle(), board.getBoardHits(), board.getCreatedTime()));

        return boardDTOS;
    }
}
