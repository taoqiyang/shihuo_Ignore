package mousepaint.taoqiyang.com.ignore;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import mousepaint.taoqiyang.com.ignore.beans.MaintainRecord;


public class MaintainRecordListAdapter extends RecyclerView.Adapter<MaintainRecordListAdapter.Holder> {
    private List<MaintainRecord> maintainRecords;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");

    public MaintainRecordListAdapter(List<MaintainRecord> maintainRecords){
        this.maintainRecords = maintainRecords;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_maintain_record, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        MaintainRecord maintainRecord = maintainRecords.get(position);
        holder.tvDate.setText(format.format(maintainRecord.createTime));
        holder.tvRecord.setText(maintainRecord.record);
    }

    @Override
    public int getItemCount() {
        return maintainRecords == null ? 0 : maintainRecords.size();
    }

    static class Holder extends RecyclerView.ViewHolder{
        TextView tvRecord;
        TextView tvDate;
        Holder(View itemView) {
            super(itemView);
            tvRecord = (TextView) itemView.findViewById(R.id.tvRecord);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }
    }
}
