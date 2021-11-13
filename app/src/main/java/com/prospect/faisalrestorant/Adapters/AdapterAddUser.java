package com.prospect.faisalrestorant.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prospect.faisalrestorant.Classes.User;
import com.prospect.faisalrestorant.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterAddUser extends RecyclerView.Adapter<AdapterAddUser.ViewHolder> implements Filterable {
    private List<User> listData;
    private List<User> exampleListFull;
    private Context mContext;
    LayoutInflater layoutInflater;

    public AdapterAddUser(Context context, List<User> listData) {
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
        View view = layoutInflater.inflate(R.layout.userlisttem, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User ld=listData.get(position);
        holder.TextViewemail.setText(ld.getEmail());
        holder.TextViewame.setText(ld.getFullname());
        holder.textViewprovence.setText(ld.getRole());
    }
    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void removeItem(int position) {
        listData.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(User item, int position) {
        listData.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }



    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (User item : exampleListFull) {
                    if (item.getFullname().toLowerCase().contains(filterPattern)) {
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

        private TextView TextViewemail,TextViewame,textViewprovence;
        public RelativeLayout reclaclik,viewBackground;

        public ViewHolder(View itemView) {
            super(itemView);
            TextViewemail= itemView.findViewById(R.id.TextViewemail);
            TextViewame= itemView.findViewById(R.id.TextViewame);
            textViewprovence=itemView.findViewById(R.id.TextViewprovience);
            reclaclik=itemView.findViewById(R.id.reclaclik);
            viewBackground = itemView.findViewById(R.id.view_background);


        }
    }
}

