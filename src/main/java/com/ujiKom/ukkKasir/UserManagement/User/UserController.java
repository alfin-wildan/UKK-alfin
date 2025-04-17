package com.ujiKom.ukkKasir.UserManagement.User;

import com.ujiKom.ukkKasir.GeneralComponent.Entity.*;
import com.ujiKom.ukkKasir.SecurityEngine.Constant.SecurityConstant;
import com.ujiKom.ukkKasir.SecurityEngine.JwtService.JwtTokenService;
import com.ujiKom.ukkKasir.SecurityEngine.TokenStoreService;
import com.ujiKom.ukkKasir.SecurityEngine.Utility.JWTTokenProvider;
import com.ujiKom.ukkKasir.UserManagement.User.DTO.DTOLogin;
import com.ujiKom.ukkKasir.UserManagement.User.DTO.DTOUser;
import com.ujiKom.ukkKasir.UserManagement.User.Domain.UserPrincipal;
import com.ujiKom.ukkKasir.UserManagement.User.Exception.EmailExistsExc;
import com.ujiKom.ukkKasir.UserManagement.User.Exception.UserNotFoundExc;
import com.ujiKom.ukkKasir.UserManagement.User.Exception.UsernameExistsExc;
import com.ujiKom.ukkKasir.GeneralComponent.Utility.ServiceHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.ujiKom.ukkKasir.GeneralComponent.Constant.ResponseMessageConstant.SUCCESS_FETCH_DATA;
import static com.ujiKom.ukkKasir.SecurityEngine.Constant.SecurityConstant.REFRESH_TOKEN_IS_MISSING;
import static com.ujiKom.ukkKasir.UserManagement.User.Constant.UserConstant.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;

@Controller
@Tag(name = "User", description = "Operations related to User")
@RequestMapping("/user")
public class UserController extends ResponseResourceEntity<UserEntity> {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;
    private final ServiceHelper serviceHelper;
    private final TokenStoreService tokenStoreService;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider, ServiceHelper serviceHelper, TokenStoreService tokenStoreService, JwtTokenService jwtTokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.serviceHelper = serviceHelper;
        this.tokenStoreService = tokenStoreService;
        this.jwtTokenService = jwtTokenService;
    }

    @Operation(summary = "Fetch All User", description = "Fetch all user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Fetch Data", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessListDataResult.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) })
    })
    @GetMapping("/listAll")
    public List<UserEntity> getAllUsers() {
        return userService.getUsers();
    }

    @Operation(summary = "Fetch All User", description = "Fetch all user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Fetch Data", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessListDataResult.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) })
    })
    @GetMapping("/list")
    public String listAll(@RequestParam(defaultValue = "") String search,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          Model model) {

        Map<String, Object> searchMap = new HashMap<>();
        if (!search.isEmpty()) {
            searchMap.put("name", search);
            searchMap.put("email", search);
            searchMap.put("role", search);
        }

        SearchResult<UserEntity> result = userService.searchData(searchMap, page, size);

        model.addAttribute("users", result.getListData());
        model.addAttribute("search", search);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", result.getTotalPage());
        model.addAttribute("size", size);

        return "user/list";
    }


    @Operation(summary = "Add New User", description = "Add New User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Add Data", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessStringResult.class)) }),
            @ApiResponse(responseCode = "409", description = "Username or Email Already exists", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) })
    })
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("user", new DTOUser()); // Menambahkan model atribut user yang baru
        return "user/add"; // Template untuk form tambah user
    }

    @PostMapping("/add")
    public String add(@ModelAttribute DTOUser user, HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
        try {
            // Panggil service untuk menambah user
            userService.addUser(user, request);
            redirectAttributes.addFlashAttribute("message", "User berhasil ditambahkan.");
            return "redirect:/user/list"; // Redirect ke halaman list user setelah berhasil
        } catch (UsernameExistsExc | EmailExistsExc e) {
            // Jika username atau email sudah ada
            model.addAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "user/add"; // Kembali ke halaman tambah user dengan error
        } catch (Exception e) {
            // Menangani error lainnya
            model.addAttribute("error", "Something went wrong!");
            redirectAttributes.addFlashAttribute("error", "Something went wrong!");
            return "user/add"; // Kembali ke halaman tambah user dengan error
        }
    }


    @Operation(summary = "Update User", description = "Update User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Update Data", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessStringResult.class)) }),
            @ApiResponse(responseCode = "409", description = "Username or Email Already exists", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) }),
            @ApiResponse(responseCode = "404", description = "User Not Found", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) })
    })
    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable("id") Integer id, Model model) {
        UserEntity user = userService.findById(id);
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "user/list"; // Redirect ke list jika user tidak ditemukan
        }
        model.addAttribute("user", user);
        return "user/edit"; // Mengembalikan ke halaman edit
    }
    @PostMapping("/update")
    public String updateUser(@ModelAttribute DTOUser user, RedirectAttributes redirectAttributes) {
        try {
            Integer id = user.getId();
            UserEntity existingUser = userService.findById(id);
            if (existingUser == null) {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/user/list"; // Redirect ke list dengan pesan error
            }

            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("message", "User updated successfully");
            return "redirect:/user/list"; // Redirect ke list setelah update sukses
        } catch (UsernameExistsExc | EmailExistsExc e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/edit/" + user.getId(); // Kembali ke form edit dengan error
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Something went wrong!");
            return "redirect:/user/edit/" + user.getId(); // Kembali ke form edit dengan error
        }
    }

    @Operation(summary = "Delete User", description = "Delete User by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Delete Data", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessStringResult.class)) }),
            @ApiResponse(responseCode = "404", description = "User Not Found", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) }),
            @ApiResponse(responseCode = "409", description = "Data Still Connected to Another Entities", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) }),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot delete User with ID 1", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) })
    })
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            if (id == 1) {
                redirectAttributes.addFlashAttribute("error", "User dengan ID 1 tidak boleh dihapus");
                return "redirect:/user/list";
            }

            UserEntity user = userService.findById(id);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "User tidak ditemukan");
            } else {
                userService.deleteUser(id, null); // gak perlu request, isi null aja
                redirectAttributes.addFlashAttribute("message", "User berhasil dihapus");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan saat menghapus user");
        }

        return "redirect:/user/list";
    }


    @Operation(summary = "Find User by Username", description = "Find user by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Fetch Data", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessDataResult.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) })
    })
    @GetMapping("/find")
    public ResponseEntity<?> getUser(@RequestParam("username") String name) {
        try {
            UserEntity user = userService.findUserByName(name);
            if (user == null) {
                throw new UserNotFoundExc(String.format("User with username: %s isn't found", name));
            }
            return responseWithData(OK, RETURNING_FOUND_USER_BY_USERNAME, user);
        } catch (Exception e) {

            return response(INTERNAL_SERVER_ERROR, "Something went wrong!");
        }
    }

    @Operation(summary = "Login", description = "Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Login Data", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessStringResult.class)) }),
            @ApiResponse(responseCode = "403", description = "User already has an active session", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessStringResult.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessStringResult.class)) }),
            @ApiResponse(responseCode = "404", description = "Bad Credentials", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessStringResult.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) })
    })

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";  // Menampilkan halaman login.html
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        Model model,
                        HttpServletRequest request) {
        try {
            UserEntity loginUser = userService.findByEmail(email);
            if (loginUser == null) {
                model.addAttribute("error", "User not found!");
                return "login"; // Jika user tidak ditemukan, kembali ke halaman login
            }

            try {
                authenticate(email, password); // Verifikasi password
            } catch (Exception e) {
                userService.validateLoginAttempt(loginUser);
                model.addAttribute("error", "Invalid credentials");
                return "login"; // Jika password salah, kembali ke halaman login
            }

            // Login berhasil, generate token
            UserPrincipal userPrincipal = new UserPrincipal(loginUser);
            String jwtToken = jwtTokenProvider.generateJwtToken(userPrincipal);
            String refreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + jwtToken);  // Menyimpan token di header

            // Redirect ke halaman produk setelah login berhasil
            return "redirect:/dashboard";  // Redirect ke halaman produk setelah login
        } catch (Exception e) {
            model.addAttribute("error", "Login failed: " + e.getMessage());
            return "login";  // Jika ada error, kembali ke halaman login
        }
    }




    @PostMapping("/logout")
    public String logoutUser(HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute("username");

        if (username != null) {
            tokenStoreService.invalidateAccessToken(username);
            request.getSession().invalidate();
        }

        return "redirect:/user/login"; // langsung redirect ke halaman login
    }


    @Operation(summary = "Activate User", description = "Activate user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Update Data", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessStringResult.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) })
    })
    @PostMapping("/activate")
    public ResponseEntity<?> activateUser(@RequestParam("username") String username) {
        try {
            userService.activateUser(username);
            return response(OK, USER_ACTIVATION_SUCCESSFULLY);
        } catch (Exception e) {

            return response(INTERNAL_SERVER_ERROR, "Something went wrong!");
        }
    }


    @Operation(summary = "Deactivate User", description = "Deactivate User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Fetch Data", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessStringResult.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) })
    })
    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateUser(@RequestParam("username") String username) {
        try {
            userService.deactivateUser(username);
            return response(OK, USER_HAS_BEEN_DEACTIVATED);
        } catch (Exception e) {

            return response(INTERNAL_SERVER_ERROR, "Something went wrong!");
        }
    }


    @Operation(summary = "Reset Password", description = "Reset password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Reset Password", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessStringResult.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) })
    })
    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email,
                                           @RequestBody Map<String, String> requestData) {
        try {
            authenticate(email, requestData.get("currentPassword"));

            userService.resetPassword(email, requestData.get("newPassword"));
            return response(OK, "Password successfully changed");
        } catch (BadCredentialsException e) {

            return response(HttpStatus.UNAUTHORIZED, "Bad credentials");
        } catch (EmailExistsExc e) {

            return response(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {

            return response(INTERNAL_SERVER_ERROR, "Something went wrong!");
        }
    }


    @Operation(summary = "Forgot Password", description = "Forgot password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Update Password", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessStringResult.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) })
    })
    @PostMapping("/forgetPassword")
    public ResponseEntity<?> forgetPassword(@RequestParam("email") String email,
                                            @RequestBody Map<String, String> requestData) {
        try {
            userService.forgetPassword(email, requestData.get("newPassword"));
            return response(OK, "Password successfully changed");
        } catch (Exception e) {

            return response(CONFLICT, "Conflict");
        }
    }



    @Operation(summary = "Refresh JWT Token", description = "Refresh JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Refresh Token", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessListDataResult.class)) }),
            @ApiResponse(responseCode = "401", description = "Token is not valid or Refresh Token is missing", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessListDataResult.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) })
    })
    @GetMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            String authorizationToken = request.getHeader(AUTHORIZATION);
            if (authorizationToken != null && authorizationToken.startsWith(SecurityConstant.TOKEN_HEADER)) {
                String token = authorizationToken.substring(SecurityConstant.TOKEN_HEADER.length());
                String email = jwtTokenProvider.getSubject(token);
                if (jwtTokenProvider.isTokenValid(email, token)) {
                    UserEntity user = userService.findByEmail(email);
                    UserPrincipal userPrincipal = new UserPrincipal(user);
                    String newJwtToken = jwtTokenProvider.generateJwtToken(userPrincipal);
                    String newRefreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal);

                    HttpHeaders headers = new HttpHeaders();
                    headers.add(SecurityConstant.JWT_TOKEN_HEADER, newJwtToken);
                    headers.add(SecurityConstant.JWT_REFRESH_TOKEN, newRefreshToken);
                    return responseHeader(OK, "Token Refreshed Successfully", headers);
                } else {
                    httpStatus = UNAUTHORIZED;
                    httpMessage = "Token isn't valid";
                }
            } else {
                httpStatus = UNAUTHORIZED;
                httpMessage = REFRESH_TOKEN_IS_MISSING;
            }
            return response(httpStatus, httpMessage);
        } catch (Exception e) {

            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = "Something went wrong!";
            return response(httpStatus, httpMessage);
        }
    }


    @Operation(summary = "Search User", description = "Search user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Fetch Data", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = SuccessListDataResult.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResult.class)) })
    })
    @PostMapping("/search")
    public ResponseEntity<?> searchData(
            @RequestBody Map<String, Object> reqBody,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) {
        try {
            SearchResult<UserEntity> result = userService.searchData(reqBody, pageNumber, pageSize);
            return responseWithPagination(OK, SUCCESS_FETCH_DATA, result.getListData(), result.getCurrentPage(), result.getPageSize(), result.getTotalData(), result.getTotalPage());
        } catch (Exception e) {

            return response(INTERNAL_SERVER_ERROR, "Something went wrong!");
        }
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(SecurityConstant.JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        headers.add(SecurityConstant.JWT_REFRESH_TOKEN, jwtTokenProvider.generateRefreshToken(userPrincipal));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

}
