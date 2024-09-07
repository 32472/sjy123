package com.example.demo;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpringBootBootstrapLiveTest {

    private static final String API_ROOT = "http://localhost:8081/api/books";

    // 在每个测试方法之前设置 RestAssured 的基础 URL 和端口
    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
    }

    // 创建一个随机的书籍对象
    private Book createRandomBook() {
        Book book = new Book();
        book.setTitle(randomAlphabetic(10)); // 生成一个长度为10的随机标题
        book.setAuthor(randomAlphabetic(15)); // 生成一个长度为15的随机作者
        return book;
    }

    // 通过 POST 请求创建一个书籍，并返回新创建书籍的完整 URI
    private String createBookAsUri(Book book) {
        Response response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(book)
                .post(API_ROOT);
        return API_ROOT + "/" + response.jsonPath().get("id");
    }

    // 测试获取所有书籍的功能
    @Test
    public void whenGetAllBooks_thenOK() {
        Response response = RestAssured.get(API_ROOT);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode()); // 验证返回的状态码为 200
    }

    // 测试通过标题查询书籍的功能
    @Test
    public void whenGetBooksByTitle_thenOK() {
        Book book = createRandomBook(); // 创建一个随机书籍
        createBookAsUri(book); // 创建书籍并获取位置
        Response response = RestAssured.get(API_ROOT + "/title/" + book.getTitle()); // 通过标题查询书籍
        assertEquals(HttpStatus.OK.value(), response.getStatusCode()); // 验证返回的状态码为 200
        assertTrue(response.as(List.class).size() > 0); // 验证返回的结果集不为
    }

    // 测试通过 ID 查询书籍的功能
    @Test
    public void whenGetCreatedBookById_thenOK() {
        Book book = createRandomBook(); // 创建一个随机书籍
        String location = createBookAsUri(book); // 创建书籍并获取位置
        Response response = RestAssured.get(location); // 通过 ID 查询书籍
        assertEquals(HttpStatus.OK.value(), response.getStatusCode()); // 验证返回的状态码为 200
        assertEquals(book.getTitle(), response.jsonPath().get("title")); // 验证返回的书籍标题与创建时的标题一致
    }

    // 测试删除书籍的功能
    @Test
    public void whenDeleteLastCreatedBook_thenOk() {
        // 获取所有书籍
        Response allBooksResponse = RestAssured.get(API_ROOT);
        assertEquals(HttpStatus.OK.value(), allBooksResponse.getStatusCode()); // 验证返回的状态码为 200

        // 解析响应体以获取所有书籍
        List<Book> allBooks = allBooksResponse.jsonPath().getList("", Book.class);

        if (!allBooks.isEmpty()) {
            // 获取最后一个书籍的 ID
            Long lastBookId = allBooks.get(allBooks.size() - 1).getId();
            String location = API_ROOT + "/" + lastBookId;

            // 删除最后一个书籍
            Response response = RestAssured.delete(location);
            assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        }
    }

    // 测试更新书籍的功能
    @Test
    public void whenUpdateCreatedBook_thenUpdated() {
        Book book = createRandomBook(); // 创建一个随机书籍
        String location = createBookAsUri(book); // 创建书籍并获取位置
        book.setId(Long.parseLong(location.split("api/books/")[1])); // 设置书籍的 ID
        book.setAuthor("newAuthor"); // 更新作者字段
        Response response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(book)
                .put(location); // 更新书籍

        assertEquals(HttpStatus.OK.value(), response.getStatusCode()); // 验证更新操作返回的状态码为 200

        response = RestAssured.get(location); // 获取更新后的书籍
        assertEquals(HttpStatus.OK.value(), response.getStatusCode()); // 验证获取操作返回的状态码为 200
        assertEquals("newAuthor", response.jsonPath().get("author")); // 验证更新后的作者字段与预期一致
    }
}