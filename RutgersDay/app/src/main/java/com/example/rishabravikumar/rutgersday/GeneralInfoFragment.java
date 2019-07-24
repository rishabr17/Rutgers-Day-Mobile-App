package com.example.rishabravikumar.rutgersday;

import android.content.res.TypedArray;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;


public class GeneralInfoFragment extends Fragment {

    private SectionedRecyclerViewAdapter sectionAdapter;
    Handler handler = new Handler();
    ProgressBar bar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general_info, container, false);

        sectionAdapter = new SectionedRecyclerViewAdapter();


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

    public List<ProgramsCard> getGeneralInfo() {
        List<String> arrayList;
        TypedArray imgs;

        if(((MainActivity)getActivity()).newBrunswick) {
            arrayList = new ArrayList<>(Arrays.asList(getResources()
                    .getStringArray(R.array.general_info)));
            imgs = getResources().obtainTypedArray(R.array.image_general_info);
        }else{
            arrayList = new ArrayList<>(Arrays.asList(getResources()
                    .getStringArray(R.array.nc_general_info)));
            imgs = getResources().obtainTypedArray(R.array.nc_image_general_info);
        }

        List<ProgramsCard> programsList = new ArrayList<>();

        int i = 0;
        for (String str : arrayList) {
            programsList.add(new ProgramsCard(str,imgs.getResourceId(i,-1)));
            i++;
        }
        sectionAdapter.removeAllSections();
        sectionAdapter.addSection(new ProgramsSection("General Information", programsList));
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

            itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((MainActivity)getActivity()).newBrunswick) {
                        switch (sectionAdapter.getPositionInSection(itemHolder.getAdapterPosition())) {
                            case (0):
                                ((MainActivity) getActivity()).loadParkingFragment();
                                return;
                            case (1):
                                ((MainActivity) getActivity()).loadStopsFragment();
                                return;
                            case (2):
                                ((MainActivity)getActivity()).loadFoodsFragment(1);
                                return;
                            case (3):
                                ((MainActivity)getActivity()).loadBusFragment();
                                return;
                            case (4):
                                ((MainActivity) getActivity()).loadInfoFragment(false, true);
                                return;
                            case (5):
                                ((MainActivity) getActivity()).loadInfoFragment(true, false);
                                return;
                            case(6):
                                ((MainActivity) getActivity()).loadInfoFragment(false, false);
                                return;
                        }
                    }
                    if(((MainActivity)getActivity()).newark || ((MainActivity)getActivity()).camden) {
                        switch (sectionAdapter.getPositionInSection(itemHolder.getAdapterPosition())) {
                            case (0):
                                ((MainActivity) getActivity()).loadParkingFragment();
                                return;
                            case (1):
                                if(((MainActivity) getActivity()).newark) {
                                    ((MainActivity) getActivity()).loadFoodsFragment(2);
                                }else{
                                    ((MainActivity) getActivity()).loadFoodsFragment(3);
                                }
                                return;
                            case (2):
                                ((MainActivity) getActivity()).loadInfoFragment(false, true);
                                return;
                            case (3):
                                ((MainActivity) getActivity()).loadInfoFragment(true, false);
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
}