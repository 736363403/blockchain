package com.blockchain.start;//package com.blockchain.start;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.web3j.protocol.Web3j;
//import org.web3j.protocol.core.DefaultBlockParameterName;
//import org.web3j.protocol.core.methods.request.EthFilter;
//import org.web3j.protocol.core.methods.response.Log;
//import org.web3j.protocol.http.HttpService;
//import org.web3j.utils.Convert;
//
//import java.math.BigInteger;
//
//@Component
//public class startMonitoring implements CommandLineRunner {
//    private static final Logger logger = LogManager.getLogger(startMonitoring.class);
//
//    private static final String INFURA_URL = "https://mainnet.infura.io/v3/2652637c19e7472ab3a279fe26225151";
//
//    private static final String[] ADDRESSES_TO_WATCH = {
//            "0x1af331dc34dd7c5c62af28b5685328318b61888a",
//            "0x30615450f0de4188f9635dd3cf1f5648edf638b8",
//            "0x97b8dc4683514ae076490001fa39a02602ae97a0",
//            "0xe9ed3ad8e68b3925a33cab867a29c73e8357cfc4",
//            "0x500e91aca8cfe3e541aa47fafcd85a65bcb860f8",
//            "0x81b2717ef6bd449912ba112ebd6ec3bcdce92199",
//            "0x3e57efef507b4db7acfa2ee79ceca6b19e18d106",
//            "0x6fb99fc659aa96950c966114c3fe010b78aae530",
//            "0xeb89055e16ae1c1e42ad6770a7344ff5c7b4f31d"
//    };
//
//    @Override
//    public void run(String... args) throws Exception {
//        Web3j web3j = Web3j.build(new HttpService(INFURA_URL));
//        logger.info("开始监控");
//        for (String addressToWatch : ADDRESSES_TO_WATCH) {
//            EthFilter filter = new EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, addressToWatch);
//            web3j.ethLogFlowable(filter).subscribe(logResult -> {
//                handleLog(logResult);
//            });
//        }
//    }
//
//    private static void handleLog(Log log) {
//        String address = log.getAddress();
//        String data = log.getData();
//        BigInteger value = Convert.fromWei(data, Convert.Unit.ETHER).toBigIntegerExact();
//        logger.info("Transaction involving watched address: " + address + ", value: " + value);
//        logger.info("loa对象TOsTRING"+log.toString());
//    }
//}
