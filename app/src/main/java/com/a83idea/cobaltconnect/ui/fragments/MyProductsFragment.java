package com.a83idea.cobaltconnect.ui.fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.a83idea.cobaltconnect.R;
import com.a83idea.cobaltconnect.api.ApiAdapter;
import com.a83idea.cobaltconnect.api.RetrofitInterface;
import com.a83idea.cobaltconnect.generated.model.Inventory;
import com.a83idea.cobaltconnect.generated.model.MarginLocalData;
import com.a83idea.cobaltconnect.generated.model.MarginUpdateAll;
import com.a83idea.cobaltconnect.generated.model.MarginUpdateAllResponse;
import com.a83idea.cobaltconnect.generated.model.MyCloverProduct;
import com.a83idea.cobaltconnect.generated.model.MyCloverProductResponse;
import com.a83idea.cobaltconnect.model.InventoryItems;
import com.a83idea.cobaltconnect.ui.activities.LoadingDialog;
import com.a83idea.cobaltconnect.ui.adapters.MyProductAdapter;
import com.a83idea.cobaltconnect.utils.NetworkUtils;
import com.a83idea.cobaltconnect.utils.PrefUtils;
import com.a83idea.cobaltconnect.utils.SnakBarUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.a83idea.cobaltconnect.api.ApiEndPoints.BASE_URL;
import static com.a83idea.cobaltconnect.api.Client.TAG;


public class MyProductsFragment extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener{

    private RetrofitInterface.MerchantMyProductClient MyProductAdapter;
    private RetrofitInterface.MerchantFetchFromCloverClient MyCloverFetchAdapter;

    static ArrayList<Inventory> myProductList = null;
    ArrayList<InventoryItems> dataList = new ArrayList<>();
    private RecyclerView newlyUpdatedRecyclerView;
    private MyProductAdapter myProductAdapter;
    EditText etSearch;
    ListView listview;
    TextView emptyMessage,tvLastFetch,tvPageNum, tvShowStats,tvProcessing,tvLast;
    String[] items = new String[]{"10", "25", "50", "100"};
    Button updateToClover, btPrev, btNext;
    int pageNum =1;
    Spinner spDropdown;
    int spSelectedItem = 10;
    int totalItems = 0;
    int  totalNoPages = 0;
    ArrayList<Inventory> showUpdateProductList = null;

    static ArrayList<MarginLocalData> productTestId = null;


    //String[] items = new String[]{"Category", "category1", "category2","category3"};
    public MyProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_products, container, false);
        etSearch = (EditText) rootView.findViewById(R.id.etSearch);
        listview = (ListView) rootView.findViewById(R.id.listview);
        emptyMessage = (TextView) rootView.findViewById(R.id.empty);
        spDropdown = (Spinner) rootView.findViewById(R.id.spinner);
        tvLastFetch = (TextView) rootView.findViewById(R.id.last_fetch_time_date);
        tvPageNum = (TextView) rootView.findViewById(R.id.page_num);
        tvShowStats = (TextView) rootView.findViewById(R.id.show_stats);
        tvProcessing = (TextView) rootView.findViewById(R.id.processing);
        tvLast = (TextView) rootView.findViewById(R.id.last_fetch);
        btNext = (Button) rootView.findViewById(R.id.next);
        btPrev = (Button) rootView.findViewById(R.id.prev);
        updateToClover = (Button) rootView.findViewById(R.id.update_to_clover);
        updateToClover.setOnClickListener(this);
        btNext.setOnClickListener(this);
        btPrev.setOnClickListener(this);
        LoadingDialog.showLoadingDialog(getActivity(), "Loading...");
        if (pageNum ==1)
        {
            btPrev.setEnabled(false);
            btPrev.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rectangular_background_light_gray));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, items);
        spDropdown.setAdapter(adapter);
        spDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int j, long l) {
                spSelectedItem = Integer.parseInt(spDropdown.getSelectedItem().toString());
                pageNum = 1;
                tvPageNum.setText(String.valueOf(pageNum));

                btPrev.setEnabled(false);
                btPrev.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rectangular_background_light_gray));
                Log.e(TAG, "onItemSelected: -----------------" + spSelectedItem + pageNum);
                btNext.setEnabled(true);
                btNext.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_state_selector));



                if (totalItems != 0)

                {


                    if ((totalItems % 2) == 0) {
                        totalNoPages = totalItems / spSelectedItem;
                        tvShowStats.setText("Showing " +pageNum + " to " +spSelectedItem + " of "  +totalItems );
                    }

                    else {

                        totalNoPages = ((totalItems / spSelectedItem)+1);
                        Log.e("abhi", "onItemSelected: ------------total num of pages" + totalNoPages );
                        tvShowStats.setText("Showing " +pageNum + " to " +spSelectedItem + " of "  +totalItems );
                    }


                }


                if (myProductList != null)

                {
                    showUpdateProductList = new ArrayList<>();
                    for (int i = 0; i < spSelectedItem; i++)

                    {
                        Inventory inventoryItems = new Inventory();
                        inventoryItems.setName(myProductList.get(i).getName());
                        inventoryItems.setNewPrice(myProductList.get(i).getNewPrice());
                        inventoryItems.setPreviousCost(myProductList.get(i).getPreviousCost());
                        inventoryItems.setMargin(myProductList.get(i).getMargin());
                        inventoryItems.setWholeSaler(myProductList.get(i).getWholeSaler());
                        inventoryItems.setNewCost(myProductList.get(i).getNewCost());
                        inventoryItems.setStatus(myProductList.get(i).getStatus());
                        inventoryItems.setBUpdate(myProductList.get(i).getBUpdate());
                        inventoryItems.setPreviousPrice(myProductList.get(i).getPreviousPrice());
                        inventoryItems.setProductId(myProductList.get(i).getProductId());
                        inventoryItems.setCloverId(myProductList.get(i).getCloverId());
                        showUpdateProductList.add(inventoryItems);
                        Log.e(TAG, "onItemSelected: ---" + showUpdateProductList.get(i).getWholeSaler());
                    }
                    myProductAdapter = new MyProductAdapter(getActivity(), R.layout.myproduct_list_layout, R.id.item_name, showUpdateProductList, productTestId, myProductList);
                    listview.setAdapter(myProductAdapter);
                    LoadingDialog.cancelLoading();
                    listview.setDivider(new ColorDrawable(getResources().getColor(R.color.background_light)));
                    listview.setDividerHeight(1);
                    listview.setTextFilterEnabled(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myProductAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
                Log.e(TAG, "beforeTextChanged: ------------------------" );
                myProductAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setUpRestAdapter();
        MyProductDetails();
        return rootView;
    }


    private void setUpRestAdapter() {
        MyProductAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.MerchantMyProductClient.class, BASE_URL, getActivity());
        MyCloverFetchAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.MerchantFetchFromCloverClient.class, BASE_URL, getActivity());

    }




    private void MyProductDetails() {
        Call<MyCloverProductResponse> call = MyProductAdapter.merchantMyProduct(new MyCloverProduct(PrefUtils.getAuthToken(getContext()), "cloverInventory"));
        if (NetworkUtils.isNetworkConnected(getActivity())) {
            call.enqueue(new Callback<MyCloverProductResponse>() {

                @Override
                public void onResponse(Call<MyCloverProductResponse> call, Response<MyCloverProductResponse> response) {

                    if (response.isSuccessful()) {

                        tvLastFetch.setText(response.body().getLastFetch());
                        Log.e(TAG, "onResponse: ==================" + response.body().getInventory().size());
                        if (response.body().getInventory().size() != 0 )
                            setMyProducts(response);
                        else
                        {
                            LoadingDialog.cancelLoading();
                            emptyMessage.setVisibility(View.VISIBLE);
                            listview.setEmptyView(emptyMessage);
                        }




                    }
                }

                @Override
                public void onFailure(Call<MyCloverProductResponse> call, Throwable t) {

                }


            });

        } else {
            SnakBarUtils.networkConnected(getActivity());
        }
    }

    private void setMyProducts(Response<MyCloverProductResponse> response) {
          myProductList = new ArrayList<>();
        showUpdateProductList = new ArrayList<>();
        for (int i = 0; i < response.body().getInventory().size(); i++) {
            Inventory inventoryItems = new Inventory();
            inventoryItems.setName(response.body().getInventory().get(i).getName());
            inventoryItems.setNewPrice(response.body().getInventory().get(i).getNewPrice());
            inventoryItems.setPreviousCost(response.body().getInventory().get(i).getPreviousCost());
            inventoryItems.setMargin(response.body().getInventory().get(i).getMargin());
            inventoryItems.setWholeSaler(response.body().getInventory().get(i).getWholeSaler());
            inventoryItems.setNewCost(response.body().getInventory().get(i).getNewCost());
            inventoryItems.setStatus(response.body().getInventory().get(i).getStatus());
            inventoryItems.setBUpdate(response.body().getInventory().get(i).getBUpdate());
            inventoryItems.setPreviousPrice(response.body().getInventory().get(i).getPreviousPrice());
            inventoryItems.setProductId(response.body().getInventory().get(i).getProductId());
            inventoryItems.setCloverId(response.body().getInventory().get(i).getCloverId());
            Log.e(TAG, "setUpdateProduct: --------------------" + response.body().getInventory().get(i).getMargin() + " " + response.body().getInventory().get(i).getName());
            //inventoryItems.setReorder("X");
            myProductList.add(inventoryItems);
        }



        totalItems = myProductList.size();

        if (totalItems != 0)

        {

            if ((totalItems % 2) == 0) {
                totalNoPages = totalItems / spSelectedItem;
            }

            else {

                totalNoPages = ((totalItems / spSelectedItem)+1);

            }



        }
        tvShowStats.setText("Showing " +pageNum + " to " +spSelectedItem + " of "  +totalItems );




        for (int i = 0; i < spSelectedItem; i++) {
            Inventory inventoryItems = new Inventory();
            inventoryItems.setName(myProductList.get(i).getName());
            inventoryItems.setNewPrice(myProductList.get(i).getNewPrice());
            inventoryItems.setPreviousCost(myProductList.get(i).getPreviousCost());
            inventoryItems.setMargin(myProductList.get(i).getMargin());
            inventoryItems.setWholeSaler(myProductList.get(i).getWholeSaler());
            inventoryItems.setNewCost(myProductList.get(i).getNewCost());
            inventoryItems.setStatus(myProductList.get(i).getStatus());
            inventoryItems.setBUpdate(myProductList.get(i).getBUpdate());
            inventoryItems.setPreviousPrice(myProductList.get(i).getPreviousPrice());
            inventoryItems.setProductId(myProductList.get(i).getProductId());
            inventoryItems.setCloverId(myProductList.get(i).getCloverId());
            showUpdateProductList.add(inventoryItems);
        }

        myProductAdapter = new MyProductAdapter(getActivity(), R.layout.myproduct_list_layout, R.id.item_name, showUpdateProductList,productTestId, myProductList);
            listview.setAdapter(myProductAdapter);
            LoadingDialog.cancelLoading();
            listview.setDivider(new ColorDrawable(getResources().getColor(R.color.background_light)));
            listview.setDividerHeight(1);
            listview.setTextFilterEnabled(true);
            listview.setOnItemClickListener(this);



    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.update_to_clover:
                UpdateToClover();
                break;
            case R.id.next:
                btPrev.setEnabled(true);
                btPrev.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_state_selector));

                if (pageNum<totalNoPages) {
                    pageNum++;
                    filterListPages();
                }
                if (pageNum == totalNoPages)
                {
                    Log.e(TAG, "onClick: ------------------------chak deeeeeeeeeeeeeeeee" );
                    btNext.setEnabled(false);
                    btNext.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rectangular_background_light_gray));
                }
                break;

            case R.id.prev:
                btNext.setEnabled(true);
                btNext.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_state_selector));

                if (pageNum >1) {
                    pageNum--;
                    filterListPages();
                }
                if (pageNum == 1)
                {
                    btPrev.setEnabled(false);
                    btPrev.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rectangular_background_light_gray));
                }
                break;
            default:
                break;

        }
    }

    private void filterListPages() {
        etSearch.getText().clear();
        if (pageNum == 1)
        {
            tvShowStats.setText("Showing " + pageNum+ " to " + pageNum * spSelectedItem + " of " + totalItems);
        }
        else {
            tvShowStats.setText("Showing " + (pageNum - 1) * spSelectedItem + " to " + pageNum * spSelectedItem + " of " + totalItems);
        }
        tvPageNum.setText(String.valueOf(pageNum));
        showUpdateProductList = new ArrayList<>();




            for (int i = (pageNum-1)*spSelectedItem; i < pageNum*spSelectedItem && i<totalItems; i++)
        {

            Inventory inventoryItems = new Inventory();
            inventoryItems.setName(myProductList.get(i).getName());
            inventoryItems.setNewPrice(myProductList.get(i).getNewPrice());
            inventoryItems.setPreviousCost(myProductList.get(i).getPreviousCost());
            inventoryItems.setMargin(myProductList.get(i).getMargin());
            inventoryItems.setWholeSaler(myProductList.get(i).getWholeSaler());
            inventoryItems.setNewCost(myProductList.get(i).getNewCost());
            inventoryItems.setStatus(myProductList.get(i).getStatus());
            inventoryItems.setBUpdate(myProductList.get(i).getBUpdate());
            inventoryItems.setPreviousPrice(myProductList.get(i).getPreviousPrice());
            inventoryItems.setProductId(myProductList.get(i).getProductId());
            inventoryItems.setCloverId(myProductList.get(i).getCloverId());
            showUpdateProductList.add(inventoryItems);
            Log.e(TAG, "filterListPages: ---value of i " + i +  " " + myProductList.get(i).getName()  );
        }

        myProductAdapter = new MyProductAdapter(getActivity(), R.layout.myproduct_list_layout, R.id.item_name, showUpdateProductList, productTestId, myProductList);
        listview.setAdapter(myProductAdapter);
        LoadingDialog.cancelLoading();
        listview.setDivider(new ColorDrawable(getResources().getColor(R.color.background_light)));
        listview.setDividerHeight(1);
        listview.setTextFilterEnabled(true);
    }



    private void UpdateToClover() {
        Call<MarginUpdateAllResponse> call = MyCloverFetchAdapter.merchantFetchFromClover(new MarginUpdateAll(PrefUtils.getAuthToken(getContext()), "fetchCloverInventory"));
        if (NetworkUtils.isNetworkConnected(getActivity())) {
            call.enqueue(new Callback<MarginUpdateAllResponse>() {

                @Override
                public void onResponse(Call<MarginUpdateAllResponse> call, Response<MarginUpdateAllResponse> response) {

                    if (response.isSuccessful()) {
                        if (response.body().getType().equals(1)) {
                            tvLastFetch.setVisibility(View.GONE);
                            tvLast.setVisibility(View.GONE);
                            tvProcessing.setVisibility(View.VISIBLE);
                            tvProcessing.setText(response.body().getMsg());
                            tvProcessing.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rectangular_background_green));
                        }


                    }
                }

                @Override
                public void onFailure(Call<MarginUpdateAllResponse> call, Throwable t) {

                }


            });

        } else {
            SnakBarUtils.networkConnected(getActivity());
        }
    }

}
