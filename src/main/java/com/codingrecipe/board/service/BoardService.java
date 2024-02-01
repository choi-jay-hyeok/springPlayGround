package com.codingrecipe.board.service;

import com.codingrecipe.board.dto.BoardDTO;
import com.codingrecipe.board.entity.BoardEntity;
import com.codingrecipe.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//Repository의 JpaRepository에서 상속받은 save() 메서드는 매개변수를 entity 타입으로 받도록 되어있고, 리턴도 entity 타입으로 반환하게끔 되어있음
//따라서 DTO -> Entity로 변환(controller로부터 DTO 타입으로 받으므로 / Entity 클래스에서 변환) 및,
// Entity -> DTO로 변환(데이터 조회 시, repository로부터 Entity 타입으로 받아서 controller로 반환할 때는 DTO 타입으로 줘야 하므로 / DTO 클래스에서 변환)하는 작업을 해야함

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public void save(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
        boardRepository.save(boardEntity);
    }
}
