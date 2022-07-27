package me.xaxis.ultimatemoderation.configmanagement;

import me.xaxis.ultimatemoderation.UltimateModeration;

public enum Lang {

    ;

    public String s(){
        return instance.getConfig().getString(path);
    }

    private final String path;
    private final UltimateModeration instance;

    Lang(String path, UltimateModeration instance){
        this.path = path;
        this.instance = instance;
    }

}
