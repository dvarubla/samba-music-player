package com.dvarubla.sambamusicplayer;

import java.util.HashMap;

public class ItemSingleton<T>{
    private T item;

    private static HashMap<Class<?>,ItemSingleton<?>> instMap = new HashMap<>();

    public static <E> ItemSingleton<E> getInstance(Class<E> instClass) {
        if(instMap.containsKey(instClass)) {
            //noinspection unchecked
            return (ItemSingleton<E>) instMap.get(instClass);
        } else {
            ItemSingleton<E> instance = new ItemSingleton<>();
            instMap.put(instClass, instance);
            return instance;
        }
    }
    public void setItem(T item){
        this.item = item;
    }

    public boolean hasItem(){
        return item != null;
    }

    public void removeItem(){
        item = null;
    }

    public T getItem(){
        return item;
    }

    private ItemSingleton() {
        item = null;
    }
}
