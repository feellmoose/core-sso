package com.qingyou.sso.infra.repository.domain;

import com.qingyou.sso.domain.user.Account;
import com.qingyou.sso.infra.repository.base.BaseRepository;
import io.vertx.core.Future;

public interface AccountRepository extends BaseRepository<Account> {

    Future<Account> findByUsername(String username);

}
