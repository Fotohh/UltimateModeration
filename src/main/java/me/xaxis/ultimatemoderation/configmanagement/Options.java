package me.xaxis.ultimatemoderation.configmanagement;

import me.xaxis.ultimatemoderation.UltimateModeration;

public enum Options {

    ;

    public boolean b(){
        return instance.getConfig().getBoolean(path);
    }

    private final String path;
    private final UltimateModeration instance;

    Options(String path, UltimateModeration instance){
        this.path = path;
        this.instance = instance;
    }

}
