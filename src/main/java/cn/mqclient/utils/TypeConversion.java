package cn.mqclient.utils;

import android.text.TextUtils;

import cn.mqclient.Layer.AudioProcessor;
import cn.mqclient.Layer.BackGroundProcessor;
import cn.mqclient.Layer.ClockProcessor;
import cn.mqclient.Layer.CountDownProcessor;
import cn.mqclient.Layer.ImageProcessor;
import cn.mqclient.Layer.TemplateProcessor;
import cn.mqclient.Layer.TextProcessor;
import cn.mqclient.Layer.VideoProcessor;
import cn.mqclient.Layer.WeatherProcessor;
import cn.mqclient.Layer.WebProcessor;

/**
 * Created by LinZaixiong on 2016/9/9.
 */
public class TypeConversion {

    public static int typeConversion(String str){

        if(!TextUtils.isEmpty(str)){

            if(ImageProcessor.TYPE_STR.compareToIgnoreCase(str) == 0){

                return ImageProcessor.TYPE_INT;

            }
            else if(TextProcessor.TYPE_STR.compareToIgnoreCase(str) == 0){

                return TextProcessor.TYPE_INT;

            }
            else if(BackGroundProcessor.TYPE_STR.compareToIgnoreCase(str) == 0){
                return BackGroundProcessor.TYPE_INT;
            }
            else if(VideoProcessor.TYPE_STR.compareToIgnoreCase(str) == 0){

                return VideoProcessor.TYPE_INT;

            }
            else if(AudioProcessor.TYPE_STR.compareToIgnoreCase(str) == 0){

                return AudioProcessor.TYPE_INT;

            }
            else if(WebProcessor.TYPE_STR.compareToIgnoreCase(str) == 0){
                return WebProcessor.TYPE_INT;
            }
            else if(CountDownProcessor.TYPE_STR.compareToIgnoreCase(str) == 0){
                return CountDownProcessor.TYPE_INT;
            }
            else if(WeatherProcessor.TYPE_STR.compareToIgnoreCase(str) == 0){
                return WeatherProcessor.TYPE_INT;
            }
            else if(ClockProcessor.TYPE_STR.compareToIgnoreCase(str) == 0){
                return ClockProcessor.TYPE_INT;
            }
            else if(TemplateProcessor.TYPE_STR.compareToIgnoreCase(str) == 0 || str.contains(TemplateProcessor.TYPE_STR)){
                return TemplateProcessor.TYPE_INT;
            }

        }

        return 0;
    }
}
