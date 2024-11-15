package ru.kovalenkojuls.cookhub.controllers;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import ru.kovalenkojuls.cookhub.domains.User;
import ru.kovalenkojuls.cookhub.services.UserService;

@Controller
@RequestMapping("/users")
@AllArgsConstructor
public class FollowerController {

    private final UserService userService;

    @GetMapping("follow/{user}")
    public String followToUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable User user,
            RedirectAttributes redirectAttributes,
            @RequestHeader(required = false) String referer
    ) {
        User currentUser = userService.getAuthorizedUser(userDetails);
        userService.addFollowing(currentUser, user);

        UriComponents components = ControllerUtils.getUriComponents(redirectAttributes, referer);
        return "redirect:" + components.getPath();
    }

    @GetMapping("unfollow/{user}")
    public String unFollowFromUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable User user,
            RedirectAttributes redirectAttributes,
            @RequestHeader(required = false) String referer
    ) {
        User currentUser = userService.getAuthorizedUser(userDetails);
        userService.removeFollowing(currentUser, user);

        UriComponents components = ControllerUtils.getUriComponents(redirectAttributes, referer);
        return "redirect:" + components.getPath();
    }

    @GetMapping("followers")
    public String allFollowers(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userService.getAuthorizedUser(userDetails);
        model.addAttribute("currentUser", currentUser);
        return "followers";
    }
}
