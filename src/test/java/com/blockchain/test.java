package com.blockchain;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

public class test {

    public static void main(String[] args) throws Exception {
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "7890"); // 你的代理端口
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "7890"); // 你的代理端口
        String[] ADDRESSES = {"0x424bc7e30aed4ca7d523683956c659e59eecf2a1"};
        OkHttpClient client = new OkHttpClient();
//        String block = getBlock();
        String block = "19059623";
        System.out.println(block);
        for (String address : ADDRESSES) {
            String url = "https://api.etherscan.io/api?module=account&action=txlist&address=" + address +
                    "&startblock=" + block + "&endblock=99999999&sort=asc&apikey=" + "D1QMETV9TRWPXBSXEBHYRUGC31MX1TNRUQ";

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

    public static String getBlock() throws Exception {
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "7890"); // 你的代理端口
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "7890"); // 你的代理端口

        String infuraUrl = "https://mainnet.infura.io/v3/2652637c19e7472ab3a279fe26225151";
        Web3j web3j = Web3j.build(new HttpService(infuraUrl));
        EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send();
        return String.valueOf(ethBlock.getBlock().getNumber());
    }
}
