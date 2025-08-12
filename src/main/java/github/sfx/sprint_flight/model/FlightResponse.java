package github.sfx.sprint_flight.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class FlightResponse {
    @JsonProperty("pagination")
    private Pagination pagination;
    
    @JsonProperty("data")
    private List<Flight> data;
    
    // Constructors
    public FlightResponse() {}
    
    // Getters and Setters
    public Pagination getPagination() {
        return pagination;
    }
    
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
    
    public List<Flight> getData() {
        return data;
    }
    
    public void setData(List<Flight> data) {
        this.data = data;
    }
    
    // Inner class for pagination
    public static class Pagination {
        @JsonProperty("limit")
        private Integer limit;
        
        @JsonProperty("offset")
        private Integer offset;
        
        @JsonProperty("count")
        private Integer count;
        
        @JsonProperty("total")
        private Integer total;
        
        // Constructors
        public Pagination() {}
        
        // Getters and Setters
        public Integer getLimit() {
            return limit;
        }
        
        public void setLimit(Integer limit) {
            this.limit = limit;
        }
        
        public Integer getOffset() {
            return offset;
        }
        
        public void setOffset(Integer offset) {
            this.offset = offset;
        }
        
        public Integer getCount() {
            return count;
        }
        
        public void setCount(Integer count) {
            this.count = count;
        }
        
        public Integer getTotal() {
            return total;
        }
        
        public void setTotal(Integer total) {
            this.total = total;
        }
    }
}
