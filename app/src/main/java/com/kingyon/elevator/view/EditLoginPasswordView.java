package com.kingyon.elevator.view;

import com.kingyon.elevator.mvpbase.BaseView;

/**
 * Created By SongPeng  on 2019/12/3
 * Email : 1531603384@qq.com
 */
public interface EditLoginPasswordView extends BaseView {

    void  passwordEditSuccess();

    void showCountDownTime(int downtime);

}
