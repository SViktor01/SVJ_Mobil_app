package com.example.mobilvasarlas;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ShopingItemAdapter extends RecyclerView.Adapter<ShopingItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<ShopingItem> ShopingItemData;
    private ArrayList<ShopingItem> ShopingItemsDataAll;
    private Context context;
    private  int lastPosition = -1;

    ShopingItemAdapter(Context context, ArrayList<ShopingItem> itemsData){
        this.ShopingItemData=itemsData;
        this.ShopingItemsDataAll=itemsData;
        this.context=context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item,parent,false));

    }

    @Override
    public void onBindViewHolder(ShopingItemAdapter.ViewHolder holder, int position) {
        ShopingItem currentItem=ShopingItemData.get(position);

        holder.bindto(currentItem);

        if(holder.getAdapterPosition()>lastPosition){
            Animation animation= AnimationUtils.loadAnimation(context,R.anim.slide);
            holder.itemView.startAnimation(animation);
            lastPosition=holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return ShopingItemData.size();
    }

    @Override
    public Filter getFilter() {
        return ShopingFilter;
    }
    private Filter ShopingFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ShopingItem> filterList=new ArrayList<>();
            FilterResults results=new FilterResults();

            if(charSequence==null || charSequence.length()==0){
                results.count=ShopingItemsDataAll.size();
                results.values=ShopingItemsDataAll;
            }else{
                String filterPattern=charSequence.toString().toLowerCase().trim();
                for(ShopingItem item:ShopingItemsDataAll){
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filterList.add(item);
                    }
                }
                results.count=filterList.size();
                results.values=filterList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ShopingItemData=(ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView TitleText;
        private TextView InfoText;
        private TextView PriceText;
        private ImageView ItemImage;
        private RatingBar RatingBar;


        public ViewHolder(View itemView) {
            super(itemView);
            TitleText=itemView.findViewById(R.id.itemTitle);
            InfoText=itemView.findViewById(R.id.subTitle);
            PriceText=itemView.findViewById(R.id.price);
            ItemImage=itemView.findViewById(R.id.itemImage);
            RatingBar=itemView.findViewById(R.id.ratingBar);

        }

        public void bindto(ShopingItem currentItem) {
            TitleText.setText(currentItem.getName());
            InfoText.setText(currentItem.getInfo());
            PriceText.setText(currentItem.getPrice());
            RatingBar.setRating(currentItem.getRatedInfo());
            Glide.with(context).load(currentItem.getImageResource()).into(ItemImage);
            itemView.findViewById(R.id.add_to_cart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Activity","Add cart button clicked");
                    ((ShopListActivity)context).update(currentItem);
                }
            });
            itemView.findViewById(R.id.add_to_cart).setOnClickListener(view -> ((ShopListActivity)context).update(currentItem));
            itemView.findViewById(R.id.deleteItem).setOnClickListener(view-> ((ShopListActivity)context).deleteItem(currentItem));
        }
    }
}

