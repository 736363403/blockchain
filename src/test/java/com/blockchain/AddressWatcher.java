package com.blockchain;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class AddressWatcher {

    private static final String ETHERSCAN_API_KEY = "Your Etherscan API Key";
    private static final String[] ADDRESSES = {"0x...", "0x...", "0x..."};  // Replace with your wallet addresses
    private static final long START_BLOCK = 1234567;  // Replace with your start block

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();

        for (String address : ADDRESSES) {
            String url = "https://api.etherscan.io/api?module=account&action=txlist&address=" + address +
                    "&startblock=" + START_BLOCK + "&endblock=99999999&sort=asc&apikey=" + ETHERSCAN_API_KEY;

            Request request = new Request.Builder().url(url).build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                JSONArray transactions = jsonObject.getJSONArray("result");

                for (int i = 0; i < transactions.length(); i++) {
                    JSONObject transaction = transactions.getJSONObject(i);

                    String hash = transaction.getString("hash");
                    String from = transaction.getString("from");
                    String to = transaction.getString("to");
                    String value = transaction.getString("value");
                    String timeStamp = transaction.getString("timeStamp");

                    System.out.println("Transaction hash: " + hash);
                    System.out.println("From: " + from);
                    System.out.println("To: " + to);
                    System.out.println("Value: " + value);
                    System.out.println("Timestamp: " + timeStamp);
                    System.out.println("-----");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
