package com.kingyon.elevator.uis.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kingyon.elevator.R;
import com.kingyon.elevator.entities.IncomeEntity;
import com.kingyon.elevator.entities.YesterdayIncomeEntity;

import java.util.List;

/**
 * Created By SongPeng  on 2019/11/25
 * Email : 1531603384@qq.com
 */
public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.ViewHolder> {

    private List<YesterdayIncomeEntity> incomeEntityList;
    private Context context;
    protected LayoutInflater inflater;

    public IncomeAdapter(Context context, List<YesterdayIncomeEntity> incomeEntityList) {
        this.context = context;
        this.incomeEntityList = incomeEntityList;
        this.inflater = LayoutInflater.from(context);
    }


    public void reflashData(List<YesterdayIncomeEntity> incomeEntityList) {
        this.incomeEntityList = incomeEntityList;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.income_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            YesterdayIncomeEntity yesterdayIncomeEntity = incomeEntityList.get(position);
            holder.tv_income_money.setText("¥\t" + yesterdayIncomeEntity.getAmount());
            holder.tv_income_time.setText(yesterdayIncomeEntity.getDateValue() + "\t\t" + yesterdayIncomeEntity.getTimeValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return incomeEntityList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_income_time;
        TextView tv_income_money;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_income_time = itemView.findViewById(R.id.tv_income_time);
            tv_income_money = itemView.findViewById(R.id.tv_income_money);
        }
    }

}
