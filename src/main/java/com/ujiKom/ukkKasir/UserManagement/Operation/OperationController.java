package com.ujiKom.ukkKasir.UserManagement.Operation;

import com.ujiKom.ukkKasir.GeneralComponent.Entity.ErrorResult;
import com.ujiKom.ukkKasir.GeneralComponent.Entity.ResponseResourceEntity;
import com.ujiKom.ukkKasir.GeneralComponent.Entity.SearchResult;
import com.ujiKom.ukkKasir.GeneralComponent.Entity.SuccessListDataResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ujiKom.ukkKasir.GeneralComponent.Constant.ResponseMessageConstant.SUCCESS_FETCH_DATA;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@Slf4j
@Tag(name = "Operation", description = "Operations related to Operation")
@RequestMapping("/operation")
public class OperationController extends ResponseResourceEntity<OperationEntity> {
    private final OperationService operationService;

    @Autowired
    public OperationController(OperationService operationService) {
        this.operationService = operationService;
    }

    @Operation(summary = "Fetch Operation", description = "Fetch Operation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Fetch Data", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessListDataResult.class)) }),
            @ApiResponse(responseCode = "404", description = "Not Found - No project management matching the criteria", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) })
    })
    @GetMapping("/list")
    public ResponseEntity<?> listAll(
            @RequestParam(value = "search",required = false ,defaultValue = "") String search,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size

    ) {
        try {
            SearchResult<OperationEntity> result = operationService.listAll( search,  page, size);
            return responseWithPagination(OK, SUCCESS_FETCH_DATA, result.getListData(), result.getCurrentPage(), result.getPageSize(), result.getTotalData(), result.getTotalPage());
        } catch (Exception e) {
            return response(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}