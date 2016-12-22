package cn.mqclient.utils;

import com.alibaba.fastjson.JSON;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import cn.mqclient.App;
import cn.mqclient.Layer.common.PSubject;
import cn.mqclient.Log;
import cn.mqclient.entity.Command;
import cn.mqclient.entity.ComponentArray;
import cn.mqclient.entity.ComponentData;
import cn.mqclient.entity.LayerData;
import cn.mqclient.provider.Layer;
import cn.mqclient.provider.OpLayerDao;
import cn.mqclient.service.TimerService;

import static android.R.attr.data;

/**
 * Created by LinZaixiong on 2016/12/19.
 */

public class LayerParser {


    public boolean prepare(ComponentArray layer, Command cmd){
        boolean isRet = false;

        List<LayerData.BTime> times = getBTime(layer);
        if(times != null && times.size() > 0) {
            doWriteLayer(layer, times, cmd);
            isRet = true;
        }
        else{
            Log.d("jacklam", "single time discard");
        }

        return isRet;
    }

    public boolean execute(ComponentArray data, Command cmd, int status, String percent){

        boolean isRet = false;

        if(data != null && data.getData() != null){

            List<LayerData.BTime> times = getBTime(data);
            String programmes = JSON.toJSONString(data.getData().getProgrammes());
            for (int i = 0; i < times.size(); i++) {

                Layer layer = invert2(times.get(i).getBroadcastStartTime(),
                        times.get(i).getBroadcastEndTime(),
                        data.getData().getId(),  getModuleName(data), programmes, cmd, status, percent);
                Log.d("jacklam", "execute:");
                OpLayerDao.getInstance(App.getInstance()).write(layer);
            }

            isRet = true;
        }




        return isRet;
    }


    private void doWriteLayer(ComponentArray data,  List<LayerData.BTime>  times, Command cmd){

        String programmes = JSON.toJSONString(data.getData().getProgrammes());

        for (int i = 0; i < times.size(); i++) {

            long curmills = System.currentTimeMillis();
            if(curmills < times.get(i).getBroadcastEndTime() +  TimerService.MILLISTIME){

                Layer layer = invert2(times.get(i).getBroadcastStartTime(),
                        times.get(i).getBroadcastEndTime(),
                        data.getData().getId(),  getModuleName(data), programmes, cmd);
                OpLayerDao.getInstance(App.getInstance()).write(layer);
            }
        }
    }

//    private void doMultiTime(ComponentArray data,  List<LayerData.BTime>  times){
//        String programmes = JSON.toJSONString(data.getData().getProgrammes());
//        for (int i = 0; i < times.size(); i++) {
//            long curmills = System.currentTimeMillis();
//            Log.e("jacklam", "current millis:" + curmills);
//            Log.e("jacklam", "BroadcastStartTime:" + times.get(i).getBroadcastStartTime());
//            Log.e("jacklam", "BroadcastEndTime:" + (times.get(i).getBroadcastEndTime()+
//                    TimerService.MILLISTIME));
//            if(curmills >= times.get(i).getBroadcastStartTime() &&
//                    curmills < times.get(i).getBroadcastEndTime() +  TimerService.MILLISTIME){
//
//                ModuleFile module = new ModuleFile("");
//                module.write(getModuleName(data), programmes);
//                PSubject.getInstance().notify(data.get(0), data.get(0).getProgrammeItems(), data.get(0).getTemplate());
//            }
//            else{
//
//                Layer layer = invert2(times.get(i).getBroadcastStartTime(),
//                        times.get(i).getBroadcastEndTime(),
//                        data.getData().getId(),  getModuleName(data), programmes);
//                OpLayerDao.getInstance(App.getInstance()).write(layer);
//            }
//        }
//    }


    public String getModuleName(ComponentArray data){

        String moduleName = "";

        if(data != null && data.getData() != null){

            if(data.getData().getProgrammes() != null &&
                    data.getData().getProgrammes().size() > 0){

                moduleName = data.getData().getProgrammes().get(0).getTemplate();
            }
        }

        return moduleName;
    }

    public  List<LayerData.BTime> getBTime(ComponentArray layer){
        return layer != null && layer.getData() != null &&
                layer.getData().getBroadcastTimes() != null &&
                layer.getData().getBroadcastTimes().size() > 0 ?
                layer.getData().getBroadcastTimes() : null;
    }


    private Layer invert2(long start, long end, String id, String moduleName, String msg, Command cmd){
        Layer layer = new Layer();
        // 用开始时间和结束时间和id和moduleName确认这条消息唯一
        layer.setId(Md5.MD5(id + start + end +moduleName));
        layer.setBegin_time(new Date(start));
        layer.setEnd_time(new Date(end));
        layer.setGroup_name(id);
        layer.setJson(msg);
        layer.setCmd_id(cmd.getId());
        layer.setCmd_server_name(cmd.getServerName());
        layer.setName(moduleName);
        return layer;
    }

    private Layer invert2(long start, long end, String id, String moduleName, String msg , Command cmd, int status, String percent){
        Layer layer = new Layer();
        // 用开始时间和结束时间和id和moduleName确认这条消息唯一
        layer.setId(Md5.MD5(id + start + end + moduleName));
        layer.setBegin_time(new Date(start));
        layer.setEnd_time(new Date(end));
        layer.setGroup_name(id);
        layer.setJson(msg);
        layer.setName(moduleName);
        layer.setCmd_id(cmd.getId());
        layer.setCmd_server_name(cmd.getServerName());
        layer.setDown_status(status);
        layer.setDown_percent(percent);
        return layer;
    }


}
