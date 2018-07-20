package com.dvarubla.sambamusicplayer;

public class Util {
    public static Throwable getCause(Throwable e) {
        Throwable cause;
        Throwable result = e;

        while(null != (cause = result.getCause())  && (result != cause) ) {
            result = cause;
        }
        return result;
    }
}
