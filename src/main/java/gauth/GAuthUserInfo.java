package gauth;

import java.util.Map;

public class GAuthUserInfo {
    private String email;
    private String name;
    private Integer grade;
    private Integer classNum;
    private Integer num;
    private String gender;
    private String profileUrl;
    private String role;

    public GAuthUserInfo(Map<String, Object> map) {
        this.email = (String) map.get("email");
        this.name = (String) map.get("name");
        this.grade = (Integer) map.get("grade");
        this.classNum = (Integer) map.get("classNum");
        this.num = (Integer) map.get("num");
        this.gender = (String) map.get("gender");
        this.profileUrl = (String) map.get("profileUrl");
        this.role = (String) map.get("role");
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Integer getGrade() {
        return grade;
    }

    public Integer getClassNum() {
        return classNum;
    }

    public Integer getNum() {
        return num;
    }

    public String getGender() {
        return gender;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getRole() {
        return role;
    }
}
