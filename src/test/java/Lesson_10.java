import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;

public class Lesson_10 {
    public static void main(String[] args) throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            driver.get("https://www.mts.by");

            try {
                wait.until(ExpectedConditions.elementToBeClickable(By.id("cookie-agree"))).click();
            } catch (Exception e) {}

            WebElement selectElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".select__header select, select")));
            Select dropdown = new Select(selectElement);

            System.out.println( "1. Связь: " + driver.findElement(By.id("connection-phone")).getAttribute("placeholder"));

            String[] options = {"Домашний интернет", "Рассрочка", "Задолженность"};
            String[] ids = {"internet-phone", "score-instalment", "score-arrears"};

            for (int i = 0; i < options.length; i++) {
                dropdown.selectByVisibleText(options[i]);
                js.executeScript("arguments[0].dispatchEvent(new Event('change'));", selectElement);
                System.out.println((i + 2) + ". " + options[i] + ": " + wait.until(ExpectedConditions.presenceOfElementLocated(By.id(ids[i]))).getAttribute("placeholder"));
            }

            dropdown.selectByVisibleText("Услуги связи");
            js.executeScript("arguments[0].dispatchEvent(new Event('change'));", selectElement);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("connection-phone"))).sendKeys("297777777");
            driver.findElement(By.id("connection-sum")).sendKeys("100");

            WebElement continueBtn = driver.findElement(By.xpath("//form[@id='pay-connection']//button[contains(text(), 'Продолжить')]"));
            js.executeScript("arguments[0].click();", continueBtn);

            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath("//iframe[contains(@src, 'bepaid')]")));

            try {
                WebElement body = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));

                System.out.println("Данные из чека:");

                List<WebElement> allTexts = driver.findElements(By.xpath("//*[contains(text(), '100') or contains(@class, 'amount')]"));
                if (!allTexts.isEmpty()) {
                    System.out.println("Найдено совпадений по сумме: " + allTexts.size());
                    System.out.println("Сумма: " + allTexts.get(0).getText());
                } else {
                    System.out.println("Текст в чеке: " + body.getText().substring(0, Math.min(body.getText().length(), 200)));
                }

                try {
                    WebElement phoneInFrame = driver.findElement(By.xpath("//*[contains(text(), '297777777')]"));
                    System.out.println("Номер подтвержден: " + phoneInFrame.getText());
                } catch (Exception e) {
                    System.out.println("Номер в чеке не найден");
                }


                List<WebElement> icons = driver.findElements(By.tagName("img"));
                System.out.println("Логотипов платежных систем: " + icons.size());

            } catch (Exception e) {
                System.out.println("Ошибка при чтении данных внутри фрейма.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Thread.sleep(5000);
            driver.quit();
        }
    }
}