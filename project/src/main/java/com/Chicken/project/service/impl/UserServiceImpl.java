package com.Chicken.project.service.impl;

import com.Chicken.project.dto.request.Book.BookFilterDto;
import com.Chicken.project.dto.request.User.LoginRequest;
import com.Chicken.project.dto.request.User.UserFilterDto;
import com.Chicken.project.dto.request.User.UserRequest;
import com.Chicken.project.dto.request.User.UserUpdateRequest;
import com.Chicken.project.dto.response.LoginResponse;
import com.Chicken.project.dto.response.PageResponse;
import com.Chicken.project.dto.response.User.UserResponseForAdmin;
import com.Chicken.project.dto.response.User.UserShortResponse;
import com.Chicken.project.entity.Article;
import com.Chicken.project.entity.Book;
import com.Chicken.project.entity.UserPrincipal;
import com.Chicken.project.entity.V_User;
import com.Chicken.project.exception.BusinessException;
import com.Chicken.project.repository.RoleGroupRepo;
import com.Chicken.project.repository.UserRepo;
import com.Chicken.project.service.ArticleService;
import com.Chicken.project.service.CategoryService;
import com.Chicken.project.service.CommentService;
import com.Chicken.project.service.UserService;
import com.Chicken.project.utils.PageResponseUtil;
import com.opencsv.CSVWriter;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo repo;
    @Autowired
    private ArticleServiceImpl articleServiceImpl;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private ArticleService aService;
    @Autowired
    private CommentServiceImpl cService;
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    private RoleGroupRepo rRepo;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    private BorrowServiceImpl borrowService;
    private static final Logger log =  LoggerFactory.getLogger(UserServiceImpl.class);


    private V_User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) authentication.getPrincipal()).getUser();
        }
        throw new RuntimeException("No authenticated user found");
    }
    public UserShortResponse toShortResponse(V_User user){
        log.info("Converting user information into short response");
        UserShortResponse res = new UserShortResponse();
        res.setFullname(user.getFullname());
        res.setId(user.getId());
        res.setUsername(user.getUsername());
        return res;
    }
    public UserResponseForAdmin toResponse(V_User user) {
        log.info("Converting user information into response");
        UserResponseForAdmin res = new UserResponseForAdmin();
        res.setId(user.getId());
        res.setUsername(user.getUsername());
        res.setFullname(user.getFullname());
        res.setEmail(user.getEmail());
        res.setPhone(user.getPhone());
        res.setAddress(user.getAddress());
        res.setIdentityNumber(user.getIdentityNumber());
        res.setAge(user.getAge());
        res.setBirthday(user.getBirthday());
        res.setArticles(Optional.ofNullable(user.getArticles())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(ArticleServiceImpl::toResponse)
                .collect(Collectors.toList()));
        res.setComments(Optional.ofNullable(user.getComments())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(CommentServiceImpl::toResponse)
                .collect(Collectors.toList()));
        if(user.getRoleGroup()!= null) {
            String rgs = user.getRoleGroup().getRoleGroupName();
            res.setRoleGroupName(rgs);

        }
        res.setBorrows(
                Optional.ofNullable(user.getIsBorrowing())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(borrowService::toResponse)
                        .collect(Collectors.toList())
        );
        res.setLikePostIds(new ArrayList<>(user.getLikedPosts()));
        res.setDislikePostIds(new ArrayList<>(user.getDisLikedPosts()));
        return res;
    }
    @Override
    public UserResponseForAdmin createUser(UserRequest request) {
        log.info("Received request to create new user");
        if(repo.existsByUsername(request.getUsername())) {
            log.warn("Username '{}' already exists", request.getUsername());
            throw new BusinessException("error.username.existed");
        }
        V_User user = new V_User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFullname(request.getFullname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setIdentityNumber(request.getIdentityNumber());
        user.setAge(request.getAge());
        user.setBirthday(request.getBirthday());
        log.info("Created new user '{}'", user.getUsername());
        return toResponse(repo.save(user));
    }
    @Override
    public UserResponseForAdmin updateUser(long id, UserUpdateRequest request) {
        log.info("Received request to update user id {}", id);
        if(!repo.existsById(id)) {
            log.warn("User id '{}' not found", id);
            throw new BusinessException("error.user.notFound");
        }
        else {
            V_User user = repo.findById(id).get();
            String username = request.getUsername();
            if(username != null)user.setUsername(username);
            String password = request.getPassword();
            if(password != null) user.setPassword(encoder.encode(password));
            String fullname = request.getFullname();
            if(fullname != null) user.setFullname(fullname);
            String email = request.getEmail();
            if(email != null) user.setEmail(email);
            String phoneNumber = request.getPhone();
            if(phoneNumber != null)user.setPhone(phoneNumber);
            String address= request.getAddress();
            if(address != null) user.setAddress(address);
            String idNumber = request.getIdentityNumber();
            if(idNumber != null) user.setIdentityNumber(idNumber);
            Integer age = request.getAge();
            if(age != null)user.setAge(age);
            LocalDate birthday = request.getBirthday();
            if(birthday != null) user.setBirthday(birthday);
            log.info("updated user");
            return toResponse(repo.save(user));
        }
    }
    @Override
    public PageResponse<UserShortResponse> viewUser(int page, int size){
        log.info("Viewing list of users: page {}", page);
        Pageable pageable = PageRequest.of(page, size);
        return PageResponseUtil.fromPage(repo.findAll(pageable).map(this::toShortResponse));
    }
    @Override
    public Object viewUserDetail(Long id, V_User currentUser)
    {
        log.info("Viewing details of user id '{}'", id);
        boolean isAdmin = "ADMIN".equals(currentUser.getRoleGroup().getRoleGroupCode());
        boolean isCurrentUser = currentUser.getId()==id;
        if(!repo.existsById(id)) {
            log.warn("User id '{}' not found", id);
            throw new BusinessException("error.user.notFound");
        }
        if(isAdmin || isCurrentUser) return toResponse(repo.findById(id).orElseThrow());
        else return toShortResponse(repo.findById(id).orElseThrow());
    }
    @Override
    public List<UserShortResponse> search(String keyword){
        return repo.searchUser(keyword).stream().map(this::toShortResponse).collect(Collectors.toList());
    }
    @Override
    public boolean deleteUser(long id){
        log.info("Received request to delete user id {}", id);
        if(repo.existsById(id)) {
//            repo.deleteById(id);
            V_User user = repo.findById(id).get();

            V_User currentUser = getCurrentUser();
            user.getArticles().forEach(c -> aService.deleteArticle(c.getId(), currentUser));
            user.getComments().forEach(comment -> cService.softDeleteTree(comment.getId()));

            user.setDeleted(true);
            repo.save(user);
            log.info("Deleted user id {}", id);
            return true;
        }
        log.warn("User id '{}' not found", id);
        return false;
    }
    public PageResponse<UserShortResponse> filterUser(@RequestParam(required = false) String username,
                                                       @RequestParam(required = false) String email,
                                                       @RequestParam(required = false) String phone,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<V_User> result = repo.filterUser(username, email, phone, pageable);
        log.info("Viewing filtered users, total users: '{}'", result.getTotalElements());
        return PageResponseUtil.fromPage(result.map(this::toShortResponse));
    }
    public PageResponse<UserShortResponse> filterUserDto(@RequestBody UserFilterDto filter,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<V_User> result = repo.filterUser(filter.getUsername(), filter.getEmail(), filter.getPhone(), pageable);
        log.info("Viewing filtered users, total users: '{}'", result.getTotalElements());
        return PageResponseUtil.fromPage(result.map(this::toShortResponse));
    }

    public ByteArrayInputStream exportToCsv() throws IOException {
        log.info("Received request to export to csv file");
        List<V_User> users = repo.findAll();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(out))) {
            writer.writeNext(new String[]{
                    "Full Name", "Username", "Identity Number", "Birthday",
                    "Role", "Created Date", "Updated Date"
            });

            for (V_User user : users) {
                writer.writeNext(new String[]{
                        user.getFullname() != null ? user.getFullname() : "",
                        user.getUsername() != null ? user.getUsername() : "",
                        user.getIdentityNumber() != null ? user.getIdentityNumber() : "",
                        user.getBirthday() != null ? user.getBirthday().toString() : "",
                        user.getRoleGroup() != null ? user.getRoleGroup().getRoleGroupCode() : "",
                        user.getCreatedDate() != null ? user.getCreatedDate().toString() : "",
                        user.getUpdatedDate() != null ? user.getUpdatedDate().toString() : ""
                });
            }
        }
        log.info("Exported users to csv");
        return new ByteArrayInputStream(out.toByteArray());
    }


    public void exportToExcel(UserFilterDto filter, HttpServletResponse response) throws IOException {
        log.info("Received request to export users to excel file");
        if (filter == null) {
            filter = new UserFilterDto();
        }
        List<V_User> users = repo.filterUser(filter.getUsername(), filter.getEmail(), filter.getPhone());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=filtered_books.xlsx");
        try(Workbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet("users");
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "Full Name", "Username", "Identity Number", "Birthday", "Email",
                    "Role", "Created Date", "Updated Date"
            };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            int rowIdx = 1;
            for(V_User b : users){
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(b.getFullname());
                row.createCell(1).setCellValue(b.getUsername());
                row.createCell(2).setCellValue(b.getIdentityNumber());
                row.createCell(3).setCellValue(b.getBirthday());
                row.createCell(4).setCellValue(b.getEmail());
                row.createCell(5).setCellValue(b.getRoleGroup().getRoleGroupName());
                row.createCell(6).setCellValue(b.getCreatedDate());
                row.createCell(7).setCellValue(b.getUpdatedDate());
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            try (ServletOutputStream out = response.getOutputStream()) {
                workbook.write(out);
            }
        }
        log.info("Exported {} books to Excel", users.size());
    }


    public UserResponseForAdmin register(UserRequest request) {
        log.info("Received request to create new user");
        if(repo.existsByUsername(request.getUsername())) {
            log.warn("Username '{}' already exists", request.getUsername());
            throw new BusinessException("error.username.existed");
        }
        V_User user = new V_User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setFullname(request.getFullname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setIdentityNumber(request.getIdentityNumber());
        user.setAge(request.getAge());
        user.setBirthday(request.getBirthday());
        user.setRoleGroup(rRepo.findByRoleGroupCode("USER"));
        log.info("Created new user '{}'", user.getUsername());
        return toResponse(repo.save(user));
    }

    public LoginResponse verify(LoginRequest request) {
        log.info("Verify user authentication");
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        if(auth.isAuthenticated()) {
            String token = jwtService.generateAccessToken(request.getUsername());
            SecurityContextHolder.getContext().setAuthentication(auth);
            V_User currentUser = getCurrentUser();
            LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin(currentUser.getId(), currentUser.getUsername(), currentUser.getEmail());
            LoginResponse loginResponse = new LoginResponse(token, userLogin);
            log.info("user '{}' logged in", currentUser.getUsername());
            return loginResponse;
        }
        else {
            log.info("failed to verify user authentication");
            return null;
        }
    }

}
