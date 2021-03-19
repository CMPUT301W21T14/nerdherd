package com.example.nerdherd;

import android.content.Context;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Controller class for Adapter model class
 * The controller class for Adapter to get notified of the user's behaviour and update the model class as needed
 * @author Zhipeng Z. zhipeng4
 */

public class AdapterController {

    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    /**
     * Details per adapter
     * Getter/setter of the class
     * @param context of the adapter
     * @param recyclerView group that contains the views of the data
     * @param adapter itself
     */

    public AdapterController(Context context, RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.adapter = adapter;
    }

    public void useAdapter(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}
