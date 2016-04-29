package com.drwat.lab2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class GroceryRecyclerViewAdapter extends RecyclerView.Adapter<GroceryRecyclerViewAdapter.ViewHolder> {
    private List<Grocery> groceries;
    private Bitmap bitmap;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mIdTextView;
        public TextView mNameTextView;
        public TextView mCountTextView;
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
           // mIdTextView = (TextView)view.findViewById(R.id.id);
            imageView = (ImageView) view.findViewById(R.id.grocery_photo);
            mNameTextView = (TextView)view.findViewById(R.id.name);
            mCountTextView = (TextView)view.findViewById(R.id.count);

        }

    }

    public GroceryRecyclerViewAdapter(List<Grocery> groceries) {
        this.groceries = groceries;
    }

    @Override
    public GroceryRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_grocery, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (groceries.get(position).getImagePath()!=null) {
            bitmap = BitmapFactory.decodeFile(groceries.get(position).getImagePath());
            int targetH = 100;
            int targetW = 100;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(groceries.get(position).getImagePath(), options);
            int photoW = options.outWidth;
            int photoH = options.outHeight;

            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            options.inJustDecodeBounds = false;
            options.inSampleSize = scaleFactor;

            Bitmap bitmap = BitmapFactory.decodeFile(groceries.get(position).getImagePath(), options);
//        holder.mIdTextView.setText(String.valueOf(groceries.get(position).getId()));
            holder.imageView.setImageBitmap(bitmap);
        }
        holder.mNameTextView.setText(groceries.get(position).getName());
        holder.mCountTextView.setText(groceries.get(position).getCount());
    }

    @Override
    public int getItemCount() {
        return groceries.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }




}
