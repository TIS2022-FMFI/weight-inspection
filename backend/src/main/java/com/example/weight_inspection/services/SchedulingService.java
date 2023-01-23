package com.example.weight_inspection.services;

import com.example.weight_inspection.models.Weighing;
import com.example.weight_inspection.repositories.EmailRepository;
import com.example.weight_inspection.repositories.WeighingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulingService {
    private  final EmailSenderService emailSenderService;
    private final EmailRepository emailRepository;
    private final WeighingRepository weighingRepository;
    @Autowired
    public SchedulingService(EmailSenderService emailSenderService, EmailRepository emailRepository, WeighingRepository weighingRepository) {
        this.emailSenderService = emailSenderService;
        this.emailRepository = emailRepository;
        this.weighingRepository = weighingRepository;
    }
    @Scheduled(cron = "0 0 * * * *")
    public void sendExports(){
        Weighing[] weighings = weighingRepository.findNotExportedWeighings();
        if (weighings.length > 0) {
            String[] emails = emailRepository.getEmailsBySendExportsIsTrue();
            String subject = "Export z aplikácie \"Váženie\" za poslednú hodinu.";
            String text = "Dobrý deň.\n\n V prílohe nájdete "   + subject;

            emailSenderService.sendEmailWithExports(emails, subject, text, true);
        }



    }
}
