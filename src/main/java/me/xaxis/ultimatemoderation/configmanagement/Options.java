package me.xaxis.ultimatemoderation.configmanagement;

import me.xaxis.ultimatemoderation.UMP;

public enum Options {

    ;

    public boolean b(){
        return instance.getConfig().getBoolean(path);
    }

    private final String path;
    private final UMP instance;

    Options(String path, UMP instance){
        this.path = path;
        this.instance = instance;
    }

}
