package JobTrack;

import org.springframework.core.io.Resource;

public class FileLoadResponseDTO {
    private Resource resource;

    private String nomOrigine;

    private String typeMime;

    public FileLoadResponseDTO(Resource resource,String nomOrigine, String typeMime){
        this.resource = resource;
        this.nomOrigine = nomOrigine;
        this.typeMime = typeMime;
    }

    public Resource getResource(){
        return resource;
    }

    public String getNomOrigine(){
        return nomOrigine;
    }

    public String getTypeMime(){
        return typeMime;
    }
}
