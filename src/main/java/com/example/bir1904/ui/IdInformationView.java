package com.example.bir1904.ui;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

import com.example.base.ui.ViewTitle;
import com.example.bir1904.AuditLogEntry;
import com.example.bir1904.AuditLogService;
import com.example.bir1904.IdInformation;
import com.example.bir1904.IdInformationRepository;
import com.example.bir1904.RegistrationDetails;
import com.example.bir1904.RegistrationDetailsRepository;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("ids")
@PageTitle("ID Information")
@Menu(order = 4, title = "IDs")
class IdInformationView extends VerticalLayout {

    private final IdInformationRepository repository;
    private final RegistrationDetailsRepository registrationDetailsRepository;
    private final AuditLogService auditLogService;

    private final Grid<IdInformation> grid = new Grid<>(IdInformation.class, false);
    private final Binder<IdInformation> binder = new Binder<>(IdInformation.class);

    private final TextField idNumber = new TextField("ID Number");
    private final TextField idType = new TextField("ID Type");
    private final DatePicker idEffective = new DatePicker("Effective Date");
    private final DatePicker idExpiry = new DatePicker("Expiry Date");
    private final ComboBox<String> registrationId = new ComboBox<>("Registration ID");

    private final Button saveButton = new Button("Create");
    private final Button deleteButton = new Button("Delete");
    private IdInformation current = new IdInformation();

    IdInformationView(IdInformationRepository repository,
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

        var header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.add(new ViewTitle("ID Information"));
        header.add(new Paragraph("Supporting identity documents linked to registrations."));

        grid.setWidthFull();
        grid.setHeight("28rem");
        grid.addColumn(IdInformation::getIdNumber).setHeader("ID Number").setSortable(true);
        grid.addColumn(IdInformation::getIdType).setHeader("Type").setSortable(true);
        grid.addColumn(IdInformation::getIdEffective).setHeader("Effective");
        grid.addColumn(item -> item.getIdExpiry() == null ? "—" : item.getIdExpiry().toString()).setHeader("Expiry");
        grid.addColumn(IdInformation::getRegistrationId).setHeader("Registration ID").setSortable(true);
        grid.asSingleSelect().addValueChangeListener(event -> edit(event.getValue()));

        var gridCard = new VerticalLayout();
        gridCard.addClassName("crud-card");
        gridCard.setPadding(true);
        gridCard.setSpacing(false);
        gridCard.setWidthFull();
        gridCard.add(new H2("Rows"), grid);

        var form = new FormLayout();
        form.add(idNumber, idType, idEffective, idExpiry, registrationId);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("48em", 2));

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> save());

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> confirmDelete());
        deleteButton.setEnabled(false);

        var newButton = new Button("New", e -> edit(new IdInformation()));

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
        edit(new IdInformation());
    }

    private void configureFields() {
        idNumber.setWidthFull();
        idNumber.setRequired(true);
        idNumber.setMaxLength(20);
        idNumber.setHelperText("Max 20 characters");

        idType.setWidthFull();
        idType.setRequired(true);
        idType.setMaxLength(30);
        idType.setHelperText("e.g. Passport, Driver's License, UMID");

        idEffective.setWidthFull();
        idEffective.setRequired(true);

        idExpiry.setWidthFull();
        idExpiry.setHelperText("Optional — leave blank for IDs with no expiry");

        registrationId.setWidthFull();
        registrationId.setRequired(true);
        registrationId.setHelperText("Select from existing registrations");
    }

    private void configureBindings() {
        binder.forField(idNumber)
                .asRequired("ID number is required")
                .withValidator(new StringLengthValidator("Max 20 characters", 1, 20))
                .bind(IdInformation::getIdNumber, IdInformation::setIdNumber);

        binder.forField(idType)
                .asRequired("ID type is required")
                .withValidator(new StringLengthValidator("Max 30 characters", 1, 30))
                .bind(IdInformation::getIdType, IdInformation::setIdType);

        binder.forField(idEffective)
                .asRequired("Effective date is required")
                .bind(IdInformation::getIdEffective, IdInformation::setIdEffective);

        binder.forField(idExpiry)
                .bind(IdInformation::getIdExpiry, IdInformation::setIdExpiry);

        binder.forField(registrationId)
                .asRequired("Registration ID is required")
                .bind(IdInformation::getRegistrationId, IdInformation::setRegistrationId);
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
        grid.setItems(repository.findAll(Sort.by("idNumber")));
    }

    private void edit(IdInformation entity) {
        current = entity == null ? new IdInformation() : entity;
        binder.setBean(current);
        clearValidationState();
        refreshRegistrationOptions();

        boolean isExisting = current.getIdNumber() != null && !current.getIdNumber().isBlank()
                && repository.existsById(current.getIdNumber());
        idNumber.setReadOnly(isExisting);
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
        try {
            boolean isNew = !repository.existsById(current.getIdNumber());
            repository.save(current);
            auditLogService.log(
                    isNew ? AuditLogEntry.Action.CREATED : AuditLogEntry.Action.UPDATED,
                    "id_information",
                    current.getIdNumber(),
                    (isNew ? "Created" : "Updated") + " ID " + current.getIdNumber()
                            + " (" + current.getIdType() + ") for reg " + current.getRegistrationId());
            refreshGrid();
            edit(new IdInformation());
            notify("ID record saved successfully.", NotificationVariant.LUMO_SUCCESS);
        } catch (DataIntegrityViolationException ex) {
            handleSaveError("ID record", ex);
        }
    }

    private void confirmDelete() {
        if (current.getIdNumber() == null || current.getIdNumber().isBlank()) return;
        var dialog = new ConfirmDialog();
        dialog.setHeader("Delete ID Record?");
        dialog.setText("Delete ID \"" + current.getIdNumber() + "\" (" + current.getIdType() + ")?");
        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> delete());
        dialog.open();
    }

    private void delete() {
        String id = current.getIdNumber();
        String type = current.getIdType();
        try {
            repository.delete(current);
            auditLogService.log(AuditLogEntry.Action.DELETED, "id_information", id,
                    "Deleted ID " + id + " (" + type + ")");
            refreshGrid();
            edit(new IdInformation());
            notify("ID record \"" + id + "\" deleted.", NotificationVariant.LUMO_SUCCESS);
        } catch (DataIntegrityViolationException ex) {
            notify("Cannot delete: this record is still referenced by other data.", NotificationVariant.LUMO_ERROR);
        }
    }

    private void handleSaveError(String entity, DataIntegrityViolationException ex) {
        String msg = ex.getMostSpecificCause().getMessage();
        if (msg != null && msg.toLowerCase().contains("unique")) {
            notify("Cannot save: an ID with this number already exists.", NotificationVariant.LUMO_ERROR);
        } else if (msg != null && msg.toLowerCase().contains("foreign key")) {
            notify("Cannot save: the selected Registration ID does not exist.", NotificationVariant.LUMO_ERROR);
        } else {
            notify("Cannot save " + entity + ": please check all required fields are filled correctly.", NotificationVariant.LUMO_ERROR);
        }
    }

    private void notify(String message, NotificationVariant variant) {
        var n = Notification.show(message, 4000, Notification.Position.BOTTOM_END);
        n.addThemeVariants(variant);
    }
}
