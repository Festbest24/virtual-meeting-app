package com.webilestudio.vmeet.schedule;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;
import com.daimajia.swipe.util.Attributes;
import com.webilestudio.vmeet.R;
import com.webilestudio.vmeet.bean.Schedule;
import com.webilestudio.vmeet.utils.AppConstants;
import com.webilestudio.vmeet.utils.SharedObjects;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScheduleAdapter extends RecyclerSwipeAdapter<ScheduleAdapter.ViewHolder> {

    ArrayList<Schedule> list;
    Context context;
    OnItemClickListener onItemClickListener;
    SwipeItemRecyclerMangerImpl mItemManger;

    public ScheduleAdapter(ArrayList<Schedule> list, Context context) {
        this.list = list;
        this.context = context;
        mItemManger = new SwipeItemRecyclerMangerImpl(this);
        setMode(Attributes.Mode.Single);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_schedule, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Schedule bean = list.get(position);

        if (!TextUtils.isEmpty(bean.getTitle())) {
            holder.txtName.setText(bean.getTitle());
            holder.txtNameInitial.setText(bean.getTitle().substring(0,1));
        }

        if (!TextUtils.isEmpty(bean.getDate())) {
            holder.txtDate.setText(bean.getDate());
            if (AppConstants.checkDateisFuture(bean.getDate())){
                holder.btnStart.setVisibility(View.VISIBLE);
                holder.imgShare.setVisibility(View.VISIBLE);
            }else{
                holder.btnStart.setVisibility(View.GONE);
                holder.imgShare.setVisibility(View.GONE);
            }
        }else{
            holder.btnStart.setVisibility(View.GONE);
            holder.imgShare.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(bean.getStartTime()) && !TextUtils.isEmpty(bean.getDuration())) {
            String startTime = SharedObjects.convertDateFormat(bean.getStartTime(),AppConstants.DateFormats.TIME_FORMAT_24
                    ,AppConstants.DateFormats.TIME_FORMAT_12);

            holder.txtTime.setText("Starts at " + startTime + " (" + bean.getDuration() + " Mins)");

            holder.txtTime.setVisibility(View.VISIBLE);
        }else{
            holder.txtTime.setVisibility(View.GONE);
        }

        holder.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    mItemManger.closeItem(position);
                    onItemClickListener.onStartClickListener(position, list.get(position));
                }
            }
        });

        holder.llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    mItemManger.closeItem(position);
                    onItemClickListener.onDeleteClickListener(position, list.get(position));
                }
            }
        });

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClickListener(position, list.get(position));
                }
            }
        });

        holder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onShareClickListener(position, list.get(position));
                }
            }
        });

        holder.swipe.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.swipe.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
            }

            @Override
            public void onOpen(SwipeLayout layout) {
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
            }

            @Override
            public void onClose(SwipeLayout layout) {
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
            }
        });

        mItemManger.bindView(holder.itemView, position);
    }
    
    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position, Schedule bean);
        void onDeleteClickListener(int position, Schedule bean);
        void onStartClickListener(int position, Schedule bean);
        void onShareClickListener(int position, Schedule bean);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtName) TextView txtName;
        @BindView(R.id.txtNameInitial) TextView txtNameInitial;
        @BindView(R.id.txtDate) TextView txtDate;
        @BindView(R.id.txtTime) TextView txtTime;
        @BindView(R.id.imgShare) ImageView imgShare;
        @BindView(R.id.btnStart)
        Button btnStart;

        @BindView(R.id.llMain) LinearLayout llMain;
        @BindView(R.id.swipe) SwipeLayout swipe;
        @BindView(R.id.llDelete) LinearLayout llDelete ;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}



