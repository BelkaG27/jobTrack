package JobTrack;

import java.util.HashMap;

public class CandidatureStatsDTOResponse {
    private Long totalCandidatures;

    private HashMap<Statut,Long> groupByStatutCount;

    private float tauxDeReponse;

    public CandidatureStatsDTOResponse(Long totalCandidatures,HashMap<Statut,Long> groupByStatutCount,float tauxDeReponse){
        this.groupByStatutCount=groupByStatutCount;
        this.totalCandidatures=totalCandidatures;
        this.tauxDeReponse=tauxDeReponse;
    }

    

    // public void setTotalCandidatures(Long totalCandidatures){
    //    this.totalCandidatures=totalCandidatures;
    // }

    // public void setgroupByStatutCount(HashMap<Statut,Long> groupByStatutCount){
    //     this.groupByStatutCount=groupByStatutCount;;
    // }

    // public void setTauxDeReponse(Long tauxDeReponse){
    //     this.tauxDeReponse=tauxDeReponse;;
    // }

    

    public float getTauxDeReponse(){
        return tauxDeReponse;
    }

    public HashMap<Statut,Long> getGroupByStatutCount(){
        return groupByStatutCount;
    }

    public Long getTotalCandidatures(){
        return totalCandidatures;
    }
}
