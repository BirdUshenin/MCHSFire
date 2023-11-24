package com.birdushenin.mchsfire.presentation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.birdushenin.mchsfire.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddItemFragment extends Fragment implements View.OnClickListener {

    private EditText editTextItemName, editTextBrand;
    private Button buttonAddItem;

    public AddItemFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextItemName = view.findViewById(R.id.et_item_name);
        editTextBrand = view.findViewById(R.id.et_brand);

        buttonAddItem = view.findViewById(R.id.btn_add_item);
        buttonAddItem.setOnClickListener(this);

        view.findViewById(R.id.btn_add_item).setOnClickListener(this);
        TextView dateTextView = view.findViewById(R.id.dateTextView);

        String currentDate = getCurrentDate();
        dateTextView.setText(currentDate);
    }

    private void addItemToSheet() {
        final ProgressDialog loading = ProgressDialog.show(requireActivity(), "Adding Item", "Please wait");

        final String name = editTextItemName.getText().toString().trim();
        final String brand = editTextBrand.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbwPgiWEVXfzeV2PnzkvWw2YKL_e8MkyIoVIRGlIuUoz0hmjxiuh5CVgtGjyzIVeVHRC/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(requireActivity(), response, Toast.LENGTH_LONG).show();

                        NavController navController = Navigation.findNavController(requireActivity(), R.id.main_container);
                        navController.popBackStack();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        // Обработка ошибки
                        Toast.makeText(requireActivity(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("action", "addItem");
                params.put("itemName", name);
                params.put("brand", brand);

                return params;
            }
        };

        int socketTimeOut = 50000;

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(requireActivity());
        queue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonAddItem) {
            addItemToSheet();

        }
    }
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }
}