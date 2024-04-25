package decode.travel.holidaycheckbackend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Code {
    @Schema( description = "generated code for qr" )
    private String code;
    @Schema( description = "session from websocket" )
    private String session;
}
