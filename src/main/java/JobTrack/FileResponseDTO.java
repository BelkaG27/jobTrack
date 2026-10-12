package JobTrack;

import java.time.LocalDateTime;



public class FileResponseDTO {
    private int id;

    private String nomOrigine;

    private String typeMime;

    private long taille;

    private TypeDocument typeDocument;

    private LocalDateTime dateUpload;

    private Integer candidature;

    public FileResponseDTO(int id,String nomOrigine,String typeMime,long taille,TypeDocument typeDocument,LocalDateTime dateUpload,Integer candidature) {
        setCandidature(candidature);
        setDateUpload(dateUpload);
        setId(id);
        setNomOrigine(nomOrigine);
        setTaille(taille);
        setTypeDocument(typeDocument);
        setTypeMime(typeMime);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomOrigine() {
        return nomOrigine;
    }

    public void setNomOrigine(String nomOrigine) {
        this.nomOrigine = nomOrigine;
    }

    public String getTypeMime() {
        return typeMime;
    }

    public void setTypeMime(String typeMime) {
        this.typeMime = typeMime;
    }

    public long getTaille() {
        return taille;
    }

    public void setTaille(long taille) {
        this.taille = taille;
    }

    public TypeDocument getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(TypeDocument typeDocument) {
        this.typeDocument = typeDocument;
    }

    public LocalDateTime getDateUpload() {
        return dateUpload;
    }

    public void setDateUpload(LocalDateTime dateUpload) {
        this.dateUpload = dateUpload;
    }

    public Integer getCandidature() {
        return candidature;
    }

    public void setCandidature(Integer candidature) {
        this.candidature = candidature;
    }

    public static FileResponseDTO fromFichier(Fichier fichier){
        return new FileResponseDTO(fichier.getId(), fichier.getNomOrigine(), fichier.getTypeMime(), fichier.getTaille(), fichier.getTypeDocument(), fichier.getDateUpload(), fichier.getCandidature().getId());

    }
}
