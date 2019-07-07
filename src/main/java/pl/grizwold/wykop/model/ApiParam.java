package pl.grizwold.wykop.model;

public enum ApiParam {
    OUTPUT_CLEAR("output", "clear"),
    OUTPUT_BOTH("output", "both"),
    DATA_FULL("data", "full"),
    DATA_COMPACTED("data", "compacted");

    public final String key;
    public final String value;

    ApiParam(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
