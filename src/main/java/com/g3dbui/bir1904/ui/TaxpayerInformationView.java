package com.g3dbui.bir1904.ui;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

import com.g3dbui.base.ui.ViewTitle;
import com.g3dbui.bir1904.AuditLogEntry;
import com.g3dbui.bir1904.AuditLogService;
import com.g3dbui.bir1904.RegistrationDetails;
import com.g3dbui.bir1904.RegistrationDetailsRepository;
import com.g3dbui.bir1904.TaxpayerInformation;
import com.g3dbui.bir1904.TaxpayerInformationRepository;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Route("taxpayers")
@PageTitle("Taxpayer Information")
@Menu(order = 3, title = "Taxpayers")
class TaxpayerInformationView extends VerticalLayout {

    private final TaxpayerInformationRepository repository;
    private final RegistrationDetailsRepository registrationDetailsRepository;
    private final AuditLogService auditLogService;

    private final Grid<TaxpayerInformation> grid = new Grid<>(TaxpayerInformation.class, false);
    private final Binder<TaxpayerInformation> binder = new Binder<>(TaxpayerInformation.class);

    private final TextField searchField = new TextField();
    private final ComboBox<String> registrationId = new ComboBox<>("Registration ID");
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

    private final Button saveButton = new Button("Create");
    private final Button deleteButton = new Button("Delete");
    private TaxpayerInformation current = new TaxpayerInformation();

    TaxpayerInformationView(TaxpayerInformationRepository repository,
            RegistrationDetailsRepository registrationDetailsRepository,
            AuditLogService auditLogService) {
        this.repository = repository;
        this.registrationDetailsRepository = registrationDetailsRepository;
        this.auditLogService = auditLogService;

        addClassName("crud-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.STRETCH);

        configureFields();
        configureBindings();
        configureSearch();

        var header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.add(new ViewTitle("Taxpayer Information"));
        header.add(new Paragraph("Applicant profile data linked to a registration row."));

        grid.setWidthFull();
        grid.setHeight("28rem");
        grid.addColumn(TaxpayerInformation::getRegistrationId).setHeader("Registration ID").setSortable(true);
        grid.addColumn(TaxpayerInformation::getTaxpayerName).setHeader("Taxpayer Name").setSortable(true);
        grid.addColumn(TaxpayerInformation::getNameCategory).setHeader("Category");
        grid.addColumn(t -> "M".equals(t.getGender()) ? "Male" : "F".equals(t.getGender()) ? "Female" : "Other").setHeader("Gender");
        grid.addColumn(t -> {
            return switch (t.getCivilStatus()) {
                case "S" -> "Single";
                case "M" -> "Married";
                case "W" -> "Widowed";
                case "L" -> "Legally Separated";
                default -> t.getCivilStatus();
            };
        }).setHeader("Civil Status");
        grid.asSingleSelect().addValueChangeListener(event -> edit(event.getValue()));

        var gridCard = new VerticalLayout();
        gridCard.addClassName("crud-card");
        gridCard.setPadding(true);
        gridCard.setSpacing(false);
        gridCard.setWidthFull();
        gridCard.add(new H2("Rows"), searchField, grid);

        var form = new FormLayout();
        form.add(registrationId, taxpayerName, nameCategory, birthDate, birthPlace,
                localAddress, gender, civilStatus, contactNo, email,
                motherName, fatherName,
                philsysNumber, foreignTin, residence, foreignAddress, arrivalDate);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("56em", 2));

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> save());

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> confirmDelete());
        deleteButton.setEnabled(false);

        var newButton = new Button("New", e -> edit(new TaxpayerInformation()));

        var actions = new HorizontalLayout(saveButton, deleteButton, newButton);
        actions.setWidthFull();
        actions.setSpacing(true);

        var editorCard = new VerticalLayout();
        editorCard.addClassName("crud-card");
        editorCard.setPadding(true);
        editorCard.setSpacing(false);
        editorCard.setWidthFull();
        editorCard.add(new H2("Editor"), form, actions);

        var content = new HorizontalLayout(gridCard, editorCard);
        content.setWidthFull();
        content.setSpacing(true);
        content.setWrap(true);
        content.setFlexGrow(2, gridCard);
        content.setFlexGrow(1, editorCard);
        content.setAlignItems(FlexComponent.Alignment.STRETCH);

        add(header, content);

        refreshRegistrationOptions();
        refreshGrid();
        edit(new TaxpayerInformation());
    }

    private void configureFields() {
        registrationId.setWidthFull();
        registrationId.setRequired(true);
        registrationId.setHelperText("Select from existing registrations");

        philsysNumber.setWidthFull();
        philsysNumber.setMaxLength(19);
        philsysNumber.setHelperText("Optional, e.g. 1234-5678-9012-3456");

        foreignTin.setWidthFull();
        foreignTin.setMaxLength(20);
        foreignTin.setHelperText("Optional, foreign applicants only");

        residence.setWidthFull();
        residence.setMaxLength(150);
        residence.setHelperText("Optional — foreign applicants only");

        taxpayerName.setWidthFull();
        taxpayerName.setRequired(true);
        taxpayerName.setMaxLength(70);
        taxpayerName.setHelperText("Last, First Middle format");

        nameCategory.setWidthFull();
        nameCategory.setRequired(true);
        nameCategory.setItems("Individual", "Non-Individual", "Estate");

        birthDate.setWidthFull();
        birthDate.setRequired(true);
        setDateFormat(birthDate, "Format: DD-MM-YYYY");

        birthPlace.setWidthFull();
        birthPlace.setRequired(true);
        birthPlace.setMaxLength(150);

        localAddress.setWidthFull();
        localAddress.setRequired(true);
        localAddress.setMaxLength(150);

        foreignAddress.setWidthFull();
        foreignAddress.setMaxLength(150);
        foreignAddress.setHelperText("Optional");

        arrivalDate.setWidthFull();
        setDateFormat(arrivalDate, "Optional, format: DD-MM-YYYY");

        gender.setWidthFull();
        gender.setRequired(true);
        gender.setItems("M", "F", "I");
        gender.setItemLabelGenerator(g -> "M".equals(g) ? "Male (M)" : "F".equals(g) ? "Female (F)" : "Other (I)");

        civilStatus.setWidthFull();
        civilStatus.setRequired(true);
        civilStatus.setItems("S", "M", "W", "L");
        civilStatus.setItemLabelGenerator(s -> switch (s) {
            case "S" -> "Single (S)";
            case "M" -> "Married (M)";
            case "W" -> "Widowed (W)";
            case "L" -> "Legally Separated (L)";
            default -> s;
        });

        contactNo.setWidthFull();
        contactNo.setRequired(true);
        contactNo.setMaxLength(15);
        contactNo.setHelperText("Max 15 characters, e.g. 09171234567");

        email.setWidthFull();
        email.setRequired(true);
        email.setMaxLength(40);
        email.setHelperText("Example: name@example.com");

        motherName.setWidthFull();
        motherName.setRequired(true);
        motherName.setMaxLength(70);
        motherName.setHelperText("Last, First Middle format");

        fatherName.setWidthFull();
        fatherName.setRequired(true);
        fatherName.setMaxLength(70);
        fatherName.setHelperText("Last, First Middle format");
    }

    private void configureBindings() {
        binder.forField(registrationId)
                .asRequired("Registration ID is required")
                .bind(TaxpayerInformation::getRegistrationId, TaxpayerInformation::setRegistrationId);

        binder.forField(philsysNumber)
                .withValidator(v -> v == null || v.isBlank() || v.length() <= 19, "Max 19 characters")
                .bind(TaxpayerInformation::getPhilsysNumber, TaxpayerInformation::setPhilsysNumber);

        binder.forField(foreignTin)
                .withValidator(v -> v == null || v.isBlank() || v.length() <= 20, "Max 20 characters")
                .bind(TaxpayerInformation::getForeignTin, TaxpayerInformation::setForeignTin);

        binder.forField(residence)
                .withValidator(v -> v == null || v.isBlank() || v.length() <= 150, "Max 150 characters")
                .bind(TaxpayerInformation::getResidence, TaxpayerInformation::setResidence);

        binder.forField(taxpayerName)
                .asRequired("Taxpayer name is required")
                .withValidator(new StringLengthValidator("Max 70 characters", 1, 70))
                .bind(TaxpayerInformation::getTaxpayerName, TaxpayerInformation::setTaxpayerName);

        binder.forField(nameCategory)
                .asRequired("Name category is required")
                .bind(TaxpayerInformation::getNameCategory, TaxpayerInformation::setNameCategory);

        binder.forField(birthDate)
                .asRequired("Birth date is required")
                .bind(TaxpayerInformation::getBirthDate, TaxpayerInformation::setBirthDate);

        binder.forField(birthPlace)
                .asRequired("Birth place is required")
                .withValidator(new StringLengthValidator("Max 150 characters", 1, 150))
                .bind(TaxpayerInformation::getBirthPlace, TaxpayerInformation::setBirthPlace);

        binder.forField(localAddress)
                .asRequired("Local address is required")
                .withValidator(new StringLengthValidator("Max 150 characters", 1, 150))
                .bind(TaxpayerInformation::getLocalAddress, TaxpayerInformation::setLocalAddress);

        binder.forField(foreignAddress)
                .withValidator(v -> v == null || v.isBlank() || v.length() <= 150, "Max 150 characters")
                .bind(TaxpayerInformation::getForeignAddress, TaxpayerInformation::setForeignAddress);

        binder.forField(arrivalDate)
                .bind(TaxpayerInformation::getArrivalDate, TaxpayerInformation::setArrivalDate);

        binder.forField(gender)
                .asRequired("Gender is required")
                .bind(TaxpayerInformation::getGender, TaxpayerInformation::setGender);

        binder.forField(civilStatus)
                .asRequired("Civil status is required")
                .bind(TaxpayerInformation::getCivilStatus, TaxpayerInformation::setCivilStatus);

        binder.forField(contactNo)
                .asRequired("Contact number is required")
                .withValidator(new StringLengthValidator("Max 15 characters", 1, 15))
                .bind(TaxpayerInformation::getContactNo, TaxpayerInformation::setContactNo);

        binder.forField(email)
                .asRequired("Email is required")
                .withValidator(new EmailValidator("Enter a valid email address"))
                .withValidator(new StringLengthValidator("Max 40 characters", 1, 40))
                .bind(TaxpayerInformation::getEmail, TaxpayerInformation::setEmail);

        binder.forField(motherName)
                .asRequired("Mother's name is required")
                .withValidator(new StringLengthValidator("Max 70 characters", 1, 70))
                .bind(TaxpayerInformation::getMotherName, TaxpayerInformation::setMotherName);

        binder.forField(fatherName)
                .asRequired("Father's name is required")
                .withValidator(new StringLengthValidator("Max 70 characters", 1, 70))
                .bind(TaxpayerInformation::getFatherName, TaxpayerInformation::setFatherName);
    }

    private void configureSearch() {
        searchField.setPlaceholder("Search taxpayers");
        searchField.setAriaLabel("Search taxpayers");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.setWidthFull();
        searchField.addValueChangeListener(event -> refreshGrid());
    }

    private void setDateFormat(DatePicker picker, String helperText) {
        var i18n = new DatePicker.DatePickerI18n();
        i18n.setDateFormat("dd-MM-yyyy");
        picker.setI18n(i18n);
        picker.setHelperText(helperText);
    }

    private void refreshRegistrationOptions() {
        List<String> ids = registrationDetailsRepository.findAll(Sort.by("registrationId"))
                .stream().map(RegistrationDetails::getRegistrationId).toList();
        registrationId.setItems(ids);
        if (ids.isEmpty()) {
            registrationId.setHelperText("⚠ No registrations exist yet — create one first");
        } else {
            registrationId.setHelperText("Select from existing registrations");
        }
    }

    private void refreshGrid() {
        List<TaxpayerInformation> rows = repository.findAll(Sort.by("registrationId"));
        String searchTerm = searchField.getValue();
        if (searchTerm == null || searchTerm.isBlank()) {
            grid.setItems(rows);
            return;
        }

        String needle = searchTerm.trim().toLowerCase(Locale.ROOT);
        grid.setItems(rows.stream()
                .filter(row -> matchesSearch(row, needle))
                .toList());
    }

    private boolean matchesSearch(TaxpayerInformation row, String needle) {
        return Stream.of(
                row.getRegistrationId(),
                row.getPhilsysNumber(),
                row.getForeignTin(),
                row.getResidence(),
                row.getTaxpayerName(),
                row.getNameCategory(),
                row.getBirthDate() == null ? null : row.getBirthDate().toString(),
                row.getBirthPlace(),
                row.getLocalAddress(),
                row.getForeignAddress(),
                row.getArrivalDate() == null ? null : row.getArrivalDate().toString(),
                row.getGender(),
                genderLabel(row.getGender()),
                row.getCivilStatus(),
                civilStatusLabel(row.getCivilStatus()),
                row.getContactNo(),
                row.getEmail(),
                row.getMotherName(),
                row.getFatherName())
                .filter(value -> value != null)
                .map(value -> value.toLowerCase(Locale.ROOT))
                .anyMatch(value -> value.contains(needle));
    }

    private String genderLabel(String gender) {
        return "M".equals(gender) ? "Male" : "F".equals(gender) ? "Female" : "I".equals(gender) ? "Other" : gender;
    }

    private String civilStatusLabel(String civilStatus) {
        if (civilStatus == null) {
            return null;
        }
        return switch (civilStatus) {
            case "S" -> "Single";
            case "M" -> "Married";
            case "W" -> "Widowed";
            case "L" -> "Legally Separated";
            default -> civilStatus;
        };
    }

    private void edit(TaxpayerInformation entity) {
        current = entity == null ? new TaxpayerInformation() : entity;
        refreshRegistrationOptions();
        binder.setBean(current);
        clearValidationState();

        boolean isExisting = current.getRegistrationId() != null && !current.getRegistrationId().isBlank()
                && repository.existsById(current.getRegistrationId());
        registrationId.setReadOnly(isExisting);
        deleteButton.setEnabled(isExisting);
        saveButton.setText(isExisting ? "Update" : "Create");
    }

    private void clearValidationState() {
        binder.getFields().forEach(field -> {
            if (field instanceof HasValidation validation) {
                validation.setInvalid(false);
            }
        });
    }

    private void save() {
        List<String> regIds = registrationDetailsRepository.findAll()
                .stream().map(RegistrationDetails::getRegistrationId).toList();
        if (regIds.isEmpty()) {
            notify("No registration records exist. Please create a Registration first.", NotificationVariant.LUMO_ERROR);
            return;
        }
        if (!binder.writeBeanIfValid(current)) {
            notify("Please fix the highlighted fields before saving.", NotificationVariant.LUMO_ERROR);
            return;
        }
        // Normalize optional blank strings to null for nullable DB columns
        current.setPhilsysNumber(blankToNull(current.getPhilsysNumber()));
        current.setForeignTin(blankToNull(current.getForeignTin()));
        current.setResidence(blankToNull(current.getResidence()));
        current.setForeignAddress(blankToNull(current.getForeignAddress()));
        try {
            boolean isNew = !repository.existsById(current.getRegistrationId());
            repository.save(current);
            auditLogService.log(
                    isNew ? AuditLogEntry.Action.CREATED : AuditLogEntry.Action.UPDATED,
                    "taxpayer_information",
                    current.getRegistrationId(),
                    (isNew ? "Created" : "Updated") + " taxpayer " + current.getRegistrationId()
                            + " — " + current.getTaxpayerName());
            refreshGrid();
            edit(new TaxpayerInformation());
            notify("Taxpayer saved successfully.", NotificationVariant.LUMO_SUCCESS);
        } catch (DataIntegrityViolationException ex) {
            handleSaveError("taxpayer", ex);
        }
    }

    private void confirmDelete() {
        if (current.getRegistrationId() == null || current.getRegistrationId().isBlank()) return;
        var dialog = new ConfirmDialog();
        dialog.setHeader("Delete Taxpayer Record?");
        dialog.setText("Delete taxpayer \"" + current.getTaxpayerName() + "\" (Registration: "
                + current.getRegistrationId() + ")?");
        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> delete());
        dialog.open();
    }

    private void delete() {
        String id = current.getRegistrationId();
        String name = current.getTaxpayerName();
        try {
            repository.delete(current);
            auditLogService.log(AuditLogEntry.Action.DELETED, "taxpayer_information", id,
                    "Deleted taxpayer " + id + " — " + name);
            refreshGrid();
            edit(new TaxpayerInformation());
            notify("Taxpayer record deleted.", NotificationVariant.LUMO_SUCCESS);
        } catch (DataIntegrityViolationException ex) {
            notify("Cannot delete: this record is still referenced by other data.", NotificationVariant.LUMO_ERROR);
        }
    }

    private void handleSaveError(String entity, DataIntegrityViolationException ex) {
        String msg = ex.getMostSpecificCause().getMessage();
        if (msg != null && msg.toLowerCase().contains("unique")) {
            notify("Cannot save: a taxpayer record for this Registration ID already exists.", NotificationVariant.LUMO_ERROR);
        } else if (msg != null && msg.toLowerCase().contains("foreign key")) {
            notify("Cannot save: the selected Registration ID does not exist.", NotificationVariant.LUMO_ERROR);
        } else {
            notify("Cannot save " + entity + ": please check all required fields are filled correctly.", NotificationVariant.LUMO_ERROR);
        }
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    private void notify(String message, NotificationVariant variant) {
        var n = Notification.show(message, 4000, Notification.Position.BOTTOM_END);
        n.addThemeVariants(variant);
    }
}
