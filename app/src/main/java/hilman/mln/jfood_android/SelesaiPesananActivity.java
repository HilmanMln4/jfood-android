package hilman.mln.jfood_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SelesaiPesananActivity extends AppCompatActivity {

    private static final String TAG = "SelesaiPesananActivity";
    private int currentUserId;
    private int invoiceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selesai_pesanan);

        currentUserId = getIntent().getExtras().getInt("currentUserId");
        final TextView id_invoice = findViewById(R.id.id_invoice);
        final TextView nama_customer = findViewById(R.id.nama);
        final TextView nama_makanan = findViewById(R.id.nama_makanan);
        final TextView tanggal_pesan = findViewById(R.id.tanggal_pesan);
        final TextView total_biaya = findViewById(R.id.total_biaya);
        final TextView status_invoice = findViewById(R.id.status_invoice);
        final Button selesai = findViewById(R.id.selesai);
        final Button batal = findViewById(R.id.batal);

        findViewById(R.id.selesai_pesanan).setVisibility(View.GONE);
        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    findViewById(R.id.selesai_pesanan).setVisibility(View.VISIBLE);
                    JSONArray jsonResponse = new JSONArray(response);
                    JSONObject invoice = jsonResponse.getJSONObject(jsonResponse.length()-1);
                    Log.d(TAG, String.valueOf(invoice));
                    JSONObject food = invoice.getJSONArray("foods").getJSONObject(0);
                    Log.d(TAG, food.getString("name"));
                    JSONObject customer = invoice.getJSONObject("customer");
                    Log.d(TAG, String.valueOf(customer));

                    invoiceId = invoice.getInt("id");

                    id_invoice.setText(String.valueOf(invoice.getInt("id")));
                    nama_customer.setText(customer.getString("name"));
                    nama_makanan.setText(food.getString("name"));
                    tanggal_pesan.setText(invoice.getString("date"));
                    total_biaya.setText(String.valueOf(invoice.getInt("totalPrice")));
                    status_invoice.setText(invoice.getString("invoiceStatus"));

                }
                catch (JSONException e){
                    startActivity(new Intent(SelesaiPesananActivity.this, MainActivity.class));
                    Log.d(TAG, "Load data failed.");

                }
            }
        };

        PesananFetchRequest fetchRequest = new PesananFetchRequest(currentUserId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(SelesaiPesananActivity.this);
        queue.add(fetchRequest);

        findViewById(R.id.selesai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(SelesaiPesananActivity.this, "Invoice change successful", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SelesaiPesananActivity.this, "Invoice change failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                };

                PesananSelesaiRequest selesaiRequest = new PesananSelesaiRequest(invoiceId + "", responseListener);
                RequestQueue queue = Volley.newRequestQueue(SelesaiPesananActivity.this);
                queue.add(selesaiRequest);
            }
        });

        findViewById(R.id.batal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(SelesaiPesananActivity.this, "Invoice change successful", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SelesaiPesananActivity.this, "Invoice change failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                PesananSelesaiRequest batalRequest = new PesananSelesaiRequest(invoiceId + "", responseListener);
                RequestQueue queue = Volley.newRequestQueue(SelesaiPesananActivity.this);
                queue.add(batalRequest);
            }
        });
    }
}