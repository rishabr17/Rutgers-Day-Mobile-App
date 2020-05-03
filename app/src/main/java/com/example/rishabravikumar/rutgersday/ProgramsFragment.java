package com.example.rishabravikumar.rutgersday;

import android.content.res.TypedArray;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;


public class ProgramsFragment extends Fragment {

    private SectionedRecyclerViewAdapter sectionAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_programs, container, false);
        /*Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar);//view.findViewById(R.id.mr_title_bar);*/

        //AppCompatActivity.setSupportActionBar(toolbar);


        sectionAdapter = new SectionedRecyclerViewAdapter();
            sectionAdapter.addSection(new ProgramsSection("Programs By Campus", getProgramsByCampus()));
            sectionAdapter.addSection(new ProgramsSection("Programs By Interest", getProgramsByInterest()));
            sectionAdapter.addSection(new ProgramsSection("Performance Stages", getPerformanceStages()));

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        GridLayoutManager glm = new GridLayoutManager(getContext(), 2);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(sectionAdapter.getSectionItemViewType(position)) {
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        return 2;
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
    @Override
    public void onPause() {
        super.onPause();
    }

    private List<ProgramsCard> getProgramsByCampus() {
        List<String> arrayList = new ArrayList<>(Arrays.asList(getResources()
                .getStringArray(R.array.program_by_campus)));
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_by_campus);

        List<ProgramsCard> programsList = new ArrayList<>();

        int i = 0;
        for (String str : arrayList) {
            programsList.add(new ProgramsCard(str,imgs.getResourceId(i,-1)));
            i++;
        }

        return programsList;
    }

    private List<ProgramsCard> getProgramsByInterest() {
        List<String> arrayList = new ArrayList<>(Arrays.asList(getResources()
                .getStringArray(R.array.program_by_interest)));
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_by_interest);

        List<ProgramsCard> programsList = new ArrayList<>();

        int i = 0;
        for (String str : arrayList) {
            programsList.add(new ProgramsCard(str,imgs.getResourceId(i,-1)));
            i++;
        }

        return programsList;
    }

    private List<ProgramsCard> getPerformanceStages() {
        List<String> arrayList = new ArrayList<>(Arrays.asList(getResources()
                .getStringArray(R.array.performance_stages)));
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_by_stages);

        List<ProgramsCard> programsList = new ArrayList<>();

        int i = 0;
        for (String str : arrayList) {
            programsList.add(new ProgramsCard(str,imgs.getResourceId(i,-1)));
            i++;
        }

        return programsList;
    }

    private class ProgramsSection extends StatelessSection {

        String title;
        List<ProgramsCard> list;

        ProgramsSection(String title, List<ProgramsCard> list) {
            super(new SectionParameters.Builder(R.layout.programitems)
                    .headerResourceId(R.layout.programheaders)
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
                    if(!((MainActivity)getActivity()).isProgramSelected) {
                        ((MainActivity)getActivity()).isProgramSelected = true;
                        switch (list.get(positron).getName()) {
                            case ("Cook/Douglass"):
                                ((MainActivity) getActivity()).setProgramsList("l5");
                                return;
                            case ("College Avenue"):
                                ((MainActivity) getActivity()).setProgramsList("l6");
                                return;
                            case ("Busch"):
                                ((MainActivity) getActivity()).setProgramsList("l8");
                                return;
                            case ("Get to Know Rutgers"):
                                ((MainActivity) getActivity()).setProgramsList("c23");
                                return;
                            case ("Experience the Arts & Humanities"):
                                ((MainActivity) getActivity()).setProgramsList("c24");
                                return;
                            case ("Sciences & Technology"):
                                ((MainActivity) getActivity()).setProgramsList("c25");
                                return;
                            case ("All Things Green"):
                                ((MainActivity) getActivity()).setProgramsList("c26");
                                return;
                            case ("Business & Careers"):
                                ((MainActivity) getActivity()).setProgramsList("c28");
                                return;
                            case ("Sports & Recreation"):
                                ((MainActivity) getActivity()).setProgramsList("c29");
                                return;
                            case ("Kids Stuff"):
                                ((MainActivity) getActivity()).setProgramsList("c30");
                                return;
                            case ("Health & Fitness"):
                                ((MainActivity) getActivity()).setProgramsList("c27");
                                return;
                            case ("Alumni"):
                                ((MainActivity) getActivity()).setProgramsList("c31");
                                return;
                        case("College Avenue Big R Stage"):
                            ((MainActivity)getActivity()).setProgramsList("bigR");
                            return;
                        case("Busch Big R Stage"):
                            ((MainActivity)getActivity()).setProgramsList("l8t51");
                            return;
                        case("Cook/Douglass Big R Stage"):
                            ((MainActivity)getActivity()).setProgramsList("l5t51");
                            return;
                        case("College Avenue Scarlet Stage"):
                            ((MainActivity)getActivity()).setProgramsList("scarletstage");
                            return;
                        }
                    }
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
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //Code executes EVERY TIME user views the fragment
        if(isVisibleToUser) {
        }
    }
}
