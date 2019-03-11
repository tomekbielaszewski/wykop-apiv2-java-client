package pl.grizwold.wykop.model;

import lombok.Value;

@Value
public class ApiKey {
    private String prv;
    private String pub;
}
