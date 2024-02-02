package com.codingrecipe.board.service;

import com.codingrecipe.board.dto.BoardDTO;
import com.codingrecipe.board.entity.BoardEntity;
import com.codingrecipe.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void save(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO); //DTO객체를 Entity객체로 옮겨담음
        boardRepository.save(boardEntity);
    }

    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();
        for (BoardEntity boardEntity : boardEntityList) {
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }
        return boardDTOList;
    }

    @Transactional
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

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

    public BoardDTO update(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);
        boardRepository.save(boardEntity);
        return findById(boardDTO.getId());
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    public Page<BoardDTO> paging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 3; //한 페이지에 보여줄 글의 개수
        //한 페이지 당 3개씩 글을 보여주고, 정렬 기준은 id(엔티티의 필드 이름으로 써야 함) 기준으로 내림차순 정렬
       //page 위치에 있는 값은 0부터 시작하기 때문에 위의 int page 선언하는 부분에 -1을 빼는 것임
        Page<BoardEntity> boardEntities = boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
        return null;
    }
}
