package com.prospect.faisalrestorant.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prospect.faisalrestorant.Activities.FoodActitvity;
import com.prospect.faisalrestorant.Activities.FoodDetails;
import com.prospect.faisalrestorant.Classes.Food;
import com.prospect.faisalrestorant.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterFoodUser extends RecyclerView.Adapter<AdapterFoodUser.ViewHolder>  implements Filterable {
    private List<Food> listData;
    private Context mContext;
    private List<Food> exampleListFull;
    LayoutInflater layoutInflater;


    public AdapterFoodUser(Context context, List<Food> listData) {
        this.listData = listData;
        this.mContext =  context;
        layoutInflater = LayoutInflater.from(mContext);
        exampleListFull = new ArrayList<>(listData);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_data,parent,false);
        // View view= LayoutInflater.from(mContext).inflate(R.layout.list_data,parent,false);
        View view = layoutInflater.inflate(R.layout.foodlisttem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food ld=listData.get(position);
        holder.TextViewname.setText(ld.getFoodname());
        holder.TextViewprice.setText(ld.getPrice().toString());
        holder.reclaclik.setOnClickListener(v -> {
           Intent intent=new Intent(mContext, FoodDetails.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("getname",listData.get(position).getFoodname());
            intent.putExtra("getprice",listData.get(position).getPrice());
            intent.putExtra("getKey",listData.get(position).getKey());
            mContext.startActivity(intent);
            //  ((AppCompatActivity)mContext).finish();
        });

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void removeItem(int position) {
        listData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Food item, int position) {
        listData.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Food> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Food item : exampleListFull) {
                    if (item.getFoodname().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listData.clear();
            listData.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView TextViewname;
        private final TextView TextViewprice;
        public RelativeLayout reclaclik,viewBackground;
        public ViewHolder(View itemView) {
            super(itemView);
            TextViewname= itemView.findViewById(R.id.TextViewname);
            TextViewprice= itemView.findViewById(R.id.TextViewprice);
            reclaclik=itemView.findViewById(R.id.reclaclik);
            viewBackground = itemView.findViewById(R.id.view_background);
        }
    }
}

