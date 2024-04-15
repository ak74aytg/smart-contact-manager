package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class MainController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserDetailsService getUserDetailsService;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home-Smart Contact Manager");
		return "home";
	}

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Register-Smart contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	
	@GetMapping("/signin")
	public String signin() {
		return "signin";
	}

	@PostMapping("/process")
	public String processForm(@RequestParam("profileImage") MultipartFile file, @Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(value = "check", defaultValue = "false") Boolean agreement, Model model, HttpSession session ) {
		try {
			if(result.hasErrors()) {
				System.out.println(result.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			if(!agreement) {
				System.out.println("you have not agreed the terms and conditions");
				throw new Exception("you have not agreed the terms and conditions");
			}
			if (file != null && !file.isEmpty()) {
	            // Get the file extension
	            String fileName = file.getOriginalFilename();
	            String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
	            
	            if (fileExtension.equals("jpg")
	            		|| fileExtension.equals("jpeg")
	            		|| fileExtension.equals("png")
	            		|| fileExtension.equals("gif")) 
	            {
	                
	            	String UPLOAD_DIR = new ClassPathResource("/static/img").getFile().getAbsolutePath();
	            	String FILE_NAME = user.getEmail()+ file.getOriginalFilename();
	            	Path path = Paths.get(UPLOAD_DIR+File.separator+FILE_NAME);
	            	Files.copy(file.getInputStream(), 
	            			path, 
	            			StandardCopyOption.REPLACE_EXISTING);
	            	user.setImageUrl(FILE_NAME);
	            } else {
	                System.out.println("The uploaded file is not an image.");
	            }
	        } else {
	            System.out.println("No file uploaded.");
	        }
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			System.out.println(user);
			model.addAttribute("user", user);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userRepository.save(user);
			UserDetails userDetails = getUserDetailsService.loadUserByUsername(user.getEmail());
			Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			System.out.println("registered successfully");

			return "redirect:/user/index";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return "signup";
		}
	}
}
