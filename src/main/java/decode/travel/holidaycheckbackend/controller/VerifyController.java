package decode.travel.holidaycheckbackend.controller;

import decode.travel.holidaycheckbackend.model.NFTInfo;
import decode.travel.holidaycheckbackend.service.CaminoService;
import decode.travel.holidaycheckbackend.service.CodeService;
import decode.travel.holidaycheckbackend.model.VerifyRequest;
import decode.travel.holidaycheckbackend.service.DBService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping( value = {"/api/"}, produces = {"application/json", "application/xml"} )
@CrossOrigin( origins = "*", allowedHeaders = "*", allowCredentials = "false", maxAge = 180 )
@RequiredArgsConstructor
public class VerifyController {

    private String contractAddress = "0x323fdE68785C3b7A97c3ba9FaE2FaE9F084d303f";

    private final CodeService codeService;
    private final CaminoService caminoService;
    private final DBService dbService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "412", description = "Key or Code is missing", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
    })
    @PostMapping( path = "verify" )
    public ResponseEntity verify(@RequestBody VerifyRequest request) throws Exception {
        if( request == null || request.getCode().isEmpty() || request.getKey().isEmpty())
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        if( ! caminoService.isKyc(request.getKey()))
            return new ResponseEntity(HttpStatus.PRECONDITION_FAILED);


        if( ! codeService.checkKey(request.getCode()))
            return new ResponseEntity(HttpStatus.PRECONDITION_FAILED);

        String uuid = UUID.randomUUID().toString();
        dbService.addToCache(uuid);
        NFTInfo nftInfo = caminoService.createNFT(request.getWalletAddress());
        HttpHeaders headers = new HttpHeaders();
        headers.add("AUTH-TOKEN", uuid);
        headers.add("TOKENID", nftInfo.getNftId());
        headers.add("TXID", nftInfo.getTx());
        headers.add("CONTRACTADRESS",contractAddress);
        return new ResponseEntity<>(headers,HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "Unauthorized", useReturnTypeSchema = true)
    })
    @GetMapping( path = "verified/{auth}")
    public ResponseEntity verified(@PathVariable String auth){
        if(dbService.checkKey(auth)){
            return new ResponseEntity(HttpStatus.OK);
        }else{
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }

}
