import cn.hutool.core.lang.Validator;

/**
 * @author 安逸i
 * @version 1.0
 */
public class TestHutool {
    public static void main(String[] args) {
        boolean isPhone = Validator.isMobile("13456789100");
        System.out.println(isPhone);
    }
}
