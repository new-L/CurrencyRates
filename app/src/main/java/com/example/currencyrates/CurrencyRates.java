package com.example.currencyrates;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class CurrencyRates extends ListActivity {
    private final static String KEY_CHAR_CODE = "CharCode";
    private final static String KEY_VALUE = "Value";
    private final static String KEY_NOMINAL = "Nominal";
    private final static String KEY_NAME = "Name";
    ArrayList<Map<String, String>> list =
            new ArrayList<Map<String, String>>();
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        populate();
    }

    private void populate() {
        if (list == null) {
            new ProgressTask().execute(getString(R.string.rates_url));
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String[] from = {KEY_CHAR_CODE, KEY_VALUE, KEY_NOMINAL, KEY_NAME};
        int[] to = {R.id.charCodeView, R.id.valueView, R.id.nominalView, R.id.nameView};
        SimpleAdapter sa = new SimpleAdapter(this, list, R.layout.activity_currency_rates, from, to);
        setListAdapter(sa);
    }

    private class ProgressTask extends AsyncTask<String, Void, ArrayList<Map<String, String>>> {

        Map<String, String> m;

        @Override
        protected ArrayList<Map<String, String>> doInBackground(String... strings) {
            return getData(strings[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Map<String, String>> maps) {
            list = maps;
            Toast.makeText(getBaseContext(), "Данные загружены", Toast.LENGTH_SHORT).show();
        }

        private ArrayList<Map<String, String>> getData(String path) {
            list = new ArrayList<>();
            Map<String, String> m;
            try {
                URL url = new URL(path);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

                int responseCode = httpConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = httpConnection.getInputStream();
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();

                    Document dom = db.parse(in);
                    Element docElement = dom.getDocumentElement();
                    String date = docElement.getAttribute("Date");
                    setTitle(getTitle() + " на " + date);

                    NodeList nodeList = docElement.getElementsByTagName("Valute");

                    int count = nodeList.getLength();
                    if (nodeList != null & count > 0) ;
                    {
                        for (int i = 0; i < count; i++) {
                            Element entry = (Element) nodeList.item(i);
                            m = new HashMap<String, String>();

                            String charCode = entry
                                    .getElementsByTagName(KEY_CHAR_CODE)
                                    .item(0).getFirstChild().getNodeValue();

                            String value = entry
                                    .getElementsByTagName(KEY_VALUE)
                                    .item(0).getFirstChild().getNodeValue();

                            String nominal = "за " + entry
                                    .getElementsByTagName(KEY_NOMINAL)
                                    .item(0).getFirstChild().getNodeValue();

                            String name = entry
                                    .getElementsByTagName(KEY_NAME)
                                    .item(0).getFirstChild().getNodeValue();

                            m.put(KEY_CHAR_CODE, charCode);
                            m.put(KEY_VALUE, value);
                            m.put(KEY_NOMINAL, nominal);
                            m.put(KEY_NAME, name);

                            list.add(m);
                        }
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Данные загружены", Toast.LENGTH_SHORT).show();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            return list;
        }
    }
}

