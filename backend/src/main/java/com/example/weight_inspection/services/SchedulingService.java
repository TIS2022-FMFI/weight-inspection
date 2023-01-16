package com.example.weight_inspection.services;

import com.example.weight_inspection.repositories.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulingService {
    private  final EmailSenderService emailSenderService;
    private final EmailRepository emailRepository;
    @Autowired
    public SchedulingService(EmailSenderService emailSenderService, EmailRepository emailRepository) {
        this.emailSenderService = emailSenderService;
        this.emailRepository = emailRepository;
    }
    @Scheduled(cron = "0 0 * * * *")
    public void sendExports(){
        String[] emails = emailRepository.getEmailsBySendExportsIsTrue();
        String subject = "Export z aplikácie \"Váženie\" za poslednú hodinu.";
        String text = "Dobrý deň.\n\n V prílohe nájdete "   + subject;
        String filePath = "somePath";
        //TODO replace value  of filePath with actual path to export

        emailSenderService.sendEmailWithExports(emails, subject, text,
               filePath);
    }
}
