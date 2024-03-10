package com.blockchain.service.impl;

import com.blockchain.service.AddressMonitoringService;
import com.blockchain.utils.MyProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AddressMonitoringServiceImpl implements AddressMonitoringService {

    private static final Logger logger = LogManager.getLogger(AddressMonitoringServiceImpl.class);

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

    @Autowired
    private MyProperties myProperties;

    //用于记录当前查询的价格
    private Map<String,Double> priceInfo = new HashMap<>();

    //区块号
    private  Integer START_BLOCK = null;

    @Override
    public void startMonitoring() throws Exception {
        priceInfo = new HashMap<>();
        //是否设置代理
        if (ISPROXY == true){
            System.setProperty("http.proxyHost", "127.0.0.1");
            System.setProperty("http.proxyPort", proxyPort); // 你的代理端口
            System.setProperty("https.proxyHost", "127.0.0.1");
            System.setProperty("https.proxyPort", proxyPort); // 你的代理端口
        }

        if (START_BLOCK == null){
            START_BLOCK = getBlock();
        }


        StringBuilder info = new StringBuilder();

        for (String ADDRESS : ADDRESSList.split(";")) {
            //根据地址区块获取
            String urlString = "https://api.etherscan.io/api?module=account&action=tokentx&address=" + ADDRESS + "&startblock="+START_BLOCK+"&endblock=99999999&sort=asc&apikey=" + API_KEY;

            try {
                URL url = new URL(urlString);
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

                    // 如果交易失败，跳过这个交易
                    if (transaction.has("isError") && transaction.getString("isError").equals("1")) {
                        continue;
                    }

                    String hash = transaction.getString("hash"); // 交易哈希
                    Integer blockNumber = transaction.getInt("blockNumber"); // 区块

                    //如果非本人交易的跳过
                    if (!isSwap(hash)){
                        continue;
                    }

                    //更新区号
                    if (blockNumber>START_BLOCK){
                        START_BLOCK = blockNumber;
                    }


                    String timeStamp = transaction.getString("timeStamp"); // 交易时间

                    // 转换时间戳为日期
                    Date transactionDate = new Date(Long.parseLong(timeStamp) * 1000L);


                    String contractAddress = transaction.getString("contractAddress");
                    String toAddress = transaction.getString("to");
                    String tokenName = transaction.getString("tokenName");
                    String valueInWei = transaction.getString("value"); // 购买金额 (in Wei)
                    String gasUsed = transaction.getString("gas"); // Gas 用量
                    String gasPrice = transaction.getString("gasPrice"); // Gas 价格

                    int tokenDecimal = transaction.getInt("tokenDecimal"); // 代币的精度


                    // 转换购买金额为代币数量
                    BigDecimal valueInToken = new BigDecimal(valueInWei).divide(new BigDecimal(Math.pow(10, tokenDecimal)), MathContext.DECIMAL64);
                    valueInToken = valueInToken.setScale(4, RoundingMode.HALF_UP);

                    // 转换 Gas 费用为以太坊
                    BigDecimal gasInEth = new BigDecimal(gasUsed).multiply(new BigDecimal(gasPrice)).divide(new BigDecimal("1E18"));
                    gasInEth = gasInEth.setScale(4, RoundingMode.HALF_UP);

                    // 格式化交易日期
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT+8")); // 设置为中国时区
                    String formattedDate = sdf.format(transactionDate);

                    info.append("教育信息: \n");
                    info.append("乾包地址: " + toAddress+ "\n");
                    info.append("盒跃地址: " + contractAddress+ "\n");
                    info.append("呆必名称: " + tokenName+ "\n");
                    if (transaction.get("to").equals(ADDRESS)){
                        info.append("钩迈number: " + valueInToken + " " + tokenName+ "\n");
                    }else {
                        info.append("脉处Number: " + valueInToken + " " + tokenName+ "\n");
                    }

//                    try {
//                        info.append("GMZJ: " + getPrice(contractAddress,String.valueOf(valueInToken)) +"$ \n");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        info.append("GMZJ: 计算失败 \n");
//                    }

                    info.append("GasFY: " + gasInEth + " ETH"+ "\n");
                    info.append("Hash: " + hash+ "\n");
                    info.append("执行时间: " + formattedDate+ "\n");
                    info.append("\n");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!info.toString().equals("")){
            Map map = new HashMap<>();
            map.put("msg",info.toString());
            map.put("receiver","45199915897@chatroom");
            // 创建HttpHeaders实例，设置Content-Type为application/json
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 创建HttpEntity，将User对象和HttpHeaders传入
            HttpEntity<Map> request = new HttpEntity<>(map, headers);

            // 发送POST请求，将User对象转为JSON格式
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.exchange("http://103.101.205.46:9999/text", HttpMethod.POST, request, String.class);
        }

    }

    @Override
    public void startCoinMonitoring() throws Exception {
        priceInfo = new HashMap<>();
        if (ISPROXY == true){
            System.setProperty("http.proxyHost", "127.0.0.1");
            System.setProperty("http.proxyPort", proxyPort); // 你的代理端口
            System.setProperty("https.proxyHost", "127.0.0.1");
            System.setProperty("https.proxyPort", proxyPort); // 你的代理端口
        }

        StringBuilder info = new StringBuilder();

        for (String key : myProperties.startCoinMonitoringInfo.keySet()) {
            info.append(key + " = " + getPrice(myProperties.startCoinMonitoringInfo.get(key))+ "\uD83D\uDD2A" + "\n");
        }

        if (!info.toString().equals("")){
            Map map = new HashMap<>();
            map.put("msg",info.toString());
            map.put("receiver","45849287450@chatroom");
            // 创建HttpHeaders实例，设置Content-Type为application/json
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 创建HttpEntity，将User对象和HttpHeaders传入
            HttpEntity<Map> request = new HttpEntity<>(map, headers);

            // 发送POST请求，将User对象转为JSON格式
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.exchange("http://103.101.205.46:9999/text", HttpMethod.POST, request, String.class);
        }
    }

    public String getPrice(String contract,String nuber) throws Exception {

        if (!priceInfo.containsKey(contract)){
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
            priceInfo.put(contract,price);
            if (String.valueOf(totalValue).startsWith("0.")){
                return String.valueOf(totalValue);
            }else {
                return String.valueOf(Math.floor(totalValue));
            }
        }else {
            double totalValue = priceInfo.get(contract) * Double.valueOf(nuber);
            if (String.valueOf(totalValue).startsWith("0.")){
                return String.valueOf(totalValue);
            }else {
                return String.valueOf(Math.floor(totalValue));
            }
        }

    }

    public  Integer getBlock() throws Exception {
        if (ISPROXY == true){
            System.setProperty("http.proxyHost", "127.0.0.1");
            System.setProperty("http.proxyPort", proxyPort); // 你的代理端口
            System.setProperty("https.proxyHost", "127.0.0.1");
            System.setProperty("https.proxyPort", proxyPort); // 你的代理端口
        }

        String infuraUrl = "https://mainnet.infura.io/v3/"+infuraKey;
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
            if (transaction.getString("input").length() == 52 ){
                return true;
            }else {
                return false;
            }
        }
        return false;
    }

    public String getPrice(String contract) throws Exception {
        if (ISPROXY == true){
            System.setProperty("http.proxyHost", "127.0.0.1");
            System.setProperty("http.proxyPort", proxyPort); // 你的代理端口
            System.setProperty("https.proxyHost", "127.0.0.1");
            System.setProperty("https.proxyPort", proxyPort); // 你的代理端口
        }
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