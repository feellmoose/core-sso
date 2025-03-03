package com.qingyou.sso.auth.internal.rbac;

import com.qingyou.sso.auth.api.dto.Action;
import com.qingyou.sso.auth.internal.Enforcement;

public interface IRbac<U extends IRbac.IUser, T extends IRbac.ITarget> extends Enforcement<Action<U, T>> {

    interface IUser {
    }

    interface ITarget {
    }

}
