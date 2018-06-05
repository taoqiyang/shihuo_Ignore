package mousepaint.taoqiyang.com.ignore;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import mousepaint.taoqiyang.com.ignore.beans.Monitor;


public class MonitorListAdapter extends RecyclerView.Adapter<MonitorListAdapter.Holder> implements View.OnClickListener {
    private RecyclerView recyclerView;
    private OnItemClickListener listener;
    private List<Monitor> monitors;

    public MonitorListAdapter(RecyclerView recyclerView, List<Monitor> monitors){
        this.monitors = monitors;
        this.recyclerView = recyclerView;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        Holder holder = new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_monitor, parent, false));
        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Monitor monitor = monitors.get(position);
        holder.tvName.setText(monitor.name);
        holder.tvState.setText(monitor.getStateText());
        holder.itemView.setSelected(monitor.state == -1);
    }

    @Override
    public int getItemCount() {
        return monitors == null ? 0 : monitors.size();
    }

    @Override
    public void onClick(View v) {
        RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(v);
        if(listener != null){
            int adapterPosition = holder.getAdapterPosition();
            listener.onItemClick(monitors.get(adapterPosition), adapterPosition, v);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(Monitor monitor, int position, View view);
    }

    static class Holder extends RecyclerView.ViewHolder{
        TextView tvName;
        TextView tvState;
        Holder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvState = (TextView) itemView.findViewById(R.id.tvState);
        }
    }
}
