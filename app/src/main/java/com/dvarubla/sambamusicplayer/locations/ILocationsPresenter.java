package com.dvarubla.sambamusicplayer.locations;

import io.reactivex.Observable;

public interface ILocationsPresenter {
    ILocationsFixedCtrl getLocFixComp();
    ILocationsEditableCtrl getLocEdComp();
    boolean isEditPressed();
    void setView(ILocationsView view);
}
