package com.Chicken.project.entity;

import com.Chicken.project.entity.Borrow.Borrow;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
@SQLRestriction("is_deleted = 'false'")
public class V_User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;

    private String fullname;
    private String email;
    private String phone;
    private String address;

    private String identityNumber;
    private int age;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleGroup.getFunctions().stream()
                .map(f -> new SimpleGrantedAuthority(f.getFunctionCode()))
                .collect(Collectors.toList());
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate birthday;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Article> articles;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "role_group_id")
    private RoleGroup roleGroup;
    @OneToMany(mappedBy = "user")
    private List<Borrow>  isBorrowing;

    private Set<Long> likedPosts = ConcurrentHashMap.newKeySet();
    public void addToLikedPosts(long articleId){
        likedPosts.add(articleId);
    }

    private Set<Long> disLikedPosts = ConcurrentHashMap.newKeySet();

    public void addToDislikedPosts(long articleId){
        disLikedPosts.add(articleId);
    }
    public void removeFromLikedPosts(long articleID){
        likedPosts.remove(articleID);
    }
    public void removeFromDislikedPosts(long articleID){

        disLikedPosts.remove(articleID);
    }
    public Set<Long> getLikedPosts() {
        if (likedPosts == null) likedPosts = ConcurrentHashMap.newKeySet();
        return likedPosts;
    }
    public Set<Long> getDisLikedPosts() {
        if (disLikedPosts == null) disLikedPosts = ConcurrentHashMap.newKeySet();
        return disLikedPosts;
    }

    @Column(name = "refresh_token")
    private String refreshToken;

}
