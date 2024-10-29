package com.example.triptracker;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Scraper extends AsyncTask<Void, Void, Void> {
    private Context context;
    private RecyclerView.Adapter adapter;
    private boolean initFlag;
    private boolean existingItemFlag = Boolean.FALSE;
    private int existingItemPosition;
    private Database db = new Database();
    private double fuelPrice = 0.00;


    public Scraper(Context context, RecyclerView.Adapter adapter, boolean initFlag) {
        this.context = context;
        this.adapter = adapter;
        this.initFlag = initFlag;
    }

    public Scraper(){}

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        DataCalculations.setFuelPrice(fuelPrice);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        scrapePrice();

        return null;
    }
    private void scrapePrice() {
        try{
            Document document;
            String url = "https://bg.fuelo.net/brand/id/2";
            document = Jsoup.connect(url).get();


            Element table = document.select("table").get(0); //select the first table.
            Elements rows = table.select("tr");

            for (int i = 1; i < rows.size(); i++) { //first row is the col names so skip it.
                Element row = rows.get(i);
                Elements cols = row.select("td");
                try{
                    if(cols.text().contains("Super Diesel"))
                    {
                        fuelPrice = Double.parseDouble(cols.text().split(" ")[2].replace(",","."));
                        Log.d("SCRAPER", String.valueOf(fuelPrice));
                    }
                }
                catch (NullPointerException e){
                    Log.d("SCRAPER", "NULL POINTER ON ELEMENT");
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
