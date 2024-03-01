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
import com.developerobaida.postcraft.adapters.AdapterRecentPost;
import com.developerobaida.postcraft.database.DatabaseHelper;
import com.developerobaida.postcraft.model.ItemRecentPost;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
public class RecentPostCategory extends Fragment implements AdapterRecentPost.RecentPostFavClickListener{

    AdapterRecentPost adapterRecentPost;
    ArrayList<ItemRecentPost> arrayList = new ArrayList<>();
    RecyclerView recyclerRecentPostCat;
    ShimmerFrameLayout effect;
    RelativeLayout retryLay;
    JsonArrayRequest jsonArrayRequest;
    DatabaseHelper database;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container!=null){
            container.removeAllViews();
        }
        View v =  inflater.inflate(R.layout.fragment_recent_post_category, container, false);
        recyclerRecentPostCat = v.findViewById(R.id.recyclerRecentPostCat);
        effect = v.findViewById(R.id.effect);
        retryLay = v.findViewById(R.id.retryLay);
        database = new DatabaseHelper(getContext());
        recyclerRecentPostCat.setHasFixedSize(true);
        getData();

        return v;
    }
    private void getData(){
        String url = "https://developerobaida.com/post_bank/show_recent_post.php";
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            recyclerRecentPostCat.setVisibility(View.VISIBLE);
            effect.setVisibility(View.GONE);
            try {
                for (int i=0; i<response.length();i++){

                    JSONObject jsonObject = response.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String status = jsonObject.getString("status");
                    String image = jsonObject.getString("image");
                    String font = jsonObject.getString("font");
                    String category = jsonObject.getString("category");
                    String color = jsonObject.getString("color");
                    String size = jsonObject.getString("size");
                    String dy = jsonObject.getString("v_shadow");
                    String dx = jsonObject.getString("h_shadow");
                    String radius = jsonObject.getString("r_shadow");
                    String shadowColor = jsonObject.getString("c_shadow");
                    String type = jsonObject.getString("type");

                    arrayList.add(new ItemRecentPost(""+status,""+image,""+font,""+color,""+size,""+category,""+dy,""+dx,""+radius,""+shadowColor,""+id,""+type));
                }
                adapterRecentPost = new AdapterRecentPost(arrayList, getContext(),this);
                recyclerRecentPostCat.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                recyclerRecentPostCat.setAdapter(adapterRecentPost);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            recyclerRecentPostCat.setVisibility(View.GONE);
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
    public void postFavClick(int position, boolean isFav) {
        if (position >= 0 && position < arrayList.size()) {
            ItemRecentPost clickedItem = arrayList.get(position);
            if (isFav)
                database.addPost_fav(""+clickedItem.getStatus(),""+clickedItem.getType(),""+clickedItem.getImage(), ""+clickedItem.getFont(),
                    ""+clickedItem.getColor(),""+clickedItem.getSize(),""+clickedItem.getDy(),""+clickedItem.getDx(),""+clickedItem.getRadius(),
                    ""+clickedItem.getShadowColor(),""+clickedItem.getId());
            else database.deletePost_fav(Integer.parseInt(clickedItem.getId()));

        }
    }
}