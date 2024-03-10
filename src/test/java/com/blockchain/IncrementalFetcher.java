package com.blockchain;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;

public class IncrementalFetcher {

    public static void main(String[] args) {
        // 使用你的Infura项目的URL替换下面的字符串
        String infuraUrl = "https://mainnet.infura.io/v3/YOUR-PROJECT-ID";

        // 创建一个新的web3j实例来连接到远程节点
        Web3j web3j = Web3j.build(new HttpService(infuraUrl));

        try {
            // 请求最新的区块
            EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send();

            // 输出最新区块的信息
            System.out.println("Latest block: " + ethBlock.getBlock());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
