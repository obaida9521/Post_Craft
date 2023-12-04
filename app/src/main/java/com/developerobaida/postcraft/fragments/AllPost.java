package com.developerobaida.postcraft.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.developerobaida.postcraft.adapters.AdapterAllPost;
import com.developerobaida.postcraft.model.ItemAllPost;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AllPost extends Fragment {
    RecyclerView recPost;
    SearchView search;
    AdapterAllPost adapterAllPost;
    ArrayList<ItemAllPost> arrayList = new ArrayList<>();
    Spinner spinner;
    ShimmerFrameLayout effect;
    JsonArrayRequest jsonArrayRequest;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_all_post, container, false);
        if (container!=null){
            container.removeAllViews();
        }
        recPost = v.findViewById(R.id.recPost);
        search = v.findViewById(R.id.search);
        spinner = v.findViewById(R.id.spinner);
        effect = v.findViewById(R.id.effect);
        getData();


        //-----------------------------------------------------------------------------
        //-----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.categories_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (arrayList != null && arrayList.size() > 0) {
                    String selectedCategory = spinner.getSelectedItem().toString();
                    if (selectedCategory.equals("All")) adapterAllPost.setFilter(arrayList);
                     else {
                        ArrayList<ItemAllPost> filteredList = filterByCategory(arrayList, selectedCategory);
                        adapterAllPost.setFilter(filteredList);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<ItemAllPost> filteredList = filter(arrayList, newText);
                adapterAllPost.setFilter(filteredList);
                return true;
            }
        });
        //-----------------------------------------------------------------------------
        //-----------------------------------------------------------------------------



        return v;
    }
    //=====================================================================-------------------------------
    private ArrayList<ItemAllPost> filter(ArrayList<ItemAllPost> items, String query) {
        ArrayList<ItemAllPost> filteredList = new ArrayList<>();
        for (ItemAllPost item : items) if (item.getStatus().trim().toLowerCase().contains(query.toLowerCase().trim())) filteredList.add(item);

        return filteredList;
    }
    private ArrayList<ItemAllPost> filterByCategory(ArrayList<ItemAllPost> items, String category) {
        ArrayList<ItemAllPost> filteredList = new ArrayList<>();
        for (ItemAllPost item : items) if (item.getCategory().equalsIgnoreCase(category)) filteredList.add(item);
        return filteredList;
    }
    //=====================================================================-------------------------------

    private void getData(){
        String url = "https://developerobaida.com/post_bank/show_all_post.php";
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            effect.setVisibility(View.GONE);
            recPost.setVisibility(View.VISIBLE);
            try {
                for (int i=0; i<response.length();i++){
                    JSONObject jsonObject = response.getJSONObject(i);
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

                    arrayList.add(new ItemAllPost(""+status,""+image,""+font,""+color,""+size,""+category,""+dy,""+dx,""+radius,""+shadowColor));
                }
                adapterAllPost = new AdapterAllPost(arrayList,getContext());
                recPost.setLayoutManager(new LinearLayoutManager(getContext()));
                recPost.setAdapter(adapterAllPost);
                adapterAllPost.notifyDataSetChanged();

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
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

        AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                .setView(dialogView)
                .create();

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