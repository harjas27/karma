import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Karma {

    public static void main(String args[]) throws IOException, InterruptedException {

        String stakeAddress = "0x00000000219ab540356cbb839cbe05303d7705fa";
        String []amms = {"0x7a250d5630B4cF539739dF2C5dAcb4c659F2488D", "0x3e66b66fd1d0b02fda6c811da9e0547970db2f21", "0x1111111254fb6c44bAC0beD2854e76F90643097d"};
        String []ammsNames = {"uniswap", "balancer", "1inch"};
        String []aggregators = {"0x7d2768dE32b0b80b7a3454c06BdAc94A69DDc7A9", "0x3d9819210A31b4961b30EF54bE2aeD79B9c9Cd3B"};
        String []aggregatorsNames = {"aave", "compound"};
        String []derivativeProtocols = {"0x5e74C9036fb86BD7eCdcb084a0673EFc32eA31cb", "0x8014595F2AB54cD7c604B00E9fb932176fDc86Ae"};
        String []derivativeProtocolsNames = {"synthetix", "convex"};
        String []topDaos = {"0x0a3f6849f78076aefaDf113F5BED87720274dDC0", "0x4cd36d6F32586177e36179a810595a33163a20BF"};
        String []centralisedExchanges = {"0x3f5CE5FBFe3E9af3971dD833D26bA9b5C936f0bE", "0x92D6C1e31e14520e676a687F0a93788B716BEff5", "0x92D6C1e31e14520e676a687F0a93788B716BEff5", "0xDFd5293D8e347dFe59E90eFd55b2956a1343963d"};
        String gnosisMultiSig = "0x7AE109A63ff4DC852e063a673b40BED85D22E585";
        String []l2Bridges = {"0xA0c68C638235ee32657e8f720a23ceC1bFc77C77", "0xaBEA9132b05A70803a4E85094fD0e1800777fBEF"};
        String []ensContract = {"0x084b1c3C81545d370f3634392De611CaaBFf8148", "0xa5409ec958C83C3f309868babACA7c86DCB077c1"};
        String []publicGoodContracts = {"0x7d655c57f71464B6f83811C55D84009Cd9f5221C"};

        String []protocols = {"uniswap", "balancer", "1inch", "aave", "compound", "synthetix", "convex"};
        String []protocolsGecko = {"uniswap", "balancer", "1inch", "aave", "compound-coin", "havven"};
        double[] maxPrice = getMaxPrices(protocolsGecko);

        HttpClient client = HttpClient.newHttpClient();

        boolean processComplete = false;
        int currPage = 0;

        int transactionCount = 0;
        int sentTx = 0;
        int recvTx = 0;
        int stakeTx = 0;
        int ammTx = 0;
        int aggregatorTx = 0;
        int derivativeProtoTx = 0;
        int exchangeTx = 0;
        int daoTx = 0;
        int multisigTx = 0;
        int l2Tx = 0;
        int ensTx = 0;
        int publicGoodsTx = 0;

        String address;
        int txCount = 0;
        int hodler = 0;
        int superStaker = 0;
        int ammPlayer = 0;
        int serialAggregator = 0;
        int derivativeGuy = 0;
        int goodDAOMan = 0;
        int ENSOwner = 0;
        int proposalMan = 0;
        int OffChainDumper = 0;
        int l2User = 0;
        int publicGoods = 0;


        try(InputStream in=Thread.currentThread().getContextClassLoader().getResourceAsStream("factors.json")){
           ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readValue(in, JsonNode.class);
            address = jsonNode.get("address").asText();
            txCount = jsonNode.get("txCount").asInt();
            hodler = jsonNode.get("hodler").asInt();
            superStaker = jsonNode.get("superStaker").asInt();
            ammPlayer = jsonNode.get("ammPlayer").asInt();
            serialAggregator = jsonNode.get("serialAggregator").asInt();
            derivativeGuy = jsonNode.get("derivativeGuy").asInt();
            goodDAOMan = jsonNode.get("goodDAOMan").asInt();
            ENSOwner = jsonNode.get("ENSOwner").asInt();
            proposalMan = jsonNode.get("proposalMan").asInt();
            OffChainDumper = jsonNode.get("OffChainDumper").asInt();
            l2User = jsonNode.get("l2User").asInt();
            publicGoods = jsonNode.get("publicGoods").asInt();
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        System.out.println("Address -> " + address);

        String tvlEndpoint = "https://api.llama.fi/tvl/";
        String txEndpoint = "https://api.covalenthq.com/v1/1/address/" + address + "/transactions_v2/?quote-currency=USD&format=JSON&block-signed-at-asc=false&no-logs=false&page-number=";


        while (!processComplete) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(txEndpoint + currPage + "&key=ckey_a9b021f03d834bf5849430509b4"))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());


            JsonElement raw = JsonParser.parseString(response.body());
            //System.out.println(raw);

            JsonObject data = raw.getAsJsonObject().getAsJsonObject("data");
            //System.out.println(data);

            JsonArray txs = data.getAsJsonArray("items");
           // System.out.println(txs.size());

            //String address = data.get("address").getAsString();
            //System.out.println(address);

            transactionCount += txs.size();

            for (JsonElement tx : txs) {
                try {
                    JsonObject txobj = tx.getAsJsonObject();
                    String from = txobj.get("from_address").getAsString();
                    String to = txobj.get("to_address").getAsString();
                    String dateStr = txobj.get("block_signed_at").getAsString();
                    String date = convertDate(dateStr.split("T")[0]);

                    if (from.equalsIgnoreCase(address)) {
                        sentTx++;
                    }

                    if (to.equalsIgnoreCase(address)) {
                        recvTx++;
                    }

                    if (to.equalsIgnoreCase(stakeAddress)) {
                        stakeTx++;
                    }

                    if (to.equalsIgnoreCase(gnosisMultiSig)) {
                        multisigTx++;
                    }

                    int i = 0;
                    int protocolIndex = 0;
                    for (String adr : amms) {
                        if (to.equalsIgnoreCase(adr)) {
                            ammTx += getPriceFactor(protocols[protocolIndex], date, maxPrice[protocolIndex]);
                            protocolIndex++;
                            i++;
                        }
                    }

                    i = 0;
                    for (String adr : aggregators) {
                        if (to.equalsIgnoreCase(adr)) {
                            aggregatorTx += getPriceFactor(protocols[protocolIndex], date, maxPrice[protocolIndex]);
                            protocolIndex++;
                            i++;
                        }
                    }

                    i = 0;
                    for (String adr : derivativeProtocols) {
                        if (to.equalsIgnoreCase(adr)) {
                            derivativeProtoTx += getPriceFactor(protocols[protocolIndex], date, maxPrice[protocolIndex]);
                            protocolIndex++;
                            i++;
                        }
                    }

                    for (String adr : topDaos) {
                        if (to.equalsIgnoreCase(adr)) {
                            daoTx++;
                        }
                    }

                    for (String adr : centralisedExchanges) {
                        if (to.equalsIgnoreCase(adr)) {
                            exchangeTx++;
                        }
                    }

                    for (String adr : l2Bridges) {
                        if (to.equalsIgnoreCase(adr)) {
                            l2Tx++;
                        }
                    }

                    for (String adr : ensContract) {
                        if (to.equalsIgnoreCase(adr)) {
                            ensTx++;
                        }
                    }

                    for (String adr : publicGoodContracts) {
                        if (to.equalsIgnoreCase(adr)) {
                            publicGoodsTx++;
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Ignoring tx");
                }

            }

            JsonObject paginationDetails = data.getAsJsonObject("pagination");
            String hasMore = paginationDetails.get("has_more").getAsString();
            if (hasMore.equalsIgnoreCase("false")) {
                processComplete = true;
            } else {
                currPage++;
            }

        }
        System.out.println("Total Tx Count: " + transactionCount);
        System.out.println("Sent tx: " + sentTx);
        System.out.println("Received Tx: " + recvTx);
        System.out.println("Staking Tx: " + stakeTx);
        System.out.println("AMM Tx: " + ammTx);
        System.out.println("Aggregator Tx: " + aggregatorTx);
        System.out.println("Derivative Tx: " + derivativeProtoTx);
        System.out.println("Dao Tx: " + daoTx);
        System.out.println("Multisig Tx: " + multisigTx);
        System.out.println("Offchain Tx: " + exchangeTx);
        System.out.println("Layer 2 Tx: " + l2Tx);
        System.out.println("ENS Tx: " + ensTx);
        System.out.println("Public Goods Tx: " + ensTx);

        int karma = 0;
        karma = karma + (transactionCount * txCount);
        karma = karma + (stakeTx * superStaker);
        karma = karma + (ammTx * ammPlayer);
        karma = karma + (aggregatorTx * serialAggregator);
        karma = karma + (derivativeProtoTx * derivativeGuy);
        karma = karma + (daoTx * goodDAOMan);
        karma = karma - (exchangeTx * OffChainDumper);
        karma = karma + (multisigTx * proposalMan);
        karma = karma + (l2Tx * l2User);
        karma = karma + (ensTx * ENSOwner);
        karma = karma + (publicGoodsTx * publicGoods);

        System.out.println("Karma: " + karma);
    }

    public static double[] getMaxPrices(String [] protocols) throws IOException, InterruptedException {
        double []maxPrices = new double[protocols.length + 1];
        HttpClient client = HttpClient.newHttpClient();
        int i = 0;
        for (String protocol : protocols) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.coingecko.com/api/v3/coins/" + protocol + "/history?date=15-02-2021"))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonElement raw = JsonParser.parseString(response.body());
            //System.out.println(raw);
            maxPrices[i] = raw.getAsJsonObject().get("market_data").getAsJsonObject().get("current_price").getAsJsonObject().get("usd").getAsDouble();
            i++;
        }
        maxPrices[i] = 49;
        return maxPrices;
    }

    public static int getPriceFactor(String protocol, String date, double maxPrice) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.coingecko.com/api/v3/coins/" + protocol + "/history?date=" + date))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonElement raw = JsonParser.parseString(response.body());
        double currPrice = raw.getAsJsonObject().get("market_data").getAsJsonObject().get("current_price").getAsJsonObject().get("usd").getAsDouble();
        return (int) (maxPrice / currPrice);
    }

    public static String convertDate(String origDate) {
        String[] origDateComp = origDate.split("-");
        return origDateComp[2] + "-" + origDateComp[1] + "-" + origDateComp[0];
    }

}
