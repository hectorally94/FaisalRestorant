package com.prospect.faisalrestorant.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prospect.faisalrestorant.Classes.FoodOrder;
import com.prospect.faisalrestorant.R;
import com.prospect.faisalrestorant.UserActitities.CheckoutActivity;

import java.util.ArrayList;
import java.util.List;

public class AdapterFoodOrder extends RecyclerView.Adapter<AdapterFoodOrder.ViewHolder>  implements Filterable {
    private List<FoodOrder> listData;
    private Context mContext;
    private List<FoodOrder> exampleListFull;
    LayoutInflater layoutInflater;
    String Deleteky;


    public AdapterFoodOrder(Context context, List<FoodOrder> listData) {
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
        View view = layoutInflater.inflate(R.layout.foodlisttemorder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodOrder ld=listData.get(position);
        holder.TextViewname.setText(ld.getFoodname());
        holder.TextViewprice.setText(ld.getPrice().toString());
        holder.reclaclik.setOnClickListener(v -> {

            String  DeleteName=  listData.get(position).getFoodname();

            Toast.makeText(mContext,  "Long Click to delete "+DeleteName+" Oder".toUpperCase(), Toast.LENGTH_LONG).show();

        });
        holder.reclaclik.setOnLongClickListener(v -> {


            Deleteky=  listData.get(position).getKey();
            String  DeleteName=  listData.get(position).getFoodname();

            if (haveNetworkConnection()) {

                final DatabaseReference deleteOrder= FirebaseDatabase.getInstance().getReference();

                deleteOrder.child("FoodOrder").child(Deleteky).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listData.remove(position);
                        notifyItemRemoved(position);
                        notifyDataSetChanged();
                        Toast.makeText(mContext," Oder "+DeleteName+" deleted ",Toast.LENGTH_SHORT).show();

                        // Toast.makeText(AddUser.this," User deleted dataase ",Toast.LENGTH_SHORT).show();
                    }
                });
                deleteOrder.child("FoodReceipt").child(Deleteky).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       // Toast.makeText(mContext," Oder "+DeleteName+" deleted ",Toast.LENGTH_SHORT).show();

                        // Toast.makeText(AddUser.this," User deleted dataase ",Toast.LENGTH_SHORT).show();
                    }
                });                            } else {
                Toast.makeText(mContext, "connect your phone", Toast.LENGTH_LONG).show();
            }

            return false;
        });
}
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void removeItem(int position) {
        listData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(FoodOrder item, int position) {
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
            List<FoodOrder> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (FoodOrder item : exampleListFull) {
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

