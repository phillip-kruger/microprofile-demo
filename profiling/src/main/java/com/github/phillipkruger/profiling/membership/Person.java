package com.github.phillipkruger.profiling.membership;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class Person {
    private int id;
    private List<String> names;
    private String surname;
}
