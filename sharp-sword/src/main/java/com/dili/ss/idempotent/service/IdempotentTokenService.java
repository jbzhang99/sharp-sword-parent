package com.dili.ss.idempotent.service;


import com.dili.ss.idempotent.dto.TokenPair;

public interface IdempotentTokenService {

    /**
     * 获取token
     * @param url
     * @return key: url + tokenValue, value: tokenValue
     */
    TokenPair getToken(String url);


}
