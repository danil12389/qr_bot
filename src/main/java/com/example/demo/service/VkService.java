package com.example.demo.service;

import com.example.demo.config.VkConfig;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class VkService {

    VkConfig vkConfig;

    public VkService(VkConfig vkConfig)  {
        this.vkConfig = vkConfig;
    }
    public void VkProcessor() throws ClientException, ApiException, InterruptedException {
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vkApiClient = new VkApiClient(transportClient);
        Random random = new Random();
        GroupActor actor = new GroupActor(vkConfig.getId(), vkConfig.getToken());

        Integer ts = vkApiClient.messages().getLongPollServer(actor).execute().getTs();

        while(true) {
            MessagesGetLongPollHistoryQuery historyQuery = vkApiClient.messages().getLongPollHistory(actor).ts(ts);
            List<Message> messages = historyQuery.execute().getMessages().getItems();
            if(!messages.isEmpty()){
                messages.forEach(message -> {
                    System.out.println(message.toString());
                    try{
                        if(message.getText().equals("Hi")) {
                            vkApiClient.messages().send(actor).message("ЧМОООО");
                        }
                    }
                    catch (RuntimeException e) {e.printStackTrace();};
                });
            }
            ts = vkApiClient.messages().getLongPollServer(actor).execute().getTs();
            Thread.sleep(500);
        }

    }

}
