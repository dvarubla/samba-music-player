package com.dvarubla.sambamusicplayer.filelist;

import com.dvarubla.sambamusicplayer.playlist.IPlaylist;
import com.dvarubla.sambamusicplayer.settings.ILoginPassMan;
import com.dvarubla.sambamusicplayer.smbutils.IFileOrFolderItem;
import com.dvarubla.sambamusicplayer.smbutils.ISmbUtils;
import com.dvarubla.sambamusicplayer.smbutils.LocationData;
import com.dvarubla.sambamusicplayer.smbutils.LoginPass;

import javax.inject.Inject;

import io.reactivex.Observable;

public class FileListModel implements IFileListModel {
    @Inject
    ISmbUtils _smbUtils;

    @Inject
    ILoginPassMan _lpman;

    @Inject
    IPlaylist _playlist;

    private LocationData _locData;

    @Inject
    FileListModel(){
    }

    private LoginPass getLoginPass(LocationData data){
        return _lpman.getLoginPass(data);
    }

    @Override
    public Observable<IFileOrFolderItem[]> getFiles() {
        return Observable.just(new Object()).flatMap(
                o -> _smbUtils.getFilesFromShare(_locData, getLoginPass(_locData)).toObservable()
        );
    }

    @Override
    public void setLoginPassForServer(LocationData data, LoginPass lp) {
        _lpman.setLoginPass(data, lp);
    }

    @Override
    public void setLocationData(LocationData location) {
        _locData = location.clone();
    }

    @Override
    public String addPath(String pathComp) {
        String path = joinPath(_locData.getPath(), pathComp);
        _locData.setPath(path);
        return path;
    }

    @Override
    public String removeFromPath() {
        _locData.setPath(_locData.getPath().replaceFirst("(?:^|/)[^/]+$", ""));
        return _locData.getPath();
    }

    @Override
    public void playFile(String file){
        LocationData fileLocData = _locData.clone();
        fileLocData.setPath(joinPath(_locData.getPath(), file));
        _playlist.addFile(fileLocData);
    }

    private String joinPath(String path1, String path2){
        if(path1.equals("")){
            return path2;
        }
        return path1 + "/" + path2;
    }

    @Override
    public void setNext() {
        _playlist.playNext();
    }

    @Override
    public void setPrevious() {
        _playlist.playPrev();
    }

    @Override
    public Observable<String> onFileAdded() {
        return _playlist.onFileAdded();
    }

    @Override
    public Observable<String> onFilePlaying() {
        return _playlist.onFilePlaying();
    }
}
