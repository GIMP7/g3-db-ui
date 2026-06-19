package com.g3dbui.bir1904.ui;

import java.time.LocalDate;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.support.TransactionTemplate;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValidation;
import com.g3dbui.base.ui.ViewTitle;
import com.g3dbui.bir1904.AgentInformation;
import com.g3dbui.bir1904.AgentInformationRepository;
import com.g3dbui.bir1904.AuditLogEntry;
import com.g3dbui.bir1904.AuditLogService;
import com.g3dbui.bir1904.IdInformation;
import com.g3dbui.bir1904.IdInformationRepository;
import com.g3dbui.bir1904.RegistrationDetails;
import com.g3dbui.bir1904.RegistrationDetailsRepository;
import com.g3dbui.bir1904.RegistrationIdSequenceService;
import com.g3dbui.bir1904.SpouseInformation;
import com.g3dbui.bir1904.SpouseInformationRepository;
import com.g3dbui.bir1904.TaxpayerInformation;
import com.g3dbui.bir1904.TaxpayerInformationRepository;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("new-form-1904")
@PageTitle("New Form")
@Menu(order = 1, title = "New Form")
class GuidedRegistrationView extends VerticalLayout {

    private final RegistrationDetailsRepository registrationRepository;
    private final TaxpayerInformationRepository taxpayerRepository;
    private final IdInformationRepository idRepository;
    private final SpouseInformationRepository spouseRepository;
    private final AgentInformationRepository agentRepository;
    private final RegistrationIdSequenceService idSequenceService;
    private final AuditLogService auditLogService;
    private final TransactionTemplate transactionTemplate;

    private final TextField registrationId = new TextField("Registration ID");
    private final DatePicker regDate = new DatePicker("Registration Date");
    private final ComboBox<String> taxpayerType = new ComboBox<>("Taxpayer Type");
    private final TextField purpose = new TextField("Purpose");

    private final TextField philsysNumber = new TextField("PhilSys Number");
    private final TextField foreignTin = new TextField("Foreign TIN");
    private final TextField residence = new TextField("Foreign Residence");
    private final TextField taxpayerName = new TextField("Taxpayer Name");
    private final ComboBox<String> nameCategory = new ComboBox<>("Name Category");
    private final DatePicker birthDate = new DatePicker("Birth Date");
    private final TextField birthPlace = new TextField("Birth Place");
    private final TextField localAddress = new TextField("Local Address");
    private final TextField foreignAddress = new TextField("Foreign Address");
    private final DatePicker arrivalDate = new DatePicker("Arrival Date");
    private final ComboBox<String> gender = new ComboBox<>("Gender");
    private final ComboBox<String> civilStatus = new ComboBox<>("Civil Status");
    private final TextField contactNo = new TextField("Contact No");
    private final EmailField email = new EmailField("Email");
    private final TextField motherName = new TextField("Mother's Name");
    private final TextField fatherName = new TextField("Father's Name");

    private final Checkbox includeId = new Checkbox("Add ID information");
    private final TextField idNumber = new TextField("ID Number");
    private final TextField idType = new TextField("ID Type");
    private final DatePicker idEffective = new DatePicker("Effective Date");
    private final DatePicker idExpiry = new DatePicker("Expiry Date");

    private final Checkbox includeSpouse = new Checkbox("Add spouse information");
    private final TextField spouseId = new TextField("Spouse ID");
    private final ComboBox<String> spouseEmployment = new ComboBox<>("Spouse Employment");
    private final TextField spouseName = new TextField("Spouse Name");
    private final TextField spouseTin = new TextField("Spouse TIN");
    private final TextField spouseEmployerName = new TextField("Employer Name");
    private final TextField spouseEmployerTin = new TextField("Employer TIN");

    private final Checkbox includeAgent = new Checkbox("Add agent information");
    private final TextField agentTin = new TextField("Agent TIN");
    private final TextField agentName = new TextField("Agent Name");
    private final TextField agentRdo = new TextField("RDO Code");
    private final TextField agentAddress = new TextField("Agent Address");
    private final TextField agentContact = new TextField("Agent Contact");
    private final EmailField agentEmail = new EmailField("Agent Email");

    private final Button saveButton = new Button("Save Form");
    private final Button resetButton = new Button("Reset");
    private Component firstInvalidField;

    GuidedRegistrationView(RegistrationDetailsRepository registrationRepository,
            TaxpayerInformationRepository taxpayerRepository,
            IdInformationRepository idRepository,
            SpouseInformationRepository spouseRepository,
            AgentInformationRepository agentRepository,
            RegistrationIdSequenceService idSequenceService,
            AuditLogService auditLogService,
            TransactionTemplate transactionTemplate) {
        this.registrationRepository = registrationRepository;
        this.taxpayerRepository = taxpayerRepository;
        this.idRepository = idRepository;
        this.spouseRepository = spouseRepository;
        this.agentRepository = agentRepository;
        this.idSequenceService = idSequenceService;
        this.auditLogService = auditLogService;
        this.transactionTemplate = transactionTemplate;

        addClassName("crud-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.STRETCH);

        configureFields();

        var header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.add(new ViewTitle("New Form"));
        header.add(new Paragraph("Create the registration and its linked records from one form."));

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(event -> saveForm());
        resetButton.addClickListener(event -> resetForm());

        var actions = new HorizontalLayout(saveButton, resetButton);
        actions.setSpacing(true);

        add(header,
                section("Registration Details", registrationForm()),
                section("Taxpayer Information", taxpayerForm()),
                optionalSection(includeId, idForm()),
                optionalSection(includeSpouse, spouseForm()),
                optionalSection(includeAgent, agentForm()),
                actions);

        resetForm();
    }

    private void configureFields() {
        registrationId.setReadOnly(true);
        registrationId.setWidthFull();
        registrationId.setHelperText("Generated when this form is saved");

        regDate.setWidthFull();
        setRequired(regDate, "Format: DD-MM-YYYY");
        taxpayerType.setWidthFull();
        taxpayerType.setItems("Filipino Citizen", "Foreign National", "One-time Filipino",
                "One-time Foreign", "Passive Income", "Estate");
        setRequired(taxpayerType, "Choose one taxpayer type");
        purpose.setWidthFull();
        purpose.setMaxLength(20);
        setRequired(purpose, "Max 20 characters, e.g. Local Employment");

        taxpayerName.setWidthFull();
        taxpayerName.setMaxLength(70);
        setRequired(taxpayerName, "Last, First Middle format");
        nameCategory.setWidthFull();
        nameCategory.setItems("Individual", "Non-Individual", "Estate");
        setRequired(nameCategory, "Choose one category");
        birthDate.setWidthFull();
        setRequired(birthDate, "Format: DD-MM-YYYY");
        birthPlace.setWidthFull();
        birthPlace.setMaxLength(150);
        setRequired(birthPlace, "City/municipality, province, or country");
        localAddress.setWidthFull();
        localAddress.setMaxLength(150);
        setRequired(localAddress, "Current local address");
        gender.setWidthFull();
        gender.setItems("M", "F", "I");
        gender.setItemLabelGenerator(g -> "M".equals(g) ? "Male (M)" : "F".equals(g) ? "Female (F)" : "Other (I)");
        setRequired(gender, "Choose one option");
        civilStatus.setWidthFull();
        civilStatus.setItems("S", "M", "W", "L");
        civilStatus.setItemLabelGenerator(s -> switch (s) {
            case "S" -> "Single (S)";
            case "M" -> "Married (M)";
            case "W" -> "Widowed (W)";
            case "L" -> "Legally Separated (L)";
            default -> s;
        });
        setRequired(civilStatus, "Choose one status");
        contactNo.setWidthFull();
        contactNo.setMaxLength(15);
        setRequired(contactNo, "Max 15 characters, e.g. 09171234567");
        email.setWidthFull();
        email.setMaxLength(40);
        setRequired(email, "Example: name@example.com");
        motherName.setWidthFull();
        motherName.setMaxLength(70);
        setRequired(motherName, "Last, First Middle format");
        fatherName.setWidthFull();
        fatherName.setMaxLength(70);
        setRequired(fatherName, "Last, First Middle format");
        philsysNumber.setWidthFull();
        philsysNumber.setMaxLength(19);
        setOptional(philsysNumber, "e.g. 1234-5678-9012-3456");
        foreignTin.setWidthFull();
        foreignTin.setMaxLength(20);
        setOptional(foreignTin, "foreign applicants only");
        residence.setWidthFull();
        residence.setMaxLength(150);
        setOptional(residence, "foreign applicants only");
        foreignAddress.setWidthFull();
        foreignAddress.setMaxLength(150);
        setOptional(foreignAddress, "foreign address, if applicable");
        arrivalDate.setWidthFull();
        setOptional(arrivalDate, "format: DD-MM-YYYY");

        idNumber.setWidthFull();
        idNumber.setMaxLength(20);
        setRequired(idNumber, "Required when ID information is enabled");
        idType.setWidthFull();
        idType.setMaxLength(30);
        setRequired(idType, "e.g. Passport, Driver's License, UMID");
        idEffective.setWidthFull();
        setRequired(idEffective, "Format: DD-MM-YYYY");
        idExpiry.setWidthFull();
        setOptional(idExpiry, "format: DD-MM-YYYY");

        spouseId.setWidthFull();
        spouseId.setMaxLength(10);
        setRequired(spouseId, "Required when spouse information is enabled");
        spouseEmployment.setWidthFull();
        spouseEmployment.setItems("Unemployed", "Employed-Locally", "Employed-Abroad", "Engaged in Business");
        setRequired(spouseEmployment, "Required when spouse information is enabled");
        spouseName.setWidthFull();
        spouseName.setMaxLength(70);
        setRequired(spouseName, "Last, First Middle format");
        spouseTin.setWidthFull();
        spouseTin.setMaxLength(15);
        setOptional(spouseTin, "e.g. 123-456-789-000");
        spouseEmployerName.setWidthFull();
        spouseEmployerName.setMaxLength(70);
        setOptional(spouseEmployerName, "spouse employer name");
        spouseEmployerTin.setWidthFull();
        spouseEmployerTin.setMaxLength(15);
        setOptional(spouseEmployerTin, "e.g. 123-456-789-000");

        agentTin.setWidthFull();
        agentTin.setMaxLength(15);
        setRequired(agentTin, "Required when agent information is enabled");
        agentName.setWidthFull();
        agentName.setMaxLength(70);
        setRequired(agentName, "Agent or firm name");
        agentRdo.setWidthFull();
        agentRdo.setMaxLength(3);
        setRequired(agentRdo, "3-digit RDO code, e.g. 043");
        agentAddress.setWidthFull();
        agentAddress.setMaxLength(150);
        setRequired(agentAddress, "Agent address");
        agentContact.setWidthFull();
        agentContact.setMaxLength(15);
        setRequired(agentContact, "Max 15 characters, e.g. 09171234567");
        agentEmail.setWidthFull();
        agentEmail.setMaxLength(40);
        setRequired(agentEmail, "Example: name@example.com");

        includeId.addValueChangeListener(event -> setIdFieldsVisible(event.getValue()));
        includeSpouse.addValueChangeListener(event -> {
            if (event.getValue() && isBlank(spouseId.getValue())) {
                spouseId.setValue(nextSpouseId());
            }
            setSpouseFieldsVisible(event.getValue());
        });
        includeAgent.addValueChangeListener(event -> setAgentFieldsVisible(event.getValue()));
    }

    private FormLayout registrationForm() {
        var form = new FormLayout(registrationId, regDate, taxpayerType, purpose);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("48em", 2));
        return form;
    }

    private void setDateFormat(DatePicker picker, String helperText) {
        var i18n = new DatePicker.DatePickerI18n();
        i18n.setDateFormat("dd-MM-yyyy");
        picker.setI18n(i18n);
        picker.setHelperText(helperText);
    }

    private void setRequired(TextField field, String helperText) {
        field.setRequired(true);
        field.setRequiredIndicatorVisible(true);
        field.setHelperText("Required. " + helperText);
    }

    private void setRequired(EmailField field, String helperText) {
        field.setRequired(true);
        field.setRequiredIndicatorVisible(true);
        field.setHelperText("Required. " + helperText);
    }

    private void setRequired(ComboBox<String> field, String helperText) {
        field.setRequired(true);
        field.setRequiredIndicatorVisible(true);
        field.setHelperText("Required. " + helperText);
    }

    private void setRequired(DatePicker field, String helperText) {
        field.setRequired(true);
        field.setRequiredIndicatorVisible(true);
        setDateFormat(field, "Required. " + helperText);
    }

    private void setOptional(TextField field, String helperText) {
        field.setRequired(false);
        field.setRequiredIndicatorVisible(false);
        field.setHelperText("Optional. " + helperText);
    }

    private void setOptional(DatePicker field, String helperText) {
        field.setRequired(false);
        field.setRequiredIndicatorVisible(false);
        setDateFormat(field, "Optional. " + helperText);
    }

    private FormLayout taxpayerForm() {
        var form = new FormLayout(taxpayerName, nameCategory, birthDate, birthPlace,
                localAddress, gender, civilStatus, contactNo, email, motherName, fatherName,
                philsysNumber, foreignTin, residence, foreignAddress, arrivalDate);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("56em", 2));
        return form;
    }

    private FormLayout idForm() {
        var form = new FormLayout(idNumber, idType, idEffective, idExpiry);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("48em", 2));
        return form;
    }

    private FormLayout spouseForm() {
        var form = new FormLayout(spouseId, spouseEmployment, spouseName, spouseTin,
                spouseEmployerName, spouseEmployerTin);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("48em", 2));
        return form;
    }

    private FormLayout agentForm() {
        var form = new FormLayout(agentTin, agentName, agentRdo, agentAddress, agentContact, agentEmail);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("48em", 2));
        return form;
    }

    private VerticalLayout section(String title, FormLayout form) {
        var card = new VerticalLayout();
        card.addClassName("crud-card");
        card.setPadding(true);
        card.setSpacing(false);
        card.setWidthFull();
        card.add(new H2(title), form);
        return card;
    }

    private VerticalLayout optionalSection(Checkbox checkbox, FormLayout form) {
        var card = new VerticalLayout();
        card.addClassName("crud-card");
        card.setPadding(true);
        card.setSpacing(false);
        card.setWidthFull();
        card.add(checkbox, form);
        return card;
    }

    private void resetForm() {
        clearValidationErrors();
        registrationId.setValue(idSequenceService.peekNextRegistrationId());
        regDate.setValue(LocalDate.now());
        taxpayerType.clear();
        purpose.clear();

        philsysNumber.clear();
        foreignTin.clear();
        residence.clear();
        taxpayerName.clear();
        nameCategory.clear();
        birthDate.clear();
        birthPlace.clear();
        localAddress.clear();
        foreignAddress.clear();
        arrivalDate.clear();
        gender.clear();
        civilStatus.clear();
        contactNo.clear();
        email.clear();
        motherName.clear();
        fatherName.clear();

        includeId.setValue(false);
        idNumber.clear();
        idType.clear();
        idEffective.setValue(LocalDate.now());
        idExpiry.clear();
        setIdFieldsVisible(false);

        includeSpouse.setValue(false);
        spouseId.clear();
        spouseEmployment.clear();
        spouseName.clear();
        spouseTin.clear();
        spouseEmployerName.clear();
        spouseEmployerTin.clear();
        setSpouseFieldsVisible(false);

        includeAgent.setValue(false);
        agentTin.clear();
        agentName.clear();
        agentRdo.clear();
        agentAddress.clear();
        agentContact.clear();
        agentEmail.clear();
        setAgentFieldsVisible(false);
    }

    private void saveForm() {
        clearValidationErrors();
        String validationError = validateForm();
        if (validationError != null) {
            notify(validationError, NotificationVariant.LUMO_ERROR);
            if (firstInvalidField != null) {
                firstInvalidField.getElement().callJsFunction("focus");
            }
            return;
        }

        try {
            transactionTemplate.executeWithoutResult(status -> {
                String newRegistrationId = idSequenceService.consumeNextRegistrationId();

                var registration = new RegistrationDetails();
                registration.setRegistrationId(newRegistrationId);
                registration.setRegDate(regDate.getValue());
                registration.setTaxpayerType(taxpayerType.getValue());
                registration.setPurpose(trim(purpose.getValue()));
                registrationRepository.save(registration);

                var taxpayer = new TaxpayerInformation();
                taxpayer.setRegistrationId(newRegistrationId);
                taxpayer.setTaxpayerName(trim(taxpayerName.getValue()));
                taxpayer.setNameCategory(nameCategory.getValue());
                taxpayer.setBirthDate(birthDate.getValue());
                taxpayer.setBirthPlace(trim(birthPlace.getValue()));
                taxpayer.setLocalAddress(trim(localAddress.getValue()));
                taxpayer.setGender(gender.getValue());
                taxpayer.setCivilStatus(civilStatus.getValue());
                taxpayer.setContactNo(trim(contactNo.getValue()));
                taxpayer.setEmail(trim(email.getValue()));
                taxpayer.setMotherName(trim(motherName.getValue()));
                taxpayer.setFatherName(trim(fatherName.getValue()));
                taxpayer.setPhilsysNumber(blankToNull(philsysNumber.getValue()));
                taxpayer.setForeignTin(blankToNull(foreignTin.getValue()));
                taxpayer.setResidence(blankToNull(residence.getValue()));
                taxpayer.setForeignAddress(blankToNull(foreignAddress.getValue()));
                taxpayer.setArrivalDate(arrivalDate.getValue());
                taxpayerRepository.save(taxpayer);

                if (includeId.getValue()) {
                    var id = new IdInformation();
                    id.setRegistrationId(newRegistrationId);
                    id.setIdNumber(trim(idNumber.getValue()));
                    id.setIdType(trim(idType.getValue()));
                    id.setIdEffective(idEffective.getValue());
                    id.setIdExpiry(idExpiry.getValue());
                    idRepository.save(id);
                }

                if (includeSpouse.getValue()) {
                    var spouse = new SpouseInformation();
                    spouse.setRegistrationId(newRegistrationId);
                    spouse.setSpouseId(trim(spouseId.getValue()));
                    spouse.setSpouseEmployment(spouseEmployment.getValue());
                    spouse.setSpouseName(trim(spouseName.getValue()));
                    spouse.setSpouseTin(blankToNull(spouseTin.getValue()));
                    spouse.setSpouseEmployerName(blankToNull(spouseEmployerName.getValue()));
                    spouse.setSpouseEmployerTin(blankToNull(spouseEmployerTin.getValue()));
                    spouseRepository.save(spouse);
                }

                if (includeAgent.getValue()) {
                    var agent = new AgentInformation();
                    agent.setRegistrationId(newRegistrationId);
                    agent.setAgentTin(trim(agentTin.getValue()));
                    agent.setAgentName(trim(agentName.getValue()));
                    agent.setAgentRdo(trim(agentRdo.getValue()));
                    agent.setAgentAddress(trim(agentAddress.getValue()));
                    agent.setAgentContact(trim(agentContact.getValue()));
                    agent.setAgentEmail(trim(agentEmail.getValue()));
                    agentRepository.save(agent);

                    registration.setAgentTin(agent.getAgentTin());
                    registrationRepository.save(registration);
                }

                auditLogService.log(AuditLogEntry.Action.CREATED, "registration_details", newRegistrationId,
                        "Created guided BIR Form 1904 package for " + taxpayer.getTaxpayerName());
            });

            notify("BIR Form 1904 package saved successfully.", NotificationVariant.LUMO_SUCCESS);
            resetForm();
        } catch (DataIntegrityViolationException ex) {
            notify("Cannot save form: one of the IDs already exists or a required value is invalid.",
                    NotificationVariant.LUMO_ERROR);
        } catch (DataAccessException ex) {
            notify("Cannot save form: one of the IDs already exists or a required value is invalid.",
                    NotificationVariant.LUMO_ERROR);
        }
    }

    private String validateForm() {
        if (regDate.isEmpty()) return fail(regDate, "Registration date is required.");
        if (isBlank(taxpayerType.getValue())) return fail(taxpayerType, "Taxpayer type is required.");
        if (isBlank(purpose.getValue())) return fail(purpose, "Purpose is required.");

        if (isBlank(taxpayerName.getValue())) return fail(taxpayerName, "Taxpayer name is required.");
        if (isBlank(nameCategory.getValue())) return fail(nameCategory, "Name category is required.");
        if (birthDate.isEmpty()) return fail(birthDate, "Birth date is required.");
        if (isBlank(birthPlace.getValue())) return fail(birthPlace, "Birth place is required.");
        if (isBlank(localAddress.getValue())) return fail(localAddress, "Local address is required.");
        if (isBlank(gender.getValue())) return fail(gender, "Gender is required.");
        if (isBlank(civilStatus.getValue())) return fail(civilStatus, "Civil status is required.");
        if (isBlank(contactNo.getValue())) return fail(contactNo, "Contact number is required.");
        if (isBlank(email.getValue())) return fail(email, "Email is required.");
        if (isBlank(motherName.getValue())) return fail(motherName, "Mother's name is required.");
        if (isBlank(fatherName.getValue())) return fail(fatherName, "Father's name is required.");

        if (includeId.getValue()) {
            if (isBlank(idNumber.getValue())) return fail(idNumber, "ID number is required when ID information is enabled.");
            if (isBlank(idType.getValue())) return fail(idType, "ID type is required when ID information is enabled.");
            if (idEffective.isEmpty()) return fail(idEffective, "ID effective date is required when ID information is enabled.");
            if (idRepository.existsById(trim(idNumber.getValue()))) return fail(idNumber, "An ID record with this ID number already exists.");
        }

        if (includeSpouse.getValue()) {
            if (isBlank(spouseId.getValue())) return fail(spouseId, "Spouse ID is required when spouse information is enabled.");
            if (isBlank(spouseEmployment.getValue())) return fail(spouseEmployment, "Spouse employment is required when spouse information is enabled.");
            if (isBlank(spouseName.getValue())) return fail(spouseName, "Spouse name is required when spouse information is enabled.");
            if (spouseRepository.existsById(trim(spouseId.getValue()))) return fail(spouseId, "A spouse record with this Spouse ID already exists.");
        }

        if (includeAgent.getValue()) {
            if (isBlank(agentTin.getValue())) return fail(agentTin, "Agent TIN is required when agent information is enabled.");
            if (isBlank(agentName.getValue())) return fail(agentName, "Agent name is required when agent information is enabled.");
            if (isBlank(agentRdo.getValue())) return fail(agentRdo, "Agent RDO is required when agent information is enabled.");
            if (isBlank(agentAddress.getValue())) return fail(agentAddress, "Agent address is required when agent information is enabled.");
            if (isBlank(agentContact.getValue())) return fail(agentContact, "Agent contact is required when agent information is enabled.");
            if (isBlank(agentEmail.getValue())) return fail(agentEmail, "Agent email is required when agent information is enabled.");
            if (agentRepository.existsById(trim(agentTin.getValue()))) return fail(agentTin, "An agent with this TIN already exists.");
        }

        return null;
    }

    private String fail(HasValidation field, String message) {
        field.setInvalid(true);
        field.setErrorMessage(message);
        if (firstInvalidField == null && field instanceof Component component) {
            firstInvalidField = component;
        }
        return message;
    }

    private void clearValidationErrors() {
        firstInvalidField = null;
        HasValidation[] fields = {
                regDate, taxpayerType, purpose,
                taxpayerName, nameCategory, birthDate, birthPlace, localAddress,
                gender, civilStatus, contactNo, email, motherName, fatherName,
                philsysNumber, foreignTin, residence, foreignAddress, arrivalDate,
                idNumber, idType, idEffective, idExpiry,
                spouseId, spouseEmployment, spouseName, spouseTin, spouseEmployerName, spouseEmployerTin,
                agentTin, agentName, agentRdo, agentAddress, agentContact, agentEmail
        };
        for (HasValidation field : fields) {
            field.setInvalid(false);
            field.setErrorMessage(null);
        }
    }

    private void setIdFieldsVisible(boolean visible) {
        idNumber.setVisible(visible);
        idType.setVisible(visible);
        idEffective.setVisible(visible);
        idExpiry.setVisible(visible);
    }

    private void setSpouseFieldsVisible(boolean visible) {
        spouseId.setVisible(visible);
        spouseEmployment.setVisible(visible);
        spouseName.setVisible(visible);
        spouseTin.setVisible(visible);
        spouseEmployerName.setVisible(visible);
        spouseEmployerTin.setVisible(visible);
    }

    private void setAgentFieldsVisible(boolean visible) {
        agentTin.setVisible(visible);
        agentName.setVisible(visible);
        agentRdo.setVisible(visible);
        agentAddress.setVisible(visible);
        agentContact.setVisible(visible);
        agentEmail.setVisible(visible);
    }

    private String nextSpouseId() {
        int next = spouseRepository.findAll(Sort.by("spouseId")).stream()
                .map(SpouseInformation::getSpouseId)
                .filter(id -> id != null && id.matches("SP-\\d{6}"))
                .mapToInt(id -> Integer.parseInt(id.substring(3)))
                .max()
                .orElse(0) + 1;
        return "SP-%06d".formatted(next);
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : trim(value);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private void notify(String message, NotificationVariant variant) {
        var notification = Notification.show(message, 4000, Notification.Position.BOTTOM_END);
        notification.addThemeVariants(variant);
    }
}
