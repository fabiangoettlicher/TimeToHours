package com.fabian.timetohours.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fabian.timetohours.MainActivity;
import com.fabian.timetohours.R;


/**
 * Created by Fabian on 24.08.2017.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolderClass>{

    private OnListSizeChangedListener mOnListSizeChangedListener;

    public ListAdapter(OnListSizeChangedListener onListSizeChangedListener) {
        this.mOnListSizeChangedListener = onListSizeChangedListener;
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {

        TextView mTvTime, mTvMessage, mTvDate;

        public ViewHolderClass(View itemView) {
            super(itemView);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvMessage = (TextView) itemView.findViewById(R.id.tv_message);
            mTvDate = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }
    @Override
    public ListAdapter.ViewHolderClass onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolderClass holder, final int position) {
        holder.mTvTime.setText(MainActivity.mTrackingEntryList.get(position).getTime());
        holder.mTvMessage.setText(MainActivity.mTrackingEntryList.get(position).getMessage());
        holder.mTvDate.setText(MainActivity.mTrackingEntryList.get(position).getDate());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) { //lösche lang angeklicktes element
                MainActivity.mTrackingEntryList.remove(position);
                notifyDataSetChanged();
                mOnListSizeChangedListener.onListSizeChanged();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return MainActivity.mTrackingEntryList.size();
    }

    public interface OnListSizeChangedListener {
        void onListSizeChanged();
    }
}
