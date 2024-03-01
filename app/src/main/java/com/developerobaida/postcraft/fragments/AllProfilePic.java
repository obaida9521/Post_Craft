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
import com.developerobaida.postcraft.adapters.AdapterAllProfilePic;
import com.developerobaida.postcraft.database.DatabaseHelper;
import com.developerobaida.postcraft.model.ItemAllProfilePic;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AllProfilePic extends Fragment implements AdapterAllProfilePic.ProfileFavClickListener {

    RecyclerView recProfilePic;
    ArrayList<ItemAllProfilePic> arrayList = new ArrayList<>();
    AdapterAllProfilePic adapterAllProfilePic;
    Spinner spinner;
    SearchView search;
    JsonArrayRequest jsonArrayRequest;
    ShimmerFrameLayout effect;
    SwipeRefreshLayout swiperefresh;
    RelativeLayout retryLay;
    DatabaseHelper database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_profile_pic, container, false);
        if (container!=null){
            container.removeAllViews();
        }
        database = new DatabaseHelper(getContext());

        recProfilePic = v.findViewById(R.id.recProfilePic);
        spinner = v.findViewById(R.id.spinner);
        search = v.findViewById(R.id.search);
        effect = v.findViewById(R.id.effect);
        retryLay = v.findViewById(R.id.retryLay);
        swiperefresh = v.findViewById(R.id.swiperefresh);
        getData();
        swiperefresh.setOnRefreshListener(() -> {
            arrayList = new ArrayList<>();
            getData();
            if (swiperefresh.isRefreshing()) swiperefresh.setRefreshing(false);
        });

        //-----------------------------------------------------------------------------
        //-----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(),R.array.profile_category, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if (arrayList != null && arrayList.size() > 0) {
                    String selectedCategory = spinner.getSelectedItem().toString();
                    if (selectedCategory.equals("All")) adapterAllProfilePic.setFilter(arrayList);
                    else {
                        ArrayList<ItemAllProfilePic> filteredList = filterByCategory(arrayList, selectedCategory);
                        adapterAllProfilePic.setFilter(filteredList);
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
                ArrayList<ItemAllProfilePic> filteredList = filter(arrayList, newText);

                if (filteredList!=null){
                    adapterAllProfilePic.setFilter(filteredList);
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
    private ArrayList<ItemAllProfilePic> filter(ArrayList<ItemAllProfilePic> items, String query) {
        ArrayList<ItemAllProfilePic> filteredList = new ArrayList<>();
        for (ItemAllProfilePic item : items) if (item.getTitle().toLowerCase().contains(query.toLowerCase())) filteredList.add(item);
        return filteredList;
    }
    private ArrayList<ItemAllProfilePic> filterByCategory(ArrayList<ItemAllProfilePic> items, String category) {
        ArrayList<ItemAllProfilePic> filteredList = new ArrayList<>();
        for (ItemAllProfilePic item : items) if (item.getCategory().equalsIgnoreCase(category)) filteredList.add(item);
        return filteredList;
    }
    //=====================================================================-------------------------------
    private void getData(){
        String url = "https://developerobaida.com/post_bank/show_all_profile_pic.php";
        String profilePic = "https://developerobaida.com/post_bank/Profile/";
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            recProfilePic.setVisibility(View.VISIBLE);
            effect.setVisibility(View.GONE);
            try {
                for (int i=0; i<response.length(); i++){
                    JSONObject jsonObject = response.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String image = jsonObject.getString("image");
                    String title = jsonObject.getString("title");
                    String category = jsonObject.getString("category");
                    String type = jsonObject.getString("type");

                    arrayList.add(new ItemAllProfilePic(profilePic+image,title,category,type,id));
                }
                adapterAllProfilePic = new AdapterAllProfilePic(arrayList,getContext(),this);
                recProfilePic.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                recProfilePic.setAdapter(adapterAllProfilePic);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            effect.setVisibility(View.GONE);
            recProfilePic.setVisibility(View.GONE);
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
    public void profileFavClick(int position,boolean isFav) {
        if (position >= 0 && position < arrayList.size()) {
            ItemAllProfilePic clickedItem = arrayList.get(position);
            if (isFav) database.addProfile_fav(clickedItem.getImage(),clickedItem.getTitle(),clickedItem.getType(),clickedItem.getId());
            else database.deleteProfile_fav(Integer.parseInt(clickedItem.getId()));
        }
    }
}