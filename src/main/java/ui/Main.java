package ui;

import model.EthClient;

import java.math.BigInteger;

public class Main {

    public static void main(String[] args){

        EthClient client = null;
        try {
            client = new EthClient(
//                    "0x3DAd3389691Dd760a80480C44Ec6D48B3964C0D3",
                    "0xbe96c45e0747fb0b5069b28ad1c4cad8e0ff37ac",
                    BigInteger.valueOf(
                            15025000)
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
