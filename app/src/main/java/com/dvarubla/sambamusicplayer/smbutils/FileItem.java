package com.dvarubla.sambamusicplayer.smbutils;

public class FileItem implements IFileOrFolderItem {
    private String _name;

    public FileItem(String name){
        _name = name;
    }

    @Override
    public String getName() {
        return _name;
    }
}
