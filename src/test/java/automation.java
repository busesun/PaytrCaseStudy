import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.io.File;
import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;

import java.time.Duration;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class automation {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setUp() {

        System.setProperty("chromeDriver", "src/resources/drivers/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        driver.get("https://www.paytr.com/");
    }

    @Test(priority = 1)
    public void checkPageIsOpen() {
        // Sayfa başlığını kontrol et
        String expectedTitle = "Sanal Ödeme Çözümleri & Fiziksel Ödeme Sistemleri | PayTR"; // Beklenen başlık
        String actualTitle = driver.getTitle();

        if (actualTitle.contains(expectedTitle)) {
            System.out.println("Test Passed: Sayfa başarıyla yüklendi.");
        } else {
            System.out.println("Test Failed: Sayfa başlığı beklenen ile eşleşmiyor.");
        }
    }

    @Test(priority = 2)
    public void verifySubHeadingsUnderTitle() {
        List<WebElement> subHeadings = driver.findElements(By.cssSelector("[tab-id='online-odeme-cozumleri'] #paymentCardSwiper .swiper-slide"));

        int expectedCount = 12;
        int actualCount = subHeadings.size();

        if (actualCount == expectedCount) {
            System.out.println("Test Passed: 12 alt başlık bulundu.");
        } else {
            System.out.println("Test Failed: Beklenen 12, ancak bulundu: " + actualCount);
        }

        List<WebElement> sectionTitles = driver.findElements(By.cssSelector("h2.section-title"));

        // Sayfadan alınan başlıkların metinlerini bir listeye ekle
        List<String> actualTitles = new ArrayList<String>();
        for (WebElement title : sectionTitles) {
            actualTitles.add(title.getText().trim());
        }

        // Beklenen başlıklar
        String[] expectedTitles = {
                "Ürünleri Mağaza Panelinizden Kolayca Yönetin",
                "Geliştiriciler İçin",
                "Neden Bizi Tercih Ediyorlar",
                "İş Ortakları",
                "Sıkça Sorulan Sorular"
        };

        // Beklenen başlıkların her biri için kontrol yap
        for (String expectedTitle : expectedTitles) {
            if (actualTitles.contains(expectedTitle)) {
                System.out.println("Başlık bulundu: " + expectedTitle);
            } else {
                System.out.println("Başlık eksik: " + expectedTitle);
            }
        }

    }

    @Test(priority = 3)
    public void verifyLinkleOdemePageNavigation() {
        // 1. Ürünler sekmesini bulun ve tıklayın (Eğer menü açılması gerekiyorsa)
        WebElement urunlerMenu = driver.findElement(By.cssSelector("a.menu__link-item"));
        urunlerMenu.click();
        System.out.println("Ürünler menüsü tıklandı.");

        // 2. Açılan menüden "Linkle Ödeme" seçeneğini bulun ve tıklayın
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement linkleOdemeOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a.mega-menu-card.product-mega-menu-card[href='/linkle-odeme']")));
        linkleOdemeOption.click();
        System.out.println("Linkle Ödeme seçeneği tıklandı.");

        // 3. Doğru sayfanın yüklendiğini kontrol edin
        String expectedUrl = "https://www.paytr.com/linkle-odeme";
        String actualUrl = driver.getCurrentUrl();

        if (actualUrl.equals(expectedUrl)) {
            System.out.println("Test Passed: Doğru sayfa yüklendi.");
        } else {
            System.out.println("Test Failed: Beklenen URL: " + expectedUrl + ", Ancak Bulunan URL: " + actualUrl);
        }
    }

    @Test(priority = 4)
    public void fillFormFromExcel() {
        try {
            // 1. Excel dosyasından veri okuma
            FileInputStream file = new FileInputStream(new File("data.xlsx"));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            // İlk satırdan verileri al
            Row row = sheet.getRow(1); // İlk veri satırı
            String firstName = row.getCell(0).getStringCellValue();
            String lastName = row.getCell(1).getStringCellValue();
            String email = row.getCell(2).getStringCellValue();
            String website = row.getCell(3).getStringCellValue();
            String phone = row.getCell(4).getStringCellValue();
            String businessType = row.getCell(5).getStringCellValue();

            workbook.close();
            file.close();

            // 2. Formu doldur
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            driver.findElement(By.id("first-name")).sendKeys(firstName); // Yetkili Adı
            System.out.println("Yetkili Adı dolduruldu: " + firstName);

            driver.findElement(By.name("surname")).sendKeys(lastName); // Yetkili Soyadı
            System.out.println("Yetkili Soyadı dolduruldu: " + lastName);

            driver.findElement(By.id("email")).sendKeys(email); // E-Posta
            System.out.println("E-Posta dolduruldu: " + email);

            driver.findElement(By.name("website")).sendKeys(website); // Website
            System.out.println("Website dolduruldu: " + website);

            driver.findElement(By.name("tel")).sendKeys(phone); // Telefon
            System.out.println("Telefon dolduruldu: " + phone);

            // İşletme Tipi seçimi
            WebElement businessTypeDropdown = driver.findElement(By.cssSelector(".custom-select select"));
            businessTypeDropdown.click();
            WebElement selectedOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[@value='" + businessType + "']")));
            selectedOption.click();
            System.out.println("İşletme Tipi seçildi: " + businessType);

            // Kişisel veri işleme onayı
            driver.findElement(By.id("telefon")).click();
            System.out.println("Kişisel veri işleme onayı verildi.");

            // 3. Formu gönder
            driver.findElement(By.cssSelector(".button.button-primary-light.size\\:large")).click();
            System.out.println("Form gönderildi.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Test sırasında bir hata oluştu.");
        }
    }

    @AfterClass
    public void CloseBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}
