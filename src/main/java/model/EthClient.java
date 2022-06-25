package model;

import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.BatchRequest;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

public class EthClient {

    private final String web3Provider = "https://mainnet.infura.io/v3/3c3160e338cf4034b29214bc116ed09b";
                                        //https://main-light.eth.linkpool.io")
    private final Web3j client;
    private final String address;
    private final BigInteger iniBlock;

    public EthClient(String address, BigInteger iniBlock) throws Exception {

        if( ! WalletUtils.isValidAddress(address) )
            throw new Exception("Invalid address.");

        this.address = address;

        client = Web3j.build(
                new HttpService(web3Provider)
        );
        testConnection();

        if ( iniBlock.compareTo( BigInteger.ZERO ) >= 0   //If iniBlock is zero or greater and
                && iniBlock.compareTo( getLastBlockNumber() ) <= 0   //if lower or equal to the latest block
        ){
            this.iniBlock = iniBlock;
        }
        else
            throw new Exception("Invalid Block Number.");
    }

    private void testConnection() throws Exception {
        if( client.web3ClientVersion().send().getWeb3ClientVersion() == null)
            throwConnectionException();
    }

    private BigInteger getLastBlockNumber() throws Exception {
        BigInteger latest = null;
        try {
            latest =  client.ethBlockNumber().send().getBlockNumber();
        } catch (IOException e) {
            throwConnectionException();
        }
        return latest;
    }

    public String getTransactions() {

        try {
            BigInteger latest = client.ethBlockNumber().send().getBlockNumber();

            BatchRequest br = new BatchRequest(
                    new HttpService(web3Provider)
            );

            for(BigInteger i = iniBlock; i.compareTo(latest) < 0; i = i.add(BigInteger.ONE) ){
                // While i is lower than the latest block number

                br.add( client.ethGetBlockByNumber(
                        DefaultBlockParameter.valueOf( i ), true) );
            }

            ArrayList<EthBlock.TransactionObject> transactions = new ArrayList<>();

            // Search every transaction in every block
            br.send().getResponses().forEach( (elem) -> {
                EthBlock block = (EthBlock) elem;
                transactions.addAll( searchBlockTransactions(block.getBlock()) );
                }
            );

            transactions.forEach((tx) -> {
                System.out.println(tx.getHash());
            });

//            List<? extends Response<?>> blocks = br.send().getResponses();
//
//            for(Response<?> blk : blocks){
//                searchBlockTransactions(
//                        (EthBlock.Block) blk.getResult()
//                );
//            }

//            EthBlock.Block block = client.ethGetBlockByNumber(
//                    DefaultBlockParameter.valueOf(iniBlock), true
//            ).send().getBlock();
//
//            searchBlockTransactions( block );

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * This method will search all transactions in a block to see if any of them interacted with our address
     * @param block The block to lookup in
     * @return Array of transactions related to the address set. If none found, will return empty array
     */
    private ArrayList<EthBlock.TransactionObject> searchBlockTransactions(EthBlock.Block block) {

        ArrayList<EthBlock.TransactionObject> transactions = new ArrayList<>();

        for(EthBlock.TransactionResult transaction : block.getTransactions()){

            EthBlock.TransactionObject tx = (EthBlock.TransactionObject) transaction;

            if( (tx.getTo() != null && tx.getTo().equalsIgnoreCase(address))
                    || (tx.getFrom() != null && tx.getFrom().equalsIgnoreCase(address)) )
            {
                // getTo() returns null when it's a contract creation
                transactions.add( tx );
            }
        }
        return transactions;
    }

    private void throwConnectionException() throws Exception {
        throw new Exception("Unable to connect to Ethereum node. Please check internet connection.");
    }
}
