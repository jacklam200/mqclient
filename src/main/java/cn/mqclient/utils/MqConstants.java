package cn.mqclient.utils;

/**
 * Created by Kanwah on 2016/11/28.
 */

public interface MqConstants extends SpConstants {
    String CMD_SPLIT = "10";
    String CMD_PROGRAM = "20";
    String CMD_REBOOT = "30";
    String CMD_LOCK = "1";

    String TIMED_SHUTDOWN = "40"; // 定时关机 data 里的是 关机时间
    String TIMING_BOOT = "50"; // 定时开机 data 里的是 开机时间
    String VOLUME = "60"; // 音量调节 data 里的是 音量百分比
    String PHOTOGRAPH = "70"; // 拍照 data 无
    String SCREENSHOT = "80"; // 截屏 data 无
    String STOP = "90"; // 停播 data 无
    String STATE = "100"; // 状态 data 无
    String UPGRADE = "110"; // 更新 data 里的是 更新的url(包含版本)

}
