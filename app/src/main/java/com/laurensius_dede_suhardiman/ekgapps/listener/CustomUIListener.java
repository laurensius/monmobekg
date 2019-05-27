package com.laurensius_dede_suhardiman.ekgapps.listener;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class CustomUIListener implements RecyclerView.OnItemTouchListener {

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(View childVew, int childAdapterPosition);
    }

    GestureDetector mGestureDetector;

    public CustomUIListener(Context context, OnItemClickListener listener){
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e){
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e){
        View childVew = view.findChildViewUnder(e.getX(),e.getY());
        if(childVew != null && mListener != null && mGestureDetector.onTouchEvent(e)){
            mListener.onItemClick(childVew,view.getChildAdapterPosition(childVew));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view,MotionEvent e){}

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}