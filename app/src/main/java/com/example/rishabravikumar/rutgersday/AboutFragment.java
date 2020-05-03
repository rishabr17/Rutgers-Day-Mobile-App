package com.example.rishabravikumar.rutgersday;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.net.Uri;
import android.webkit.WebView;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.content.Context;

public class AboutFragment extends Fragment {

    private SectionedRecyclerViewAdapter sectionAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        sectionAdapter = new SectionedRecyclerViewAdapter();

        sectionAdapter.addSection(new ProgramsSection("About", getAbout()));

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
                        return 0;
                    default:
                        return 2;
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

    private List<ProgramsCard> getAbout() {
        List<String> arrayList = new ArrayList<>(Arrays.asList(getResources()
                .getStringArray(R.array.about)));
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_about);

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
            super(new SectionParameters.Builder(R.layout.aboutitems)
                    .headerResourceId(R.layout.aboutheaders)
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
                 switch( sectionAdapter.getPositionInSection(itemHolder.getAdapterPosition()) )
                    {
                        case(2):
                            Intent intent0 = new Intent(Intent.ACTION_VIEW);
                            intent0.setData(Uri.parse("https://play.google.com/store/apps/details?id=edu.rutgers.css.Rutgers&feature=search_result"));
                            startActivity(intent0);
                            return;
                        case(3):
                            ((MainActivity)getActivity()).loadSocialFragment();
                            return;
                        case(4):
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://rutgers.ca1.qualtrics.com/jfe/form/SV_byl6QWjT9pq4gpT"));
                            startActivity(intent);
                            return;
                        case(0):
                            Intent intent2 = new Intent(Intent.ACTION_VIEW);
                            intent2.setData(Uri.parse("https://rutgersday.rutgers.edu/content/about-rutgers-day"));
                            startActivity(intent2);
                            return;
                        case(1):
                            Intent intent3 = new Intent(Intent.ACTION_VIEW);
                            intent3.setData(Uri.parse("https://www.rutgers.edu/about"));
                            startActivity(intent3);
                            return;
                        case(5):
                            ((MainActivity)getActivity()).empty = true;
                            PreferenceManager.getDefaultSharedPreferences(((MainActivity)getActivity()).getBaseContext()).edit().putString("Selected", "empty").apply();
                            ((MainActivity)getActivity()).makeEmpty();
                            Intent mStartActivity = new Intent(getContext(), ((MainActivity)getActivity()).getClass());
                            int mPendingIntentId = 123456;
                            PendingIntent mPendingIntent = PendingIntent.getActivity(((MainActivity)getActivity()).getBaseContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager mgr = (AlarmManager)((MainActivity)getActivity()).getBaseContext().getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis()+100, mPendingIntent);
                            ((MainActivity)getActivity()).finish();
                            //System.exit(0);
                            return;
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