package com.developerobaida.postcraft.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.activities.MainActivity;
import com.developerobaida.postcraft.slider.SlideItem;
import com.developerobaida.postcraft.slider.SliderAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {
    int millis = 5000;
    ViewPager2 viewPager2;
    LinearLayout seeStatus,seeVideo,seeProfilePic;
    final Handler handler = new Handler(Looper.getMainLooper());
    final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = viewPager2.getCurrentItem();
            int itemCount = viewPager2.getAdapter().getItemCount();
            int nextItem = (currentItem + 1) % itemCount;
            viewPager2.setCurrentItem(nextItem, true);
            handler.postDelayed(this, millis); // 10000 milliseconds = 10 seconds
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_home, container, false);
        if (container!=null){
            container.removeAllViews();
        }
        viewPager2 = v.findViewById(R.id.viewPager2);
        seeStatus = v.findViewById(R.id.seeStatus);
        seeVideo = v.findViewById(R.id.seeVideo);
        seeProfilePic = v.findViewById(R.id.seeProfilePic);




        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.frameStatus,new RecentPostCategory());
        transaction.add(R.id.frameVideo,new RecentWallpaperCategory());
        transaction.add(R.id.frameProfilePic,new RecentProfilePicCategory());
        transaction.commit();

        seeStatus.setOnClickListener(v1 -> {if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).changeTab(R.id.post);});
        seeVideo.setOnClickListener(v1 -> {if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).changeTab(R.id.video);});
        seeProfilePic.setOnClickListener(v1 -> {if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).changeTab(R.id.profilePic);});



        //==================================================================================================================================
        //==================================================================================================================================
        List<SlideItem> slideItems = new ArrayList<>();
        String slide = "https://developerobaida.com/post_bank/slider.json";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, slide, null, response -> {
            try {
                for (int i=0; i<response.length();i++){
                    JSONObject jsonObject = response.getJSONObject(i);
                    String img = jsonObject.getString("img");

                    slideItems.add(new SlideItem(img));
                }

                //-=-=-=-=-=-=-=-=-=-=-----------------=-=-=-=-=-=-=--=-=-=-=
                //-=-=-=-=-=-=-=-=-=-=-----------------=-=-=-=-=-=-=--=-=-=-=
                viewPager2.setAdapter(new SliderAdapter(slideItems,viewPager2));
                viewPager2.setClipToPadding(false);
                viewPager2.setClipChildren(false);
                viewPager2.setOffscreenPageLimit(3);
                viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
                CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                compositePageTransformer.addTransformer(new MarginPageTransformer(40));
                compositePageTransformer.addTransformer((page, position) -> {
                    float r = 1 - Math.abs(position);
                    page.setScaleY(0.85f + r * 0.15f);
                });
                viewPager2.setPageTransformer(compositePageTransformer);
                handler.postDelayed(updateRunnable, millis);
                //-=-=-=-=-=-=-=-=-=-=-----------------=-=-=-=-=-=-=--=-=-=-=
                //-=-=-=-=-=-=-=-=-=-=-----------------=-=-=-=-=-=-=--=-=-=-=
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> Toast.makeText(getContext(), "failed to load slider", Toast.LENGTH_SHORT).show());
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);


        //==================================================================================================================================
        //==================================================================================================================================
        return v;
    }

}