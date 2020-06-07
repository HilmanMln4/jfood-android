package hilman.mln.jfood_android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BuatPesananActivity extends AppCompatActivity {

    private static final String TAG = "BuatPesananActivity";
    private int currentUserId;
    private int id_food;
    private String foodName;
    private String foodCategory;
    private int foodPrice;
    private String promoCode;
    private String promoCodeRequest;
    private int priceRequest;
    private String foodList;
    private String newFoodList;
    private int foodPriceList;
    private String selectedPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_pesanan);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            currentUserId = extras.getInt("currentUserId");
            id_food = extras.getInt("id_food");
            foodName = extras.getString("foodName");
            foodCategory = extras.getString("foodCategory");
            foodPrice = extras.getInt("foodPrice");
        }

        final TextView textCode = findViewById(R.id.textCode);
        final EditText promo_code = findViewById(R.id.promo_code);
        final TextView food_name = findViewById(R.id.food_name);
        final TextView food_category = findViewById(R.id.food_category);
        final TextView food_price = findViewById(R.id.food_price);
        final TextView total_price = findViewById(R.id.total_price);
        final RadioGroup radioGroup = findViewById(R.id.radioGroup);
        final Button hitung = findViewById(R.id.hitung);
        final Button pesan = findViewById(R.id.pesan);

        pesan.setVisibility(View.GONE);
        textCode.setVisibility(View.GONE);
        promo_code.setVisibility(View.GONE);
        hitung.setEnabled(false);

        food_name.setText(foodName);
        food_category.setText(foodCategory);
        food_price.setText(String.valueOf(foodPrice));
        total_price.setText("0");

        foodPriceList = getIntent().getExtras().getInt("foodPriceList");
        foodPriceList = foodPriceList + foodPrice;
        Log.d("Harga total: ", foodPriceList+"");

        foodList = getIntent().getExtras().getString("foodList");
        if(foodList == null){
            foodList = "";
        }
        newFoodList = foodList + id_food + ",";
        Log.d("Mentahan: ", newFoodList);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                RadioButton radioButton = findViewById(id);
                String selected = radioButton.getText().toString();
                switch(selected) {
                    case "Via CASH":
                        textCode.setVisibility(View.GONE);
                        promo_code.setVisibility(View.GONE);
                        hitung.setEnabled(true);
                        break;

                    case "Via CASHLESS":
                        textCode.setVisibility(View.VISIBLE);
                        promo_code.setVisibility(View.VISIBLE);
                        hitung.setEnabled(true);
                        break;
                }
            }
        });

        hitung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioBtn = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(radioBtn);
                String selected = radioButton.getText().toString();
                switch(selected) {
                    case "Via CASH":
                        total_price.setText(String.valueOf(food_price));
                        break;
                    case "Via CASHLESS":
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jsonResponse = new JSONArray(response);
                                    for(int i = 0; i < jsonResponse.length(); i++){
                                        JSONObject promo = jsonResponse.getJSONObject(i);
                                        if(promo_code.getText().toString().equals(promo.getString("code")) && promo.getBoolean("active")) {
                                            if(foodPriceList > promo.getInt("minPrice")) {
                                                priceRequest = promo.getInt("discont");
                                                total_price.setText(String.valueOf(foodPriceList - priceRequest));
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    Log.d(TAG, "Load data failed.");
                                }
                            }
                        };
                        CheckPromoRequest promoRequest = new CheckPromoRequest(responseListener);
                        RequestQueue queue = Volley.newRequestQueue(BuatPesananActivity.this);
                        queue.add(promoRequest);

                        break;
                }
                hitung.setVisibility(View.GONE);
                pesan.setVisibility(View.VISIBLE);
            }
        });

        pesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioBtn = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(radioBtn);
                String selected = radioButton.getText().toString();
                BuatPesananRequest pesananRequest = null;

                Log.d(TAG, selected);

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(response != null) {
                                Toast.makeText(BuatPesananActivity.this, "Pesanan Berhasil", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(BuatPesananActivity.this, "Pesanan Gagal", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                };

                if(selected.equals("Via CASH")){
                    pesananRequest = new BuatPesananRequest(newFoodList.substring(0, newFoodList.length()-1), currentUserId+"", responseListener);
                }
                else if(selected.equals("Via CASHLESS")){
                    pesananRequest = new BuatPesananRequest(newFoodList.substring(0, newFoodList.length()-1), currentUserId+"", promo_code.getText().toString(), responseListener);
                }

                RequestQueue queue = Volley.newRequestQueue(BuatPesananActivity.this);
                queue.add(pesananRequest);
            }
        });
    };
}
