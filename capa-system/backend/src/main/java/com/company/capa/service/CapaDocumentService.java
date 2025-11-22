package com.company.capa.service;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class CapaDocumentService {
    
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public byte[] generateCapaDocument(Map<String, Object> variables) throws Exception {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        PDFont font = PDType1Font.HELVETICA;
        PDFont fontBold = PDType1Font.HELVETICA_BOLD;
        float margin = 50;
        float y = page.getMediaBox().getHeight() - margin;
        float width = page.getMediaBox().getWidth() - 2 * margin;

        PDPageContentStream cs = new PDPageContentStream(doc, page);

        y = writeRight(cs, font, 9, "Date de génération: " + LocalDateTime.now().format(DATE_FORMATTER), margin, y, width);
        y -= 15;

        y = writeCenter(cs, fontBold, 20, "Rapport CAPA - " + getString(variables, "capaNumber"), margin, y, width);
        y -= 10;
        drawLine(cs, margin, y, margin + width);
        y -= 20;

        y = writeSection(cs, fontBold, 14, "Informations CAPA", margin, y, width);
        y = writeKeyValue(cs, fontBold, font, 11, "Numéro CAPA", getString(variables, "capaNumber"), margin, y, width);
        y = writeKeyValue(cs, fontBold, font, 11, "Titre", getString(variables, "capaTitle"), margin, y, width);
        y = writeKeyValue(cs, fontBold, font, 11, "Département", getString(variables, "department"), margin, y, width);
        y = writeKeyValue(cs, fontBold, font, 11, "Priorité", getString(variables, "priority"), margin, y, width);

        y = writeSection(cs, fontBold, 14, "Analyse des causes racines", margin, y, width);
        y = writeKeyValue(cs, fontBold, font, 11, "Méthode d'analyse", getString(variables, "rcaMethod"), margin, y, width);
        y = writeSubHeader(cs, fontBold, 11, "Causes racines identifiées", margin, y, width);
        y = writeParagraph(cs, font, 11, getString(variables, "rootCauses"), margin, y, width);
        y = writeSubHeader(cs, fontBold, 11, "Facteurs contributifs", margin, y, width);
        y = writeParagraph(cs, font, 11, getString(variables, "contributingFactors"), margin, y, width);

        y = writeSection(cs, fontBold, 14, "Plan d'actions", margin, y, width);
        y = writeAction(cs, fontBold, font, variables, 1, margin, y, width);
        if (hasValue(variables, "action2Description")) {
            y = writeAction(cs, fontBold, font, variables, 2, margin, y, width);
        }
        if (hasValue(variables, "action3Description")) {
            y = writeAction(cs, fontBold, font, variables, 3, margin, y, width);
        }

        y = writeSubHeader(cs, fontBold, 11, "Synthèse du plan", margin, y, width);
        y = writeKeyValue(cs, fontBold, font, 11, "Budget total estimé", getString(variables, "totalBudget"), margin, y, width);
        y = writeKeyValue(cs, fontBold, font, 11, "Délai global", getString(variables, "implementationTimeline"), margin, y, width);

        y = writeSection(cs, fontBold, 14, "Évaluation des risques", margin, y, width);
        y = writeRisk(cs, fontBold, font, variables, 1, margin, y, width);
        if (hasValue(variables, "risk2Description")) {
            y = writeRisk(cs, fontBold, font, variables, 2, margin, y, width);
        }
        if (hasValue(variables, "risk3Description")) {
            y = writeRisk(cs, fontBold, font, variables, 3, margin, y, width);
        }
        y = writeSubHeader(cs, fontBold, 11, "Synthèse des risques", margin, y, width);
        y = writeKeyValue(cs, fontBold, font, 11, "Niveau de risque global", getString(variables, "globalRiskLevel"), margin, y, width);
        y = writeKeyValue(cs, fontBold, font, 11, "Risques résiduels", getString(variables, "residualRisks"), margin, y, width);
        y = writeKeyValue(cs, fontBold, font, 11, "Recommandations", getString(variables, "riskRecommendations"), margin, y, width);

        y = writeSection(cs, fontBold, 14, "Ressources allouées", margin, y, width);
        y = writeAllocation(cs, fontBold, font, variables, 1, margin, y, width);
        if (hasValue(variables, "action2Description")) {
            y = writeAllocation(cs, fontBold, font, variables, 2, margin, y, width);
        }
        if (hasValue(variables, "action3Description")) {
            y = writeAllocation(cs, fontBold, font, variables, 3, margin, y, width);
        }
        y = writeSubHeader(cs, fontBold, 11, "Synthèse des allocations", margin, y, width);
        y = writeKeyValue(cs, fontBold, font, 11, "Budget total alloué", getString(variables, "totalAllocatedBudget"), margin, y, width);
        y = writeKeyValue(cs, fontBold, font, 11, "Source de financement", getString(variables, "fundingSource"), margin, y, width);
        y = writeKeyValue(cs, fontBold, font, 11, "Commentaires de la direction", getString(variables, "managementComments"), margin, y, width);

        y = writeSection(cs, fontBold, 14, "Décision de validation", margin, y, width);
        boolean approved = "true".equals(getString(variables, "planApprouve"));
        y = writeCenter(cs, fontBold, 14, approved ? "PLAN APPROUVÉ" : "PLAN REJETÉ", margin, y, width);
        String extra = approved ? getString(variables, "approvalComments") : getString(variables, "rejectionReason");
        if (extra != null && !extra.isEmpty()) {
            y = writeParagraph(cs, font, 11, extra, margin, y, width);
        }

        y -= 10;
        drawLine(cs, margin, y, margin + width);
        y -= 15;
        y = writeCenter(cs, font, 8, "Document généré automatiquement par le système CAPA", margin, y, width);

        cs.close();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        doc.save(baos);
        doc.close();
        return baos.toByteArray();
    }

    private float writeSection(PDPageContentStream cs, PDFont font, int size, String text, float margin, float y, float width) throws Exception {
        y -= 10;
        return writeText(cs, font, size, text, margin, y, width);
    }

    private float writeSubHeader(PDPageContentStream cs, PDFont font, int size, String text, float margin, float y, float width) throws Exception {
        y -= 8;
        return writeText(cs, font, size, text, margin, y, width);
    }

    private float writeKeyValue(PDPageContentStream cs, PDFont keyFont, PDFont valueFont, int size, String key, String value, float margin, float y, float width) throws Exception {
        String line = key + ": " + (value != null ? value : "N/A");
        return writeText(cs, valueFont, size, line, margin, y, width);
    }

    private float writeAction(PDPageContentStream cs, PDFont bold, PDFont normal, Map<String, Object> variables, int num, float margin, float y, float width) throws Exception {
        String p = "action" + num;
        y = writeSubHeader(cs, bold, 11, "Action " + num, margin, y, width);
        y = writeKeyValue(cs, bold, normal, 11, "Type", getString(variables, p + "Type"), margin, y, width);
        y = writeKeyValue(cs, bold, normal, 11, "Description", getString(variables, p + "Description"), margin, y, width);
        y = writeKeyValue(cs, bold, normal, 11, "Responsable", getString(variables, p + "Owner"), margin, y, width);
        y = writeKeyValue(cs, bold, normal, 11, "Date limite", getString(variables, p + "Deadline"), margin, y, width);
        y = writeKeyValue(cs, bold, normal, 11, "Ressources nécessaires", getString(variables, p + "Resources"), margin, y, width);
        y = writeKeyValue(cs, bold, normal, 11, "Indicateurs de succès", getString(variables, p + "KPI"), margin, y, width);
        return y;
    }

    private float writeRisk(PDPageContentStream cs, PDFont bold, PDFont normal, Map<String, Object> variables, int num, float margin, float y, float width) throws Exception {
        String p = "risk" + num;
        y = writeSubHeader(cs, bold, 11, "Risques Action " + num, margin, y, width);
        y = writeKeyValue(cs, bold, normal, 11, "Risques identifiés", getString(variables, p + "Description"), margin, y, width);
        y = writeKeyValue(cs, bold, normal, 11, "Probabilité", getString(variables, p + "Probability"), margin, y, width);
        y = writeKeyValue(cs, bold, normal, 11, "Gravité", getString(variables, p + "Severity"), margin, y, width);
        y = writeKeyValue(cs, bold, normal, 11, "Mesures de mitigation", getString(variables, p + "Mitigation"), margin, y, width);
        return y;
    }

    private float writeAllocation(PDPageContentStream cs, PDFont bold, PDFont normal, Map<String, Object> variables, int num, float margin, float y, float width) throws Exception {
        y = writeSubHeader(cs, bold, 11, "Allocation Action " + num, margin, y, width);
        y = writeKeyValue(cs, bold, normal, 11, "Budget alloué", getString(variables, "allocatedBudget" + num), margin, y, width);
        y = writeKeyValue(cs, bold, normal, 11, "Responsable confirmé", getString(variables, "confirmedOwner" + num), margin, y, width);
        y = writeKeyValue(cs, bold, normal, 11, "Personnel additionnel", getString(variables, "additionalTeam" + num), margin, y, width);
        y = writeKeyValue(cs, bold, normal, 11, "Équipements", getString(variables, "allocatedEquipment" + num), margin, y, width);
        y = writeKeyValue(cs, bold, normal, 11, "Statut", getString(variables, "resourceStatus" + num), margin, y, width);
        y = writeKeyValue(cs, bold, normal, 11, "Commentaires", getString(variables, "allocationComments" + num), margin, y, width);
        return y;
    }

    private float writeCenter(PDPageContentStream cs, PDFont font, int size, String text, float margin, float y, float width) throws Exception {
        float textWidth = font.getStringWidth(text) / 1000 * size;
        float x = margin + (width - textWidth) / 2;
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
        return y - size - 4;
    }

    private float writeRight(PDPageContentStream cs, PDFont font, int size, String text, float margin, float y, float width) throws Exception {
        float textWidth = font.getStringWidth(text) / 1000 * size;
        float x = margin + width - textWidth;
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
        return y - size - 2;
    }

    private float writeParagraph(PDPageContentStream cs, PDFont font, int size, String text, float margin, float y, float width) throws Exception {
        if (text == null) text = "";
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        for (String w : words) {
            String candidate = line.length() == 0 ? w : line + " " + w;
            float tw = font.getStringWidth(candidate) / 1000 * size;
            if (tw > width) {
                y = writeText(cs, font, size, line.toString(), margin, y, width);
                line = new StringBuilder(w);
            } else {
                line = new StringBuilder(candidate);
            }
        }
        if (line.length() > 0) {
            y = writeText(cs, font, size, line.toString(), margin, y, width);
        }
        return y;
    }

    private float writeText(PDPageContentStream cs, PDFont font, int size, String text, float margin, float y, float width) throws Exception {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(margin, y);
        cs.showText(text);
        cs.endText();
        return y - size - 4;
    }

    private void drawLine(PDPageContentStream cs, float x1, float y, float x2) throws Exception {
        cs.moveTo(x1, y);
        cs.lineTo(x2, y);
        cs.stroke();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // Helper methods
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private String getString(Map<String, Object> variables, String key) {
        Object value = variables.get(key);
        return value != null ? value.toString() : "";
    }
    
    private boolean hasValue(Map<String, Object> variables, String key) {
        String value = getString(variables, key);
        return value != null && !value.trim().isEmpty();
    }
}
