package com.example.weight_inspection.services;

import com.example.weight_inspection.models.Notification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class NotificationPreparationService {


    public Notification missingProductNotification() {
        Notification notification = new Notification();
        String subject = "Prvotné váženie produktu";
        String text = "Dobrý deň.\n\n" +
                "Zamestnanec na sklade sa pokúšal vážiť nový produkt, " +
                "ktorý sa nenachádza v databáze, pridaje prosím nový produkt do databázy.\n\n" +
                "Podrobné informácie z daného váženia:\n\n";
        notification.setType(subject);
        notification.setDescription(text);
        notification.setCreatedOn(new Timestamp(System.currentTimeMillis()));

        return notification;
    }

    public  Notification missingPaletteNotification() {
        Notification notification = new Notification();
        String subject = "Je potrebné pridať novú paletu";
        String text = "Dobrý deň.\n\n" +
                "Zamestnanec na sklade žiada o pridanie novej palety.\n\n" +
                "Podrobné informácie z daného váženia:\n\n";
        notification.setType(subject);
        notification.setDescription(text);
        notification.setCreatedOn(new Timestamp(System.currentTimeMillis()));

        return notification;
    }

    public Notification missingProductPaletteRelationshipNotification() {
        Notification notification = new Notification();

        String subject = "Chýba vzťah produkt-paleta";
        String text = "Dobrý deň.\n\n" +
                "Zamestnanec na sklade žiada o pridanie novej palety k produktu.\n\n" +
                "Podrobné informácie z daného váženia:\n\n";
        notification.setType(subject);
        notification.setDescription(text);
        notification.setCreatedOn(new Timestamp(System.currentTimeMillis()));

        return notification;
    }
    public Notification missingPaletteWeightNotification() {
        Notification notification = new Notification();
        String subject = "Daný typ palety nebol ešte navážený";
        notification.setType(subject);
        String text = "Dobrý deň.\n\n" +
                "Je potrebné navážiť danú paletu.\n\n" +
                "Podrobné informácie z daného váženia:\n\n";
        notification.setDescription(text);
        notification.setCreatedOn(new Timestamp(System.currentTimeMillis()));

        return notification;
    }
    public Notification missingPackagingNotification() {
        Notification notification = new Notification();
        String subject = "Je potrebné pridať nový obal";
        notification.setType(subject);
        String text = "Dobrý deň.\n\n" +
                "Zamestnanec na sklade žiada o pridanie nového obalu.\n\n" +
                "Podrobné informácie z daného váženia:\n\n";
        notification.setDescription(text);
        notification.setCreatedOn(new Timestamp(System.currentTimeMillis()));

        return notification;
    }
    public Notification missingPackagingWeightNotification() {
        Notification notification = new Notification();
        String subject = "Daný obal nebol ešte navážený";
        notification.setType(subject);
        String text = "Dobrý deň.\n\n" +
                "Je potrebné navážiť daný obal.\n\n" +
                "Podrobné informácie z daného váženia:\n\n";
        notification.setDescription(text);
        notification.setCreatedOn(new Timestamp(System.currentTimeMillis()));

        return notification;

    }

    public Notification missingProductPackagingRelationshipNotification() {
        Notification notification = new Notification();
        String subject = "Chýba vzťah produkt-obal";
        notification.setType(subject);
        String text = "Dobrý deň.\n\n" +
                "Zamestnanec na sklade žiada o pridanie nového obalu k produktu.\n\n" +
                "Podrobné informácie z daného váženia:\n\n";
        notification.setDescription(text);
        notification.setCreatedOn(new Timestamp(System.currentTimeMillis()));

        return notification;

    }

    public Notification incorrectWeighingNotification() {
        Notification notification = new Notification();
        String subject = "Chyba váženia";
        notification.setType(subject);
        String text = "Dobrý deň.\n\n" +
                "Váženie nebolo vyhodnotené správne.\n\n" +
                "Podrobné informácie z daného váženia:\n\n";
        notification.setDescription(text);
        notification.setCreatedOn(new Timestamp(System.currentTimeMillis()));

        return notification;
    }
}

