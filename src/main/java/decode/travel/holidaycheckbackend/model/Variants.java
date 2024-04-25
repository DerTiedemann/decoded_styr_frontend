package decode.travel.holidaycheckbackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Variants {
    boolean kyb_basic;
    boolean kyc_basic;
}
