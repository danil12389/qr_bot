package com.example.demo.controller;

import com.example.demo.service.QrService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;


@RestController
public class QrController {

    QrService qrService;

    public QrController(QrService qrService) {
        this.qrService = qrService;
    }

    @GetMapping("/qr")
    public String imageAccepter() throws IOException {
        File file = new File("qr.png");

        return qrService.decodeQRCode(file);
    }
}
