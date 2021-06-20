package br.com.danilo;

import br.com.danilo.entities.VmEvent;
import br.com.danilo.enums.VmEventType;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import io.quarkus.funqy.Funq;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GreetingFunction {

    @Funq
    public String funqyHello(VmEvent event) {
        this.persistData(event.getName());
        return "hello " + event.getName();
    }



    private void persistData(String name){

        VmEvent vmEvent = new VmEvent(VmEventType.START,name,"ResourceGroupName");



        Map<String, String> credentials = getCosmosDbCredentials();
        CosmosClient cosmosClient = new CosmosClientBuilder()
                .endpoint(credentials.get("AccountEndpoint"))
                .key(credentials.get("AccountKey"))
                .buildClient();

        var cosmosContainer = cosmosClient
                .getDatabase("events-db")
                .getContainer("events");
        cosmosContainer.createItem(vmEvent);


    }

    private Map<String, String> getCosmosDbCredentials() {
        Map<String, String> credentials = new HashMap<>();

        String cosmosDbConnection = System.getenv("CosmosDbConnection");
        String[] elements = cosmosDbConnection.split(";");

        for (String element : elements) {
            String[] split = element.split("=");
            credentials.put(split[0], split[1]);
        }

        return credentials;
    }


}
