package com.dvarubla.sambamusicplayer.locations;

public interface ILocationsPresenter {
    ILocationsFixedCtrl getLocFixComp();
    ILocationsEditableCtrl getLocEdComp();
    boolean isEditPressed();
    void setView(ILocationsView view);
}
