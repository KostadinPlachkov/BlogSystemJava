package Blog.controller;

import Blog.bindingModel.ArticleBindingModel;
import Blog.entity.Article;
import Blog.entity.User;
import Blog.repository.ArticleRepository;
import Blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * Created by Kostadin on 04-Jun-17.
 */

@Controller
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserRepository userRepository;
    private ArticleBindingModel articleBindingModel;

    @GetMapping("/article/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model) {
        model.addAttribute("view", "article/create");
        return "base-layout";
    }


    @PostMapping("/article/create")
    @PreAuthorize("isAuthenticated()")
    public String createProcess(ArticleBindingModel articleBindingModel) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String databaseImagePath;
        String[] allowedContentType = {
                "images/png",
                "images/jpg",
                "images/jpeg"
        };

        // TODO: finish image upload.

        User userEntity = this.userRepository.findByEmail(userDetails.getUsername());
        String imagesPath = "\\src\\main\\resources\\static\\images\\";
        String imagesPathRoot = System.getProperty("user.dir");
        String imagesSaveDirectory = imagesPathRoot + imagesPath;
        String fileName = articleBindingModel.getImage().getOriginalFilename();
        String savePath = imagesSaveDirectory + fileName;

        File imageFile = new File(savePath);

        databaseImagePath = "/images/" + fileName;

        Article article = new Article(
                articleBindingModel.getTitle(),
                articleBindingModel.getContent(),
                userEntity,
                databaseImagePath
        );

        this.articleRepository.saveAndFlush(article);
        return "redirect:/";
    }

    @GetMapping("/article/{id}")
    public String details(Model model, @PathVariable Integer id) {
        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }

        if (!(SecurityContextHolder.getContext().getAuthentication()
            instanceof AnonymousAuthenticationToken)) {
            UserDetails user = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            User userEntity = this.userRepository.findByEmail(user.getUsername());
            model.addAttribute("user", userEntity);
        }
        Article article = this.articleRepository.findOne(id);

        model.addAttribute("article", article);
        model.addAttribute("view", "article/details");
        return "base-layout";
    }

    @GetMapping("article/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(@PathVariable Integer id, Model model) {
        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }
        Article article = this.articleRepository.findOne(id);

        if (!this.isUserAuthorOrAdmin(article)) {
            return "redirect:/article" + id;
        }
        model.addAttribute("article", article);
        model.addAttribute("view", "article/edit");
        return "base-layout";
    }

    @PostMapping("/article/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editProcess(@PathVariable Integer id, ArticleBindingModel model) {
        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);

        if (!this.isUserAuthorOrAdmin(article)) {
            return "redirect:/article" + id;
        }

        article.setTitle(model.getTitle());
        article.setContent(model.getContent());
        this.articleRepository.saveAndFlush(article);
        return "redirect:/article/" + article.getId();
    }

    @GetMapping("article/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(@PathVariable Integer id, Model model) {
        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);

        if (!this.isUserAuthorOrAdmin(article)) {
            return "redirect:/article" +id;
        }
        model.addAttribute("article", article);
        model.addAttribute("view", "article/delete");
        return "base-layout";
    }

    @PostMapping("/article/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteProcess(@PathVariable Integer id) {
        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }
        Article article = this.articleRepository.findOne(id);

        if (!this.isUserAuthorOrAdmin(article)) {
            return "redirect:/article" + id;
        }
        this.articleRepository.delete(article);
        return "redirect:/";
    }

    private boolean isUserAuthorOrAdmin(Article article) {
        UserDetails user = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User userEntity = this.userRepository.findByEmail(user.getUsername());
        return userEntity.isAdmin() || userEntity.isAuthor(article);
    }
}
