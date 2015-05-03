package com.example.anthony.webapitutorial;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.json.parsers.*;


public class MainActivity extends Activity implements OnClickListener {
    public static int NUM_ENTRIES = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.my_button).setOnClickListener(this);
        new LongRunningGetIO().execute();
    }

    @Override
    public void onClick(View arg0) {
        Button b = (Button)findViewById(R.id.my_button);
        b.setClickable(false);
        b.setText("Help Requested");
        NeximoFunctions neximoFunctions = new NeximoFunctions();
        neximoFunctions.notifyAssociate("shoes");
    }

    private class LongRunningGetIO extends AsyncTask <Void, Void, String> {
        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
            InputStream in = entity.getContent();
            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n>0) {
                byte[] b = new byte[4096];
                n =  in.read(b);
                if (n>0) out.append(new String(b, 0, n));
            }
            return out.toString();
        }
        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();

            /* initialize the request object with the http request, retrieve 3 articles */
            HttpGet httpGet = new HttpGet("https://api.zalando.com/articles?category=womens-shoes&page=1&pageSize=3");
            final HttpParams httpParams = new BasicHttpParams();

            /* set timeout to 3 seconds because Zalando never closes the http connection */
            HttpConnectionParams.setConnectionTimeout(httpParams, 2000);

            httpClient = new DefaultHttpClient(httpParams);
            String text = null;
            try {
                /* make the http request */
                HttpResponse response = httpClient.execute(httpGet, localContext);
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);
            }
            catch (Exception e) {
                return e.getLocalizedMessage();
            }
            return text;
        }
        protected void onPostExecute(String results) {
            if (results!=null) {
                /*
                EditText et = (EditText)findViewById(R.id.my_edit);
                et.setText(results);
                */

                /* results is the HTTP response, send it through the JSON processor */
                JsonParserFactory factory=JsonParserFactory.getInstance();
                com.json.parsers.JSONParser parser=factory.newJsonParser();
                Map jsonData=parser.parseJson(results);

                /* Retrive info on 3 products */
                List content= (List)jsonData.get("content"); // <--
                String titles[] = new String[NUM_ENTRIES];
                String prices[] = new String[NUM_ENTRIES];
                String images[] = new String[NUM_ENTRIES];

                for (int i = 0; i < NUM_ENTRIES; i++) {
                    /* Get Titles */
                    Map content_inner = (Map) content.get(i);
                    titles[i] = (String) content_inner.get("name");

                    /* Get Prices */
                    List units = (List) content_inner.get("units");
                    Map units_inner = (Map) units.get(0);
                    Map price = (Map) units_inner.get("price");
                    prices[i] = (String) price.get("formatted");

                    /* Get Images */
                    Map media = (Map) content_inner.get("media");
                    List image_list = (List) media.get("images");
                    Map image_map = (Map) image_list.get(0);
                    images[i] = (String) image_map.get("smallUrl");

                }

                TextView tv1 = (TextView)findViewById(R.id.title);
                tv1.setText(titles[1]);

                TextView tv2 = (TextView)findViewById(R.id.price);
                tv2.setText(prices[1]);

                /*
                EditText et2 = (EditText)findViewById(R.id.title);
                et2.setText(titles[0]);

                EditText et3 = (EditText)findViewById(R.id.price);
                et3.setText(prices[0]);
                */

                WebView myWebView = (WebView) findViewById(R.id.image);
                myWebView.loadUrl(images[0]);

            }
            Button b = (Button)findViewById(R.id.my_button);
            b.setClickable(true);
        }
    }
}

