package com.g3dbui.schema.ui;

import java.util.List;

import com.g3dbui.base.ui.ViewTitle;
import com.g3dbui.bir1904.AgentInformationRepository;
import com.g3dbui.bir1904.IdInformationRepository;
import com.g3dbui.bir1904.RegistrationDetailsRepository;
import com.g3dbui.bir1904.SpouseInformationRepository;
import com.g3dbui.bir1904.TaxpayerInformationRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "")
@PageTitle("Database Dashboard")
@Menu(order = 0, title = "Database")
class DatabaseSchemaView extends VerticalLayout {

    private final RegistrationDetailsRepository registrationDetailsRepository;
    private final AgentInformationRepository agentInformationRepository;
    private final TaxpayerInformationRepository taxpayerInformationRepository;
    private final IdInformationRepository idInformationRepository;
    private final SpouseInformationRepository spouseInformationRepository;

    private final Span regCountSpan = new Span();
    private final Span agentCountSpan = new Span();
    private final Span taxpayerCountSpan = new Span();
    private final Span idCountSpan = new Span();
    private final Span spouseCountSpan = new Span();

    DatabaseSchemaView(RegistrationDetailsRepository registrationDetailsRepository,
            AgentInformationRepository agentInformationRepository,
            TaxpayerInformationRepository taxpayerInformationRepository,
            IdInformationRepository idInformationRepository,
            SpouseInformationRepository spouseInformationRepository) {
        this.registrationDetailsRepository = registrationDetailsRepository;
        this.agentInformationRepository = agentInformationRepository;
        this.taxpayerInformationRepository = taxpayerInformationRepository;
        this.idInformationRepository = idInformationRepository;
        this.spouseInformationRepository = spouseInformationRepository;

        addClassName("dashboard-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.STRETCH);

        var hero = new VerticalLayout();
        hero.addClassName("dashboard-hero");
        hero.setPadding(false);
        hero.setSpacing(false);
        hero.setWidthFull();
        hero.add(new ViewTitle("BIR Form 1904"));
        hero.add(new Paragraph("The database is initialized from schema.sql and data.sql, and the table views below are live CRUD screens backed by H2."));

        var refreshStatsBtn = new Button("Refresh Counts", VaadinIcon.REFRESH.create(), e -> refreshCounts());
        refreshStatsBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        var statsRow = new HorizontalLayout(
                createStatCard(regCountSpan, "Registration rows"),
                createStatCard(agentCountSpan, "Agent rows"),
                createStatCard(taxpayerCountSpan, "Taxpayer rows"),
                createStatCard(idCountSpan, "ID rows"),
                createStatCard(spouseCountSpan, "Spouse rows"),
                refreshStatsBtn
        );
        statsRow.addClassName("dashboard-stats");
        statsRow.setWidthFull();
        statsRow.setSpacing(true);
        statsRow.setWrap(true);
        statsRow.setAlignItems(FlexComponent.Alignment.CENTER);

        hero.add(statsRow);
        add(hero);
        add(createQuickLinks());
        add(createSummaryCard());

        refreshCounts();
    }

    private void refreshCounts() {
        regCountSpan.setText(String.valueOf(registrationDetailsRepository.count()));
        agentCountSpan.setText(String.valueOf(agentInformationRepository.count()));
        taxpayerCountSpan.setText(String.valueOf(taxpayerInformationRepository.count()));
        idCountSpan.setText(String.valueOf(idInformationRepository.count()));
        spouseCountSpan.setText(String.valueOf(spouseInformationRepository.count()));
    }

    private Component createStatCard(Span valueSpan, String label) {
        var card = new VerticalLayout();
        card.addClassName("stat-card");
        card.setPadding(true);
        card.setSpacing(false);
        card.setWidthFull();
        card.add(valueSpan, new Span(label));
        return card;
    }

    private Component createQuickLinks() {
        var card = new VerticalLayout();
        card.addClassName("crud-card");
        card.setPadding(true);
        card.setSpacing(false);
        card.setWidthFull();

        card.add(new H2("Open a table"));
        card.add(new Paragraph("Use the drawer menu or the links below to edit the seeded database rows."));

        var links = new HorizontalLayout(
                createLinkButton("Registrations", "registrations"),
                createLinkButton("Agents", "agents"),
                createLinkButton("Taxpayers", "taxpayers"),
                createLinkButton("IDs", "ids"),
                createLinkButton("Spouses", "spouses"),
                createLinkButton("Database Records", "records")
        );
        links.setWidthFull();
        links.setSpacing(true);
        links.setWrap(true);

        card.add(links);
        return card;
    }

    private Button createLinkButton(String text, String route) {
        Button button = new Button(text);
        button.addClickListener(event -> button.getUI().ifPresent(ui -> ui.navigate(route)));
        button.addClassName("crud-link-button");
        return button;
    }

    private Component createSummaryCard() {
        var card = new VerticalLayout();
        card.addClassName("crud-card");
        card.setPadding(true);
        card.setSpacing(false);
        card.setWidthFull();

        card.add(new H2("Seeded tables"));
        card.add(new Paragraph("All five tables are loaded on startup from data.sql. Use the CRUD screens to create, update, or delete rows. The Database Records page shows a live snapshot of every table plus an activity log of changes made this session."));

        var tableList = new VerticalLayout();
        tableList.setPadding(false);
        tableList.setSpacing(false);
        List.of(
                "registration_details — core registration rows (parent of all others)",
                "agent_information — linked agent profiles",
                "taxpayer_information — applicant identity records (1-to-1 with registration)",
                "id_information — supporting IDs per registration (many-to-1)",
                "spouse_information — optional spouse records (many-to-1)"
        ).forEach(item -> tableList.add(new Span(item)));

        card.add(tableList);
        return card;
    }
}
