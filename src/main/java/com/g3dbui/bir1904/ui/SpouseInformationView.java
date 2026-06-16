package com.g3dbui.bir1904.ui;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

import com.g3dbui.base.ui.ViewTitle;
import com.g3dbui.bir1904.AuditLogEntry;
import com.g3dbui.bir1904.AuditLogService;
import com.g3dbui.bir1904.RegistrationDetails;
import com.g3dbui.bir1904.RegistrationDetailsRepository;
import com.g3dbui.bir1904.SpouseInformation;
import com.g3dbui.bir1904.SpouseInformationRepository;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
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
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Route("spouses")
@PageTitle("Spouse Information")
@Menu(order = 5, title = "Spouses")
class SpouseInformationView extends VerticalLayout {

    private final SpouseInformationRepository repository;
    private final RegistrationDetailsRepository registrationDetailsRepository;
    private final AuditLogService auditLogService;

    private final Grid<SpouseInformation> grid = new Grid<>(SpouseInformation.class, false);
    private final Binder<SpouseInformation> binder = new Binder<>(SpouseInformation.class);

    private final TextField searchField = new TextField();
    private final TextField spouseId = new TextField("Spouse ID");
    private final ComboBox<String> spouseEmployment = new ComboBox<>("Spouse Employment");
    private final TextField spouseName = new TextField("Spouse Name");
    private final TextField spouseTin = new TextField("Spouse TIN");
    private final TextField spouseEmployerName = new TextField("Employer Name");
    private final TextField spouseEmployerTin = new TextField("Employer TIN");
    private final ComboBox<String> registrationId = new ComboBox<>("Registration ID");

    private final Button saveButton = new Button("Create");
    private final Button deleteButton = new Button("Delete");
    private SpouseInformation current = new SpouseInformation();

    SpouseInformationView(SpouseInformationRepository repository,
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
        header.add(new ViewTitle("Spouse Information"));
        header.add(new Paragraph("Optional spouse records and employer details."));

        grid.setWidthFull();
        grid.setHeight("28rem");
        grid.addColumn(SpouseInformation::getSpouseId).setHeader("Spouse ID").setSortable(true);
        grid.addColumn(SpouseInformation::getSpouseEmployment).setHeader("Employment");
        grid.addColumn(SpouseInformation::getSpouseName).setHeader("Spouse Name").setSortable(true);
        grid.addColumn(s -> s.getSpouseTin() == null ? "—" : s.getSpouseTin()).setHeader("Spouse TIN");
        grid.addColumn(SpouseInformation::getRegistrationId).setHeader("Registration ID").setSortable(true);
        grid.asSingleSelect().addValueChangeListener(event -> edit(event.getValue()));

        var gridCard = new VerticalLayout();
        gridCard.addClassName("crud-card");
        gridCard.setPadding(true);
        gridCard.setSpacing(false);
        gridCard.setWidthFull();
        gridCard.add(new H2("Rows"), searchField, grid);

        var form = new FormLayout();
        form.add(spouseId, registrationId, spouseEmployment, spouseName,
                spouseTin, spouseEmployerName, spouseEmployerTin);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("48em", 2));

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> save());

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> confirmDelete());
        deleteButton.setEnabled(false);

        var newButton = new Button("New", e -> edit(new SpouseInformation()));

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
        edit(new SpouseInformation());
    }

    private void configureFields() {
        spouseId.setWidthFull();
        spouseId.setRequired(true);
        spouseId.setMaxLength(10);
        spouseId.setHelperText("Max 10 characters, e.g. SP-000011");

        spouseEmployment.setWidthFull();
        spouseEmployment.setRequired(true);
        spouseEmployment.setItems("Unemployed", "Employed-Locally", "Employed-Abroad", "Engaged in Business");

        spouseName.setWidthFull();
        spouseName.setRequired(true);
        spouseName.setMaxLength(70);

        spouseTin.setWidthFull();
        spouseTin.setMaxLength(15);
        spouseTin.setHelperText("Optional");

        spouseEmployerName.setWidthFull();
        spouseEmployerName.setMaxLength(70);
        spouseEmployerName.setHelperText("Optional");

        spouseEmployerTin.setWidthFull();
        spouseEmployerTin.setMaxLength(15);
        spouseEmployerTin.setHelperText("Optional");

        registrationId.setWidthFull();
        registrationId.setRequired(true);
        registrationId.setHelperText("Select from existing registrations");
    }

    private void configureBindings() {
        binder.forField(spouseId)
                .asRequired("Spouse ID is required")
                .withValidator(new StringLengthValidator("Max 10 characters", 1, 10))
                .bind(SpouseInformation::getSpouseId, SpouseInformation::setSpouseId);

        binder.forField(spouseEmployment)
                .asRequired("Employment status is required")
                .bind(SpouseInformation::getSpouseEmployment, SpouseInformation::setSpouseEmployment);

        binder.forField(spouseName)
                .asRequired("Spouse name is required")
                .withValidator(new StringLengthValidator("Max 70 characters", 1, 70))
                .bind(SpouseInformation::getSpouseName, SpouseInformation::setSpouseName);

        binder.forField(spouseTin)
                .withValidator(v -> v == null || v.isBlank() || v.length() <= 15, "Max 15 characters")
                .bind(SpouseInformation::getSpouseTin, SpouseInformation::setSpouseTin);

        binder.forField(spouseEmployerName)
                .withValidator(v -> v == null || v.isBlank() || v.length() <= 70, "Max 70 characters")
                .bind(SpouseInformation::getSpouseEmployerName, SpouseInformation::setSpouseEmployerName);

        binder.forField(spouseEmployerTin)
                .withValidator(v -> v == null || v.isBlank() || v.length() <= 15, "Max 15 characters")
                .bind(SpouseInformation::getSpouseEmployerTin, SpouseInformation::setSpouseEmployerTin);

        binder.forField(registrationId)
                .asRequired("Registration ID is required")
                .bind(SpouseInformation::getRegistrationId, SpouseInformation::setRegistrationId);
    }

    private void configureSearch() {
        searchField.setPlaceholder("Search spouses");
        searchField.setAriaLabel("Search spouses");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.setWidthFull();
        searchField.addValueChangeListener(event -> refreshGrid());
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
        List<SpouseInformation> rows = repository.findAll(Sort.by("spouseId"));
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

    private boolean matchesSearch(SpouseInformation row, String needle) {
        return Stream.of(
                row.getSpouseId(),
                row.getSpouseEmployment(),
                row.getSpouseName(),
                row.getSpouseTin(),
                row.getSpouseEmployerName(),
                row.getSpouseEmployerTin(),
                row.getRegistrationId())
                .filter(value -> value != null)
                .map(value -> value.toLowerCase(Locale.ROOT))
                .anyMatch(value -> value.contains(needle));
    }

    private void edit(SpouseInformation entity) {
        current = entity == null ? new SpouseInformation() : entity;
        binder.setBean(current);
        clearValidationState();
        refreshRegistrationOptions();

        boolean isExisting = current.getSpouseId() != null && !current.getSpouseId().isBlank()
                && repository.existsById(current.getSpouseId());
        spouseId.setReadOnly(isExisting);
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
        current.setSpouseTin(blankToNull(current.getSpouseTin()));
        current.setSpouseEmployerName(blankToNull(current.getSpouseEmployerName()));
        current.setSpouseEmployerTin(blankToNull(current.getSpouseEmployerTin()));
        try {
            boolean isNew = !repository.existsById(current.getSpouseId());
            repository.save(current);
            auditLogService.log(
                    isNew ? AuditLogEntry.Action.CREATED : AuditLogEntry.Action.UPDATED,
                    "spouse_information",
                    current.getSpouseId(),
                    (isNew ? "Created" : "Updated") + " spouse " + current.getSpouseId()
                            + " — " + current.getSpouseName() + " for reg " + current.getRegistrationId());
            refreshGrid();
            edit(new SpouseInformation());
            notify("Spouse record saved successfully.", NotificationVariant.LUMO_SUCCESS);
        } catch (DataIntegrityViolationException ex) {
            handleSaveError("spouse record", ex);
        }
    }

    private void confirmDelete() {
        if (current.getSpouseId() == null || current.getSpouseId().isBlank()) return;
        var dialog = new ConfirmDialog();
        dialog.setHeader("Delete Spouse Record?");
        dialog.setText("Delete spouse \"" + current.getSpouseName() + "\" (ID: " + current.getSpouseId() + ")?");
        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> delete());
        dialog.open();
    }

    private void delete() {
        String id = current.getSpouseId();
        String name = current.getSpouseName();
        try {
            repository.delete(current);
            auditLogService.log(AuditLogEntry.Action.DELETED, "spouse_information", id,
                    "Deleted spouse " + id + " — " + name);
            refreshGrid();
            edit(new SpouseInformation());
            notify("Spouse record \"" + id + "\" deleted.", NotificationVariant.LUMO_SUCCESS);
        } catch (DataIntegrityViolationException ex) {
            notify("Cannot delete: this record is still referenced by other data.", NotificationVariant.LUMO_ERROR);
        }
    }

    private void handleSaveError(String entity, DataIntegrityViolationException ex) {
        String msg = ex.getMostSpecificCause().getMessage();
        if (msg != null && msg.toLowerCase().contains("unique")) {
            notify("Cannot save: a spouse record with this ID already exists.", NotificationVariant.LUMO_ERROR);
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
