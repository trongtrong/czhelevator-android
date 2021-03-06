package com.kingyon.elevator.view;

import com.kingyon.elevator.mvpbase.BaseView;
import com.kingyon.elevator.photopicker.MediaDirectory;

import java.util.List;

/**
 * Created By SongPeng  on 2019/12/17
 * Email : 1531603384@qq.com
 */
public interface PhotoPickerView extends BaseView {

    void loadFolderListSuccess(List<MediaDirectory> mResultFolder);

    void loadFolderListFailed();

    void loadAdInfoSuccess(String coverPath,String count);

    void loadAdInfoFailed();

}
