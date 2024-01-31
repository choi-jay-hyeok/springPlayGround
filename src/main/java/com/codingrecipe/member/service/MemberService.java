package com.codingrecipe.member.service;

import com.codingrecipe.member.dto.MemberDTO;
import com.codingrecipe.member.entity.MemberEntity;
import com.codingrecipe.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void save(MemberDTO memberDTO) {
        /*repository의 save 메서드 호출 (조건. repository는 jpa를 사용하기 때문에 jpa를 사용하기 위해서는
        repository로 넘겨줄 때 반드시 "entity 객체를 넘겨줘야 하기 때문"에 아래와 같이 dto를 entity로 변환 후 넘겨줘야 함")*/
        
        // 1 dto -> entity로 변환
        MemberEntity memberEntity = MemberEntity.toMemberEntity(memberDTO);
        // 2. repository의 save 메서드 호출
        memberRepository.save(memberEntity);
    }

    public MemberDTO login(MemberDTO memberDTO) {
//      1. 회원이 입력한 이메일로 DB에서 조회를 함
        Optional<MemberEntity> byMemberEmail = memberRepository.findByMemberEmail(memberDTO.getMemberEmail());
        if (byMemberEmail.isPresent()) {
            //조회 결과가 있으면 실행(해당 이메일을 가진 회원 정보가 있음)
            MemberEntity memberEntity = byMemberEmail.get();
            if (memberEntity.getMemberPassword().equals(memberDTO.getMemberPassword())) {
                //비밀번호가 일치하는 경우
                /*이 프로젝트에서는 entity 객체는 service에서만 쓸 것이기 때문에
                controller로 넘겨주기 위해서 entity -> dto로 변환 후 리턴*/
                MemberDTO dto = MemberDTO.toMemberDTO(memberEntity);
                return dto;
            } else {
                //비밀번호 불일치(로그인 실패)
                return null;
            }
        } else {
            //조회 결과가 없으면 실행(해당 이메일을 가진 회원이 없음)
            return null;
        }
//      2. DB에서 조회한 비밀번호와 사용자가 입력한 비밀번호가 일치하는지 판단
    }

    public List<MemberDTO> findAll() {
        List<MemberEntity> memberEntityList = memberRepository.findAll();
        List<MemberDTO> memberDTOList = new ArrayList<>();
        for (MemberEntity memberEntity : memberEntityList) {
            memberDTOList.add(MemberDTO.toMemberDTO(memberEntity));
        }
        return memberDTOList;
    }

    public MemberDTO findById(Long id) {
        Optional<MemberEntity> optionalMemberEntity = memberRepository.findById(id);
        if (optionalMemberEntity.isPresent()) {
            /*
            MemberEntity memberEntity = optionalMemberEntity.get();
            MemberDTO memberDTO = MemberDTO.toMemberDTO(memberEntity);
            return memberDTO;
            */
            return MemberDTO.toMemberDTO(optionalMemberEntity.get()); //위의 세 줄을 한 줄로 표현한 것
        } else {
            return null;
        }
    }

    public MemberDTO updateForm(String myEmail) {
        Optional<MemberEntity> optionalMemberEntity = memberRepository.findByMemberEmail(myEmail);
        if (optionalMemberEntity.isPresent()) {
            return MemberDTO.toMemberDTO(optionalMemberEntity.get());
        } else {
            return null;
        }
    }

    public void update(MemberDTO memberDTO) {
        memberRepository.save(MemberEntity.toUpdateMemberEntity(memberDTO)); //toMemberEntity로 해버리면 update가 아니라 insert가 됨
    }

    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }
}
