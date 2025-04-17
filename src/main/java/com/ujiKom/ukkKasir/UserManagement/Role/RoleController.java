package com.ujiKom.ukkKasir.UserManagement.Role;


import com.ujiKom.ukkKasir.GeneralComponent.Entity.*;
import com.ujiKom.ukkKasir.UserManagement.Role.DTO.DTORole;
import com.ujiKom.ukkKasir.UserManagement.Role.Exception.RoleExistExc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ujiKom.ukkKasir.GeneralComponent.Constant.ResponseMessageConstant.SUCCESS_FETCH_DATA;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@Slf4j
@Tag(name = "Role", description = "Operations related to Role")
@RequestMapping("/role")
public class RoleController extends ResponseResourceEntity<RoleEntity> {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "Fetch All Role", description = "Fetch all role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Fetch Data", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessListDataResult.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found - No project management matching the criteria", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class))})
    })
    @GetMapping("/list")
    public ResponseEntity<?> listAll(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size

    ) {
        try {
            SearchResult<RoleEntity> result = roleService.listAll(search, page, size);
            return responseWithPagination(OK, SUCCESS_FETCH_DATA, result.getListData(), result.getCurrentPage(), result.getPageSize(), result.getTotalData(), result.getTotalPage());
        } catch (Exception e) {
            return response(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Operation(summary = "Fetch All Role", description = "Fetch all role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Fetch Data", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessListDataResult.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found - No roles found", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class))})
    })
    @GetMapping("/listAll")
    public List<RoleEntity> listRole() {
        return roleService.listRole();
    }

    @Operation(summary = "Add New Role", description = "Add new role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Add Data", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessStringResult.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad Request - Missing required fields", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) }),
            @ApiResponse(responseCode = "409", description = "Role already exists", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) })
    })
    @PostMapping("/add")
    public ResponseEntity<?> addRole(@RequestBody DTORole role, HttpServletRequest httpServletRequest) {
        try {
            RoleEntity addRole = roleService.addRole(role, httpServletRequest);
            return responseWithData(HttpStatus.CREATED, "Success Add Data", addRole);
        } catch (RoleExistExc e) {
            return response(HttpStatus.CONFLICT, "Role already exists.");
        } catch (Exception e) {
            log.error("Error adding role", e);
            return response(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
        }
    }

    @Operation(summary = "Update Role", description = "Update role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request - Missing required fields", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) }),
            @ApiResponse(responseCode = "409", description = "Role already exists", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) }),
            @ApiResponse(responseCode = "200", description = "Success Update Data", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessListDataResult.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) })
    })
    @PostMapping("/update")
    public ResponseEntity<?> updateRole(@RequestBody DTORole role, HttpServletRequest httpServletRequest) {
        try {
            RoleEntity updateRole = roleService.updateRole(role, httpServletRequest);
            return responseWithData(HttpStatus.OK, "Success Update Data", updateRole);
        } catch (RoleExistExc e) {
            return response(HttpStatus.CONFLICT, "Role already exists.");
        } catch (Exception e) {
            log.error("Error updating role", e);
            return response(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
        }
    }

    @Operation(summary = "Delete Role", description = "Delete role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Delete Data", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessStringResult.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad Request - Missing required fields", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) }),
            @ApiResponse(responseCode = "404", description = "Data Not Found", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) }),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot delete Role with ID 1", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) })
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable("id") long id, HttpServletRequest httpServletRequest) {
        try {
            if (id == 1) {
                return response(HttpStatus.FORBIDDEN, "Role with ID 1 cannot be deleted");
            }

            RoleEntity role = roleService.findById(id);
            if (role == null) {
                return response(HttpStatus.NOT_FOUND, "Data Not Found");
            } else {
                roleService.deleteRole(id, httpServletRequest);
                return response(HttpStatus.OK, "Success Delete Data");
            }
        } catch (Exception e) {
            log.error("Error deleting role", e);
            return response(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
        }
    }


}
