package JobTrack;

public class PageResponseDTO<T> {
    
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private java.util.List<T> content;

    public PageResponseDTO(int pageNumber, int pageSize, long totalElements, int totalPages, boolean last, java.util.List<T> content) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
        this.content = content;
    }

    public int getPageNumber() {
        return pageNumber;
    }   

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isLast() {
        return last;
    }

    public java.util.List<T> getContent() {
        return content;
    }

    
}
