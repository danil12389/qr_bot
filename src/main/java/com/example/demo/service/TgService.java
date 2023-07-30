package com.example.demo.service;

import com.example.demo.config.TgConfig;
import com.example.demo.model.GetFileEntity;
import com.example.demo.model.Image;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.InputStream;
import java.util.Date;

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
                String file_path = imageUriDownloader(update.getMessage().getPhoto().get(0).getFileId()).getFile_path();
                imageDownloader(file_path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            File f = new File("qr_images/Sun Jul 30 22:49:35 SAMT 2023");

            try {
                System.out.println(qrService.decodeQRCode(f));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            long chatId = update.getMessage().getChatId();
            if (update.getMessage().hasPhoto()) {
                String file_id = update.getMessage().getPhoto().get(0).getFileId();
                switch (messageText) {
                    case ("/la"):
                        sendMessage(chatId, String.valueOf(file_id.length()));
                }

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

    private void imageDownloader(String file_path) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        System.out.println("URL: " + "https://api.telegram.org/file/bot" + tgConfig.getToken()+ "/" + file_path);
        Request request = new Request.Builder()
                .url("https://api.telegram.org/file/bot" + tgConfig.getToken()+ "/"   + file_path)
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        saveImage(response);
    }

    public String saveImage(Response response) throws IOException {
        InputStream inputStream = response.body().byteStream();
        String name = new Date().toString();
        File file = new File("qr_images/" + name);
        BufferedImage image = ImageIO.read(inputStream);
        ImageIO.write(image, "jpg", file);
        return name;
    }
}
