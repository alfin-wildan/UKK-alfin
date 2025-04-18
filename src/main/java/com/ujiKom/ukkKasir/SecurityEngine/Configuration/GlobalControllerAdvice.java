package com.ujiKom.ukkKasir.SecurityEngine.Configuration;
import com.ujiKom.ukkKasir.UserManagement.User.UserEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Component
@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addUserToModel(HttpSession session, Model model) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }
    }
}
