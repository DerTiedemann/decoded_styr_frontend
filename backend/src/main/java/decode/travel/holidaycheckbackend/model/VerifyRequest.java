package decode.travel.holidaycheckbackend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyRequest {
    @Schema( description = "key from wallet" )
    @NotEmpty
    String key;
    @Schema( description = "code from websocket" )
    @NotEmpty
    String code;
    @Schema( description = "session from websocket" )
    @NotEmpty
    String sessionId;
    @Schema( description = "adress from wallet" )
    @NotEmpty
    String walletAddress;
}
