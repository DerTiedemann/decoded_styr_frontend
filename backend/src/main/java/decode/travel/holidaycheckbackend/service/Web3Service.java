package decode.travel.holidaycheckbackend.service;

import java.io.IOException;
import java.math.BigInteger;

import decode.travel.holidaycheckbackend.model.contract.NFT721;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;



@Service
public class Web3Service {

    private final static int  POLLING_MAX_ATTEMPTS  = 60;
    private final static long POLLING_MAX_WAIT_TIME = 1000;


    @Value("${network.chainUrl}")
    private String            chainUrl="https://columbus.camino.network/ext/bc/C/rpc";

    @Value("${network.chainId}")
    private long              chainId = 501;

    @Value("${smartContract.walletPrivateKey}")
    private String            walletPrivateKey = "30fad50f16999ca215e7c98fe9ad9c0f54c071cec401c953c8bb447883d738dc";

    @Value("${smartContract.contractAddress}")
    private String            contractAddress ="0x323fdE68785C3b7A97c3ba9FaE2FaE9F084d303f";

    private Web3j             web3j;


    public NFT721 loadSmartContract() {
        var web3j = getWeb3j();
        var credentials = Credentials.create(walletPrivateKey);
        var transactionManager = new RawTransactionManager(web3j, credentials, chainId, POLLING_MAX_ATTEMPTS, POLLING_MAX_WAIT_TIME);
        var contract = NFT721.load(contractAddress, web3j, transactionManager, new DynamicGasProvider(web3j));
        return contract;
    }


    public Web3j getWeb3j() {
        if (web3j == null) {
            web3j = Web3j.build(new HttpService(chainUrl));
        }

        return web3j;
    }


    private static class DynamicGasProvider implements ContractGasProvider {

        private final static Logger logger   = LoggerFactory.getLogger(DynamicGasProvider.class);

        private Web3j               web3j;
        private BigInteger          gasLimit = BigInteger.valueOf(8_000_000);


        public DynamicGasProvider(Web3j web3j) {
            this.web3j = web3j;
        }


        @Override
        public BigInteger getGasPrice(String contractFunc) {
            return getGasPrice();
        }


        @Override
        public BigInteger getGasPrice() {
            try {
                var gasPrice = web3j.ethGasPrice().send();
                return gasPrice.getGasPrice();
            }
            catch (IOException e) {
                logger.error("Could not get gas price from network.", e);
                return null;
            }
        }


        @Override
        public BigInteger getGasLimit(String contractFunc) {
            return getGasLimit();
        }


        @Override
        public BigInteger getGasLimit() {
            return gasLimit;
        }
    }
}

