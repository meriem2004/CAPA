package com.company.capa.service;


import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

// OpenPDF imports
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.Element;
import java.awt.Color;

@Service
public class CapaDocumentService {
    
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public byte[] generateCapaDocument(Map<String, Object> variables) throws Exception {
        XWPFDocument document = new XWPFDocument();
        
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
        
        // Convert to bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.write(baos);
        document.close();
        
        return baos.toByteArray();
    }
    
    // === New: Generate PDF version using OpenPDF ===
    public byte[] generateCapaPdf(Map<String, Object> variables) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document pdf = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(pdf, baos);
        pdf.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLACK);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(31, 97, 141));
        Font keyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);
        Font smallGrayFont = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(128, 128, 128));

        // Header (date)
        Paragraph header = new Paragraph("Date de génération: " + LocalDateTime.now().format(DATE_FORMATTER), smallGrayFont);
        header.setAlignment(Element.ALIGN_RIGHT);
        pdf.add(header);
        pdf.add(new Paragraph("\n"));

        // Title
        Paragraph title = new Paragraph("Rapport CAPA - " + getString(variables, "capaNumber"), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        pdf.add(title);
        pdf.add(new Paragraph("\n"));

        // Section: Informations CAPA
        pdf.add(new Paragraph("📋 Informations CAPA", headerFont));
        pdf.add(new Paragraph("\n"));
        PdfPTable infoTable = createTwoColumnTable();
        addTableRow(infoTable, "Numéro CAPA", getString(variables, "capaNumber"), keyFont, normalFont);
        addTableRow(infoTable, "Titre", getString(variables, "capaTitle"), keyFont, normalFont);
        addTableRow(infoTable, "Département", getString(variables, "department"), keyFont, normalFont);
        addTableRow(infoTable, "Priorité", getString(variables, "priority"), keyFont, normalFont);
        pdf.add(infoTable);
        pdf.add(new Paragraph("\n"));

        // Section: Analyse des causes racines
        pdf.add(new Paragraph("🔍 Analyse des causes racines", headerFont));
        pdf.add(new Paragraph("\n"));
        addKeyValueParagraph(pdf, "Méthode d'analyse", getString(variables, "rcaMethod"), keyFont, normalFont);
        pdf.add(new Paragraph("Causes racines identifiées", keyFont));
        pdf.add(new Paragraph(getString(variables, "rootCauses"), normalFont));
        pdf.add(new Paragraph("\n"));
        pdf.add(new Paragraph("Facteurs contributifs", keyFont));
        pdf.add(new Paragraph(getString(variables, "contributingFactors"), normalFont));
        pdf.add(new Paragraph("\n"));

        // Section: Plan d'actions
        pdf.add(new Paragraph("📝 Plan d'actions", headerFont));
        pdf.add(new Paragraph("\n"));
        addActionPdf(pdf, variables, 1, keyFont, normalFont);
        if (hasValue(variables, "action2Description")) {
            addActionPdf(pdf, variables, 2, keyFont, normalFont);
        }
        if (hasValue(variables, "action3Description")) {
            addActionPdf(pdf, variables, 3, keyFont, normalFont);
        }
        PdfPTable summaryTable = createTwoColumnTable();
        addTableRow(summaryTable, "Budget total estimé", getString(variables, "totalBudget"), keyFont, normalFont);
        addTableRow(summaryTable, "Délai global", getString(variables, "implementationTimeline"), keyFont, normalFont);
        pdf.add(summaryTable);
        pdf.add(new Paragraph("\n"));

        // Section: Évaluation des risques
        pdf.add(new Paragraph("⚠️ Évaluation des risques", headerFont));
        pdf.add(new Paragraph("\n"));
        addRiskPdf(pdf, variables, 1, keyFont, normalFont);
        if (hasValue(variables, "risk2Description")) {
            addRiskPdf(pdf, variables, 2, keyFont, normalFont);
        }
        if (hasValue(variables, "risk3Description")) {
            addRiskPdf(pdf, variables, 3, keyFont, normalFont);
        }
        pdf.add(new Paragraph("Synthèse des risques", keyFont));
        PdfPTable riskSummary = createTwoColumnTable();
        addTableRow(riskSummary, "Niveau de risque global", getString(variables, "globalRiskLevel"), keyFont, normalFont);
        addTableRow(riskSummary, "Risques résiduels", getString(variables, "residualRisks"), keyFont, normalFont);
        addTableRow(riskSummary, "Recommandations", getString(variables, "riskRecommendations"), keyFont, normalFont);
        pdf.add(riskSummary);
        pdf.add(new Paragraph("\n"));

        // Section: Ressources allouées
        pdf.add(new Paragraph("💰 Ressources allouées", headerFont));
        pdf.add(new Paragraph("\n"));
        addAllocationPdf(pdf, variables, 1, keyFont, normalFont);
        if (hasValue(variables, "action2Description")) {
            addAllocationPdf(pdf, variables, 2, keyFont, normalFont);
        }
        pdf.add(new Paragraph("\n"));

        // Section: Décision de validation
        pdf.add(new Paragraph("✅ Décision de validation", headerFont));
        pdf.add(new Paragraph("\n"));
        addKeyValueParagraph(pdf, "Décision", getString(variables, "validationDecision"), keyFont, normalFont);
        if (hasValue(variables, "validationComments")) {
            addKeyValueParagraph(pdf, "Commentaires", getString(variables, "validationComments"), keyFont, normalFont);
        }

        // Footer
        Paragraph footer = new Paragraph("Document généré automatiquement par le système CAPA.", smallGrayFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        pdf.add(new Paragraph("\n"));
        pdf.add(footer);

        pdf.close();
        return baos.toByteArray();
    }

    private PdfPTable createTwoColumnTable() {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5);
        table.setSpacingAfter(5);
        return table;
    }

    private void addTableRow(PdfPTable table, String key, String value, Font keyFont, Font valueFont) {
        PdfPCell keyCell = new PdfPCell(new Phrase(key, keyFont));
        keyCell.setBackgroundColor(new Color(240, 240, 240));
        keyCell.setPadding(6);
        table.addCell(keyCell);

        PdfPCell valCell = new PdfPCell(new Phrase(value != null ? value : "", valueFont));
        valCell.setPadding(6);
        table.addCell(valCell);
    }

    private void addKeyValueParagraph(Document pdf, String key, String value, Font keyFont, Font valueFont) throws DocumentException {
        Paragraph p = new Paragraph(key + ": ", keyFont);
        p.add(new Phrase(value != null ? value : "", valueFont));
        pdf.add(p);
    }

    private void addActionPdf(Document pdf, Map<String, Object> variables, int actionNum, Font keyFont, Font normalFont) throws DocumentException {
        String prefix = "action" + actionNum;
        pdf.add(new Paragraph("Action " + actionNum, keyFont));
        PdfPTable t = createTwoColumnTable();
        addTableRow(t, "Type", getString(variables, prefix + "Type"), keyFont, normalFont);
        addTableRow(t, "Description", getString(variables, prefix + "Description"), keyFont, normalFont);
        addTableRow(t, "Responsable", getString(variables, prefix + "Owner"), keyFont, normalFont);
        addTableRow(t, "Date limite", getString(variables, prefix + "Deadline"), keyFont, normalFont);
        addTableRow(t, "Ressources nécessaires", getString(variables, prefix + "Resources"), keyFont, normalFont);
        addTableRow(t, "Indicateurs de succès", getString(variables, prefix + "KPI"), keyFont, normalFont);
        pdf.add(t);
        pdf.add(new Paragraph("\n"));
    }

    private void addRiskPdf(Document pdf, Map<String, Object> variables, int riskNum, Font keyFont, Font normalFont) throws DocumentException {
        String prefix = "risk" + riskNum;
        pdf.add(new Paragraph("Risques Action " + riskNum, keyFont));
        PdfPTable t = createTwoColumnTable();
        addTableRow(t, "Risques identifiés", getString(variables, prefix + "Description"), keyFont, normalFont);
        addTableRow(t, "Probabilité", getString(variables, prefix + "Probability"), keyFont, normalFont);
        addTableRow(t, "Gravité", getString(variables, prefix + "Severity"), keyFont, normalFont);
        addTableRow(t, "Mesures de mitigation", getString(variables, prefix + "Mitigation"), keyFont, normalFont);
        pdf.add(t);
        pdf.add(new Paragraph("\n"));
    }

    private void addAllocationPdf(Document pdf, Map<String, Object> variables, int allocNum, Font keyFont, Font normalFont) throws DocumentException {
        String prefix = "action" + allocNum;
        pdf.add(new Paragraph("Allocation " + allocNum, keyFont));
        PdfPTable t = createTwoColumnTable();
        addTableRow(t, "Budget", getString(variables, prefix + "Budget"), keyFont, normalFont);
        addTableRow(t, "Ressources", getString(variables, prefix + "Resources"), keyFont, normalFont);
        addTableRow(t, "Équipe assignée", getString(variables, prefix + "Team"), keyFont, normalFont);
        pdf.add(t);
        pdf.add(new Paragraph("\n"));
    }
    
    private void addHeader(XWPFDocument document, Map<String, Object> variables) {
        XWPFParagraph header = document.createParagraph();
        header.setAlignment(ParagraphAlignment.RIGHT);
        
        XWPFRun run = header.createRun();
        run.setText("Date de génération: " + LocalDateTime.now().format(DATE_FORMATTER));
        run.setFontSize(9);
        run.setColor("808080");
        
        addSpacer(document);
    }
    
    private void addTitle(XWPFDocument document, String title) {
        XWPFParagraph titlePara = document.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.CENTER);
        
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText(title);
        titleRun.setBold(true);
        titleRun.setFontSize(20);
        titleRun.setColor("2E4057");
        
        addSpacer(document);
        addHorizontalLine(document);
        addSpacer(document);
    }
    
    private void addSectionHeader(XWPFDocument document, String header) {
        addSpacer(document);
        
        XWPFParagraph para = document.createParagraph();
        para.setSpacingBefore(200);
        
        XWPFRun run = para.createRun();
        run.setText(header);
        run.setBold(true);
        run.setFontSize(14);
        run.setColor("1F618D");
        
        addSpacer(document);
    }
    
    private void addCapaInfo(XWPFDocument document, Map<String, Object> variables) {
        XWPFTable table = document.createTable(4, 2);
        styleTable(table);
        
        setTableCell(table.getRow(0), 0, "Numéro CAPA", true);
        setTableCell(table.getRow(0), 1, getString(variables, "capaNumber"), false);
        
        setTableCell(table.getRow(1), 0, "Titre", true);
        setTableCell(table.getRow(1), 1, getString(variables, "capaTitle"), false);
        
        setTableCell(table.getRow(2), 0, "Département", true);
        setTableCell(table.getRow(2), 1, getString(variables, "department"), false);
        
        setTableCell(table.getRow(3), 0, "Priorité", true);
        setTableCell(table.getRow(3), 1, getString(variables, "priority"), false);
    }
    
    private void addRootCauseAnalysis(XWPFDocument document, Map<String, Object> variables) {
        addKeyValue(document, "Méthode d'analyse", getString(variables, "rcaMethod"));
        
        addSubHeader(document, "Causes racines identifiées");
        addParagraph(document, getString(variables, "rootCauses"));
        
        addSubHeader(document, "Facteurs contributifs");
        addParagraph(document, getString(variables, "contributingFactors"));
    }
    
    private void addActionPlan(XWPFDocument document, Map<String, Object> variables) {
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
        XWPFTable summaryTable = document.createTable(2, 2);
        styleTable(summaryTable);
        
        setTableCell(summaryTable.getRow(0), 0, "Budget total estimé", true);
        setTableCell(summaryTable.getRow(0), 1, getString(variables, "totalBudget"), false);
        
        setTableCell(summaryTable.getRow(1), 0, "Délai global", true);
        setTableCell(summaryTable.getRow(1), 1, getString(variables, "implementationTimeline"), false);
    }
    
    private void addAction(XWPFDocument document, Map<String, Object> variables, int actionNum) {
        String prefix = "action" + actionNum;
        
        addSubHeader(document, "Action " + actionNum);
        
        XWPFTable actionTable = document.createTable(6, 2);
        styleTable(actionTable);
        
        setTableCell(actionTable.getRow(0), 0, "Type", true);
        setTableCell(actionTable.getRow(0), 1, getString(variables, prefix + "Type"), false);
        
        setTableCell(actionTable.getRow(1), 0, "Description", true);
        setTableCell(actionTable.getRow(1), 1, getString(variables, prefix + "Description"), false);
        
        setTableCell(actionTable.getRow(2), 0, "Responsable", true);
        setTableCell(actionTable.getRow(2), 1, getString(variables, prefix + "Owner"), false);
        
        setTableCell(actionTable.getRow(3), 0, "Date limite", true);
        setTableCell(actionTable.getRow(3), 1, getString(variables, prefix + "Deadline"), false);
        
        setTableCell(actionTable.getRow(4), 0, "Ressources nécessaires", true);
        setTableCell(actionTable.getRow(4), 1, getString(variables, prefix + "Resources"), false);
        
        setTableCell(actionTable.getRow(5), 0, "Indicateurs de succès", true);
        setTableCell(actionTable.getRow(5), 1, getString(variables, prefix + "KPI"), false);
        
        addSpacer(document);
    }
    
    private void addRiskAssessment(XWPFDocument document, Map<String, Object> variables) {
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
        
        XWPFTable riskTable = document.createTable(3, 2);
        styleTable(riskTable);
        
        setTableCell(riskTable.getRow(0), 0, "Niveau de risque global", true);
        setTableCell(riskTable.getRow(0), 1, getString(variables, "globalRiskLevel"), false);
        
        setTableCell(riskTable.getRow(1), 0, "Risques résiduels", true);
        setTableCell(riskTable.getRow(1), 1, getString(variables, "residualRisks"), false);
        
        setTableCell(riskTable.getRow(2), 0, "Recommandations", true);
        setTableCell(riskTable.getRow(2), 1, getString(variables, "riskRecommendations"), false);
    }
    
    private void addRisk(XWPFDocument document, Map<String, Object> variables, int riskNum) {
        String prefix = "risk" + riskNum;
        
        addSubHeader(document, "Risques Action " + riskNum);
        
        XWPFTable riskTable = document.createTable(4, 2);
        styleTable(riskTable);
        
        setTableCell(riskTable.getRow(0), 0, "Risques identifiés", true);
        setTableCell(riskTable.getRow(0), 1, getString(variables, prefix + "Description"), false);
        
        setTableCell(riskTable.getRow(1), 0, "Probabilité", true);
        setTableCell(riskTable.getRow(1), 1, getString(variables, prefix + "Probability"), false);
        
        setTableCell(riskTable.getRow(2), 0, "Gravité", true);
        setTableCell(riskTable.getRow(2), 1, getString(variables, prefix + "Severity"), false);
        
        setTableCell(riskTable.getRow(3), 0, "Mesures de mitigation", true);
        setTableCell(riskTable.getRow(3), 1, getString(variables, prefix + "Mitigation"), false);
        
        addSpacer(document);
    }
    
    private void addResourceAllocation(XWPFDocument document, Map<String, Object> variables) {
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
        
        XWPFTable allocTable = document.createTable(3, 2);
        styleTable(allocTable);
        
        setTableCell(allocTable.getRow(0), 0, "Budget total alloué", true);
        setTableCell(allocTable.getRow(0), 1, getString(variables, "totalAllocatedBudget"), false);
        
        setTableCell(allocTable.getRow(1), 0, "Source de financement", true);
        setTableCell(allocTable.getRow(1), 1, getString(variables, "fundingSource"), false);
        
        setTableCell(allocTable.getRow(2), 0, "Commentaires de la direction", true);
        setTableCell(allocTable.getRow(2), 1, getString(variables, "managementComments"), false);
    }
    
    private void addAllocation(XWPFDocument document, Map<String, Object> variables, int allocNum) {
        addSubHeader(document, "Allocation Action " + allocNum);
        
        XWPFTable allocTable = document.createTable(6, 2);
        styleTable(allocTable);
        
        setTableCell(allocTable.getRow(0), 0, "Budget alloué", true);
        setTableCell(allocTable.getRow(0), 1, getString(variables, "allocatedBudget" + allocNum), false);
        
        setTableCell(allocTable.getRow(1), 0, "Responsable confirmé", true);
        setTableCell(allocTable.getRow(1), 1, getString(variables, "confirmedOwner" + allocNum), false);
        
        setTableCell(allocTable.getRow(2), 0, "Personnel additionnel", true);
        setTableCell(allocTable.getRow(2), 1, getString(variables, "additionalTeam" + allocNum), false);
        
        setTableCell(allocTable.getRow(3), 0, "Équipements", true);
        setTableCell(allocTable.getRow(3), 1, getString(variables, "allocatedEquipment" + allocNum), false);
        
        setTableCell(allocTable.getRow(4), 0, "Statut", true);
        setTableCell(allocTable.getRow(4), 1, getString(variables, "resourceStatus" + allocNum), false);
        
        setTableCell(allocTable.getRow(5), 0, "Commentaires", true);
        setTableCell(allocTable.getRow(5), 1, getString(variables, "allocationComments" + allocNum), false);
        
        addSpacer(document);
    }
    
    private void addValidationDecision(XWPFDocument document, Map<String, Object> variables) {
        boolean approved = "true".equals(getString(variables, "planApprouve"));
        
        XWPFParagraph decisionPara = document.createParagraph();
        XWPFRun decisionRun = decisionPara.createRun();
        
        if (approved) {
            decisionRun.setText("✅ PLAN APPROUVÉ");
            decisionRun.setColor("27AE60");
        } else {
            decisionRun.setText("❌ PLAN REJETÉ");
            decisionRun.setColor("E74C3C");
        }
        decisionRun.setBold(true);
        decisionRun.setFontSize(14);
        
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
    
    private void addFooter(XWPFDocument document) {
        addSpacer(document);
        addHorizontalLine(document);
        
        XWPFParagraph footer = document.createParagraph();
        footer.setAlignment(ParagraphAlignment.CENTER);
        
        XWPFRun run = footer.createRun();
        run.setText("Document généré automatiquement par le système CAPA");
        run.setFontSize(8);
        run.setColor("95A5A6");
        run.setItalic(true);
    }
    
    // Helper methods
    
    private void addSubHeader(XWPFDocument document, String text) {
        XWPFParagraph para = document.createParagraph();
        para.setSpacingBefore(100);
        
        XWPFRun run = para.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontSize(11);
        run.setColor("34495E");
    }
    
    private void addKeyValue(XWPFDocument document, String key, String value) {
        XWPFParagraph para = document.createParagraph();
        
        XWPFRun keyRun = para.createRun();
        keyRun.setText(key + ": ");
        keyRun.setBold(true);
        
        XWPFRun valueRun = para.createRun();
        valueRun.setText(value != null ? value : "N/A");
    }
    
    private void addParagraph(XWPFDocument document, String text) {
        XWPFParagraph para = document.createParagraph();
        XWPFRun run = para.createRun();
        run.setText(text != null ? text : "");
    }
    
    private void addSpacer(XWPFDocument document) {
        document.createParagraph();
    }
    
    private void addHorizontalLine(XWPFDocument document) {
        XWPFParagraph para = document.createParagraph();
        para.setBorderBottom(Borders.SINGLE);
    }
    
    private void styleTable(XWPFTable table) {
        table.setWidth("100%");
        table.getCTTbl().addNewTblPr().addNewTblW().setW(BigInteger.valueOf(9000));
    }
    
    private void setTableCell(XWPFTableRow row, int cellIndex, String text, boolean isHeader) {
        XWPFTableCell cell = row.getCell(cellIndex);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        
        if (isHeader) {
            cell.setColor("ECF0F1");
        }
        
        XWPFParagraph para = cell.getParagraphs().get(0);
        XWPFRun run = para.createRun();
        run.setText(text);
        
        if (isHeader) {
            run.setBold(true);
        }
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