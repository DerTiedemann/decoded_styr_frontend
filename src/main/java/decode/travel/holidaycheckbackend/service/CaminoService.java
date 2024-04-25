package decode.travel.holidaycheckbackend.service;


import com.google.gson.Gson;
import decode.travel.holidaycheckbackend.cache.InMemoryCacheMap;
import decode.travel.holidaycheckbackend.model.Code;
import decode.travel.holidaycheckbackend.model.KycResponse;
import decode.travel.holidaycheckbackend.model.NFTInfo;
import decode.travel.holidaycheckbackend.model.contract.NFT721;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class CaminoService {
    @Value("${camino.kyc.server}")
    private  String host;
    @Value("${camino.kyc.path}")
    private String path;
    @Autowired
    private Web3Service web3Service;

    public boolean isKyc(String key) throws IOException {
        String uri = host+path+key;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        KycResponse kycResponse;
        try {
            HttpEntity entity1 = response.getEntity();
            String responseBody = EntityUtils.toString(response.getEntity());
            Gson gson = new Gson();
            kycResponse = gson.fromJson(responseBody, KycResponse.class);
            EntityUtils.consume(entity1);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } finally {
            response.close();
        }
        if( kycResponse != null && kycResponse.getVariants() != null && kycResponse.getVariants().isKyc_basic())
            return true;
        return false;
    }


    public NFTInfo createNFT(String to) throws Exception {
        var mintId = UUID.randomUUID().toString().replace("-","");
        BigInteger big = new BigInteger(mintId, 16);
        Web3Service w = new Web3Service();

        NFT721 nft721 = w.loadSmartContract();
        RemoteFunctionCall<TransactionReceipt> mintResponse = nft721.mint(to, big);
        TransactionReceipt response = mintResponse.send();
        return NFTInfo.builder().nftId(big.toString()).tx(response.getTransactionHash()).build();
    }
}
