package com.example.weight_inspection.controllers;

import com.example.weight_inspection.models.*;
import com.example.weight_inspection.repositories.*;
import com.example.weight_inspection.services.ConfigurationService;
import com.example.weight_inspection.services.EmailSenderService;
import com.example.weight_inspection.services.NotificationPreparationService;
import com.example.weight_inspection.services.WeighingService;
import com.example.weight_inspection.transfer.AddWeighingDTO;
import com.example.weight_inspection.transfer.GetWeighingDTO;
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

import java.util.stream.Collectors;

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
    private final NotificationPreparationService notificationPreparationService;
    private final ConfigurationRepository configurationRepository;

    private final ConfigurationService configurationService;

    @Autowired
    public WeighingController(WeighingRepository weighingRepository,
                              WeighingService weighingService,
                              ProductPackagingRepository productPackagingRepository,
                              PackagingRepository packagingRepository,
                              ProductRepository productRepository,
                              PaletteRepository paletteRepository, NotificationRepository notificationRepository, EmailSenderService emailSenderService, EmailRepository emailRepository, NotificationPreparationService notificationPreparationService,
                              ConfigurationRepository configurationRepository, ConfigurationService configurationService) {
        this.weighingRepository = weighingRepository;
        this.weighingService = weighingService;
        this.productPackagingRepository = productPackagingRepository;
        this.packagingRepository = packagingRepository;
        this.productRepository = productRepository;
        this.paletteRepository = paletteRepository;
        this.notificationRepository = notificationRepository;
        this.emailSenderService = emailSenderService;
        this.emailRepository = emailRepository;
        this.notificationPreparationService = notificationPreparationService;
        this.configurationService = configurationService;
        this.modelMapper = new ModelMapper();

        this.configurationRepository = configurationRepository;
    }

    @GetMapping
    public ResponseEntity<ListResponse<GetWeighingDTO>> getWeighings(
            @RequestParam(value = "page", defaultValue = "0") int currentPage,
            @RequestParam(value = "page_size", defaultValue = "100") int pageSize) {
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("id").descending());
        Page<Weighing> page = weighingRepository.findAll(pageable);
        ListResponse<GetWeighingDTO> listResponse = new ListResponse<>();

        listResponse.setPage(page.getNumber());
        listResponse.setTotalItems(page.getTotalElements());
        listResponse.setTotalPages(page.getTotalPages());
        listResponse.setItems(
                page.getContent().stream()
                        .map(item -> {
                            GetWeighingDTO getWeighingDTO = modelMapper.map(item, GetWeighingDTO.class);
                            getWeighingDTO.setPaletteName(item.getPalette().getName());
                            getWeighingDTO.setPackagingName(item.getPackaging().getName());
                            getWeighingDTO.setProductReference(item.getProduct().getReference());
                            return getWeighingDTO;
                        })
                        .collect(Collectors.toList())
        );
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


        String subjectHead = "Notifikácia z Aplikácie \"Váženie\" - ";

        Product product = productRepository.findByReferenceOrderByIdDesc(reference);
        Palette palette = null;
        Packaging packaging = null;
        ProductPackaging productPackaging = null;

        if (weighingDTO.getPaletteId() != null) {
            palette = paletteRepository.findById((long) weighingDTO.getPaletteId());
        }
        if(weighingDTO.getPackagingId() != null) {
            packaging = packagingRepository.findById((long) weighingDTO.getPackagingId());
        }
        if (product != null && packaging != null) {
            productPackaging = productPackagingRepository.findByPackagingAndProduct(packaging, product);
        }

        if (product == null) {
            Notification notification = notificationPreparationService.missingProductNotification();

            notification.setDescription(notification.getDescription() +
                    "Referencia: " + reference + "\n" +
                    "Množstvo: " + weighingDTO.getQuantity() + "\n" +
                    "IDP: " + weighingDTO.getIDP() + "\n");
            notificationRepository.save(notification);
            emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + notification.getType(),
                    notification.getDescription());
        }

        if (palette == null) {
            Notification notification = notificationPreparationService.missingPaletteNotification();
            notification.setDescription(notification.getDescription() +
                    "Referencia: " + reference + "\n" +
                    "Množstvo: " + weighingDTO.getQuantity() + "\n" +
                    "IDP: " + weighingDTO.getIDP() + "\n");
            notificationRepository.save(notification);
            emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + notification.getType(),
                    notification.getDescription());
        }

        if (product != null &&  palette != null && !product.getPalette().contains(palette)) {
            Notification notification = notificationPreparationService.missingProductPaletteRelationshipNotification();
            notification.setDescription(notification.getDescription() +
                    "Referencia: " + reference + "\n" +
                    "Množstvo: " + weighingDTO.getQuantity() + "\n" +
                    "IDP: " + weighingDTO.getIDP() + "\n\n" +
                    "Názov palety: " + palette.getName() + "\n" +
                    "Typ palety: " + palette.getType() + "\n");

            notificationRepository.save(notification);
            emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + notification.getType(),
                    notification.getDescription());
        }

        if (palette != null && palette.getWeight() == null) {
            Notification notification = notificationPreparationService.missingPaletteWeightNotification();
            notification.setDescription(notification.getDescription() +
                    "Referencia: " + reference + "\n" +
                    "Množstvo: " + weighingDTO.getQuantity() + "\n" +
                    "IDP: " + weighingDTO.getIDP() + "\n\n" +
                    "Názov palety: " + palette.getName() + "\n" +
                    "Typ palety: " + palette.getType() + "\n");

            notificationRepository.save(notification);
            emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + notification.getType(),
                    notification.getDescription());
        }


        if (packaging == null) {
            Notification notification = notificationPreparationService.missingPackagingNotification();
            notification.setDescription(notification.getDescription() +
                    "Referencia: " + reference + "\n" +
                    "Množstvo: " + weighingDTO.getQuantity() + "\n" +
                    "IDP: " + weighingDTO.getIDP() + "\n");

            notificationRepository.save(notification);
            emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + notification.getType(),
                    notification.getDescription());
        }


        if (packaging != null && packaging.getWeight() == null) {
            Notification notification = notificationPreparationService.missingPackagingWeightNotification();
            notification.setDescription(notification.getDescription() +
                    "Referencia: " + reference + "\n" +
                    "Množstvo: " + weighingDTO.getQuantity() + "\n" +
                    "IDP: " + weighingDTO.getIDP() + "\n\n" +
                    "Názov obalu " + packaging.getName() + "\n" +
                    "Typ obalu: " + packaging.getType() + "\n");
            notificationRepository.save(notification);
            emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + notification.getType(),
                    notification.getDescription());
        }



        if (productPackaging == null  && packaging != null) {
            Notification notification = notificationPreparationService.missingProductPackagingRelationshipNotification();
            notification.setDescription(notification.getDescription() +
                    "Referencia: " + reference + "\n" +
                    "Množstvo: " + weighingDTO.getQuantity() + "\n" +
                    "IDP: " + weighingDTO.getIDP() + "\n\n" +
                    "Názov obalu: " + packaging.getName() + "\n" +
                    "Typ obalu: " + packaging.getType() + "\n");

            notificationRepository.save(notification);
            emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + notification.getType(),
                    notification.getDescription());
        }

        if (productPackaging != null && productPackaging.getQuantity() == null) {
            Notification notification = notificationPreparationService.missingProductPackagingQuantityNotification();
            notification.setDescription(notification.getDescription() +
                    "Referencia: " + reference + "\n" +
                    "Celkové množstvo: " + weighingDTO.getQuantity() + "\n" +
                    "IDP: " + weighingDTO.getIDP() + "\n\n" +
                    "Názov obalu: " + packaging.getName() + "\n" +
                    "Typ obalu: " + packaging.getType() + "\n");

            notificationRepository.save(notification);
            emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + notification.getType(),
                    notification.getDescription());
        }

        if(palette == null  || packaging == null  || packaging.getWeight() == null ||
                product == null || productPackaging == null || productPackaging.getQuantity() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Float tolerance = productPackaging.getTolerance();
        if(tolerance == null) {
            Configuration toleranceConfiguration = configurationRepository.findByName(configurationService.getDefaultToleranceName());
            if(toleranceConfiguration != null) {
                tolerance = toleranceConfiguration.getValue();
            }
        }

        if (tolerance == null) {
            Notification notification = notificationPreparationService.missingProductPackagingToleranceNotification();
            notification.setDescription(notification.getDescription() +
                    "Referencia: " + reference + "\n" +
                    "Celkové množstvo: " + weighingDTO.getQuantity() + "\n" +
                    "IDP: " + weighingDTO.getIDP() + "\n\n" +
                    "Názov obalu: " + packaging.getName() + "\n" +
                    "Typ obalu: " + packaging.getType() + "\n");

            notificationRepository.save(notification);
            emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + notification.getType(),
                    notification.getDescription());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Float paletteWeight = palette.getWeight();
        Float packagingWeight = packaging.getWeight();
        Float productWeight = product.getWeight();
        int numberOfProductsInPackaging = productPackaging.getQuantity();
        int totalNumberOfProducts = weighing.getQuantity();

        if (productWeight == null) {
            productWeight = weighingService.calculateWeightOfOneProduct(weighingDTO.getWeight(), totalNumberOfProducts,
                    numberOfProductsInPackaging, packagingWeight, paletteWeight);
            if (productWeight < 0) {
                Notification notification = notificationPreparationService.incorrectWeighingNotification();
                notification.setDescription(notification.getDescription() +
                        "Referencia: " + reference + "\n" +
                        "Množstvo: " + weighingDTO.getQuantity() + "\n" +
                        "IDP: " + weighingDTO.getIDP() + "\n\n" +
                        "Názov obalu: " + packaging.getName() + "\n" +
                        "Hmotnosť obalu: " + packaging.getWeight() + " kg\n\n" +
                        "Názov palety: " + palette.getName() + "\n" +
                        "Hmotnosť palety: " + palette.getWeight() + " kg\n\n" +
                        "Vypočítaná hmotnosť: " + productWeight + " kg\n" +
                        "Nameraná hmotnosť: " + weighingDTO.getWeight() + " kg\n" +
                        "Tolerancia: " + tolerance + "\n");


                notificationRepository.save(notification);
                emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + notification.getType(),
                        notification.getDescription());
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            product.setWeight(productWeight);
            productRepository.save(product);
        }

        float calculatedWeight = weighingService.calculateExpectedWeight(totalNumberOfProducts, productWeight,
                numberOfProductsInPackaging, packagingWeight, paletteWeight);
        float differenceInWeight = Math.abs(weighingDTO.getWeight() - calculatedWeight);
        boolean correctWeighing = differenceInWeight < productWeight * tolerance;

        if (!correctWeighing) {
            Notification notification = notificationPreparationService.incorrectWeighingNotification();
            notification.setDescription(
                    notification.getDescription() +
                            "Referencia: " + reference + "\n" +
                            "Množstvo: " + weighingDTO.getQuantity() + "\n" +
                            "IDP: " + weighingDTO.getIDP() + "\n\n" +
                            "Názov obalu: " + packaging.getName() + "\n" +
                            "Hmotnosť obalu: " + packaging.getWeight() + " kg\n\n" +
                            "Názov palety: " + palette.getName() + "\n" +
                            "Hmotnosť palety: " + palette.getWeight() + " kg\n\n" +
                            "Vypočítaná hmotnosť: " + calculatedWeight + " kg\n" +
                            "Nameraná hmotnosť: " + weighingDTO.getWeight() + " kg\n" +
                            "Tolerancia: " + tolerance + "\n");

            notificationRepository.save(notification);
            emailSenderService.sendNotificationEmail(emailRecipients, subjectHead + notification.getType(),
                    notification.getDescription());
        }

        weighing.setCalculatedWeight(calculatedWeight);
        weighing.setWeighedOn(new Timestamp(System.currentTimeMillis()));
        weighing.setCorrect(correctWeighing);
        weighing.setExported(false);

        weighing.setPalette(palette);
        weighing.setProduct(product);
        weighing.setPackaging(productPackaging.getPackaging());
        weighingRepository.save(weighing);

        weighing.getProduct().setPalette(null);
        weighing.getProduct().setProductPackaging(null);
        weighing.getPackaging().setProductPackaging(null);
        weighing.getPalette().setProduct(null);
        return new ResponseEntity<>(weighing, HttpStatus.CREATED);
    }
}
