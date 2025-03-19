package com.qingyou.sso.api.dto;

import com.qingyou.sso.api.constants.DataType;
import com.qingyou.sso.api.constants.PlatformType;

import java.util.Map;

public record ThirdPartySSOUserInfo (
        String name,
        String email,
        String phone,
        Info info
){
    public record Info(
            String metadata,
            DataType dataType,
            PlatformType platformType
    ){
        public Map<String,String> getValue(){
            return dataType.decode(metadata);
        }
        public static Info from(Map<String,String> obj, DataType dataType, PlatformType platformType){
            return new Info(dataType.encode(obj),dataType,platformType);
        }
    }
}
