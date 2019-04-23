package netty_hello;

import com.blade.Blade;
import com.blade.event.EventType;
import com.blade.mvc.http.EmptyBody;
import com.blade.mvc.http.ByteBody;
import com.blade.mvc.http.StreamBody;
import com.blade.mvc.http.StringBody;

import java.io.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author biezhi
 * 2017/6/5
 */
public class Hello {

    private static final StringBody hello = StringBody.of("Hello World.");

    public static void main(String[] args) {
        Blade.of()
                .get("/", ctx -> {
                    String[] chars = new String[]{"Here a special char \" that not escaped", "And Another \\ char"};
                    ctx.json(chars);
                })
                .get("/user/aa", ctx -> ctx.render("upload.html"))
                .get("/up", ctx -> ctx.render("upload.html"))
                .get("/d1", ctx -> {
                    File file = new File("/Users/biezhi/Pictures/rand/003.jpg");
                    ctx.response().contentType("image/jpeg");
                    ctx.response().header("Content-Disposition", "attachment; filename=003.jpg");
                    ctx.response().body(ByteBody.of(file));
                })
                .get("/d2", ctx -> {
                    File file = new File("/Users/biezhi/Pictures/rand/003.jpg");
                    try (FileInputStream inputStream = new FileInputStream(file)) {
                        ctx.response().contentType("image/jpef");
                        ctx.response().header("Content-Disposition", "attachment; filename=m1.png");
                        ctx.response().body(StreamBody.of(inputStream));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .get("/d3", ctx -> {
                    String str = "hello world";
                    ctx.response().contentType("text/html");
                    ctx.response().body(ByteBody.of(str.getBytes()));
                })
                .get("/error", ctx -> {
                    int a = 1 / 0;
                    ctx.text("ok");
                })
                .get("/hello", ctx -> ctx.body(hello))
                .get("/error", ctx -> {
                    int a = 1 / 0;
                    ctx.text("Hello World.");
                })
                .post("/hello", ctx -> ctx.text("Hello World."))
                .put("/body", ctx -> {
                    ctx.text(ctx.bodyToString());
                })
                .put("/hello", ctx -> ctx.text("Hello World."))
                .delete("/hello", ctx -> ctx.text("Hello World."))
                .get("/download", ctx -> {
                    try {
                        ctx.response().download("hello.txt", new File("/Users/biezhi/workspace/projects/java/blade/src/test/resources/static/a.txt"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .get("/rand", ctx -> {
                    try {
                        int timeout = ctx.fromInt("timeout", new Random().nextInt(1000));
                        TimeUnit.SECONDS.sleep(timeout);
                        ctx.text("sleep " + timeout + "s");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                })
                .before("/user/*", ctx ->
                        {
                            System.out.println("before: " + ctx.uri());
                            ctx.text("Hello World");
                            ctx.abort();
                        }
                )
                .enableCors(true)
                .event(EventType.ENVIRONMENT_CHANGED, new ConfigChanged())
                .event(EventType.SESSION_DESTROY, e -> {
                    System.out.println("session 失效了");
                })
                .start(Hello.class, args);
    }

}
