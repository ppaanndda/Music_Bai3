package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;

import java.util.List;
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {

    private List<ItemModel> itemList;
    private LayoutInflater inflater;
    private OnItemClickListener listener;
    private Context context;
    private int selectedItemPosition = RecyclerView.NO_POSITION; // Vị trí của mục được chọn
    private int newPosition;
    public void setSelectedItemPosition(int position) {
        this.selectedItemPosition = position;
    }

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    public interface OnItemClickListener {
        void onItemClick(String folderName,int ItemPosition);
    }

    public FolderAdapter(List<ItemModel> itemList, Context context, OnItemClickListener listener) {
        this.itemList = itemList;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    public void moveItemUp() {
        if (selectedItemPosition != RecyclerView.NO_POSITION && selectedItemPosition > 0) {
            newPosition = selectedItemPosition - 1;
            setSelectedItemPosition(newPosition);
            notifyDataSetChanged();
        }
    }
    public void moveItemDown() {
        if (selectedItemPosition != RecyclerView.NO_POSITION && selectedItemPosition < getItemCount() - 1) {
            newPosition = selectedItemPosition + 1;
            setSelectedItemPosition(newPosition);
            notifyDataSetChanged();
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemModel item = itemList.get(position);
        holder.Name.setText(item.getName());


        if(item.getType().equals("FOLDER")){

            holder.imagePath.setImageResource(R.drawable.folder);
        }if(item.getType().equals("MUSIC_FILE")){

            holder.Name.setPadding(0,0,0,0);
            if (item.getImageMusicPath() != null) {
                holder.imagePath.setImageBitmap(item.getImageMusicPath());


            }else{
                holder.imagePath.setImageResource(R.drawable.music);
            }
            if (item.getAlbum() != null) {
                holder.album.setText(item.getAlbum());
                holder.album.setVisibility(View.VISIBLE);
            }

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    selectedItemPosition = holder.getAdapterPosition();
                    notifyItemChanged(selectedItemPosition);
                    if (selectedItemPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(item.getName(),  selectedItemPosition);
                }
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public ItemModel getItem(int position) {
        return itemList.get(position);
    }
    public int getPositionOfItem(ItemModel item) {
        return itemList.indexOf(item);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePath;
        TextView Name, album;

        public ViewHolder(View itemView) {
            super(itemView);
            imagePath = itemView.findViewById(R.id.itemImage);
            Name = itemView.findViewById(R.id.itemName);
            album = itemView.findViewById(R.id.nameAlbum);  // Thêm dòng này để tìm view có id là nameAlbum
        }
    }

}
