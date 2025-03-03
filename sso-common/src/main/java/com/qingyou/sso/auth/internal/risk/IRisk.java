package com.qingyou.sso.auth.internal.risk;

import com.qingyou.sso.auth.api.dto.Checked;
import com.qingyou.sso.auth.internal.Enforcement;

public interface IRisk extends Enforcement<Checked<IRisk.Request>> {

    interface Request {
    }

}