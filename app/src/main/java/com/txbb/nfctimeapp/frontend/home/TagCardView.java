package com.txbb.nfctimeapp.frontend.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Typeface;
import android.media.Image;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.txbb.nfctimeapp.R;
import com.txbb.nfctimeapp.backend.CustomActivity;
import com.txbb.nfctimeapp.category.Category;
import com.txbb.nfctimeapp.frontend.MainActivity;
import com.txbb.nfctimeapp.util.Units;

import org.w3c.dom.Text;

import java.util.Random;

public class TagCardView extends CardView implements View.OnClickListener {

    private String title;
    private String tagId;
    private String text;
    private Category category;

    private TextView durationTextView;
    private Fragment fragment;

    private int activeColor = 0xFFC0FFCE;
    private int inactiveColor = 0xFFFFE3BA;

    private int[] colors = new int[] {
            0xFF87F1FF, 0xFFC8FF8A, 0xFFDDBBFF, 0xFFA1D5FF, 0xFFFFDEAD
    };

    private int colorIndex;

    public TagCardView(@NonNull Context context, Fragment fragment, String tagId, String title, Category category) {
        super(context);
        this.tagId = tagId;
        this.title = title;
        this.category = category;
        this.fragment = fragment;

        this.init();
        this.addOtherComponents();
    }

    private void init() {
        this.colorIndex = new Random().nextInt(5);

        this.setOnClickListener(this);
        this.setForegroundGravity(Gravity.CENTER_HORIZONTAL);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int width = Units.dpConvert(150, displayMetrics);
        int height = Units.dpConvert(80, displayMetrics);

        int padding = Units.dpConvert(8, displayMetrics);
        int marginTop = Units.dpConvert(15, displayMetrics);

        CardView.LayoutParams layoutParams = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        layoutParams.setMargins(0, marginTop, 0, 0);

        this.setPadding(padding, padding, padding, padding);
        this.setLayoutParams(layoutParams);
        this.setCardBackgroundColor(activeColor);
    }

    private void addOtherComponents() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        LinearLayout linearLayout1 = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout1.setLayoutParams(layoutParams1);

        LinearLayout linearLayout2 = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(Units.dpConvert(260, displayMetrics), ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout2.setOrientation(LinearLayout.VERTICAL);
        linearLayout2.setLayoutParams(layoutParams2);

        LinearLayout linearLayout3 = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout3.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout3.setLayoutParams(layoutParams3);


        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(this.category.getIcon());
        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                Units.dpConvert(60, displayMetrics), Units.dpConvert(55, displayMetrics));
        int imageMargin = Units.dpConvert(10, displayMetrics);
        int margin = Units.dpConvert(10, displayMetrics);
        imageLayoutParams.setMargins(imageMargin, imageMargin, imageMargin, imageMargin);
        imageView.setLayoutParams(imageLayoutParams);

        TextView textView1 = new TextView(getContext());
        textView1.setText(this.title);
        textView1.setTextSize(18);
        textView1.setGravity(Gravity.CENTER);
        textView1.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textLayoutParams.setMargins(0, margin, 0, margin);
        textView1.setLayoutParams(textLayoutParams);

        this.durationTextView = new TextView(getContext());
        durationTextView.setText(this.text);
        durationTextView.setTextSize(15);
        durationTextView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams textLayoutParams2 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textLayoutParams2.setMargins(0, 0, 0, margin);
        durationTextView.setLayoutParams(textLayoutParams2);

        linearLayout3.addView(durationTextView);

        linearLayout2.addView(textView1);
        linearLayout2.addView(linearLayout3);


        linearLayout1.addView(imageView);
        linearLayout1.addView(linearLayout2);

        this.addView(linearLayout1);
    }

    public void updateText(long duration, long total, boolean isActive) {
        String text;
        if (isActive) {
            text = "Active for " + this.longToDuration(duration);
        } else {
            text = "Paused for now";
        }

        if (!isActive) {
            this.setBackgroundColor(inactiveColor);
        } else {
            this.setBackgroundColor(colors[this.colorIndex]);
        }

        this.durationTextView.setText(text);
    }

    private String longToDuration(long l) {
        return (l / 60) + " minutes";
    }

    public String getTagId() {
        return this.tagId;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public void onClick(View v) {
        NavHostFragment navHostFragment =
                (NavHostFragment) this.fragment.getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.action_nav_home_to_editorFragment);

        CustomActivity activity = (CustomActivity) this.fragment.getActivity();
        activity.setSelectedTagId(this.tagId);
        activity.setSelectedTagTitle(this.title);
        activity.setSelectedTagCategory(this.category);
    }
}
