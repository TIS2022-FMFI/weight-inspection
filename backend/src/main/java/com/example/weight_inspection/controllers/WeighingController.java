package com.example.weight_inspection.controllers;

import com.example.weight_inspection.models.*;
import com.example.weight_inspection.repositories.*;
import com.example.weight_inspection.services.EmailSenderService;
import com.example.weight_inspection.services.WeighingService;
import com.example.weight_inspection.transfer.AddWeighingDTO;
import com.example.weight_inspection.transfer.ListResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;

@RestController
@RequestMapping("api/weighing")
public class WeighingController {
    private final WeighingRepository weighingRepository;
    private final WeighingService weighingService;
    private final ProductPackagingRepository productPackagingRepository;
    private final PackagingRepository packagingRepository;
    private final ProductRepository productRepository;
    private final PaletteRepository paletteRepository;
    private final NotificationRepository notificationRepository;

    private final ModelMapper modelMapper;
    private final EmailSenderService emailSenderService;
    private final EmailRepository emailRepository;

    @Autowired
    public WeighingController(WeighingRepository weighingRepository,
                              WeighingService weighingService,
                              ProductPackagingRepository productPackagingRepository,
                              PackagingRepository packagingRepository,
                              ProductRepository productRepository,
                              PaletteRepository paletteRepository, NotificationRepository notificationRepository, EmailSenderService emailSenderService, EmailRepository emailRepository) {
        this.weighingRepository = weighingRepository;
        this.weighingService = weighingService;
        this.productPackagingRepository = productPackagingRepository;
        this.packagingRepository = packagingRepository;
        this.productRepository = productRepository;
        this.paletteRepository = paletteRepository;
        this.notificationRepository = notificationRepository;
        this.emailSenderService = emailSenderService;
        this.emailRepository = emailRepository;
        this.modelMapper = new ModelMapper();

    }

    @GetMapping
    public ResponseEntity<ListResponse<Weighing>> getWeighings(
            @RequestParam(value = "page", defaultValue = "0") int currentPage,
            @RequestParam(value = "page_size", defaultValue = "100") int pageSize) {
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("id").descending());
        Page<Weighing> page = weighingRepository.findAll(pageable);
        ListResponse<Weighing> listResponse = new ListResponse<>(page);
        return new ResponseEntity<>(listResponse, HttpStatus.OK);

    }

    @PostMapping("")
    public ResponseEntity<Weighing> addWeighing(@RequestBody @Valid AddWeighingDTO weighingDTO,
                                                BindingResult bindingResult) {

        if (bindingResult.hasErrors() || weighingDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Weighing weighing = modelMapper.map(weighingDTO, Weighing.class);
        String reference = weighingDTO.getReference();


        String[] emailRecipients = emailRepository.getEmailsBySendExportsIsTrue();

        Notification notification = new Notification();
        String subjectHead = "Notifikácia z Aplikácie \"Váženie\" - ";

        Product product = productRepository.findByReferenceOrderByIdDesc(reference);
        Palette palette = null;
        Packaging packaging = null;
        ProductPackaging productPackaging = null;

        if (weighingDTO.getPaletteId() != null && weighingDTO.getPackagingId() != null) {
            palette = paletteRepository.findById((long) weighingDTO.getPaletteId());
            packaging = packagingRepository.findById((long) weighingDTO.getPackagingId());
            productPackaging = productPackagingRepository.findByPackagingAndProduct(packaging, product);
            //TODO skontroluj ci tolerancia nie je null a kvantitu ak nemam posli mail
        }

        if (product == null) {
            String subject = "Prvotné váženie produktu";

            notification.setType(subject);
            String text = "Dobrý deň.\n\n" +
                    "Zamestnanec na sklade sa pokúšal vážiť nový produkt, " +
                    "ktorý sa nenachádza v databáze, pridaje prosím nový produkt do databázy.\n\n" +
                    "Podrobné informácie z daného váženia:\n\n" +
                    "Referencia: " + reference + "\n" +
                    "Množstvo: " + weighingDTO.getQuantity() + "\n" +
                    "IDP: " + weighingDTO.getIDP() + "\n";
            notification.setDescription(text);
            notification.setCreatedOn(new Timestamp(System.currentTimeMillis()));
            notificationRepository.save(notification);
            emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + subject,
                    notification.getDescription());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (palette == null) {
            String subject = "Je potrebné pridať novú paletu";
            notification.setType(subject);
            String text = "Dobrý deň.\n\n" +
                    "Zamestnanec na sklade žiada o pridanie novej palety.\n\n" +
                    "Podrobné informácie z daného váženia:\n\n" +
                    "Referencia: " + reference + "\n" +
                    "Množstvo: " + weighingDTO.getQuantity() + "\n" +
                    "IDP: " + weighingDTO.getIDP() + "\n";
            notification.setDescription(text);
            notification.setCreatedOn(new Timestamp(System.currentTimeMillis()));
            notificationRepository.save(notification);
            emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + subject,
                    notification.getDescription());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
        if (palette.getWeight() == null) {
            String subject = "Daná paleta nebola ešte navážená";
            notification.setType(subject);
            String text = "Dobrý deň.\n\n" +
                    "Je potrebné navážiť danú paletu.\n\n" +
                    "Podrobné informácie z daného váženia:\n\n" +
                    "Referencia: " + reference + "\n" +
                    "Množstvo: " + weighingDTO.getQuantity() + "\n" +
                    "IDP: " + weighingDTO.getIDP() + "\n\n" +
                    "Názov palety: " + palette.getName() + "\n" +
                    "Typ palety: " + palette.getType() +"\n";

            notification.setDescription(text);
            notification.setCreatedOn(new Timestamp(System.currentTimeMillis()));
            notificationRepository.save(notification);
            emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + subject,
                    notification.getDescription());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (packaging == null) {
            String subject = "Je potrebné pridať nový obal";
            notification.setType(subject);
            String text = "Dobrý deň.\n\n" +
                    "Zamestnanec na sklade žiada o pridanie nového obalu.\n\n" +
                    "Podrobné informácie z daného váženia:\n\n" +
                    "Referencia: " + reference + "\n" +
                    "Množstvo: " + weighingDTO.getQuantity() + "\n" +
                    "IDP: " + weighingDTO.getIDP() + "\n";
            notification.setDescription(text);
            notification.setCreatedOn(new Timestamp(System.currentTimeMillis()));
            notificationRepository.save(notification);
            emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + subject,
                    notification.getDescription());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (packaging.getWeight() == null) {
            String subject = "Daný obal nebol ešte navážený";
            notification.setType(subject);
            String text = "Dobrý deň.\n\n" +
                    "Je potrebné navážiť daný obalu.\n\n" +
                    "Podrobné informácie z daného váženia:\n\n" +
                    "Referencia: " + reference + "\n" +
                    "Množstvo: " + weighingDTO.getQuantity() + "\n" +
                    "IDP: " + weighingDTO.getIDP() + "\n\n" +
                    "Názov obalu " + packaging.getName() + "\n" +
                    "Typ obalu: " + packaging.getType() +"\n";
            notification.setDescription(text);
            notification.setCreatedOn(new Timestamp(System.currentTimeMillis()));
            notificationRepository.save(notification);
            emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + subject,
                    notification.getDescription());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (productPackaging == null) {
            String subject = "Chýba vzťah produkt-obal";
            String text = "Dobrý deň.\n\n" +
                    "Zamestnanec na sklade žiada o pridanie nového obalu k produktu.\n\n" +
                    "Podrobné informácie z daného váženia:\n\n" +
                    "Referencia: " + reference + "\n" +
                    "Množstvo: " + weighingDTO.getQuantity() + "\n" +
                    "IDP: " + weighingDTO.getIDP() + "\n\n" +
                    "Názov obalu: " + packaging.getName() + "\n" +
                    "Typ obalu: " + packaging.getType() +"\n";
            notification.setType(subject);

            notification.setDescription(text);
            notification.setCreatedOn(new Timestamp(System.currentTimeMillis()));
            notificationRepository.save(notification);
            emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + subject,
                    notification.getDescription());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Float paletteWeight = palette.getWeight();
        Float packagingWeight = packaging.getWeight();
        Float productWeight = product.getWeight();
        int numberOfProductsInPackaging = productPackaging.getQuantity();
        float productPackagingTolerance = productPackaging.getTolerance();
        int totalNumberOfProducts = weighing.getQuantity();

        if (productWeight == null) {
            productWeight = weighingService.calculateWeightOfOneProduct(weighingDTO.getWeight(), totalNumberOfProducts,
                    numberOfProductsInPackaging, packagingWeight, paletteWeight);
            if (productWeight < 0) {
                String subject = "Chyba váženia";
                String text = "Dobrý deň.\n\n" +
                        "Váženie nebolo vyhodnotené správne.\n\n" +
                        "Podrobné informácie z daného váženia:\n\n" +
                        "Referencia: " + reference + "\n" +
                        "Množstvo: " + weighingDTO.getQuantity() + "\n" +
                        "IDP: " + weighingDTO.getIDP() + "\n\n" +
                        "Názov obalu: " + packaging.getName() + "\n" +
                        "Hmotnosť obalu: " + packaging.getWeight() + "\n\n" +
                        "Názov palety: " + palette.getName() + "\n" +
                        "Hmotnosť palety: " + palette.getWeight() + "\n\n" +
                        "Vypočítaná hmotnosť: " + productWeight + "\n" +
                        "Nameraná hmotnosť: " + weighingDTO.getWeight() + "\n" +
                        "Tolerancia: " + productPackagingTolerance + "\n";
                notification.setType(subject);
                notification.setDescription(text);
                notification.setCreatedOn(new Timestamp(System.currentTimeMillis()));
                notificationRepository.save(notification);
                emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + subject,
                        notification.getDescription());
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            product.setWeight(productWeight);
            productRepository.save(product);
        }

        float calculatedWeight = weighingService.calculateExpectedWeight(totalNumberOfProducts, product.getWeight(),
                packagingWeight, paletteWeight);
        float differenceInWeight = Math.abs(weighingDTO.getWeight() - calculatedWeight);
        boolean correctWeighing = differenceInWeight < productWeight * productPackagingTolerance;

        if (!correctWeighing) {
            String subject = "Chyba váženia";
            String text = "Dobrý deň.\n\n" +
                    "Váženie nebolo vyhodnotené správne.\n\n" +
                    "Podrobné informácie z daného váženia:\n\n" +
                    "Referencia: " + reference + "\n" +
                    "Množstvo: " + weighingDTO.getQuantity() + "\n" +
                    "IDP: " + weighingDTO.getIDP() + "\n\n" +
                    "Názov obalu: " + packaging.getName() + "\n" +
                    "Hmotnosť obalu: " + packaging.getWeight() + "\n\n" +
                    "Názov palety: " + palette.getName() + "\n" +
                    "Hmotnosť palety: " + palette.getWeight() + "\n\n" +
                    "Vypočítaná hmotnosť: " + calculatedWeight + "\n" +
                    "Nameraná hmotnosť: " + weighingDTO.getWeight() + "\n" +
                    "Tolerancia: " + productPackagingTolerance + "\n";
            notification.setType(subject);
            notification.setDescription(text);
            notification.setCreatedOn(new Timestamp(System.currentTimeMillis()));
            notificationRepository.save(notification);
            emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + subject,
                    notification.getDescription());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        weighing.setCalculatedWeight(calculatedWeight);
        weighing.setWeighedOn(new Timestamp(System.currentTimeMillis()));
        weighing.setCorrect(correctWeighing);
        weighing.setExported(false);

        weighing.setPalette(palette);
        weighing.setProduct(product);
        weighing.setPackaging(productPackaging.getPackaging());
        weighingRepository.save(weighing);

        return new ResponseEntity<>(weighing, HttpStatus.CREATED);
    }
}
