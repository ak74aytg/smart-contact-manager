package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	ContactRepository contactRepository;
	
	
	@ModelAttribute
	public void common(Model model, Principal principal) {
		String username = principal.getName();
		User user = userRepository.getUserByUsername(username);
		model.addAttribute("user", user);
	}

	@GetMapping("/index")
	public String dashboard() {
		return "normal/user_dashboard";
	}
	
	
	@GetMapping("/add-contact")
	public String addContactPageHandler(Model model) {
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	
	@PostMapping("/process-contact")
	public String contactProcessHandler(
			@Valid @ModelAttribute("contact") Contact contact, 
			BindingResult result, 
			@RequestParam("profileImage") MultipartFile file, 
			Principal principal,
			HttpSession session) {
		
		
		try {
			System.out.println(contact);
			String username = principal.getName();
			User user = userRepository.getUserByUsername(username);
			int userId = user.getId();
			contact.setUser(user);
			
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
	            	String formattedDateTime = LocalDateTime.now().toString().replace(':', '_');
	            	String FILE_NAME = userId + "_" + formattedDateTime + "_" + file.getOriginalFilename();
	            	Path path = Paths.get(UPLOAD_DIR+File.separator+FILE_NAME);
	            	Files.copy(file.getInputStream(), 
	            			path, 
	            			StandardCopyOption.REPLACE_EXISTING);
	            	contact.setImage(FILE_NAME);
	            } else {
	                System.out.println("The uploaded file is not an image.");
	            }
	        } else {
	            System.out.println("No file uploaded.");
	        }
			
			
			user.getContacts().add(contact);
			userRepository.save(user);
			session.setAttribute("message", new Message("Contact added successfully", "success"));
			
		} catch (Exception e) {
			ArrayList<Message> list = new ArrayList<>();
			if(result.hasErrors()) {
				if(result.toString().contains("field 'name'")) {
					list.add(new Message("Name cannot be blank", "danger"));
				}
				if(result.toString().contains("field 'phone'"))
					list.add(new Message("Phone cannot be blank", "danger"));
				if(result.toString().contains("field 'email'"))
					list.add(new Message("Email is invalid", "danger"));
			}else {
				list.add(new Message("Something went wrong!", "danger"));
			}
			System.out.println(result);
			session.setAttribute("message", list);
		}
		return "/normal/add_contact_form";
	}
	
	
	@GetMapping("/view-contact/{page}")
	public String viewContactHandler(@PathVariable("page") Integer page ,Model model, Principal principal, HttpSession session) {
		String username = principal.getName();
		User user = userRepository.getUserByUsername(username);
		
			Pageable pageable = PageRequest.of(page-1, 7); 
			
			Page<Contact> contacts = contactRepository.findByUserId(user.getId(), pageable);
			if(contacts.isEmpty() && page==1) {
				model.addAttribute("contacts", null);
			}else {

			model.addAttribute("contacts", contacts);
			model.addAttribute("total_pages", contacts.getTotalPages());
			model.addAttribute("current_page", page);
			session.setAttribute("current_page", page);
			}
		return "normal/view_contacts";
	}
	
	
	@GetMapping("/{contactId}/contact")
	public String showContactsHandler(@PathVariable("contactId") String contactId, Model model, Principal principal) {
		try {
			int cId = Integer.parseInt(contactId.substring(0, contactId.length()-3));
			Contact contact = contactRepository.findById(cId).get();
			int userId = userRepository.getUserByUsername(principal.getName()).getId();
			if(userId==contact.getUser().getId())
				model.addAttribute("contact", contact);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "normal/contact";
	}
	
	@GetMapping("/contacts/delete/{contactId}")
	public String deleteContactHandler(@PathVariable("contactId") String contactId, Principal principal, HttpSession session) {
		int page = session.getAttribute("current_page")==null ? 1 : (int) session.getAttribute("current_page");
		try {
			int cId = Integer.parseInt(contactId.substring(0, contactId.length()-3));
			Contact contact = contactRepository.findById(cId).get();
			int userId = userRepository.getUserByUsername(principal.getName()).getId();
			if(userId==contact.getUser().getId()) {
				if(contact.getImage()!=null) {
					String UPLOAD_DIR = new ClassPathResource("/static/img").getFile().getAbsolutePath();
	            	String FILE_NAME = contact.getImage();
	            	Path path = Paths.get(UPLOAD_DIR+File.separator+FILE_NAME);
	            	Files.delete(path);
	            	System.out.println("photo is deleted");
				}
				contact.setUser(null);
				contactRepository.delete(contact);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "redirect:/user/view-contact/"+page;
	}
	
	
	@GetMapping("contact/edit/{contactId}")
	public String updateHandler(@PathVariable("contactId") String contactId, Principal principal, Model model) {
		int cId = Integer.parseInt(contactId.substring(0, contactId.length()-3));
		Contact contact = contactRepository.findById(cId).get();
		int userId = userRepository.getUserByUsername(principal.getName()).getId();
		if(userId==contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}
		System.out.println(contact.getDescription());
		return "normal/update_contact_form";
	}
	
	@PostMapping("/update-contact/{contactId}")
	public String updateContactHandler(
			@PathVariable("contactId") String contactId, 
			HttpSession session, 
			@ModelAttribute("contact") Contact contact, 
			@RequestParam("profileImage") MultipartFile file) {
		
		int page = session.getAttribute("current_page")==null ? 1 : (int) session.getAttribute("current_page");
		int cId = Integer.parseInt(contactId.substring(0, contactId.length()-3));
		Contact savedContact = contactRepository.findById(cId).get();
		
		
		
		if(contact.getDescription()!=null) {
			savedContact.setDescription(contact.getDescription());
		}
		if(contact.getEmail()!=null) {
			savedContact.setEmail(contact.getEmail());
		}
		if(contact.getName()!=null) {
			savedContact.setName(contact.getName());
		}
		if(contact.getNickName()!=null) {
			savedContact.setNickName(contact.getNickName());
		}
		if(contact.getPhone()!=null) {
			savedContact.setPhone(contact.getPhone());
		}
		if(contact.getWork()!=null) {
			savedContact.setWork(contact.getWork());
		}
		if(file!=null  && !file.isEmpty()) {
			try {
				String UPLOAD_DIR = new ClassPathResource("/static/img").getFile().getAbsolutePath();
				String FILE_NAME;
				Path path;
				//Deleting the image
				if(savedContact.getImage()!=null) {
					FILE_NAME = savedContact.getImage();
					path = Paths.get(UPLOAD_DIR+File.separator+FILE_NAME);
					Files.deleteIfExists(path);
					System.out.println("delete photo");
				}
				
				//Adding new image
				String formattedDateTime = LocalDateTime.now().toString().replace(':', '_');
            	FILE_NAME = savedContact.getUser().getId() + "_" + formattedDateTime + "_" + file.getOriginalFilename();
            	path = Paths.get(UPLOAD_DIR+File.separator+FILE_NAME);
            	Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				savedContact.setImage(FILE_NAME);
			}catch (Exception e) {
			// TODO: handle exception
				e.printStackTrace();
			}
		} 
		
		contactRepository.save(savedContact);
		
		
		
		return "redirect:/user/view-contact/"+page;
	}

	@GetMapping("/profile")
	public String viewProfileHandler() {
		return "normal/user_profile";
	}
	
}
