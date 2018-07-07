package com.dvarubla.sambamusicplayer.smbutils;

public class LocationData implements Cloneable{
    private String _server;
    private String _share;
    private String _path;

    public LocationData(String str){
        String [] arr = str.split("[/|\\\\]+");
        _server = arr[0];
        _share = arr[1];
        _path = (arr.length == 3) ? arr[2] : "";
    }

    public String getServer() {
        return _server;
    }

    public String getShare() {
        return _share;
    }

    public String getPath() {
        return _path;
    }

    public void setServer(String server) {
        this._server = server;
    }

    public void setShare(String share) {
        this._share = share;
    }

    public void setPath(String path) {
        this._path = path;
    }

    @Override
    public LocationData clone() {
        try {
            return (LocationData) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
