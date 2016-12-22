package cn.mqclient.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import java.util.List;

import cn.mqclient.Layer.AudioProcessor;
import cn.mqclient.Layer.BackGroundProcessor;
import cn.mqclient.Layer.BaseProcessor;
import cn.mqclient.Layer.ClockProcessor;
import cn.mqclient.Layer.CountDownProcessor;
import cn.mqclient.Layer.DefaultProcessor;
import cn.mqclient.Layer.ImageProcessor;
import cn.mqclient.Layer.TemplateProcessor;
import cn.mqclient.Layer.TextProcessor;
import cn.mqclient.Layer.VideoProcessor;
import cn.mqclient.Layer.WeatherProcessor;
import cn.mqclient.Layer.WebProcessor;
import cn.mqclient.entity.Component;
import cn.mqclient.entity.ComponentData;
import cn.mqclient.entity.IProcessor;

/**
 *  layer管理器
 * Created by LinZaixiong on 2016/9/9.
 */
public class LayerCutManager {

    private Context context;

    /**
     *  layer持有器
     */
    public static class LayerHolder{

        private View view;
        private int type;
        private BaseProcessor processor;

        public BaseProcessor getProcessor() {
            return processor;
        }

        public void setProcessor(BaseProcessor processor) {
            this.processor = processor;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }
    }

    public LayerCutManager(Context context){
        this.context  = context;
    }

    public void notifyDataSetChange(int pos, List<ComponentData> list, Component component, String template){

       if(!TextUtils.isEmpty(template)){

           if(template.compareToIgnoreCase(component.getTemplate()) == 0){

               if(component.getProcessor() != null)
                    component.getProcessor().notifyDataSetChange(list, template);
           }
       }
    }

    /**
     * 生成view
     * @param pos
     * @param type
     * @param component
     * @return
     */
    public View generateView(int pos, int type, Component component, IProcessor processor){

        View view = null;

        if(processor != null){

            BaseProcessor base = createProcessor(type, component);
            //TODO: need to optmize
            processor.setProcessor(base);
            view = base.generateView(this.context, component);

            if(view == null){
                base = new DefaultProcessor(this.context, component.getTemplate());
                processor.setProcessor(base);
                view =  base.generateView(this.context, component);
            }
        }

        return view;

    }


    public void updateViewAndData(int pos, Component item, View convertView, LayerHolder holder){

        if(holder != null && holder.getProcessor() != null){

            holder.getProcessor().updateView(this.context, item);
        }
    }

    public LayerHolder generateHolder(View view, int type, Component data, IProcessor item){

        LayerHolder holder = new LayerHolder();
        holder.view = view;
        holder.type = type;
        holder.processor = (item != null ? item.getProcessor() : createProcessor(type, data));

        return holder;
    }


    public int typeConversion(String str){

        return  TypeConversion.typeConversion(str);
    }


    private BaseProcessor createProcessor(int type, Component item){

        BaseProcessor processor = null;

        switch (type){

            case ImageProcessor.TYPE_INT:
                processor = new ImageProcessor(this.context, item.getTemplate());
                break;

            case TextProcessor.TYPE_INT:
                processor = new TextProcessor(this.context, item.getTemplate());
                break;

            case VideoProcessor.TYPE_INT:
                processor = new VideoProcessor(this.context, item.getTemplate());
                break;

            case WebProcessor.TYPE_INT:
                processor = new WebProcessor(this.context, item.getTemplate());
                break;

            case BackGroundProcessor.TYPE_INT:
                processor = new BackGroundProcessor(this.context, item.getTemplate());
                break;

            case AudioProcessor.TYPE_INT:
                processor = new AudioProcessor(this.context, item.getTemplate());
                break;

            case WeatherProcessor.TYPE_INT:
                processor = new WeatherProcessor(this.context, item.getTemplate());
                break;

            case ClockProcessor.TYPE_INT:
                processor = new ClockProcessor(this.context, item.getTemplate());
                break;

            case CountDownProcessor.TYPE_INT:
                processor = new CountDownProcessor(this.context, item.getTemplate());
                break;
            case TemplateProcessor.TYPE_INT:
                processor = new TemplateProcessor(this.context, item.getTemplate());
                break;
            default:
                processor = new DefaultProcessor(this.context, item.getTemplate());
        }


        return processor;
    }

}
