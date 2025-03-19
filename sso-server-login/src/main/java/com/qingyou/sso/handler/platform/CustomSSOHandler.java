package com.qingyou.sso.handler.platform;

import com.qingyou.sso.api.Login;
import com.qingyou.sso.api.Register;


public interface CustomSSOHandler extends Login, Register {
    default String getName() { return "";}
}
