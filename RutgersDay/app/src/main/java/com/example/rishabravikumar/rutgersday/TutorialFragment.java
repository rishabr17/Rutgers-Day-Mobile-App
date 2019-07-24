package com.example.rishabravikumar.rutgersday;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.view.ViewGroup.LayoutParams;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.Toast;
import java.util.ArrayList;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;
import android.widget.ViewSwitcher.ViewFactory;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

public class TutorialFragment extends Fragment {

    int i = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);

        final ArrayList<Integer> drawableList = new ArrayList<Integer>();
        drawableList.add(R.drawable.aazero);
        drawableList.add(R.drawable.aaone);
        drawableList.add(R.drawable.aatwo);
        drawableList.add(R.drawable.aathree);
        drawableList.add(R.drawable.aafour);
        drawableList.add(R.drawable.aafive);
        drawableList.add(R.drawable.aasix);

        final ImageSwitcher imageSwitcher = (ImageSwitcher)view.findViewById(R.id.imageswitcher);


        imageSwitcher.setFactory(new ViewFactory() {
                                     public View makeView() {
                                         ImageView myView = new ImageView(getContext());
                                         myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                         myView.setLayoutParams(new
                                                 ImageSwitcher.LayoutParams(LayoutParams.WRAP_CONTENT,
                                                 LayoutParams.WRAP_CONTENT));
                                         return myView;
                                     }
                                 });
        imageSwitcher.setBackgroundResource(drawableList.get(0));
        imageSwitcher.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeLeft() {
                i++;
                if(i>=drawableList.size()){
                    FragmentManager fragmentManager = ((MainActivity)getActivity()).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    fragmentTransaction.hide(((MainActivity)getActivity()).tutorialFragment);
                    fragmentTransaction.show(((MainActivity)getActivity()).campusSelectorFragment);
                    fragmentTransaction.commit();
                }
                imageSwitcher.setBackgroundResource(drawableList.get(i));
            }
            public void onSwipeRight() {
                i--;
                if(i<0){
                    i = 0;
                }
                imageSwitcher.setBackgroundResource(drawableList.get(i));
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}
