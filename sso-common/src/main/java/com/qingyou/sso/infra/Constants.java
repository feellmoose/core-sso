package com.qingyou.sso.infra;

public interface Constants {

    static String classPath() {
        var resource = Constants.class.getClassLoader().getResource("");
        if(resource == null) return null;
        return resource.getPath();
    }

    String _logo = """
            _____    __                            ____  ____
            _/ __ \\ _(_)_  ___   _   ___  __  __ ___/ _ )/ __/
            / /_/ / / / _ \\/ _ `/ // / _ \\/ // /___/ _  / _/
            \\___\\_\\/_/_//_/\\_, /\\_, /\\___/\\_,_/  _/____/___/
                        __/___//___/
            Lite And Fast""";

    static String logo(String name, String version) {
        return _logo + "          ::" + name + "::  " + version;
    }
}
