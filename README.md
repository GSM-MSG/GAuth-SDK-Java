# GAuth SDK Java
## Dependency
---
### Gradle
<br>
repositories

```gradle
// build.gradle
maven { url 'https://jitpack.io' }

// or

// build.gradle.kts
maven { url = uri("https://jitpack.io") }
```

dependencies

```gradle
// build.gradle
implementation 'com.github.GSM-MSG:GAuth-SDK-Java:v2.0.1'

// or

// build.gradle.kts
implementation("com.github.GSM-MSG:GAuth-SDK-Java:v2.0.1")
```

### Maven
<br>
repositories

```html
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

dependencies
```html
<dependency>
	  <groupId>com.github.GSM-MSG</groupId>
	  <artifactId>GAuth-SDK-Java</artifactId>
	  <version>v2.0.1</version>
</dependency>
```
<br>

## Configuration
---
### Bean
```java
@Bean
public GAuth gauth() {
	return new GAuthImpl();
}
```

### DI
```java
@Component
public class Component{
	private GAuth gAuth;

	public Component(GAuth gAuth){
		this.gAuth = gAuth;
	}
}

```
<br>

## Code
---

해당 메서드를 통해서 코드 발급 가능하다.
```java
gAuth.generateCode(email, password);
```
```java
public class GAuthCode {
    private String code;
}
```
코드를 발급한 후 해당 객체를 리턴한다.

<br>

## Token
---
```java
gAuth.generateToken(email, password, clientId, clientSecret, redirectUri);

gAuth.generateToken(code, clientId, clientSecret, redirectUri);
```

이메일, 패스워드, 클라이언트 아이디, 클라이언트 시크릿, 리다이렉트 uri를 사용해서 토큰을 발급할 수도 있고,

코드, 클라이언트 아이디, 클라이언트 시크릿, 리다이렉트 uri를 사용해서 발급할 수도 있다.

```java
public class GAuthToken {
    private String accessToken;
    private String refreshToken;
}
```
토큰 발급한 후 해당 객체를 리턴한다.

<br>

## RefreshToken
---
```java
gAuth.refresh(refreshToken);
```
refreshToken을 통하여 토큰을 발급받을 수 있다.

```java
public class GAuthToken {
    private String accessToken;
    private String refreshToken;
}
```
토큰을 발급받은 후 해당 객체를 리턴한다.

<br>

## User Info
---
```java
gAuth.getUserInfo(accessToken);
```
accessToken을 이용하여 유저의 정보를 가져올 수 있다.

```java
public class GAuthUserInfo {
    private String email;
    private String name;
    private Integer grade;
    private Integer classNum;
    private Integer num;
    private String gender; // MALE | FEMALE
    private String profileUrl;
    private String role; // ROLE_STUDENT | ROLE_TEACHER | ROLE_GRADUATE
}
```
유저 정보를 해당 객체에 담아 리턴한다.

<br>

## Exception
---
응답코드가 200이 아니면 예외코드를 담은 GAuthException을 던짐