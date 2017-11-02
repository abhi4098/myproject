package com.a83ideas.cobaltConnect.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a83ideas.cobaltConnect.R;
import com.a83ideas.cobaltConnect.model.InventoryItems;
import com.github.mikephil.charting.charts.BarChart;

import java.util.ArrayList;

/**
 * Created by Abhinandan on 18/8/17.
 */
public class ChangePasswordFragment extends Fragment {


    ArrayList<InventoryItems> apiDataList;
    BarChart revenueBarChart;



    public ChangePasswordFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_change_password, container, false);



        return rootView;
    }


}
