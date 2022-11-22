package com.example.dto;

import lombok.Data;

// 아현작성 - 메인페이지 게시글 조회시 사용
@Data
public class BoardMainDTO {
    Long bno ;
    String title ;
    String content ;
    Long hit ;
    String regdate ;
    String nickname; //사용자 닉네임
    int likecount; // 사용자의 좋아요 개수
    int replycount; // 사용자의 댓글 개수
    String imgurl; // 이미지 url
    
    // 날짜 포맷 변경을 위한 임시변수
    // @Transient
    // String date;   
}
