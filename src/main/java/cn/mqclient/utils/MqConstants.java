package cn.mqclient.utils;

/**
 * Created by Kanwah on 2016/11/28.
 */

public interface MqConstants extends SpConstants {
    String CMD_SPLIT = "11";
    String CMD_PROGRAM = "21";
    String CMD_REBOOT = "31";
    String CMD_LOCK = "2";

    String TIMED_SHUTDOWN = "41"; // 定时关机 data 里的是 关机时间
    String TIMING_BOOT = "51"; // 定时开机 data 里的是 开机时间
    String VOLUME = "61"; // 音量调节 data 里的是 音量百分比
    String PHOTOGRAPH = "71"; // 拍照 data 无
    String SCREENSHOT = "81"; // 截屏 data 无
    String STOP = "91"; // 停播 data 无
    String STATE = "101"; // 状态 data 无
    String UPGRADE = "111"; // 更新 data 里的是 更新的url(包含版本)

}
