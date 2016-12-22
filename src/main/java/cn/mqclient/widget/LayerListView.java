/**
 * 
 */
package cn.mqclient.widget;



import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import cn.mqclient.Layer.BaseProcessor;
import cn.mqclient.adapter.LayerAdapter;

/**
 * @author jacklam
 *
 */
public class LayerListView extends FrameLayout {

	private static final String TAG = "FixedListView";

	private LayerAdapter mAdapter = null;
	private Context mContext = null;

	private FixedListDataChange mDataChange = null;

	private boolean mIsShowDivider = false;
	/** 列数 */
	private int mItemCount = 0;

	private OnItemsClickListener mListener = null;
	private HashMap<Integer, SoftReference<View>> mViewMap = new HashMap<Integer, SoftReference<View>>();

	public interface OnItemsClickListener{
		void OnItemClick(View v, int pos);
	}
	/**
	 * @param context
	 */
	public LayerListView(Context context) {

		super(context);

		initView(context, null);
	}

	public void setShowDivider(boolean isShowDivider){

		mIsShowDivider = isShowDivider;

	}

	public void setItemClickListener(OnItemsClickListener l){
		mListener = l;
	}
	/**
	 * @param context
	 * @param attrs
	 */
	public LayerListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context, attrs);
	}
	
	public void setAdapter(LayerAdapter adapter){
		
		
		if(mAdapter != null && mDataChange != null){
			
			mAdapter.unregisterDataSetObserver(mDataChange);
		}
		
	
		mAdapter = adapter;
		if(mAdapter != null){
			
			mDataChange = new FixedListDataChange();
			mItemCount = mAdapter.getCount();
			
			//注册更改数据
			mAdapter.registerDataSetObserver(mDataChange);
			//清除掉之前的所有views
			for(int i = 0; i < this.getChildCount(); i++){
				View v = this.getChildAt(i);
				IViewLifeCycle lc = (IViewLifeCycle)v.getTag(BaseProcessor.KEY_TAG);
				if(lc != null){
					lc.onRemoveView(this, v);
				}
			}
			this.removeAllViews();
			// 让其重复生成
			mViewMap.clear();
			if(mItemCount != 0){
				
				for(int i = 0; i < mItemCount; i++){

					View temp = null;
					if(mViewMap.get(i) != null)
						temp = mViewMap.get(i).get();
					View v = mAdapter.getView(i, temp, null);
					if(temp == null){
						mViewMap.put(i, new SoftReference<View>(v));
					}
					
					final int pos = i;
					
					if(mListener != null){
						
						v.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View v) {
								
								mListener.OnItemClick(v, pos);
								
							}
							
						});
					}
					if(mAdapter.getLayerIndex(pos) != -1) {
						this.addView(v, mAdapter.getLayerIndex(pos));
						IViewLifeCycle lc = (IViewLifeCycle)v.getTag(BaseProcessor.KEY_TAG);
						if(lc != null){
							lc.onAddView(this, v);
						}
					}
					else{
						this.addView(v);
						IViewLifeCycle lc = (IViewLifeCycle)v.getTag(BaseProcessor.KEY_TAG);
						if(lc != null){
							lc.onAddView(this, v);
						}
						this.bringChildToFront(v);
					}

					if(mIsShowDivider){
						ImageView view = new ImageView(mContext);
						
						LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, 1);
						view.setLayoutParams(params);
						
						view.setBackgroundColor(0xffdddddd);
						this.addView(view);
						IViewLifeCycle lc = (IViewLifeCycle)v.getTag(BaseProcessor.KEY_TAG);
						if(lc != null){
							lc.onAddView(this, v);
						}
					}
				}
			}
			

			requestLayout();
			
		}
		
		
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	private void initView(Context context, AttributeSet attrs){
		mContext = context;
//		this.setOrientation(LinearLayout.VERTICAL);
	}
	
	private void changed(){
		
		synchronized (this) {
			if(mAdapter != null){
				
				mItemCount = mAdapter.getCount();
				//清除掉之前的所有views
				for(int i = 0; i < this.getChildCount(); i++){
					View v = this.getChildAt(i);
					IViewLifeCycle lc = (IViewLifeCycle)v.getTag(BaseProcessor.KEY_TAG);
					if(lc != null){
						lc.onRemoveView(this, v);
					}
				}
				this.removeAllViews();
				// 让其重复生成
				mViewMap.clear();
				if(mItemCount != 0){
					
					for(int i = 0; i < mItemCount; i++){
						View temp = null;
						if(mViewMap.get(i) != null)
							temp = mViewMap.get(i).get();
						View v = mAdapter.getView(i, temp, null);
						if(temp == null){
							mViewMap.put(i, new SoftReference<View>(v));
						}

						final int pos = i;
						
						if(mListener != null){
							
							v.setOnClickListener(new OnClickListener(){

								@Override
								public void onClick(View v) {
									
									mListener.OnItemClick(v, pos);
									
								}
								
							});
						}
						
						this.addView(v);
						IViewLifeCycle lc = (IViewLifeCycle)v.getTag(BaseProcessor.KEY_TAG);
						if(lc != null){
							lc.onAddView(this, v);
						}
						if(mIsShowDivider){
							ImageView view = new ImageView(mContext);
							
							LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, 1);
							view.setLayoutParams(params);
							
							view.setBackgroundColor(0xffdddddd);
							this.addView(view);
							IViewLifeCycle lc1 = (IViewLifeCycle)v.getTag(BaseProcessor.KEY_TAG);
							if(lc1 != null){
								lc1.onRemoveView(this, v);
							}
						}
					}
				}
				
				requestLayout();
			}
		}
		
	}
	private class FixedListDataChange extends DataSetObserver{
		@Override
		public void onChanged() {
			super.onChanged();
			changed();
			
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
			
		
		}
	}

}
