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
import com.developerobaida.postcraft.adapters.AdapterRecentProfilePic;
import com.developerobaida.postcraft.database.DatabaseHelper;
import com.developerobaida.postcraft.model.ItemRecentProfilePic;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecentProfilePicCategory extends Fragment implements AdapterRecentProfilePic.RecentProfileFavClickListener{
    AdapterRecentProfilePic adapterRecentProfilePic;
    RecyclerView recyclerRecentProfilePicCat;
    ArrayList<ItemRecentProfilePic> arrayList = new ArrayList<>();
    ShimmerFrameLayout effect;
    RelativeLayout retryLay;
    JsonArrayRequest jsonArrayRequest;
    DatabaseHelper database;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if (container!=null){
            container.removeAllViews();
        }
        View v =  inflater.inflate(R.layout.fragment_recent_profile_pic_category, container, false);
        recyclerRecentProfilePicCat = v.findViewById(R.id.recyclerRecentProfilePicCat);
        database = new DatabaseHelper(getContext());
        effect = v.findViewById(R.id.effect);
        retryLay = v.findViewById(R.id.retryLay);
        getData();


        return v;
    }
    private void getData(){
        String url = "https://developerobaida.com/post_bank/show_recent_profile.php";
        String profilePic = "https://developerobaida.com/post_bank/Profile/";
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            recyclerRecentProfilePicCat.setVisibility(View.VISIBLE);
            effect.setVisibility(View.GONE);
            try {
                for (int i=0; i<response.length();i++){
                    JSONObject jsonObject = response.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String image = jsonObject.getString("image");
                    String title = jsonObject.getString("title");
                    String category = jsonObject.getString("category");
                    String type = jsonObject.getString("type");

                    arrayList.add(new ItemRecentProfilePic(profilePic+image,title,category,id,type));
                }
                adapterRecentProfilePic = new AdapterRecentProfilePic(arrayList,getContext(),this);
                recyclerRecentProfilePicCat.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                recyclerRecentProfilePicCat.setAdapter(adapterRecentProfilePic);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, error -> {
            recyclerRecentProfilePicCat.setVisibility(View.GONE);
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

    @Override
    public void recentProfileFavClickListener(int position, boolean isFav) {
        if (position >= 0 && position < arrayList.size()) {
            ItemRecentProfilePic clickedItem = arrayList.get(position);
            if (isFav)database.addProfile_fav(clickedItem.getImage(),clickedItem.getTitle(),clickedItem.getType(),clickedItem.getId());
            else database.deleteProfile_fav(Integer.parseInt(clickedItem.getId()));
        }
    }
}