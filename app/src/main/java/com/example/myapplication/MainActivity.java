package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;
public class MainActivity extends AppCompatActivity implements FolderAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private FolderAdapter folderAdapter;
    private PlayingActivity playingActivity;
    private List<ItemModel> itemList;
    private String currentFolderPath;
    private  TextView textViewFolder;
    private int ClickFolder=0;
    Bitmap ImageBitmap;
    private int selectedItemPosition = RecyclerView.NO_POSITION; // Vị trí của mục được chọn
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList = getRootItems();
        currentFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        textViewFolder=findViewById(R.id.folder);

        folderAdapter = new FolderAdapter(itemList, this, this);
        recyclerView.setAdapter(folderAdapter);

        ImageButton buttonSelect = findViewById(R.id.buttonSelect);
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCurrentPathMusicFile(currentFolderPath)){
                    Intent intent = new Intent(MainActivity.this, PlayingActivity.class);
                    intent.putExtra("folderPath", currentFolderPath);
                    intent.putExtra("selectedItemPosition", selectedItemPosition);
                    startActivity(intent);
                }else{
                    updateItemList(currentFolderPath);
                    selectedItemPosition = RecyclerView.NO_POSITION;
                    ClickFolder=0;
                }

            }
        });

        ImageButton buttonUp = findViewById(R.id.buttonUp);
        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    if(selectedItemPosition<0){
                        getSelectedFolderPath();
                        updateCurrentFolderText();
                        ClickFolder++;
                    }else{
                        currentFolderPath=getParentFolderPath(currentFolderPath);
                        folderAdapter.setSelectedItemPosition(selectedItemPosition);
                        folderAdapter.moveItemUp();
                        selectedItemPosition=folderAdapter.getSelectedItemPosition();
                        recyclerView.smoothScrollToPosition(folderAdapter.getSelectedItemPosition());
                        getSelectedFolderPath();
                        updateCurrentFolderText();
                        ClickFolder++;
                    }

            }
            });

        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    currentFolderPath = getParentFolderPath(currentFolderPath);
                    updateItemList(currentFolderPath);
                    selectedItemPosition = RecyclerView.NO_POSITION;
                    ClickFolder++;
                    updateCurrentFolderText();



            }
        });
        ImageButton buttonDown = findViewById(R.id.buttonDown);
        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedItemPosition != RecyclerView.NO_POSITION &&selectedItemPosition< itemList.size() - 1){
                    currentFolderPath=getParentFolderPath(currentFolderPath);
                    folderAdapter.setSelectedItemPosition(selectedItemPosition);
                    folderAdapter.moveItemDown();
                    selectedItemPosition=folderAdapter.getSelectedItemPosition();
                    recyclerView.smoothScrollToPosition(folderAdapter.getSelectedItemPosition());
                    getSelectedFolderPath();
                    updateCurrentFolderText();
                    ClickFolder++;
                }

            }
        });
        ImageButton buttonExit = findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        updateCurrentFolderText();
    }
    private void updateCurrentFolderText() {

        textViewFolder.setText("Current Folder Path: " + currentFolderPath);




    }
    private String getParentFolderPath(String currentFolderPath) {
        File currentFolder = new File(currentFolderPath);
        File parentFolder = currentFolder.getParentFile();

        if (parentFolder != null) {
            return parentFolder.getAbsolutePath();
        } else {
            // Nếu không có thư mục cha (đã ở thư mục gốc), có thể xử lý hoặc trả về giá trị mặc định
            return "Root Folder"; // Đây là một giá trị mặc định, bạn có thể thay đổi nó tùy ý.
        }
    }
    private boolean isCurrentPathMusicFile(String currentFolderPath) {
        File currentFolder = new File(currentFolderPath);

        if (currentFolder.isFile()) {
            String folderName = currentFolder.getName();
            return isMusicFile(folderName);
        }

        return false;
    }
    public void getSelectedFolderPath() {

        if(selectedItemPosition <0){
            ItemModel item=itemList.get(itemList.size()-1);
            selectedItemPosition=itemList.size()-1;
            if (item != null) {
                currentFolderPath= currentFolderPath + "/" + item.getName();
            }

        }else{
            ItemModel item=itemList.get(selectedItemPosition);
            if (item != null) {
                currentFolderPath=currentFolderPath + "/" + item.getName();
            }
        }

    }
    private void updateItemList(String folderPath) {
        currentFolderPath = folderPath;
        itemList.clear();
        itemList.addAll(getItems(folderPath));
        folderAdapter.notifyDataSetChanged();
    }

    private List<ItemModel> getRootItems() {
        List<ItemModel> items = new ArrayList<>();
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File rootDirectory = new File(rootPath);
        File[] files = rootDirectory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && !isMusicFile(file.getName())) {
                    items.add(new ItemModel(file.getName(), "FOLDER"));
                } else {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(file.getAbsolutePath());
                    String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                    byte[] ImageBytes = retriever.getEmbeddedPicture();

                    if (ImageBytes != null) {

                       ImageBitmap = BitmapFactory.decodeByteArray(ImageBytes, 0, ImageBytes.length);


                    }
                    items.add(new ItemModel(file.getName(), "MUSIC_FILE", album, ImageBitmap));
                }
            }
        }
        return items;

    }

    private List<ItemModel> getItems(String folderPath) {
        List<ItemModel> items = new ArrayList<>();
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && !isMusicFile(file.getName())) {
                    items.add(new ItemModel(file.getName(), "FOLDER"));
                } else {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(file.getAbsolutePath());
                    String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                    byte[] ImageBytes = retriever.getEmbeddedPicture();
                    if (ImageBytes != null) {

                        ImageBitmap = BitmapFactory.decodeByteArray(ImageBytes, 0, ImageBytes.length);
                    }
                    items.add(new ItemModel(file.getName(), "MUSIC_FILE", album, ImageBitmap));
                }
            }
        }
            return items;

    }
    private boolean isMusicFile(String fileName) {
        String[] musicExtensions = {".mp3", ".aac", ".wav", ".ogg", ".flac", ".mid", ".midi", ".wma", ".amr", ".3gp"};

        for (String extension : musicExtensions) {
            if (fileName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void onItemClick(String folderName, int ItemPosition) {
        File currentFolder = new File(currentFolderPath);
        String Name = currentFolder.getName();
        if(ClickFolder>0 ){
            if( !Name.equals(folderName)){
                if(selectedItemPosition>0){
                    currentFolderPath=getParentFolderPath(currentFolderPath);
                    ClickFolder=0;
                }
            }else{
                currentFolderPath=getParentFolderPath(currentFolderPath);
            }


        }


        String newFolderPath = currentFolderPath + "/" + folderName;
        textViewFolder.setText("Current Folder Path: " + newFolderPath);
       if(isCurrentPathMusicFile(newFolderPath)){
           Intent intent = new Intent(MainActivity.this, PlayingActivity.class);
           intent.putExtra("folderPath", newFolderPath);
           intent.putExtra("selectedItemPosition", ItemPosition);
           startActivity(intent);
        }
        updateItemList(newFolderPath);
        selectedItemPosition = RecyclerView.NO_POSITION;
    }
}
