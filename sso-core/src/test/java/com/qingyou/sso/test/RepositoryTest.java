package com.qingyou.sso.test;

import com.qingyou.sso.Application;
import com.qingyou.sso.domain.user.Account;
import com.qingyou.sso.domain.user.User;
import com.qingyou.sso.inject.provider.BaseModule;
import com.qingyou.sso.utils.PasswordEncodeUtils;
import io.vertx.core.Vertx;
import lombok.SneakyThrows;

public class RepositoryTest {

//    @Test
    @SneakyThrows
    public void test() {

        BaseModule baseModule = Application.startCore(Vertx.vertx()).toCompletionStage().toCompletableFuture().get();
        System.out.println(baseModule.webClient().get("localhost:8080").send().toCompletionStage().toCompletableFuture().join().body().toString());


        String username = "root";
        PasswordEncodeUtils.EncodedPassword password = PasswordEncodeUtils.encode("qingyou+1s");


        User user = new User(null, "XiaoChenxv", "B20012001", null, null);

        baseModule.sessionFactory()
                .withTransaction(session -> session.persist(user))
                .convert()
                .toCompletableFuture().get();

        Account account = new Account(user.getId(), username, password.encoded(), password.salt(), null);

        baseModule.sessionFactory()
                .withTransaction(session -> session.persist(account))
                .convert()
                .toCompletableFuture().get();




    }
}
