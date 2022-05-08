package com.example.mobilvasarlas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilvasarlas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class ShopListActivity extends AppCompatActivity {
    private static final String LOG_TAG = ShopListActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private FirebaseUser user;

    private FrameLayout redCircle;
    private TextView contextView;
    private int cartItems = 0;
    private int gridNumber = 1;


    private RecyclerView mRecyclerView;
    private ArrayList<ShopingItem> mItemsData;
    private ShopingItemAdapter mAdapter;

    private FirebaseFirestore mFirestore;
    private CollectionReference mitems;

    private SharedPreferences preferences;
    private Notification notification;
    private boolean viewRow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }


        mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new GridLayoutManager(
                this, gridNumber));

        mItemsData = new ArrayList<>();

        mAdapter = new ShopingItemAdapter(this, mItemsData);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore=FirebaseFirestore.getInstance();
        mitems=mFirestore.collection("items");
        notification=new Notification(this);
        queryData();
    }
    private void queryData(){
        mItemsData.clear();

        mitems.orderBy("cartedCount", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document:queryDocumentSnapshots){
                ShopingItem item=document.toObject(ShopingItem.class);
                item.setId((document.getId()));
                mItemsData.add(item);
            }
            if(mItemsData.size()==0){
                initializeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });



    }
    private void initializeData() {
        // Get the resources from the XML file.
        String[] itemsList = getResources()
                .getStringArray(R.array.shopping_item_names);
        String[] itemsInfo = getResources()
                .getStringArray(R.array.shopping_item_desc);
        String[] itemsPrice = getResources()
                .getStringArray(R.array.shopping_item_price);
        TypedArray itemsImageResources =
                getResources().obtainTypedArray(R.array.shopping_item_images);
        TypedArray itemRate = getResources().obtainTypedArray(R.array.shopping_item_rates);

        for (int i = 0; i < itemsList.length; i++) {
            mitems.add(new ShopingItem(
                    itemsList[i],
                    itemsInfo[i],
                    itemsPrice[i],
                    itemRate.getFloat(i, 0),
                    itemsImageResources.getResourceId(i, 0),
                    0));
        }

        itemsImageResources.recycle();
    }
    public void deleteItem(ShopingItem item){
        mitems.document(item._getId()).delete()
                .addOnSuccessListener(success -> {
                    Log.d(LOG_TAG, "Item is successfully deleted: " + item._getId());
                })
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "Item " + item._getId() + " cannot be deleted.", Toast.LENGTH_LONG).show();
                });

        queryData();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ((item.getItemId())) {
            case R.id.log_out_button:
                Log.d(LOG_TAG, "Logout");
                FirebaseAuth.getInstance().signOut();
                finish();
            case R.id.settings_button:
                Log.d(LOG_TAG, "Settings");
                return true;
            case R.id.cart:
                Log.d(LOG_TAG, "Cart");
                return true;
            case R.id.view_selector:
                Log.d(LOG_TAG, "Selector");

                if (viewRow) {
                    changeSpanCount(item, R.drawable.ic_view_grid, 1);
                } else {
                    changeSpanCount(item, R.drawable.ic_view_grid, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawable, int spamcount) {
        viewRow = !viewRow;
        item.setIcon(drawable);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spamcount);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_list_menu, menu);

        MenuItem menuitem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuitem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(LOG_TAG, newText);
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertmenuitem=menu.findItem(R.id.cart);
        FrameLayout rootview=(FrameLayout)alertmenuitem.getActionView();
        redCircle=(FrameLayout) rootview.findViewById(R.id.view_alert_red_circle);
        contextView=(TextView) rootview.findViewById(R.id.view_alert_count_textview);

        rootview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertmenuitem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }
    public void update(ShopingItem item){
        cartItems=(cartItems+1);
        if(cartItems>0){
            contextView.setText(String.valueOf(cartItems));
        }else{
            contextView.setText("");
        }
       mitems.document(item._getId()).update("cartedCount", item.getCartedCount() + 1)
                .addOnFailureListener(fail ->
                {Toast.makeText(this, "Item " + item._getId() + " cannot be changed.", Toast.LENGTH_LONG).show();

            });
        notification.send(item.getName());
        queryData();
    }

}