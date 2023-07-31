package com.example.demo.service;

import com.example.demo.config.TgConfig;
import com.example.demo.model.GetFileEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

@Component
@Log4j2
public class TgService extends TelegramLongPollingBot {

    private final TgConfig tgConfig;
    private QrService qrService;


    public TgService(TgConfig tgConfig, QrService qrService) {
        this.tgConfig = tgConfig;
        this.qrService = qrService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("New update received, file_id url: " + update.getMessage().getPhoto().get(0).getFilePath()
        + " " + update.getMessage().getPhoto().get(0).getFileId());
        if (update.hasMessage()) {
            System.out.println("New update received, message text: " + update.getMessage().getText());
            String messageText = update.getMessage().getCaption();
            try {
                String file_path = imageUriDownloader(update.getMessage().getPhoto().get(update.getMessage().getPhoto().size() - 1).getFileId()).getFile_path();
                System.out.println("Размер PhotoSize: " +update.getMessage().getPhoto().size());
                String name = imageSaverAndDownloader(file_path);
                File f = new File(name);
                BufferedImage i;
                i = ImageIO.read(f);
                String paymentInfo = qrService.decodeQRCode(i);
                String[] s = paymentInfo.split("&");
                Double d = Double.valueOf(s[1].substring(2));
                System.out.println("Массив: "  + d);
                System.out.println("PaymentInfo: " + paymentInfo);
                long chatId = update.getMessage().getChatId();
                if (update.getMessage().hasPhoto()) {
                    if (messageText.equals("/2")) {
                        sendMessage(chatId, "Каждый скидывает по " + String.valueOf(d/2));
                    }else if(messageText.equals("/3")){
                        sendMessage(chatId, "Каждый скидывает по " + String.valueOf(d/3));
                    }else if(messageText.equals("/4")){
                        sendMessage(chatId, "Каждый скидывает по " + String.valueOf(d/4));
                    }else if(messageText.equals("/5")){
                        sendMessage(chatId, "Каждый скидывает по " + String.valueOf(d/5));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }



        }
    }
    @Override
    public String getBotUsername() {
        return tgConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return tgConfig.getToken();
    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }

    }

    private GetFileEntity imageUriDownloader(String file_id) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        System.out.println("URL: " + "https://api.telegram.org/bot" + tgConfig.getToken() + "/getFile?file_id="+file_id );
        Request request = new Request.Builder()
                .url("https://api.telegram.org/bot" + tgConfig.getToken()+"/getFile?file_id="+file_id)
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        GetFileEntity entity = new ObjectMapper()
                .readerFor(GetFileEntity.class)
                .readValue(response.body().string());
        return entity;
    }

    private String imageSaverAndDownloader(String file_path) throws IOException {
        System.out.println("URL: " + "https://api.telegram.org/file/bot" + tgConfig.getToken()+ "/" + file_path);
        String name = "qr_images/"+ new Date() + ".png";
        Files.write(Path.of(name), new URL("https://api.telegram.org/file/bot" + tgConfig.getToken()+ "/"   + file_path).openStream().readAllBytes());
        return name;
    }

}
