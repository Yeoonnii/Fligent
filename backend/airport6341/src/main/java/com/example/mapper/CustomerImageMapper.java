package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.entity.MemberImg;

@Mapper
public interface CustomerImageMapper {

    public MemberImg selectmemberimage(Long mimageno);

    public List<MemberImg> selectmemberimageList(String userid);

    public int updatememberimage(MemberImg memberImg);

    public int deletememberImage( Long mimageno );

    public Long selectMemberImageNo( String userid );
}
