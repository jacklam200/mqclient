package cn.mqclient.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.mqclient.Layer.TemplateProcessor;
import cn.mqclient.Log;
import cn.mqclient.entity.Component;
import cn.mqclient.entity.ComponentData;
import cn.mqclient.provider.Layer;
import cn.mqclient.utils.LayerCutManager;

/**
 * Created by LinZaixiong on 2016/9/9.
 */
public class LayerAdapter extends BaseAdapter<Component> implements BaseAdapter.ViewCreator<Component> {

    private LayerCutManager utils;
    private List<Component> components = new ArrayList<>();

    public LayerAdapter(Context context, List<Component> mDataList) {
        super(context, mDataList);
        utils = new LayerCutManager(context);
        if (mDataList != null) {
            components.addAll(mDataList);
        }

        setViewCreator(this);
    }

    public int getLayerIndex(int position) {
        int index = (Component) getItem(position) != null ?
                ((Component) getItem(position)).getIndex() : -1;

        return index;
    }

    @Override
    public int getItemViewType(int position) {

        String name = (Component) getItem(position) != null ?
                ((Component) getItem(position)).getName() : "";
        return utils.typeConversion(name);
    }

    @Override
    public int getContentView(int type, int pos) {
        return 0;
    }

    @Override
    public View getTypeView(int type, int pos) {
        Component item = (Component) getItem(pos);
        View view = utils.generateView(pos, type, (Component) getItem(pos), item);
        //TODO:当view为null
        return view;
    }

    @Override
    public void updateViewAndData(int pos, Component item, View convertView, Object viewHolder) {
        LayerCutManager.LayerHolder layerHolder = (LayerCutManager.LayerHolder) viewHolder;

        if (layerHolder != null) {
            utils.updateViewAndData(pos, item, convertView, layerHolder);
        }
    }

    @Override
    public Object getViewHolder(View view, int type, int pos) {
        return utils.generateHolder(view, type, (Component) getItem(pos), (Component) getItem(pos));
    }

    public void notifyDataSetChanged(Layer data, List<ComponentData> list, String template) {

        if (!TextUtils.isEmpty(template)) {

//            if(template.contains(TemplateProcessor.TYPE_STR)){
//                this.notifyDataSetChanged();
//            }
//            else
            if (template.contains(TemplateProcessor.TYPE_STR)) {

                for (int i = 0; i < this.getCount(); i++) {
                    // 命令id不同，刷新界面，相同不刷新
                    Log.d(this.getClass().getName(), "dst Template:" + template);
                    Log.d(this.getClass().getName(), "current Template:" + ((Component) this.getItem(i)).getTemplate());
                    if(template.compareToIgnoreCase(((Component) this.getItem(i)).getTemplate()) == 0){
                        ((Component) this.getItem(i)).setGroupId(data.getId());
                        // 用于让模块检查时间是否到达
                        ((Component) this.getItem(i)).setBroadcastEndTime(data.getEnd_time().getTime());
                        ((Component) this.getItem(i)).setBroadcastStartTime(data.getBegin_time().getTime());
                        utils.notifyDataSetChange(i, list, (Component) this.getItem(i), template);
                    }
//                    if (data != null && !TextUtils.isEmpty(data.getCmd_id()) && !TextUtils.isEmpty(((Component) this.getItem(i)).getGroupId()) &&
//                            data.getId().compareToIgnoreCase(((Component) this.getItem(i)).getGroupId()) == 0) {
//                        continue;
//                    } else {
//                        Log.d(this.getClass().getName(), "dst Template:" + template);
//                        Log.d(this.getClass().getName(), "current Template:" + ((Component) this.getItem(i)).getTemplate());
//                        if(template.compareToIgnoreCase(((Component) this.getItem(i)).getTemplate()) == 0){
//                            ((Component) this.getItem(i)).setGroupId(data.getId());
//                            // 用于让模块检查时间是否到达
//                            ((Component) this.getItem(i)).setBroadcastEndTime(data.getEnd_time().getTime());
//                            ((Component) this.getItem(i)).setBroadcastStartTime(data.getBegin_time().getTime());
//                            utils.notifyDataSetChange(i, list, (Component) this.getItem(i), template);
//                        }
//
//
//                    }

                }
            }
        }
    }

    @Override
    public void notifyDataSetChanged() {
        // clear 操作
        if (this.getCount() == 0 ||
                (components != null && this.getCount() != components.size())) { // 清除子view
            if (components.size() > 0) {

                for (int i = 0; i < components.size(); i++) {
                    utils.notifyDataSetChange(i, null, (Component) components.get(i), ((Component) components.get(i)).getName());
                }

            }
            components.clear();
            if (getData() != null) {
                components.addAll(getData());
            }
        } else {
            components.clear();
            if (getData() != null) {
                components.addAll(getData());
            }

        }

        for (int i = 0; i < this.getCount(); i++) {
            utils.notifyDataSetChange(i, null, (Component) this.getItem(i), ((Component) this.getItem(i)).getName());
        }

        super.notifyDataSetChanged();
    }

    public void setBackGround(String url, String template) {

        for (int i = 0; i < this.getCount(); i++) {
            Component component = (Component) this.getItem(i);

            if (component != null && !TextUtils.isEmpty(component.getTemplate()) &&
                    component.getTemplate().compareToIgnoreCase(template) == 0) {
                if (component.getProcessor() != null) {

                    component.getProcessor().setBackGround(url);
                }
            }
        }
    }
}
