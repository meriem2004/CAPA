package com.company.capa.service;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class CapaDocumentPdfService {
    
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    // Color palette matching the DOCX design
    private static final Color GRAY = new DeviceRgb(128, 128, 128);
    private static final Color DARK_BLUE = new DeviceRgb(46, 64, 87);
    private static final Color SECTION_BLUE = new DeviceRgb(31, 97, 141);
    private static final Color HEADER_GRAY = new DeviceRgb(236, 240, 241);
    private static final Color SUBHEADER_GRAY = new DeviceRgb(52, 73, 94);
    private static final Color GREEN = new DeviceRgb(39, 174, 96);
    private static final Color RED = new DeviceRgb(231, 76, 60);
    private static final Color FOOTER_GRAY = new DeviceRgb(149, 165, 166);
    
    public byte[] generateCapaDocument(Map<String, Object> variables) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(40, 40, 40, 40);
        
        // Add header
        addHeader(document, variables);
        
        // Add title
        addTitle(document, "Rapport CAPA - " + variables.get("capaNumber"));
        
        // Section 1: CAPA Information
        addSectionHeader(document, "📋 Informations CAPA");
        addCapaInfo(document, variables);
        
        // Section 2: Root Cause Analysis
        addSectionHeader(document, "🔍 Analyse des causes racines");
        addRootCauseAnalysis(document, variables);
        
        // Section 3: Action Plan
        addSectionHeader(document, "📝 Plan d'actions");
        addActionPlan(document, variables);
        
        // Section 4: Risk Assessment
        addSectionHeader(document, "⚠️ Évaluation des risques");
        addRiskAssessment(document, variables);
        
        // Section 5: Resource Allocation
        addSectionHeader(document, "💰 Ressources allouées");
        addResourceAllocation(document, variables);
        
        // Section 6: Validation Decision
        addSectionHeader(document, "✅ Décision de validation");
        addValidationDecision(document, variables);
        
        // Add footer
        addFooter(document);
        
        document.close();
        
        return baos.toByteArray();
    }
    
    private void addHeader(Document document, Map<String, Object> variables) {
        Paragraph header = new Paragraph("Date de génération: " + LocalDateTime.now().format(DATE_FORMATTER))
                .setFontSize(9)
                .setFontColor(GRAY)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(10);
        
        document.add(header);
        addSpacer(document);
    }
    
    private void addTitle(Document document, String title) {
        Paragraph titlePara = new Paragraph(title)
                .setFontSize(20)
                .setBold()
                .setFontColor(DARK_BLUE)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        
        document.add(titlePara);
        addSpacer(document);
        addHorizontalLine(document);
        addSpacer(document);
    }
    
    private void addSectionHeader(Document document, String header) {
        addSpacer(document);
        
        Paragraph para = new Paragraph(header)
                .setFontSize(14)
                .setBold()
                .setFontColor(SECTION_BLUE)
                .setMarginTop(15)
                .setMarginBottom(10);
        
        document.add(para);
        addSpacer(document);
    }
    
    private void addCapaInfo(Document document, Map<String, Object> variables) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                .useAllAvailableWidth();
        
        addTableRow(table, "Numéro CAPA", getString(variables, "capaNumber"), true);
        addTableRow(table, "Titre", getString(variables, "capaTitle"), true);
        addTableRow(table, "Département", getString(variables, "department"), true);
        addTableRow(table, "Priorité", getString(variables, "priority"), true);
        
        document.add(table);
    }
    
    private void addRootCauseAnalysis(Document document, Map<String, Object> variables) {
        addKeyValue(document, "Méthode d'analyse", getString(variables, "rcaMethod"));
        
        addSubHeader(document, "Causes racines identifiées");
        addParagraph(document, getString(variables, "rootCauses"));
        
        addSubHeader(document, "Facteurs contributifs");
        addParagraph(document, getString(variables, "contributingFactors"));
    }
    
    private void addActionPlan(Document document, Map<String, Object> variables) {
        // Action 1
        addAction(document, variables, 1);
        
        // Action 2 (if exists)
        if (hasValue(variables, "action2Description")) {
            addAction(document, variables, 2);
        }
        
        // Action 3 (if exists)
        if (hasValue(variables, "action3Description")) {
            addAction(document, variables, 3);
        }
        
        // Budget summary
        addSpacer(document);
        Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                .useAllAvailableWidth();
        
        addTableRow(summaryTable, "Budget total estimé", getString(variables, "totalBudget"), true);
        addTableRow(summaryTable, "Délai global", getString(variables, "implementationTimeline"), true);
        
        document.add(summaryTable);
    }
    
    private void addAction(Document document, Map<String, Object> variables, int actionNum) {
        String prefix = "action" + actionNum;
        
        addSubHeader(document, "Action " + actionNum);
        
        Table actionTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                .useAllAvailableWidth();
        
        addTableRow(actionTable, "Type", getString(variables, prefix + "Type"), true);
        addTableRow(actionTable, "Description", getString(variables, prefix + "Description"), true);
        addTableRow(actionTable, "Responsable", getString(variables, prefix + "Owner"), true);
        addTableRow(actionTable, "Date limite", getString(variables, prefix + "Deadline"), true);
        addTableRow(actionTable, "Ressources nécessaires", getString(variables, prefix + "Resources"), true);
        addTableRow(actionTable, "Indicateurs de succès", getString(variables, prefix + "KPI"), true);
        
        document.add(actionTable);
        addSpacer(document);
    }
    
    private void addRiskAssessment(Document document, Map<String, Object> variables) {
        // Risk 1
        addRisk(document, variables, 1);
        
        // Risk 2 (if exists)
        if (hasValue(variables, "risk2Description")) {
            addRisk(document, variables, 2);
        }
        
        // Risk 3 (if exists)
        if (hasValue(variables, "risk3Description")) {
            addRisk(document, variables, 3);
        }
        
        // Global risk summary
        addSpacer(document);
        addSubHeader(document, "Synthèse des risques");
        
        Table riskTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                .useAllAvailableWidth();
        
        addTableRow(riskTable, "Niveau de risque global", getString(variables, "globalRiskLevel"), true);
        addTableRow(riskTable, "Risques résiduels", getString(variables, "residualRisks"), true);
        addTableRow(riskTable, "Recommandations", getString(variables, "riskRecommendations"), true);
        
        document.add(riskTable);
    }
    
    private void addRisk(Document document, Map<String, Object> variables, int riskNum) {
        String prefix = "risk" + riskNum;
        
        addSubHeader(document, "Risques Action " + riskNum);
        
        Table riskTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                .useAllAvailableWidth();
        
        addTableRow(riskTable, "Risques identifiés", getString(variables, prefix + "Description"), true);
        addTableRow(riskTable, "Probabilité", getString(variables, prefix + "Probability"), true);
        addTableRow(riskTable, "Gravité", getString(variables, prefix + "Severity"), true);
        addTableRow(riskTable, "Mesures de mitigation", getString(variables, prefix + "Mitigation"), true);
        
        document.add(riskTable);
        addSpacer(document);
    }
    
    private void addResourceAllocation(Document document, Map<String, Object> variables) {
        // Allocation 1
        addAllocation(document, variables, 1);
        
        // Allocation 2 (if exists)
        if (hasValue(variables, "action2Description")) {
            addAllocation(document, variables, 2);
        }
        
        // Allocation 3 (if exists)
        if (hasValue(variables, "action3Description")) {
            addAllocation(document, variables, 3);
        }
        
        // Allocation summary
        addSpacer(document);
        addSubHeader(document, "Synthèse des allocations");
        
        Table allocTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                .useAllAvailableWidth();
        
        addTableRow(allocTable, "Budget total alloué", getString(variables, "totalAllocatedBudget"), true);
        addTableRow(allocTable, "Source de financement", getString(variables, "fundingSource"), true);
        addTableRow(allocTable, "Commentaires de la direction", getString(variables, "managementComments"), true);
        
        document.add(allocTable);
    }
    
    private void addAllocation(Document document, Map<String, Object> variables, int allocNum) {
        addSubHeader(document, "Allocation Action " + allocNum);
        
        Table allocTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                .useAllAvailableWidth();
        
        addTableRow(allocTable, "Budget alloué", getString(variables, "allocatedBudget" + allocNum), true);
        addTableRow(allocTable, "Responsable confirmé", getString(variables, "confirmedOwner" + allocNum), true);
        addTableRow(allocTable, "Personnel additionnel", getString(variables, "additionalTeam" + allocNum), true);
        addTableRow(allocTable, "Équipements", getString(variables, "allocatedEquipment" + allocNum), true);
        addTableRow(allocTable, "Statut", getString(variables, "resourceStatus" + allocNum), true);
        addTableRow(allocTable, "Commentaires", getString(variables, "allocationComments" + allocNum), true);
        
        document.add(allocTable);
        addSpacer(document);
    }
    
    private void addValidationDecision(Document document, Map<String, Object> variables) {
        boolean approved = "true".equals(getString(variables, "planApprouve"));
        
        Paragraph decisionPara;
        if (approved) {
            decisionPara = new Paragraph("✅ PLAN APPROUVÉ")
                    .setFontSize(14)
                    .setBold()
                    .setFontColor(GREEN);
        } else {
            decisionPara = new Paragraph("❌ PLAN REJETÉ")
                    .setFontSize(14)
                    .setBold()
                    .setFontColor(RED);
        }
        
        document.add(decisionPara);
        addSpacer(document);
        
        if (approved) {
            String comments = getString(variables, "approvalComments");
            if (comments != null && !comments.isEmpty()) {
                addKeyValue(document, "Commentaires de validation", comments);
            }
        } else {
            addKeyValue(document, "Raison du rejet", getString(variables, "rejectionReason"));
        }
    }
    
    private void addFooter(Document document) {
        addSpacer(document);
        addHorizontalLine(document);
        
        Paragraph footer = new Paragraph("Document généré automatiquement par le système CAPA")
                .setFontSize(8)
                .setFontColor(FOOTER_GRAY)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10);
        
        document.add(footer);
    }
    
    // Helper methods
    
    private void addSubHeader(Document document, String text) {
        Paragraph para = new Paragraph(text)
                .setFontSize(11)
                .setBold()
                .setFontColor(SUBHEADER_GRAY)
                .setMarginTop(8)
                .setMarginBottom(5);
        
        document.add(para);
    }
    
    private void addKeyValue(Document document, String key, String value) {
        Paragraph para = new Paragraph()
                .add(new Text(key + ": ").setBold())
                .add(new Text(value != null ? value : "N/A"))
                .setMarginBottom(5);
        
        document.add(para);
    }
    
    private void addParagraph(Document document, String text) {
        Paragraph para = new Paragraph(text != null ? text : "")
                .setMarginBottom(5);
        
        document.add(para);
    }
    
    private void addSpacer(Document document) {
        document.add(new Paragraph("\n").setMarginBottom(0).setMarginTop(0));
    }
    
    private void addHorizontalLine(Document document) {
        document.add(new Paragraph()
                .setBorderBottom(new SolidBorder(GRAY, 1))
                .setMarginBottom(10));
    }
    
    private void addTableRow(Table table, String label, String value, boolean styleHeader) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label).setBold())
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(8);
        
        if (styleHeader) {
            labelCell.setBackgroundColor(HEADER_GRAY);
        }
        
        Cell valueCell = new Cell()
                .add(new Paragraph(value))
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(8);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    private String getString(Map<String, Object> variables, String key) {
        Object value = variables.get(key);
        return value != null ? value.toString() : "";
    }
    
    private boolean hasValue(Map<String, Object> variables, String key) {
        String value = getString(variables, key);
        return value != null && !value.trim().isEmpty();
    }
}

