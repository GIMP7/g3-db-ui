package com.g3dbui.bir1904.ui;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

import com.g3dbui.base.ui.ViewTitle;
import com.g3dbui.bir1904.AgentInformation;
import com.g3dbui.bir1904.AgentInformationRepository;
import com.g3dbui.bir1904.AuditLogEntry;
import com.g3dbui.bir1904.AuditLogService;
import com.g3dbui.bir1904.RegistrationDetails;
import com.g3dbui.bir1904.RegistrationDetailsRepository;
import com.g3dbui.bir1904.RegistrationIdSequenceService;
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
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Route("registrations")
@PageTitle("Registration Details")
@Menu(order = 1, title = "Registrations")
class RegistrationDetailsView extends VerticalLayout {

    private final RegistrationDetailsRepository repository;
    private final AgentInformationRepository agentInformationRepository;
    private final AuditLogService auditLogService;
    private final RegistrationIdSequenceService idSequenceService;

    private final Grid<RegistrationDetails> grid = new Grid<>(RegistrationDetails.class, false);
    private final Binder<RegistrationDetails> binder = new Binder<>(RegistrationDetails.class);

    private final TextField searchField = new TextField();
    private final TextField registrationId = new TextField("Registration ID");
    private final ComboBox<String> agentTin = new ComboBox<>("Agent TIN");
    private final DatePicker regDate = new DatePicker("Registration Date");
    private final ComboBox<String> taxpayerType = new ComboBox<>("Taxpayer Type");
    private final TextField purpose = new TextField("Purpose");

    private final Button saveButton = new Button("Create");
    private final Button deleteButton = new Button("Delete");
    private RegistrationDetails current = new RegistrationDetails();

    RegistrationDetailsView(RegistrationDetailsRepository repository,
            AgentInformationRepository agentInformationRepository,
            AuditLogService auditLogService,
            RegistrationIdSequenceService idSequenceService) {
        this.repository = repository;
        this.agentInformationRepository = agentInformationRepository;
        this.auditLogService = auditLogService;
        this.idSequenceService = idSequenceService;

        addClassName("crud-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.STRETCH);

        configureBindings();
        configureFields();
        configureSearch();

        var header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.add(new ViewTitle("Registration Details"));
        header.add(new Paragraph("Core registration rows. Agent TIN is optional and can be assigned after the agent exists."));

        grid.setWidthFull();
        grid.setHeight("28rem");
        grid.addColumn(RegistrationDetails::getRegistrationId).setHeader("Registration ID").setSortable(true);
        grid.addColumn(d -> d.getAgentTin() == null ? "" : d.getAgentTin()).setHeader("Agent TIN");
        grid.addColumn(RegistrationDetails::getRegDate).setHeader("Reg Date").setSortable(true);
        grid.addColumn(RegistrationDetails::getTaxpayerType).setHeader("Taxpayer Type");
        grid.addColumn(RegistrationDetails::getPurpose).setHeader("Purpose");
        grid.asSingleSelect().addValueChangeListener(event -> edit(event.getValue()));

        var gridCard = new VerticalLayout();
        gridCard.addClassName("crud-card");
        gridCard.setPadding(true);
        gridCard.setSpacing(false);
        gridCard.setWidthFull();
        gridCard.add(new H2("Rows"), searchField, grid);

        var form = new FormLayout();
        form.add(registrationId, agentTin, regDate, taxpayerType, purpose);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("48em", 2));

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> save());

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> confirmDelete());
        deleteButton.setEnabled(false);

        var clearButton = new Button("Clear", e -> clearEditor());
        var newButton = new Button("New", e -> openNewDraft());

        var actions = new HorizontalLayout(saveButton, deleteButton, clearButton, newButton);
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

        refreshAgentOptions();
        refreshGrid();
        edit(new RegistrationDetails());
    }

    private void configureFields() {
        registrationId.setWidthFull();
        registrationId.setRequired(true);
        registrationId.setMaxLength(10);
        registrationId.setReadOnly(true);
        registrationId.setHelperText("Generated automatically");

        agentTin.setWidthFull();
        agentTin.setClearButtonVisible(true);
        agentTin.setHelperText("Optional — leave blank if no agent");

        regDate.setWidthFull();
        regDate.setRequired(true);
        setDateFormat(regDate, "Format: DD-MM-YYYY");

        taxpayerType.setWidthFull();
        taxpayerType.setRequired(true);
        taxpayerType.setItems(
                "Filipino Citizen", "Foreign National",
                "One-time Filipino", "One-time Foreign",
                "Passive Income", "Estate");

        purpose.setWidthFull();
        purpose.setRequired(true);
        purpose.setMaxLength(20);
        purpose.setHelperText("Max 20 characters, e.g. Local Employment");
    }

    private void configureSearch() {
        searchField.setPlaceholder("Search registrations");
        searchField.setAriaLabel("Search registrations");
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

    private void configureBindings() {
        binder.forField(registrationId)
                .asRequired("Registration ID is required")
                .withValidator(new StringLengthValidator("Max 10 characters", 1, 10))
                .bind(RegistrationDetails::getRegistrationId, RegistrationDetails::setRegistrationId);

        binder.forField(agentTin)
                .bind(RegistrationDetails::getAgentTin, RegistrationDetails::setAgentTin);

        binder.forField(regDate)
                .asRequired("Registration date is required")
                .bind(RegistrationDetails::getRegDate, RegistrationDetails::setRegDate);

        binder.forField(taxpayerType)
                .asRequired("Taxpayer type is required")
                .bind(RegistrationDetails::getTaxpayerType, RegistrationDetails::setTaxpayerType);

        binder.forField(purpose)
                .asRequired("Purpose is required")
                .withValidator(new StringLengthValidator("Max 20 characters", 1, 20))
                .bind(RegistrationDetails::getPurpose, RegistrationDetails::setPurpose);
    }

    private void refreshAgentOptions() {
        agentTin.setItems(agentInformationRepository.findAll(Sort.by("agentTin"))
                .stream().map(AgentInformation::getAgentTin).toList());
    }

    private void refreshGrid() {
        List<RegistrationDetails> rows = repository.findAll(Sort.by("registrationId"));
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

    private boolean matchesSearch(RegistrationDetails row, String needle) {
        return Stream.of(
                row.getRegistrationId(),
                row.getAgentTin(),
                row.getRegDate() == null ? null : row.getRegDate().toString(),
                row.getTaxpayerType(),
                row.getPurpose())
                .filter(value -> value != null)
                .map(value -> value.toLowerCase(Locale.ROOT))
                .anyMatch(value -> value.contains(needle));
    }

    private void edit(RegistrationDetails entity) {
        var next = entity == null ? new RegistrationDetails() : entity;
        boolean isExisting = next.getRegistrationId() != null && !next.getRegistrationId().isBlank()
                && repository.existsById(next.getRegistrationId());

        current = next;
        binder.setBean(current);
        clearValidationState();
        refreshAgentOptions();

        deleteButton.setEnabled(isExisting);
        saveButton.setText(isExisting ? "Update" : "Create");
    }

    private void openNewDraft() {
        var draft = new RegistrationDetails();
        draft.setRegistrationId(idSequenceService.peekNextRegistrationId());
        edit(draft);
    }

    private void clearEditor() {
        grid.asSingleSelect().clear();
        edit(new RegistrationDetails());
    }

    private void clearValidationState() {
        binder.getFields().forEach(field -> {
            if (field instanceof HasValidation validation) {
                validation.setInvalid(false);
            }
        });
    }

    private void save() {
        if (!binder.writeBeanIfValid(current)) {
            notify("Please fix the highlighted fields before saving.", NotificationVariant.LUMO_ERROR);
            return;
        }
        try {
            boolean isNew = current.getRegistrationId() == null
                    || current.getRegistrationId().isBlank()
                    || !repository.existsById(current.getRegistrationId());
            if (isNew) {
                current.setRegistrationId(idSequenceService.consumeNextRegistrationId());
            }
            repository.save(current);
            auditLogService.log(
                    isNew ? AuditLogEntry.Action.CREATED : AuditLogEntry.Action.UPDATED,
                    "registration_details",
                    current.getRegistrationId(),
                    (isNew ? "Created" : "Updated") + " registration " + current.getRegistrationId()
                            + " — " + current.getTaxpayerType() + ", " + current.getPurpose());
            refreshGrid();
            edit(new RegistrationDetails());
            notify("Registration saved successfully.", NotificationVariant.LUMO_SUCCESS);
        } catch (DataIntegrityViolationException ex) {
            handleSaveError("registration", ex);
        }
    }

    private void confirmDelete() {
        if (current.getRegistrationId() == null || current.getRegistrationId().isBlank()) return;
        var dialog = new ConfirmDialog();
        dialog.setHeader("Delete Registration?");
        dialog.setText("Delete registration \"" + current.getRegistrationId()
                + "\"? This will also remove linked taxpayer, ID, and spouse records.");
        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> delete());
        dialog.open();
    }

    private void delete() {
        String id = current.getRegistrationId();
        try {
            repository.delete(current);
            auditLogService.log(AuditLogEntry.Action.DELETED, "registration_details", id,
                    "Deleted registration " + id);
            refreshGrid();
            edit(new RegistrationDetails());
            notify("Registration \"" + id + "\" deleted.", NotificationVariant.LUMO_SUCCESS);
        } catch (DataIntegrityViolationException ex) {
            notify("Cannot delete: this registration is still referenced by other records.", NotificationVariant.LUMO_ERROR);
        }
    }

    private void handleSaveError(String entity, DataIntegrityViolationException ex) {
        String msg = ex.getMostSpecificCause().getMessage();
        if (msg != null && msg.toLowerCase().contains("unique")) {
            notify("Cannot save " + entity + ": a record with this ID already exists.", NotificationVariant.LUMO_ERROR);
        } else if (msg != null && msg.toLowerCase().contains("foreign key")) {
            notify("Cannot save " + entity + ": a referenced record does not exist.", NotificationVariant.LUMO_ERROR);
        } else {
            notify("Cannot save " + entity + ": please check all required fields are filled correctly.", NotificationVariant.LUMO_ERROR);
        }
    }

    private void notify(String message, NotificationVariant variant) {
        var n = Notification.show(message, 4000, Notification.Position.BOTTOM_END);
        n.addThemeVariants(variant);
    }
}
