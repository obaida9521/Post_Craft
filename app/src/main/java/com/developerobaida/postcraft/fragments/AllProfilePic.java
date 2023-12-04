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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.adapters.AdapterAllProfilePic;
import com.developerobaida.postcraft.listeners.OnProfileItemClickListener;
import com.developerobaida.postcraft.model.ItemAllProfilePic;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AllProfilePic extends Fragment implements OnProfileItemClickListener {

    RecyclerView recProfilePic;
    ArrayList<ItemAllProfilePic> arrayList = new ArrayList<>();
    AdapterAllProfilePic adapterAllProfilePic;
    Spinner spinner;
    SearchView search;
    JsonArrayRequest jsonArrayRequest;
    ShimmerFrameLayout effect;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    private boolean isFavorite = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_profile_pic, container, false);
        if (container!=null){
            container.removeAllViews();
        }
        recProfilePic = v.findViewById(R.id.recProfilePic);
        spinner = v.findViewById(R.id.spinner);
        search = v.findViewById(R.id.search);
        effect = v.findViewById(R.id.effect);
        getData();


        //-----------------------------------------------------------------------------
        //-----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(),R.array.categories_profile, android.R.layout.simple_spinner_item);
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
                    String bookmark = jsonObject.getString("bookmark");

                    arrayList.add(new ItemAllProfilePic(profilePic+image,title,category,type,id,bookmark));
                }
                String userId = currentUser != null ? currentUser.getUid() : "NULL";
                adapterAllProfilePic = new AdapterAllProfilePic(arrayList,getContext(), this);
                recProfilePic.setLayoutManager(new LinearLayoutManager(getContext()));
                recProfilePic.setAdapter(adapterAllProfilePic);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            effect.setVisibility(View.GONE);
            recProfilePic.setVisibility(View.GONE);
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

    @Override
    public void onProfileItemClick(ItemAllProfilePic profilePic, ImageView imageView, int position) {
        Log.wtf("DATA", profilePic.toString());

        isFavorite = !isFavorite;
        String tag = "FAVORITE";


        Log.wtf(tag, "Outside - "+position+" "+isFavorite);
        if(isFavorite){
            Log.wtf(tag, "Inside If - "+position+" "+isFavorite);

            imageView.setImageResource(R.drawable.favorite_24);
            Toast.makeText(getContext(),"Added",Toast.LENGTH_SHORT).show();
        }else {
            Log.wtf(tag, "Inside Else - "+position+" "+isFavorite);

            imageView.setImageResource(R.drawable.favorite_border_24);
            Toast.makeText(getContext(),"Removed",Toast.LENGTH_SHORT).show();
        }
    }
}