package com.developerobaida.postcraft.fragments;


import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.adapters.AdapterRecentWallpaper;
import com.developerobaida.postcraft.model.ItemRecentWallpaper;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
public class RecentWallpaperCategory extends Fragment {
    AdapterRecentWallpaper adapterRecentWallpaper;
    ArrayList<ItemRecentWallpaper> arrayList = new ArrayList<>();
    RecyclerView recyclerRecentVideoCat;
    JsonArrayRequest jsonArrayRequest;
    ShimmerFrameLayout effect;
    RelativeLayout retryLay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recent_wallpaper_category, container, false);
        recyclerRecentVideoCat = v.findViewById(R.id.recyclerRecentVideoCat);
        effect = v.findViewById(R.id.effect);
        retryLay = v.findViewById(R.id.retryLay);
        getData();

        return v;
    }
    private void getData(){
        String url = "https://developerobaida.com/post_bank/show_recent_wallpaper.php";
        String getWallpaper = "https://developerobaida.com/post_bank/Wallpaper/";
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            recyclerRecentVideoCat.setVisibility(View.VISIBLE);
            effect.setVisibility(View.GONE);
            try {
                for (int i = 0; i<response.length(); i++){
                    JSONObject jsonObject = response.getJSONObject(i);
                    String wall = jsonObject.getString("wallpaper");
                    String title = jsonObject.getString("title");

                    arrayList.add(new ItemRecentWallpaper(getWallpaper+wall,""+title));

                }
                adapterRecentWallpaper = new AdapterRecentWallpaper(arrayList, getContext());
                recyclerRecentVideoCat.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                recyclerRecentVideoCat.setAdapter(adapterRecentWallpaper);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, error -> {
            recyclerRecentVideoCat.setVisibility(View.GONE);
            effect.setVisibility(View.GONE);
            reload(getActivity());
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);

    }
    private void reload(Activity activity){
        retryLay.setVisibility(View.VISIBLE);
        retryLay.setOnClickListener(v -> {
            effect.setVisibility(View.VISIBLE);

            if (!activity.isFinishing()) {
                retryLay.setVisibility(View.GONE);
                effect.setVisibility(View.VISIBLE);
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(jsonArrayRequest);
            }
        });
    }
}