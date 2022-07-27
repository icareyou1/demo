package com.fentric.utils;

import com.fentric.constant.ContantToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//JWT工具类
public class JwtUtils {
    //定义令牌密钥
    private static final String secret=ContantToken.SECRET;
    //令牌有效时间
    private static final int expireTime=ContantToken.EXPIRETIME;

    //生成UUID
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }
    //生成token
    public static String createToken(String data){
        HashMap<String, Object> map = new HashMap<>();
        map.put("loginUser_key",data);
        String token = Jwts.builder()
                .setClaims(map)
                .signWith(SignatureAlgorithm.HS256, secret)
                //todo  目前token一个月
                .setExpiration(new Date(System.currentTimeMillis()+ContantToken.MILLIS_MINUTE*expireTime))
                .compact();
        return token;
    }
    //解析token
    public static Claims parseToken(String token)
    {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
