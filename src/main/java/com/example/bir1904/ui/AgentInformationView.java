package com.example.bir1904.ui;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

import com.example.base.ui.ViewTitle;
import com.example.bir1904.AgentInformation;
import com.example.bir1904.AgentInformationRepository;
import com.example.bir1904.AuditLogEntry;
import com.example.bir1904.AuditLogService;
import com.example.bir1904.RegistrationDetails;
import com.example.bir1904.RegistrationDetailsRepository;
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

@Route("agents")
@PageTitle("Agent Information")
@Menu(order = 2, title = "Agents")
class AgentInformationView extends VerticalLayout {

    private final AgentInformationRepository repository;
    private final RegistrationDetailsRepository registrationDetailsRepository;
    private final AuditLogService auditLogService;

    private final Grid<AgentInformation> grid = new Grid<>(AgentInformation.class, false);
    private final Binder<AgentInformation> binder = new Binder<>(AgentInformation.class);

    private final TextField searchField = new TextField();
    private final TextField agentTin = new TextField("Agent TIN");
    private final TextField agentName = new TextField("Agent Name");
    private final TextField agentRdo = new TextField("RDO Code");
    private final TextField agentAddress = new TextField("Agent Address");
    private final TextField agentContact = new TextField("Contact Number");
    private final EmailField agentEmail = new EmailField("Agent Email");
    private final ComboBox<String> registrationId = new ComboBox<>("Registration ID");

    private final Button saveButton = new Button("Create");
    private final Button deleteButton = new Button("Delete");
    private AgentInformation current = new AgentInformation();

    AgentInformationView(AgentInformationRepository repository,
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
        header.add(new ViewTitle("Agent Information"));
        header.add(new Paragraph("Registered agents linked back to a registration row."));

        grid.setWidthFull();
        grid.setHeight("28rem");
        grid.addColumn(AgentInformation::getAgentTin).setHeader("Agent TIN").setSortable(true);
        grid.addColumn(AgentInformation::getAgentName).setHeader("Agent Name").setSortable(true);
        grid.addColumn(AgentInformation::getAgentRdo).setHeader("RDO");
        grid.addColumn(AgentInformation::getAgentEmail).setHeader("Email");
        grid.addColumn(AgentInformation::getRegistrationId).setHeader("Registration ID");
        grid.asSingleSelect().addValueChangeListener(event -> edit(event.getValue()));

        var gridCard = new VerticalLayout();
        gridCard.addClassName("crud-card");
        gridCard.setPadding(true);
        gridCard.setSpacing(false);
        gridCard.setWidthFull();
        gridCard.add(new H2("Rows"), searchField, grid);

        var form = new FormLayout();
        form.add(agentTin, agentName, agentRdo, agentAddress, agentContact, agentEmail, registrationId);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("48em", 2));

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> save());

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> confirmDelete());
        deleteButton.setEnabled(false);

        var newButton = new Button("New", e -> edit(new AgentInformation()));

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
        edit(new AgentInformation());
    }

    private void configureFields() {
        agentTin.setWidthFull();
        agentTin.setRequired(true);
        agentTin.setMaxLength(15);
        agentTin.setHelperText("Max 15 characters, e.g. 123-456-789-000");

        agentName.setWidthFull();
        agentName.setRequired(true);
        agentName.setMaxLength(70);

        agentRdo.setWidthFull();
        agentRdo.setRequired(true);
        agentRdo.setMaxLength(3);
        agentRdo.setHelperText("3-digit RDO code, e.g. 043");

        agentAddress.setWidthFull();
        agentAddress.setRequired(true);
        agentAddress.setMaxLength(150);

        agentContact.setWidthFull();
        agentContact.setRequired(true);
        agentContact.setMaxLength(15);

        agentEmail.setWidthFull();
        agentEmail.setRequired(true);
        agentEmail.setMaxLength(40);

        registrationId.setWidthFull();
        registrationId.setRequired(true);
        registrationId.setHelperText("Select from existing registrations");
    }

    private void configureBindings() {
        binder.forField(agentTin)
                .asRequired("Agent TIN is required")
                .withValidator(new StringLengthValidator("Max 15 characters", 1, 15))
                .bind(AgentInformation::getAgentTin, AgentInformation::setAgentTin);

        binder.forField(agentName)
                .asRequired("Agent name is required")
                .withValidator(new StringLengthValidator("Max 70 characters", 1, 70))
                .bind(AgentInformation::getAgentName, AgentInformation::setAgentName);

        binder.forField(agentRdo)
                .asRequired("RDO code is required")
                .withValidator(new StringLengthValidator("Must be 1–3 characters", 1, 3))
                .bind(AgentInformation::getAgentRdo, AgentInformation::setAgentRdo);

        binder.forField(agentAddress)
                .asRequired("Address is required")
                .withValidator(new StringLengthValidator("Max 150 characters", 1, 150))
                .bind(AgentInformation::getAgentAddress, AgentInformation::setAgentAddress);

        binder.forField(agentContact)
                .asRequired("Contact number is required")
                .withValidator(new StringLengthValidator("Max 15 characters", 1, 15))
                .bind(AgentInformation::getAgentContact, AgentInformation::setAgentContact);

        binder.forField(agentEmail)
                .asRequired("Email is required")
                .withValidator(new EmailValidator("Enter a valid email address"))
                .withValidator(new StringLengthValidator("Max 40 characters", 1, 40))
                .bind(AgentInformation::getAgentEmail, AgentInformation::setAgentEmail);

        binder.forField(registrationId)
                .asRequired("Registration ID is required — select from the list")
                .bind(AgentInformation::getRegistrationId, AgentInformation::setRegistrationId);
    }

    private void configureSearch() {
        searchField.setPlaceholder("Search agents");
        searchField.setAriaLabel("Search agents");
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
        List<AgentInformation> rows = repository.findAll(Sort.by("agentTin"));
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

    private boolean matchesSearch(AgentInformation row, String needle) {
        return Stream.of(
                row.getAgentTin(),
                row.getAgentName(),
                row.getAgentRdo(),
                row.getAgentAddress(),
                row.getAgentContact(),
                row.getAgentEmail(),
                row.getRegistrationId())
                .filter(value -> value != null)
                .map(value -> value.toLowerCase(Locale.ROOT))
                .anyMatch(value -> value.contains(needle));
    }

    private void edit(AgentInformation entity) {
        current = entity == null ? new AgentInformation() : entity;
        binder.setBean(current);
        clearValidationState();
        refreshRegistrationOptions();

        boolean isExisting = current.getAgentTin() != null && !current.getAgentTin().isBlank()
                && repository.existsById(current.getAgentTin());
        agentTin.setReadOnly(isExisting);
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
            boolean isNew = !repository.existsById(current.getAgentTin());
            repository.save(current);
            auditLogService.log(
                    isNew ? AuditLogEntry.Action.CREATED : AuditLogEntry.Action.UPDATED,
                    "agent_information",
                    current.getAgentTin(),
                    (isNew ? "Created" : "Updated") + " agent " + current.getAgentTin()
                            + " — " + current.getAgentName());
            refreshGrid();
            edit(new AgentInformation());
            notify("Agent saved successfully.", NotificationVariant.LUMO_SUCCESS);
        } catch (DataIntegrityViolationException ex) {
            handleSaveError("agent", ex);
        }
    }

    private void confirmDelete() {
        if (current.getAgentTin() == null || current.getAgentTin().isBlank()) return;
        var dialog = new ConfirmDialog();
        dialog.setHeader("Delete Agent?");
        dialog.setText("Delete agent \"" + current.getAgentTin() + " — " + current.getAgentName() + "\"?");
        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> delete());
        dialog.open();
    }

    private void delete() {
        String tin = current.getAgentTin();
        String name = current.getAgentName();
        try {
            repository.delete(current);
            auditLogService.log(AuditLogEntry.Action.DELETED, "agent_information", tin,
                    "Deleted agent " + tin + " — " + name);
            refreshGrid();
            edit(new AgentInformation());
            notify("Agent \"" + tin + "\" deleted.", NotificationVariant.LUMO_SUCCESS);
        } catch (DataIntegrityViolationException ex) {
            notify("Cannot delete: this agent is still referenced by other records.", NotificationVariant.LUMO_ERROR);
        }
    }

    private void handleSaveError(String entity, DataIntegrityViolationException ex) {
        String msg = ex.getMostSpecificCause().getMessage();
        if (msg != null && msg.toLowerCase().contains("unique")) {
            notify("Cannot save " + entity + ": a record with this ID already exists.", NotificationVariant.LUMO_ERROR);
        } else if (msg != null && msg.toLowerCase().contains("foreign key")) {
            notify("Cannot save " + entity + ": the selected Registration ID does not exist.", NotificationVariant.LUMO_ERROR);
        } else {
            notify("Cannot save " + entity + ": please check all required fields are filled correctly.", NotificationVariant.LUMO_ERROR);
        }
    }

    private void notify(String message, NotificationVariant variant) {
        var n = Notification.show(message, 4000, Notification.Position.BOTTOM_END);
        n.addThemeVariants(variant);
    }
}
