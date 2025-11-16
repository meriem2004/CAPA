package com.company.capa.service;


import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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