package com.dvarubla.sambamusicplayer.smbutils;

public class FolderItem implements IFileOrFolderItem {
    private String _name;

    public FolderItem(String name){
        _name = name;
    }

    @Override
    public String getName() {
        return _name;
    }
}
