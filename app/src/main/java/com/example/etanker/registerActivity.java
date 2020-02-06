package com.example.etanker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class registerActivity extends AppCompatActivity {

    Spinner selectCategory;
    String[] categoryItem={"Supplier","Consumer","Admin"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        selectCategory=(Spinner)findViewById(R.id.selectCategory);
//        ArrayAdapter<String> spinnerAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,categoryItem);
//        categoryItem.setAdapter(spinnerAdapter);
//


    }
}
