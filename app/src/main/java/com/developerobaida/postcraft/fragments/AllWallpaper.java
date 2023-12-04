package com.developerobaida.postcraft.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.adapters.AdapterAllWallpaper;
import com.developerobaida.postcraft.model.ItemAllWallpaper;
import com.facebook.shimmer.ShimmerFrameLayout;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class AllWallpaper extends Fragment {

    RecyclerView recWallpaper;
    AdapterAllWallpaper adapterAllWallpaper;
    ArrayList<ItemAllWallpaper> arrayList = new ArrayList<>();
    SearchView search;
    Spinner spinner;
    ShimmerFrameLayout effect;
    JsonArrayRequest jsonArrayRequest;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_all_wallpaper, container, false);
        if (container!=null){
            container.removeAllViews();
        }
        recWallpaper = v.findViewById(R.id.recWallpaper);
        search = v.findViewById(R.id.search);
        spinner = v.findViewById(R.id.spinner);
        effect = v.findViewById(R.id.effect);
        recWallpaper.setHasFixedSize(true);
        getData();

        //-----------------------------------------------------------------------------
        //-----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.categories_wallpaper, android.R.layout.simple_spinner_item);
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
                    String wall = jsonObject.getString("wallpaper");
                    String title = jsonObject.getString("title");
                    String category = jsonObject.getString("category");
                    arrayList.add(new ItemAllWallpaper(getWallpaper+wall,title,category));
                }
                adapterAllWallpaper = new AdapterAllWallpaper(arrayList,getContext());
                recWallpaper.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                recWallpaper.setAdapter(adapterAllWallpaper);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, error -> {
            recWallpaper.setVisibility(View.GONE);
            effect.setVisibility(View.GONE);
            noInternet(getActivity());
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void noInternet(Activity activity){

        View dialogView = getLayoutInflater().inflate(R.layout.dialogue_ui, null);

        TextView title = dialogView.findViewById(R.id.title);
        TextView message = dialogView.findViewById(R.id.message);
        CardView positive = dialogView.findViewById(R.id.positive);
        CardView negative = dialogView.findViewById(R.id.negative);
        ImageView icon = dialogView.findViewById(R.id.icon);
        TextView positiveTxt = dialogView.findViewById(R.id.positiveTxt);
        TextView negativeTxt = dialogView.findViewById(R.id.negativeTxt);

        title.setText("Connection Problem!");
        message.setText("Check your Internet connection \nTry again");
        icon.setImageResource(R.drawable.wifi_off_24);
        positiveTxt.setText("Back");
        negativeTxt.setText("Reload");

        AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.AlertDialogTheme).setView(dialogView).create();

        positive.setOnClickListener(v -> {
            alertDialog.dismiss();
            if (!activity.isFinishing()) getActivity().onBackPressed();
        });
        negative.setOnClickListener(v -> {
            alertDialog.dismiss();
            if (!activity.isFinishing()) {
                effect.setVisibility(View.VISIBLE);
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(jsonArrayRequest);
            }
        });
        if (!activity.isFinishing()) {
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        }
    }
}