package ro.teamnet.zth.api.annotations;

/**
 * Created by Irina on 06.05.2015.
 */

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyController {
    String urlPath();
}
