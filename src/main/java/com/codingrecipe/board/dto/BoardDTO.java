package com.codingrecipe.board.dto;

import com.codingrecipe.board.entity.BoardEntity;
import com.codingrecipe.board.entity.BoardFileEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor //기본생성자 자동 생성
@AllArgsConstructor //모든 필드를 매개변수로 하는 생성자 자동 생성
public class BoardDTO {
    private Long id;
    private String boardWriter;
    private String boardPass;
    private String boardTitle;
    private String boardContents;
    private int boardHits;
    private LocalDateTime boardCreatedTime;
    private LocalDateTime boardUpdatedTime;

    private List<MultipartFile> boardFile; //save.html -> Controller 넘어올 때 파일을 담는 용도
    private List<String> originalFileName; //원본 파일 이름
    private List<String> storedFileName; //서버 저장용 파일 이름
    private int fileAttached; //파일 첨부 여부(첨부:1, 미첨부:0)

    //Entity -> DTO 변환
    public static BoardDTO toBoardDTO(BoardEntity boardEntity) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(boardEntity.getId());
        boardDTO.setBoardWriter(boardEntity.getBoardWriter());
        boardDTO.setBoardPass(boardEntity.getBoardPass());
        boardDTO.setBoardTitle(boardEntity.getBoardTitle());
        boardDTO.setBoardContents(boardEntity.getBoardContents());
        boardDTO.setBoardHits(boardEntity.getBoardHits());
        boardDTO.setBoardCreatedTime(boardEntity.getCreatedTime());
        boardDTO.setBoardUpdatedTime(boardEntity.getUpdatedTime());
        if (boardEntity.getFileAttached() == 0) {
            boardDTO.setFileAttached(boardEntity.getFileAttached()); //0
        } else {
            List<String> originalFileNameList = new ArrayList<>(); //다중 파일첨부 시 List로 전달
            List<String> storedFileNameList = new ArrayList<>();
            boardDTO.setFileAttached(boardEntity.getFileAttached()); //1
            for (BoardFileEntity boardFileEntity : boardEntity.getBoardFileEntityList()) {
                originalFileNameList.add(boardFileEntity.getOriginalFileName());
                storedFileNameList.add(boardFileEntity.getStoredFileName());
            }
            boardDTO.setOriginalFileName(originalFileNameList);
            boardDTO.setStoredFileName(storedFileNameList);
            //파일 이름을 가져가야 함
            // originalFileName, storedFileName: board_file_table(BoardFileEntity)에 들어있음
            //join
            // select * from board_table b, board_file_table bf where b.id=bf.board_id and where b.id=?
            /*
            boardDTO.setOriginalFileName(boardEntity.getBoardFileEntityList().get(0).getOriginalFileName()); //boardEntity에서 BoardFileEntityList(자식)을 불러와서 그 중 0번째 인덱스의 객체에서 OriginalFileName을 얻어서 boardDTO의 originalFileName에 설정
            boardDTO.setStoredFileName(boardEntity.getBoardFileEntityList().get(0).getStoredFileName());
            */
        }
        return boardDTO;
    }

    //페이징 처리 시 필요한 데이터의 생성자
    public BoardDTO(Long id, String boardWriter, String boardTitle, int boardHits, LocalDateTime boardCreatedTime) {
        this.id = id;
        this.boardWriter = boardWriter;
        this.boardTitle = boardTitle;
        this.boardHits = boardHits;
        this.boardCreatedTime = boardCreatedTime;
    }
}
