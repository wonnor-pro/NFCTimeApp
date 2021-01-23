package com.txbb.nfctimeapp.frontend.home;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.txbb.nfctimeapp.FrontBackInterface;
import com.txbb.nfctimeapp.R;
import com.txbb.nfctimeapp.TagProperties;
import com.txbb.nfctimeapp.backend.Actor;
import com.txbb.nfctimeapp.backend.CustomActivity;
import com.txbb.nfctimeapp.category.Category;
import com.txbb.nfctimeapp.category.CategoryButtonGroup;
import com.txbb.nfctimeapp.category.CategoryManager;
import com.txbb.nfctimeapp.frontend.AddTagActivity;
import com.txbb.nfctimeapp.frontend.MainActivity;
import com.txbb.nfctimeapp.frontend.registration.RegistrationFragment;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements Actor {

    private HomeViewModel homeViewModel;
    private List<TagCardView> tagCardViews;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        this.setUp();

        return root;
    }

    private void setUp() {
        FrontBackInterface frontBackInterface = ((CustomActivity) getActivity()).getFrontBackInterface();

        /* Start a thread to update info periodically */

        this.tagCardViews = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("Sync loop", "Syncing");
                                frontBackInterface.syncRequest();                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    private void addNewCard(String tagId, String tagName, Category category, long duration) {
        String text = "Active for " + this.longToDuration(duration);
        String categoryName = category.getName();
        int categoryIconId = category.getIcon();

        for (TagCardView tagCardView : this.tagCardViews) {
            if (tagCardView.getTagId().equals(tagId)) {
                tagCardView.updateText(text);
                return;
            }
        }

        TagCardView tagCardView = new TagCardView(getContext(), tagId, text, tagName, category);
        this.tagCardViews.add(tagCardView);

        LinearLayout linearLayout = getActivity().findViewById(R.id.lin_layout);
        linearLayout.addView(tagCardView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab = getActivity().findViewById(R.id.home_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment navHostFragment =
                        (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.action_nav_home_to_designerFragment);

            }
        });
    }

    private String longToDuration(long l) {
        return (l / 60) + " minutes";
    }

    @Override
    public void onUnknownTagRead() {

    }

    @Override
    public void onKnownTagRead() {

    }

    @Override
    public void onTagRegister(String id) {

    }

    @Override
    public void sync(Map<String, TagProperties> tags) {
        for (String tagId : tags.keySet()) {
            Log.i("NFC", "Sync initiated");

            TagProperties tagProperties = tags.get(tagId);
            long startTime = tagProperties.getStartTime();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            long currentTime = timestamp.getTime() / 1000;

            long duration = currentTime - startTime;

            int categoryId = tagProperties.getCategory();
            CategoryManager categoryManager = ((CustomActivity) getActivity()).getCategoryManager();
            Category category = categoryManager.getCategoryFromId(categoryId);

            String tagName = tagProperties.getName();

            this.addNewCard(tagId, tagName, category, duration);
        }
    }
}