package hr.bskracic.squil.dto.query;

public class QueryResponse {
    private String result;

    public QueryResponse(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
