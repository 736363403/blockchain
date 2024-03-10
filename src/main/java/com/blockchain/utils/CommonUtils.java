package com.blockchain.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class CommonUtils {

    @Value("${API_KEY}")
    private String API_KEY;
    @Value("${ADDRESS}")
    private String ADDRESSList;
    @Value("${isProxy}")
    private boolean ISPROXY;
    @Value("${proxyPort}")
    private String proxyPort;

    @Value("${infuraKey}")
    private String infuraKey;

    public String getPrice(String contract,String nuber) throws Exception {
        // CoinGecko的API URL，用于获取代币价格
        String apiUrl = "https://api.coingecko.com/api/v3/simple/token_price/ethereum?contract_addresses="+contract+"&vs_currencies=usd";

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        // 关闭连接
        in.close();
        conn.disconnect();

        com.alibaba.fastjson2.JSONObject jsonObject = com.alibaba.fastjson2.JSONObject.parseObject(content.toString());

        double price = jsonObject.getJSONObject(contract).getDouble("usd");
        double totalValue = price * Double.valueOf(nuber);
        if (String.valueOf(totalValue).startsWith("0.")){
            return String.valueOf(totalValue);
        }else {
            return String.valueOf(Math.floor(totalValue));
        }
    }

    public static Integer getBlock() throws Exception {
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "7890"); // 你的代理端口
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "7890"); // 你的代理端口

        String infuraUrl = "https://mainnet.infura.io/v3/2652637c19e7472ab3a279fe26225151";
        Web3j web3j = Web3j.build(new HttpService(infuraUrl));
        EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send();
        return Integer.valueOf(String.valueOf(ethBlock.getBlock().getNumber()));
    }

    public boolean isSwap(String hash) throws IOException {
        URL url = new URL("https://api.etherscan.io/api?module=proxy&action=eth_getTransactionByHash&txhash="+hash+"&apikey="+API_KEY);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        // 关闭连接
        in.close();
        conn.disconnect();

        // 解析 JSON 结果
        JSONObject jsonObject = new JSONObject(content.toString());
        JSONArray transactions = jsonObject.getJSONArray("result");

        for (int i = 0; i < transactions.length(); i++) {
            JSONObject transaction = transactions.getJSONObject(i);
            if (transaction.getString("input").length() == 394){
                return true;
            }else {
                return false;
            }
        }
        return false;
    }

    public String getPrice(String contract) throws Exception {
        // CoinGecko的API URL，用于获取代币价格
        String apiUrl = "https://api.coingecko.com/api/v3/simple/token_price/ethereum?contract_addresses="+contract+"&vs_currencies=usd";

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        // 关闭连接
        in.close();
        conn.disconnect();

        com.alibaba.fastjson2.JSONObject jsonObject = com.alibaba.fastjson2.JSONObject.parseObject(content.toString());

        double price = jsonObject.getJSONObject(contract).getDouble("usd");
        if (String.valueOf(price).startsWith("0.")){
            return String.format("%.3f", price);
        }else {
            return String.valueOf(price);
        }

    }

}
