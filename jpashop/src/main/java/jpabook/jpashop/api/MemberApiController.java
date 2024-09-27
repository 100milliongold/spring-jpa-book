package jpabook.jpashop.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;




@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }
    

    @GetMapping("/api/v2/members")
    public Result<List<MemberDTO>> memberV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDTO> collect = findMembers.stream()
            .map(m -> new MemberDTO(m.getName()))
            .collect(Collectors.toList());

        return new Result<List<MemberDTO>>(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }
   
    @Data
    @AllArgsConstructor
    static class MemberDTO {
        private String name;
    }
    

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id =  memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse postMethodName(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());
       Long id = memberService.join(member);
       return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member findMember = memberService.findMember(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }
    

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    } 

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    @AllArgsConstructor
    public static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
     public static class UpdateMemberRequest {
        private String name;
     }
}
