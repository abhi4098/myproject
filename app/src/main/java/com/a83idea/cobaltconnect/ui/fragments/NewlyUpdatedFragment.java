package com.a83idea.cobaltconnect.ui.fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.a83idea.cobaltconnect.generated.model.ProductUpdate;
import com.a83idea.cobaltconnect.generated.model.ProductUpdateResponse;
import com.a83idea.cobaltconnect.ui.activities.LoadingDialog;
import com.a83idea.cobaltconnect.ui.adapters.NewlyUpdatedAdapter;
import com.a83idea.cobaltconnect.utils.NetworkUtils;
import com.a83idea.cobaltconnect.utils.PrefUtils;
import com.a83idea.cobaltconnect.utils.SnakBarUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.a83idea.cobaltconnect.api.ApiEndPoints.BASE_URL;
import static com.a83idea.cobaltconnect.api.Client.TAG;


public class NewlyUpdatedFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private RetrofitInterface.MerchantUpdateProductClient UpdateProductAdapter;
    private RetrofitInterface.MerchantUpdateToCloverClient MyCloverUpdateAdapter;
    ArrayList<Inventory> updateProductList = null;
    ArrayList<Inventory> showUpdateProductList = null;
    private NewlyUpdatedAdapter newlyUpdatedAdapter;
    static ArrayList<MarginLocalData> productTestId = null;
    EditText etSearch;
    ListView listview;
    Button updateToClover, btPrev, btNext;
    int pageNum =1;
    Spinner spDropdown;
    int spSelectedItem = 10;
    int totalItems = 0;
    int  totalNoPages = 0;
    String updateMarginAll = " ";
    TextView emptyMessage, tvNew, tvQueued, tvProcessed, tvOthers, tvPageNum, tvShowStats;
    String[] items = new String[]{"10", "25", "50", "100"};

    public NewlyUpdatedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_newly_updated, container, false);
        etSearch = (EditText) rootView.findViewById(R.id.etSearch);
        spDropdown = (Spinner) rootView.findViewById(R.id.spinner);
        listview = (ListView) rootView.findViewById(R.id.listview);
        emptyMessage = (TextView) rootView.findViewById(R.id.empty);
        tvNew = (TextView) rootView.findViewById(R.id.new_data);
        tvQueued = (TextView) rootView.findViewById(R.id.queued);
        tvProcessed = (TextView) rootView.findViewById(R.id.processed);
        tvOthers = (TextView) rootView.findViewById(R.id.others);
        tvPageNum = (TextView) rootView.findViewById(R.id.page_num);
        tvShowStats = (TextView) rootView.findViewById(R.id.show_stats);
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


                if (updateProductList != null)

                {
                    showUpdateProductList = new ArrayList<>();
                    for (int i = 0; i < spSelectedItem; i++)

                    {
                        Inventory inventoryItems = new Inventory();
                        inventoryItems.setName(updateProductList.get(i).getName());
                        inventoryItems.setNewPrice(updateProductList.get(i).getNewPrice());
                        inventoryItems.setPreviousCost(updateProductList.get(i).getPreviousCost());
                        inventoryItems.setMargins(updateProductList.get(i).getMargins());
                        inventoryItems.setWholeSaler(updateProductList.get(i).getWholeSaler());
                        inventoryItems.setNewCost(updateProductList.get(i).getNewCost());
                        inventoryItems.setStatus(updateProductList.get(i).getStatus());
                        inventoryItems.setBUpdate(updateProductList.get(i).getBUpdate());
                        inventoryItems.setPreviousPrice(updateProductList.get(i).getPreviousPrice());
                        inventoryItems.setProductId(updateProductList.get(i).getProductId());
                        showUpdateProductList.add(inventoryItems);
                        Log.e(TAG, "onItemSelected: ---" + showUpdateProductList.get(i).getWholeSaler());
                    }
                    newlyUpdatedAdapter = new NewlyUpdatedAdapter(getActivity(), R.layout.items_rowlayout, R.id.item_name, showUpdateProductList, productTestId, updateProductList);
                    listview.setAdapter(newlyUpdatedAdapter);
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
                newlyUpdatedAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e(TAG, "beforeTextChanged: ------------------------");
                newlyUpdatedAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        setUpRestAdapter();
        updateProductDetails();
        return rootView;
    }


    private void setUpRestAdapter() {
        UpdateProductAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.MerchantUpdateProductClient.class, BASE_URL, getActivity());
        MyCloverUpdateAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.MerchantUpdateToCloverClient.class, BASE_URL, getActivity());
    }

    private void UpdateToClover() {
        Call<MarginUpdateAllResponse> call = MyCloverUpdateAdapter.merchantUpdateToClover(new MarginUpdateAll(PrefUtils.getAuthToken(getContext()), "marginUpdateAll"));
        if (NetworkUtils.isNetworkConnected(getActivity())) {
            call.enqueue(new Callback<MarginUpdateAllResponse>() {

                @Override
                public void onResponse(Call<MarginUpdateAllResponse> call, Response<MarginUpdateAllResponse> response) {

                    if (response.isSuccessful()) {
                        if (response.body().getType().equals(1)) {
                            updateMarginAll = "updateMarginAll";
                            updateProductDetails();
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


    private void updateProductDetails() {
        Call<ProductUpdateResponse> call = UpdateProductAdapter.merchantUpdateProduct(new ProductUpdate(PrefUtils.getAuthToken(getContext()), "productUpdate"));
        if (NetworkUtils.isNetworkConnected(getActivity())) {
            call.enqueue(new Callback<ProductUpdateResponse>() {

                @Override
                public void onResponse(Call<ProductUpdateResponse> call, Response<ProductUpdateResponse> response) {

                    if (response.isSuccessful()) {
                        Log.e(TAG, "onResponse:avvvvvv ==================" + response.body().getInventory().size());
                        tvNew.setText(String.format("New : %s", response.body().getNew().toString()));
                        tvQueued.setText(String.format("Queued : %s", response.body().getQueued().toString()));
                        tvProcessed.setText(String.format("Processed : %s", response.body().getProcessed().toString()));
                        tvOthers.setText(String.format("Others : %s", response.body().getOthers().toString()));

                        if (response.body().getInventory().size() != 0)
                            setUpdateProduct(response);
                        else {
                            LoadingDialog.cancelLoading();
                            emptyMessage.setVisibility(View.VISIBLE);
                            listview.setEmptyView(emptyMessage);

                        }


                    }
                }

                @Override
                public void onFailure(Call<ProductUpdateResponse> call, Throwable t) {

                }


            });

        } else {
            SnakBarUtils.networkConnected(getActivity());
        }
    }

    private void setUpdateProduct(Response<ProductUpdateResponse> response) {
        updateProductList = new ArrayList<>();
        showUpdateProductList = new ArrayList<>();
        for (int i = 0; i < response.body().getInventory().size(); i++) {
            Inventory inventoryItems = new Inventory();
            inventoryItems.setName(response.body().getInventory().get(i).getName());
            inventoryItems.setNewPrice(response.body().getInventory().get(i).getNewPrice());
            inventoryItems.setPreviousCost(response.body().getInventory().get(i).getPreviousCost());
            inventoryItems.setMargins(response.body().getInventory().get(i).getMargins());
            inventoryItems.setWholeSaler(response.body().getInventory().get(i).getWholeSaler());
            inventoryItems.setNewCost(response.body().getInventory().get(i).getNewCost());

            if (updateMarginAll.equals("updateMarginAll") && !response.body().getInventory().get(i).getMargins().equals("0.00")) {
                inventoryItems.setStatus("Queued");
            } else {
                inventoryItems.setStatus(response.body().getInventory().get(i).getStatus());
            }

            inventoryItems.setBUpdate(response.body().getInventory().get(i).getBUpdate());
            inventoryItems.setPreviousPrice(response.body().getInventory().get(i).getPreviousPrice());
            inventoryItems.setProductId(response.body().getInventory().get(i).getProductId());
            Log.e(TAG, "setUpdateProduct: --------------------" + response.body().getInventory().get(i).getMargins() + " " + response.body().getInventory().get(i).getName());
            //inventoryItems.setReorder("X");
            updateProductList.add(inventoryItems);
        }

        totalItems = updateProductList.size();
        if (totalItems != 0)

        {
            if ((totalItems % 2) == 0) {
                // number is even
                totalNoPages = totalItems / spSelectedItem;
                Log.e("abhi", "setMyProducts: totalnumber of pages==========" + totalNoPages );
            }

            else {

                totalNoPages = ((totalItems / spSelectedItem)+1);
                Log.e("abhi", "setMyProducts: totalnumber of pages==========" + totalNoPages );
            }

        }
        tvShowStats.setText("Showing " +pageNum + " to " +spSelectedItem + " of "  +totalItems );




        for (int i = 0; i < spSelectedItem; i++) {
            Inventory inventoryItems = new Inventory();
            inventoryItems.setName(updateProductList.get(i).getName());
            inventoryItems.setNewPrice(updateProductList.get(i).getNewPrice());
            inventoryItems.setPreviousCost(updateProductList.get(i).getPreviousCost());
            inventoryItems.setMargins(updateProductList.get(i).getMargins());
            inventoryItems.setWholeSaler(updateProductList.get(i).getWholeSaler());
            inventoryItems.setNewCost(updateProductList.get(i).getNewCost());
            inventoryItems.setStatus(updateProductList.get(i).getStatus());
            inventoryItems.setBUpdate(updateProductList.get(i).getBUpdate());
            inventoryItems.setPreviousPrice(updateProductList.get(i).getPreviousPrice());
            inventoryItems.setProductId(updateProductList.get(i).getProductId());
            showUpdateProductList.add(inventoryItems);
        }

        newlyUpdatedAdapter = new NewlyUpdatedAdapter(getActivity(), R.layout.items_rowlayout, R.id.item_name, showUpdateProductList, productTestId, updateProductList);
        listview.setAdapter(newlyUpdatedAdapter);
        LoadingDialog.cancelLoading();
        listview.setDivider(new ColorDrawable(getResources().getColor(R.color.background_light)));
        listview.setDividerHeight(1);
        listview.setTextFilterEnabled(true);
        listview.setOnItemClickListener(this);


    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.e(TAG, "onItemClick: --------------------------------------------------------------");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.update_to_clover:
                LoadingDialog.showLoadingDialog(getActivity(), "Loading...");
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
        Log.e(TAG, "filterListPages: ------------- pagenum " + pageNum + " " + spSelectedItem );
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
          inventoryItems.setName(updateProductList.get(i).getName());
          inventoryItems.setNewPrice(updateProductList.get(i).getNewPrice());
          inventoryItems.setPreviousCost(updateProductList.get(i).getPreviousCost());
          inventoryItems.setMargins(updateProductList.get(i).getMargins());
          inventoryItems.setWholeSaler(updateProductList.get(i).getWholeSaler());
          inventoryItems.setNewCost(updateProductList.get(i).getNewCost());
          inventoryItems.setStatus(updateProductList.get(i).getStatus());
          inventoryItems.setBUpdate(updateProductList.get(i).getBUpdate());
          inventoryItems.setPreviousPrice(updateProductList.get(i).getPreviousPrice());
          inventoryItems.setProductId(updateProductList.get(i).getProductId());
          showUpdateProductList.add(inventoryItems);
          Log.e(TAG, "filterListPages: ---value of i " + i +  " " + updateProductList.get(i).getName()  );
      }

        newlyUpdatedAdapter = new NewlyUpdatedAdapter(getActivity(), R.layout.items_rowlayout, R.id.item_name, showUpdateProductList, productTestId, updateProductList);
        listview.setAdapter(newlyUpdatedAdapter);
        LoadingDialog.cancelLoading();
        listview.setDivider(new ColorDrawable(getResources().getColor(R.color.background_light)));
        listview.setDividerHeight(1);
        listview.setTextFilterEnabled(true);
    }

}
