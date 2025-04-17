package com.ujiKom.ukkKasir.UserManagement.UserAudit;

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
@Tag(name = "User Audit", description = "Operations related to User Audit")
@RequestMapping("/user/audit")
public class UserAuditController extends ResponseResourceEntity<UserAuditEntity> {
    protected final UserAuditService userAuditService;

    @Autowired
    public UserAuditController(UserAuditService userAuditService) {
        this.userAuditService = userAuditService;
    }

    @Operation(summary = "Fetch User Audit", description = "Fetch user audit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Fetch Data", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessListDataResult.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid fields or data", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) })
    })
    @GetMapping("/list")
    public ResponseEntity<?> getData(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        try {
            SearchResult<UserAuditEntity> result = userAuditService.listAll(search, page, size);
            return responseWithPagination(OK, SUCCESS_FETCH_DATA,result.getListData(), result.getCurrentPage(), result.getPageSize(), result.getTotalData(), result.getTotalPage());
        } catch (Exception e) {
            return response(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

//    @Operation(summary = "Fetch User Activity", description = "Fetch user activity")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Success Fetch Data", content =
//                    { @Content(mediaType = "application/json", schema =
//                    @Schema(implementation = SuccessListDataResult.class)) }),
//            @ApiResponse(responseCode = "500", description = "Internal server error", content =
//                    { @Content(mediaType = "application/json", schema =
//                    @Schema(implementation = ErrorResult.class)) })
//    })
//    @GetMapping("/user")
//    public ResponseEntity<?> fetchAllUserAudit(@PathVariable("username") String user) {
//        try {
//            List<UserAuditEntity> fetchedData = userAuditService.fetchAuditByUserId(user);
//            if (fetchedData.size() != 0) {
//                return responseWithListData(OK, SUCCESS_FETCH_MESSAGE, fetchedData);
//            } else {
//                return responseWithListData(HttpStatus.NOT_FOUND, DATA_NOT_FOUND_MESSAGE, fetchedData);
//            }
//        } catch (Exception e) {
//            log.error("", e);
//            return response(INTERNAL_SERVER_ERROR, "Something went wrong!");
//        }
//    }
}
