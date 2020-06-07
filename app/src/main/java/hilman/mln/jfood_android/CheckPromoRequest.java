package hilman.mln.jfood_android;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CheckPromoRequest extends StringRequest {

    private static String URL = "http://192.168.43.236:8080/promo";
    private Map<String, String> params;

    public CheckPromoRequest(Response.Listener<String> listener) {
        super(Method.GET, URL, listener,null);
        params = new HashMap<>();
        /*params.put("foodIdList", foodIdList);
        params.put("customerId", customerId);
        params.put("promoCode", promoCode);*/
    }

    @Override
    public Map<String, String> getParams(){
        return params;
    }
}
