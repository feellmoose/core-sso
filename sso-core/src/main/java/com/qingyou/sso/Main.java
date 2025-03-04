package com.qingyou.sso;

import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) {
        try {
            new CoreSSOApp().start().toCompletionStage().toCompletableFuture()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
