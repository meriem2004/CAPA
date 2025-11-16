package com.company.capa.model;
 
import jakarta.persistence.*;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "capa_documents")
public class CapaDocument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "process_instance_id", nullable = false)
    private Long processInstanceId;
    
    @Column(name = "capa_number", nullable = false)
    private String capaNumber;
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "BYTEA")
    private byte[] content;
    
    @Column(name = "file_size")
    private Integer fileSize;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getProcessInstanceId() { return processInstanceId; }
    public void setProcessInstanceId(Long processInstanceId) { this.processInstanceId = processInstanceId; }
    
    public String getCapaNumber() { return capaNumber; }
    public void setCapaNumber(String capaNumber) { this.capaNumber = capaNumber; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public byte[] getContent() { return content; }
    public void setContent(byte[] content) { this.content = content; }
    
    public Integer getFileSize() { return fileSize; }
    public void setFileSize(Integer fileSize) { this.fileSize = fileSize; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}