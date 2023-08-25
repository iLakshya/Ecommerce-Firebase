package com.example.ecommercefirebase;

import static com.example.ecommercefirebase.CartActivty.cartsItemList;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ecommercefirebase.databinding.ActivityDetailBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.UUID;

public class DetailActivity extends AppCompatActivity {
    ActivityDetailBinding binding;
    private ProductModel productModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();

        productModel= (ProductModel) intent.getSerializableExtra("model");

        binding.title.setText(productModel.getTitle());
        binding.price.setText(productModel.getPrice());
        binding.description.setText(productModel.getDescription());

        Glide.with(binding.getRoot())
                .load(productModel.getImage())
                .into(binding.image);

        binding.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet(0);
//                addToCart();
            }
        });
        binding.buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet(1);
                CartModel cartModel  = new CartModel(null, productModel.getTitle(),productModel.getImage(),productModel.getPrice(),"1", FirebaseAuth.getInstance().getUid(), null);
            }
        });
    }

    private void showBottomSheet(int i) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(DetailActivity.this)
                .inflate(R.layout.bottom_layout, (LinearLayout)findViewById(R.id.mainlayout),false);
        bottomSheetDialog.setContentView(view);
        EditText qty = view.findViewById(R.id.qty);
        Button btn = view.findViewById(R.id.checkOut);
        bottomSheetDialog.show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantity = qty.getText().toString();
                if (i==0){
                    addToCart(quantity);
                    bottomSheetDialog.cancel();
                }
                else if (i==1){
                    CartModel cartModel = new CartModel(null, productModel.getTitle(),productModel.getImage(),productModel.getPrice(),quantity, FirebaseAuth.getInstance().getUid(), null);
                    cartsItemList = new ArrayList<>();
                    cartsItemList.add(cartModel);
                    startActivity(new Intent(DetailActivity.this, OrderPlacingActivity.class));
                    bottomSheetDialog.cancel();
                }
            }
        });
    }

    private void addToCart(String qty) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Adding");
        progressDialog.setMessage("Item in Cart");
        progressDialog.show();

        String id = UUID.randomUUID().toString();
        CartModel cartModel = new CartModel
                (id, productModel.getTitle(),productModel.getImage(),productModel.getPrice(),qty, FirebaseAuth.getInstance().getUid(), null);
        FirebaseFirestore.getInstance()
                .collection("cart")
                .document(id)
                .set(cartModel);
        Toast.makeText(this, "Item Added to Cart", Toast.LENGTH_SHORT).show();
    }
}