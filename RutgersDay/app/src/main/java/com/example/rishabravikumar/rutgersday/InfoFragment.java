package com.example.rishabravikumar.rutgersday;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.w3c.dom.Text;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends Fragment {

    boolean emergency = false;
    boolean restrooms = false;

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        TextView textView = (TextView)view.findViewById(R.id.boldText);
        TextView textView1 = (TextView)view.findViewById(R.id.text);
        TextView textView2 = (TextView)view.findViewById(R.id.boldText2);
        TextView textView3 = (TextView)view.findViewById(R.id.text2);

        if(emergency){
            textView.setText("In Case of Emergency");
            if(((MainActivity)getActivity()).newBrunswick) {
                textView2.setVisibility(View.VISIBLE);
                textView3.setVisibility(View.VISIBLE);
                textView1.setText("Assistance is available at the information tents, from public safety personnel and from Rutgers Day staff in neon t-shirts. In case of emergency, call Rutgers Police at 732-932-7211 or 911.");
                textView2.setText("Lost Persons");
                textView3.setText("Immediately report a lost person to the nearest information tent or to any Rutgers Day staff in a neon t-shirt, or contact Rutgers Police at 732-932-7211.");
            }
            else if(((MainActivity)getActivity()).camden){
                textView1.setText("Assistance is available from Rutgers Day staff and public safety personnel. Emergency phones are located throughout our campuses. Call the Rutgers Police at 856-225-6009 or dial 911. Report or bring lost items to Camden Division of the Rutgers Police Department located at 409 North 4th Street.");
            }
        }else if(!emergency && !restrooms){
            textView2.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
            textView.setText("Accessibility");
            textView1.setText("Rutgers Day buses are wheelchair accessible. Most buildings-other than historic buildings-are also accessible by wheelchair.");
        }
        else{
            textView2.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
            textView.setText("Restrooms");
            textView1.setText("Restrooms are available in all open buildings.");
        }

        // Inflate the layout for this fragment
        return view;
    }

}
