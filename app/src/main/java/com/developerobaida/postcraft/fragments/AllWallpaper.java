package com.developerobaida.postcraft.fragments;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.adapters.AdapterAllWallpaper;
import com.developerobaida.postcraft.database.DatabaseHelper;
import com.developerobaida.postcraft.model.ItemAllWallpaper;
import com.facebook.shimmer.ShimmerFrameLayout;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class AllWallpaper extends Fragment implements AdapterAllWallpaper.WallFavClickListener{

    RecyclerView recWallpaper;
    AdapterAllWallpaper adapterAllWallpaper;
    ArrayList<ItemAllWallpaper> arrayList = new ArrayList<>();
    SearchView search;
    Spinner spinner;
    ShimmerFrameLayout effect;
    JsonArrayRequest jsonArrayRequest;
    SwipeRefreshLayout swiperefresh;
    RelativeLayout retryLay;
    DatabaseHelper database;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_all_wallpaper, container, false);
        if (container!=null) container.removeAllViews();

        recWallpaper = v.findViewById(R.id.recWallpaper);
        search = v.findViewById(R.id.search);
        spinner = v.findViewById(R.id.spinner);
        effect = v.findViewById(R.id.effect);
        retryLay = v.findViewById(R.id.retryLay);
        swiperefresh = v.findViewById(R.id.swiperefresh);
        database = new DatabaseHelper(getContext());
        recWallpaper.setHasFixedSize(true);
        getData();
        swiperefresh.setOnRefreshListener(() -> {
            arrayList = new ArrayList<>();
            getData();
            if (swiperefresh.isRefreshing()) swiperefresh.setRefreshing(false);
        });

        //-----------------------------------------------------------------------------
        //-----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.wallpaper_category, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (arrayList != null && arrayList.size() > 0) {
                    String selectedCategory = spinner.getSelectedItem().toString();
                    if (selectedCategory.equals("All")) adapterAllWallpaper.setFilter(arrayList);
                    else {
                        ArrayList<ItemAllWallpaper> filteredList = filterByCategory(arrayList, selectedCategory);
                        adapterAllWallpaper.setFilter(filteredList);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}
            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<ItemAllWallpaper> filteredList = filter(arrayList, newText);

                if (filteredList!=null){
                    adapterAllWallpaper.setFilter(filteredList);
                    return true;
                }
                return false;
            }
        });
        //-----------------------------------------------------------------------------
        //-----------------------------------------------------------------------------
        return v;
    }
    //=====================================================================-------------------------------
    private ArrayList<ItemAllWallpaper> filter(ArrayList<ItemAllWallpaper> items, String query) {
        ArrayList<ItemAllWallpaper> filteredList = new ArrayList<>();
        for (ItemAllWallpaper item : items) if (item.getTitle().trim().toLowerCase().contains(query.toLowerCase().trim())) filteredList.add(item);
        return filteredList;
    }
    private ArrayList<ItemAllWallpaper> filterByCategory(ArrayList<ItemAllWallpaper> items, String category) {
        ArrayList<ItemAllWallpaper> filteredList = new ArrayList<>();
        for (ItemAllWallpaper item : items) if (item.getCategory().equalsIgnoreCase(category)) filteredList.add(item);

        return filteredList;
    }
    //=====================================================================-------------------------------
    private void getData(){
        String url = "https://developerobaida.com/post_bank/show_all_wallpaper.php";
        String getWallpaper = "https://developerobaida.com/post_bank/Wallpaper/";
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            recWallpaper.setVisibility(View.VISIBLE);
            effect.setVisibility(View.GONE);

            try {
                for (int i = 0; i<response.length(); i++){
                    JSONObject jsonObject = response.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String wall = jsonObject.getString("wallpaper");
                    String title = jsonObject.getString("title");
                    String category = jsonObject.getString("category");
                    String type = jsonObject.getString("type");
                    arrayList.add(new ItemAllWallpaper(getWallpaper+wall,title,category,type,id));
                }
                adapterAllWallpaper = new AdapterAllWallpaper(arrayList,getContext(),this);
                recWallpaper.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                recWallpaper.setAdapter(adapterAllWallpaper);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, error -> {
            recWallpaper.setVisibility(View.GONE);
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
    public void wallFavClick(int position, boolean isFav) {
        if (position >= 0 && position < arrayList.size()) {
            ItemAllWallpaper clickedItem = arrayList.get(position);
            if (isFav) database.addWall_fav(clickedItem.getWallpaper(),clickedItem.getTitle(),clickedItem.getType(),clickedItem.getId());
            else database.deleteWall_fav(Integer.parseInt(clickedItem.getId()));
        }
    }
}