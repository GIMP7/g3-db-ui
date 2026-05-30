package com.example.schema.ui;

import com.example.base.ui.ViewTitle;
import com.example.bir1904.AgentInformation;
import com.example.bir1904.AgentInformationRepository;
import com.example.bir1904.AuditLogEntry;
import com.example.bir1904.AuditLogService;
import com.example.bir1904.IdInformation;
import com.example.bir1904.IdInformationRepository;
import com.example.bir1904.RegistrationDetails;
import com.example.bir1904.RegistrationDetailsRepository;
import com.example.bir1904.SpouseInformation;
import com.example.bir1904.SpouseInformationRepository;
import com.example.bir1904.TaxpayerInformation;
import com.example.bir1904.TaxpayerInformationRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.data.domain.Sort;

import java.time.format.DateTimeFormatter;

@Route("records")
@PageTitle("Database Records")
@Menu(order = 6, title = "Database Records")
class DatabaseRecordsView extends VerticalLayout {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RegistrationDetailsRepository registrationRepo;
    private final AgentInformationRepository agentRepo;
    private final TaxpayerInformationRepository taxpayerRepo;
    private final IdInformationRepository idRepo;
    private final SpouseInformationRepository spouseRepo;
    private final AuditLogService auditLogService;

    // Grids
    private final Grid<RegistrationDetails> regGrid = new Grid<>(RegistrationDetails.class, false);
    private final Grid<AgentInformation> agentGrid = new Grid<>(AgentInformation.class, false);
    private final Grid<TaxpayerInformation> taxpayerGrid = new Grid<>(TaxpayerInformation.class, false);
    private final Grid<IdInformation> idGrid = new Grid<>(IdInformation.class, false);
    private final Grid<SpouseInformation> spouseGrid = new Grid<>(SpouseInformation.class, false);
    private final Grid<AuditLogEntry> auditGrid = new Grid<>(AuditLogEntry.class, false);

    // Count badges
    private final Span regCount = badge("0");
    private final Span agentCount = badge("0");
    private final Span taxpayerCount = badge("0");
    private final Span idCount = badge("0");
    private final Span spouseCount = badge("0");
    private final Span auditCount = badge("0");

    DatabaseRecordsView(RegistrationDetailsRepository registrationRepo,
            AgentInformationRepository agentRepo,
            TaxpayerInformationRepository taxpayerRepo,
            IdInformationRepository idRepo,
            SpouseInformationRepository spouseRepo,
            AuditLogService auditLogService) {
        this.registrationRepo = registrationRepo;
        this.agentRepo = agentRepo;
        this.taxpayerRepo = taxpayerRepo;
        this.idRepo = idRepo;
        this.spouseRepo = spouseRepo;
        this.auditLogService = auditLogService;

        addClassName("crud-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.STRETCH);

        var header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.add(new ViewTitle("Database Records"));
        header.add(new Paragraph("Live snapshot of all five tables. Use Refresh All or per-table refresh to reload after changes."));

        var refreshAllButton = new Button("Refresh All", VaadinIcon.REFRESH.create(), e -> refreshAll());
        refreshAllButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var topBar = new HorizontalLayout(refreshAllButton);
        topBar.setAlignItems(FlexComponent.Alignment.CENTER);

        configureGrids();

        add(header, topBar,
                buildTableCard("registration_details", regCount, regGrid,
                        e -> refreshRegistrations()),
                buildTableCard("agent_information", agentCount, agentGrid,
                        e -> refreshAgents()),
                buildTableCard("taxpayer_information", taxpayerCount, taxpayerGrid,
                        e -> refreshTaxpayers()),
                buildTableCard("id_information", idCount, idGrid,
                        e -> refreshIds()),
                buildTableCard("spouse_information", spouseCount, spouseGrid,
                        e -> refreshSpouses()),
                buildAuditCard());

        refreshAll();
    }

    private void configureGrids() {
        // registration_details
        regGrid.setWidthFull();
        regGrid.setHeight("20rem");
        regGrid.addColumn(RegistrationDetails::getRegistrationId).setHeader("Registration ID").setSortable(true).setWidth("11rem").setFlexGrow(0);
        regGrid.addColumn(d -> d.getAgentTin() == null ? "—" : d.getAgentTin()).setHeader("Agent TIN").setWidth("14rem").setFlexGrow(0);
        regGrid.addColumn(RegistrationDetails::getRegDate).setHeader("Reg Date").setWidth("10rem").setFlexGrow(0);
        regGrid.addColumn(RegistrationDetails::getTaxpayerType).setHeader("Taxpayer Type").setSortable(true);
        regGrid.addColumn(RegistrationDetails::getPurpose).setHeader("Purpose");

        // agent_information
        agentGrid.setWidthFull();
        agentGrid.setHeight("20rem");
        agentGrid.addColumn(AgentInformation::getAgentTin).setHeader("Agent TIN").setSortable(true).setWidth("14rem").setFlexGrow(0);
        agentGrid.addColumn(AgentInformation::getAgentName).setHeader("Agent Name").setSortable(true);
        agentGrid.addColumn(AgentInformation::getAgentRdo).setHeader("RDO").setWidth("5rem").setFlexGrow(0);
        agentGrid.addColumn(AgentInformation::getAgentContact).setHeader("Contact").setWidth("12rem").setFlexGrow(0);
        agentGrid.addColumn(AgentInformation::getAgentEmail).setHeader("Email");
        agentGrid.addColumn(AgentInformation::getRegistrationId).setHeader("Registration ID").setWidth("11rem").setFlexGrow(0);

        // taxpayer_information
        taxpayerGrid.setWidthFull();
        taxpayerGrid.setHeight("22rem");
        taxpayerGrid.addColumn(TaxpayerInformation::getRegistrationId).setHeader("Reg ID").setSortable(true).setWidth("10rem").setFlexGrow(0);
        taxpayerGrid.addColumn(TaxpayerInformation::getTaxpayerName).setHeader("Taxpayer Name").setSortable(true);
        taxpayerGrid.addColumn(TaxpayerInformation::getNameCategory).setHeader("Category").setWidth("10rem").setFlexGrow(0);
        taxpayerGrid.addColumn(TaxpayerInformation::getBirthDate).setHeader("Birth Date").setWidth("9rem").setFlexGrow(0);
        taxpayerGrid.addColumn(t -> "M".equals(t.getGender()) ? "M" : "F".equals(t.getGender()) ? "F" : "I").setHeader("G").setWidth("3.5rem").setFlexGrow(0);
        taxpayerGrid.addColumn(TaxpayerInformation::getCivilStatus).setHeader("CS").setWidth("3.5rem").setFlexGrow(0);
        taxpayerGrid.addColumn(TaxpayerInformation::getContactNo).setHeader("Contact").setWidth("11rem").setFlexGrow(0);
        taxpayerGrid.addColumn(TaxpayerInformation::getEmail).setHeader("Email");

        // id_information
        idGrid.setWidthFull();
        idGrid.setHeight("20rem");
        idGrid.addColumn(IdInformation::getIdNumber).setHeader("ID Number").setSortable(true);
        idGrid.addColumn(IdInformation::getIdType).setHeader("Type").setSortable(true).setWidth("14rem").setFlexGrow(0);
        idGrid.addColumn(IdInformation::getIdEffective).setHeader("Effective").setWidth("9rem").setFlexGrow(0);
        idGrid.addColumn(i -> i.getIdExpiry() == null ? "—" : i.getIdExpiry().toString()).setHeader("Expiry").setWidth("9rem").setFlexGrow(0);
        idGrid.addColumn(IdInformation::getRegistrationId).setHeader("Registration ID").setWidth("11rem").setFlexGrow(0);

        // spouse_information
        spouseGrid.setWidthFull();
        spouseGrid.setHeight("18rem");
        spouseGrid.addColumn(SpouseInformation::getSpouseId).setHeader("Spouse ID").setSortable(true).setWidth("9rem").setFlexGrow(0);
        spouseGrid.addColumn(SpouseInformation::getSpouseName).setHeader("Spouse Name").setSortable(true);
        spouseGrid.addColumn(SpouseInformation::getSpouseEmployment).setHeader("Employment").setWidth("14rem").setFlexGrow(0);
        spouseGrid.addColumn(s -> s.getSpouseTin() == null ? "—" : s.getSpouseTin()).setHeader("Spouse TIN").setWidth("13rem").setFlexGrow(0);
        spouseGrid.addColumn(s -> s.getSpouseEmployerName() == null ? "—" : s.getSpouseEmployerName()).setHeader("Employer").setWidth("16rem").setFlexGrow(0);
        spouseGrid.addColumn(SpouseInformation::getRegistrationId).setHeader("Reg ID").setWidth("10rem").setFlexGrow(0);

        // audit log
        auditGrid.setWidthFull();
        auditGrid.setHeight("22rem");
        auditGrid.addColumn(e -> e.getTimestamp().format(DT_FMT)).setHeader("Timestamp").setWidth("13rem").setFlexGrow(0).setSortable(true);
        auditGrid.addColumn(e -> e.getAction().name()).setHeader("Action").setWidth("7rem").setFlexGrow(0);
        auditGrid.addColumn(AuditLogEntry::getTableName).setHeader("Table").setWidth("16rem").setFlexGrow(0).setSortable(true);
        auditGrid.addColumn(AuditLogEntry::getRecordId).setHeader("Record ID").setWidth("14rem").setFlexGrow(0);
        auditGrid.addColumn(AuditLogEntry::getDescription).setHeader("Description");
    }

    private VerticalLayout buildTableCard(String tableName, Span countBadge,
            Grid<?> grid,
            com.vaadin.flow.component.ComponentEventListener<
                    com.vaadin.flow.component.ClickEvent<Button>> refreshListener) {
        var title = new H3(tableName);
        title.getStyle().set("margin", "0");

        var refreshBtn = new Button(VaadinIcon.REFRESH.create(), refreshListener);
        refreshBtn.getElement().setAttribute("title", "Refresh " + tableName);

        var headerRow = new HorizontalLayout(title, countBadge, refreshBtn);
        headerRow.setAlignItems(FlexComponent.Alignment.CENTER);
        headerRow.setSpacing(true);
        headerRow.setFlexGrow(1, title);

        var card = new VerticalLayout();
        card.addClassName("crud-card");
        card.setPadding(true);
        card.setSpacing(false);
        card.setWidthFull();
        card.add(headerRow, grid);
        return card;
    }

    private VerticalLayout buildAuditCard() {
        var title = new H2("Activity Log");
        title.getStyle().set("margin", "0");

        var clearBtn = new Button("Clear Log", VaadinIcon.TRASH.create(), e -> {
            auditLogService.clear();
            refreshAudit();
        });
        clearBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        var refreshBtn = new Button(VaadinIcon.REFRESH.create(), e -> refreshAudit());
        refreshBtn.getElement().setAttribute("title", "Refresh audit log");

        var headerRow = new HorizontalLayout(title, auditCount, refreshBtn, clearBtn);
        headerRow.setAlignItems(FlexComponent.Alignment.CENTER);
        headerRow.setSpacing(true);
        headerRow.setFlexGrow(1, title);

        var note = new Paragraph("Records CRUD actions performed this session. Resets on application restart.");
        note.getStyle().set("color", "var(--vaadin-text-color-secondary)").set("margin", "0");

        var card = new VerticalLayout();
        card.addClassName("crud-card");
        card.setPadding(true);
        card.setSpacing(false);
        card.setWidthFull();
        card.add(headerRow, note, auditGrid);
        return card;
    }

    private void refreshAll() {
        refreshRegistrations();
        refreshAgents();
        refreshTaxpayers();
        refreshIds();
        refreshSpouses();
        refreshAudit();
    }

    private void refreshRegistrations() {
        var rows = registrationRepo.findAll(Sort.by("registrationId"));
        regGrid.setItems(rows);
        regCount.setText(rows.size() + " rows");
    }

    private void refreshAgents() {
        var rows = agentRepo.findAll(Sort.by("agentTin"));
        agentGrid.setItems(rows);
        agentCount.setText(rows.size() + " rows");
    }

    private void refreshTaxpayers() {
        var rows = taxpayerRepo.findAll(Sort.by("registrationId"));
        taxpayerGrid.setItems(rows);
        taxpayerCount.setText(rows.size() + " rows");
    }

    private void refreshIds() {
        var rows = idRepo.findAll(Sort.by("idNumber"));
        idGrid.setItems(rows);
        idCount.setText(rows.size() + " rows");
    }

    private void refreshSpouses() {
        var rows = spouseRepo.findAll(Sort.by("spouseId"));
        spouseGrid.setItems(rows);
        spouseCount.setText(rows.size() + " rows");
    }

    private void refreshAudit() {
        var entries = auditLogService.getAll();
        auditGrid.setItems(entries);
        auditCount.setText(entries.size() + " entries");
    }

    private static Span badge(String text) {
        var s = new Span(text);
        s.getElement().getThemeList().add("badge contrast");
        return s;
    }
}
