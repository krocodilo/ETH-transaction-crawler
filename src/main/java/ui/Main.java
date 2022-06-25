package ui;

import model.EthClient;

import java.math.BigInteger;

public class Main {

    public static void main(String[] args){

        EthClient client = null;
        try {
            client = new EthClient(
//                    "0x3DAd3389691Dd760a80480C44Ec6D48B3964C0D3",
                    "0xaa7a9ca87d3694b5755f213b5d04094b8d0f0a6f",
                    BigInteger.valueOf(15000000)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(
                client.getTransactions()
        );

        System.exit(0);
    }
}
