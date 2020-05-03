package com.example.rishabravikumar.rutgersday;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;


public class CampusSelectorFragment extends Fragment {

    private SectionedRecyclerViewAdapter sectionAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_campus_selector, container, false);
        sectionAdapter = new SectionedRecyclerViewAdapter();

        sectionAdapter.addSection(new ProgramsSection("", getProgramsByCampus()));

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setNestedScrollingEnabled(false);

        GridLayoutManager glm = new GridLayoutManager(getContext(), 1);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(sectionAdapter.getSectionItemViewType(position)) {
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        return 1;
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(glm);
        recyclerView.setAdapter(sectionAdapter);

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        linearLayoutManager.setItemPrefetchEnabled(true);
        linearLayoutManager.setInitialPrefetchItemCount(15);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private List<ProgramsCard> getProgramsByCampus() {
        List<ProgramsCard> programsList = new ArrayList<>();

        programsList.add(new ProgramsCard("New Brunswick",R.drawable.newbrunswick1));
        programsList.add(new ProgramsCard("Newark",R.drawable.newark));
        programsList.add(new ProgramsCard("Camden",R.drawable.camden));


        return programsList;
    }

    private class ProgramsSection extends StatelessSection {

        String title;
        List<ProgramsCard> list;

        ProgramsSection(String title, List<ProgramsCard> list) {
            super(new SectionParameters.Builder(R.layout.campusselectors)
                    .build());

            this.title = title;
            this.list = list;
        }

        @Override
        public int getContentItemsTotal() {
            return list.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;

            String name = list.get(position).getName();
            int drawableInt = list.get(position).getImage();

            itemHolder.itemView.setText(name);
            itemHolder.imageView.setBackground(getResources().getDrawable(drawableInt));
            final int positron = position;
            itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)getActivity()).newBrunswick = false;
                    ((MainActivity)getActivity()).newark = false;
                    ((MainActivity)getActivity()).camden = false;
                    if(positron == 0){
                        ((MainActivity)getActivity()).newBrunswick = true;
                        PreferenceManager.getDefaultSharedPreferences(((MainActivity)getActivity()).getBaseContext()).edit().putString("Selected", "New Brunswick").apply();
                    }else if(positron == 1){
                        ((MainActivity)getActivity()).newark = true;
                        PreferenceManager.getDefaultSharedPreferences(((MainActivity)getActivity()).getBaseContext()).edit().putString("Selected", "Newark").apply();
                        ((MainActivity)getActivity()).setProgramsList("l2");
                    }else if(positron == 2){
                        ((MainActivity)getActivity()).camden = true;
                        PreferenceManager.getDefaultSharedPreferences(((MainActivity)getActivity()).getBaseContext()).edit().putString("Selected", "Camden").apply();
                        ((MainActivity)getActivity()).setProgramsList("l7");
                    }
                    ((MainActivity)getActivity()).generalInfoFragment.getGeneralInfo();
                    FragmentTransaction fragmentTransaction = ((MainActivity)getActivity()).fragmentManager2.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    fragmentTransaction.remove( ((MainActivity)getActivity()).campusSelectorFragment );
                    ((MainActivity)getActivity()).loadMapsFragment();
                    fragmentTransaction.show( ((MainActivity)getActivity()).mapFragment ).commit();

                }
            });
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

            headerHolder.titleView.setText(title);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleView;

        HeaderViewHolder(View view) {
            super(view);

            titleView = (TextView) view.findViewById(R.id.programTitle);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private final TextView itemView;
        private final FrameLayout imageView;

        ItemViewHolder(View view) {
            super(view);

            rootView = view;
            itemView = (TextView) view.findViewById(R.id.Item);
            imageView = (FrameLayout) view.findViewById(R.id.imgView);
        }
    }

    private class ProgramsCard {
        String name;
        int image;

        ProgramsCard(String name, int image) {
            this.name = name;
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getImage(){
            return image;
        }

        public void setImage(){
            this.image = image;
        }
    }
}