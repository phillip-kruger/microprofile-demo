package com.github.phillipkruger.profiling.membership;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class Membership {
    private int membershipId;
    private Person owner;
    private Type type;
}
