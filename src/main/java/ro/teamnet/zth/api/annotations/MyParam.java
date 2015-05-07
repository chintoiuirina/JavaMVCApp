package ro.teamnet.zth.api.annotations;

import java.lang.annotation.*;

/**
 * Created by Irina on 07.05.2015.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyParam {

    String name();
}
