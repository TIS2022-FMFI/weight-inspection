package com.example.weight_inspection.services;

import com.example.weight_inspection.models.Weighing;
import com.example.weight_inspection.repositories.WeighingRepository;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EmailSenderService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private WeighingRepository weighingRepository;

    @Value("${spring.mail.username}") private String sender;

    @Async
    public void sendNotificationEmail(String[] recipients, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        for (String recipient : recipients) {
            try {
                message.setTo(recipient);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);

            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    ByteArrayDataSource createExportCSVFile(List<Weighing> weighings) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
        streamWriter.write('\ufeff');
        CSVWriter writer = new CSVWriter(streamWriter, ';');
        writer.writeNext(new String[] {
                "Id",
                "IDP",
                "Úspešné váženie",
                "Vypočítaná váha",
                "Nameraná váha",
                "Dátum váženia",
                "Počet produktov",
                "Referencia",
                "Paleta",
                "Obal"
        });
        for(Weighing weighing: weighings) {
            writer.writeNext(new String[] {
                    Long.toString(weighing.getId()),
                    weighing.getIDP(),
                    Boolean.toString(weighing.isCorrect()),
                    Float.toString(weighing.getCalculatedWeight()),
                    Float.toString(weighing.getWeight()),
                    weighing.getWeighedOn().toString(),
                    Integer.toString(weighing.getQuantity()),
                    (weighing.getProduct() != null) ? weighing.getProduct().getReference() : "",
                    (weighing.getPalette() != null) ? weighing.getPalette().getName() : "",
                    (weighing.getPackaging() != null) ? weighing.getPackaging().getName() : ""
            });
        }
        writer.close();
        return new ByteArrayDataSource(outputStream.toByteArray(), "text/csv");
    }

    @Async
    public void  sendEmailWithExports(String[] recipients, String subject, String body, boolean isAutomaticExport) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(recipients);
            helper.setSubject(subject);
            helper.setText(body);
            List<Weighing> weighings;
            if(isAutomaticExport) {
                weighings = Arrays.asList(weighingRepository.findNotExportedWeighings());
            }
            else {
                weighings = new ArrayList<>();
                weighingRepository.findAll().forEach(weighing -> {
                    weighings.add(weighing);
                });
            }
            helper.addAttachment("Exporty.csv", createExportCSVFile(weighings));
            mailSender.send(message);
            if(isAutomaticExport) {
                weighings.forEach(weighing -> {
                    weighing.setExported(true);
                    weighingRepository.save(weighing);
                });
            }
        }
        catch (MessagingException | IOException e) {
            e.printStackTrace();
        }


    }
}
