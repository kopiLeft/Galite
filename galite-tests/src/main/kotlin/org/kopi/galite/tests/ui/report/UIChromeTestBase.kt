package org.kopi.galite.tests.ui.base

import io.github.bonigarcia.wdm.WebDriverManager
import io.github.sukgu.Shadow

import org.openqa.selenium.chrome.ChromeDriver

open class UIChromeTestBase : UITestBase() {
  override val driver = ChromeDriver()

  override fun setupTest() {

    WebDriverManager.chromedriver().setup()
    super.setupTest()
  }
}
/*

class FormTest : UIChromeTestBase() {

  */
/**
   * Scenario : Fill to the form and enter valid informations then submit.
   * Expected : a popup is displayed telling that data have benn successfully saved.
   *//*

  fun submitFormShowsNotification() {
    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS)
    val shadow = Shadow(driver)

    val firstName = shadow.findElement(buildSelector(tag = "vaadin-text-field#firstname"))
    val lastName = shadow.findElement(buildSelector(tag = "vaadin-text-field#lastname"))
    val userHandle = shadow.findElement(buildSelector(tag = "vaadin-text-field#handle"))
    val password = shadow.findElement(buildSelector(tag = "vaadin-password-field#password"))
    val repeatPassword = shadow.findElement(buildSelector(tag = "vaadin-password-field#repeat-password"))
    val button = shadow.findElement(buildSelector(tag = "vaadin-button#submit"))

    firstName.sendKeys("hichem")
    lastName.sendKeys("fazai")
    userHandle.sendKeys("hfazai")
    password.sendKeys("123456789")
    repeatPassword.sendKeys("123456789")

    button.click()

    var notification: WebElement? = null
    var vaadinNotificationContainer: WebElement? = null

    WebDriverWait(driver, 5).until { driver: WebDriver? ->
      notification = findBy(By.tagName("vaadin-notification"))!!.first()
      vaadinNotificationContainer = findBy(By.tagName("vaadin-notification-container"))!!.first()
    }

    val notificationOpened = notification!!.getAttribute("opened")!!.toBoolean()
    assertTrue(notificationOpened)

    val notificationMessage = vaadinNotificationContainer!!.text
    assertEquals(notificationMessage, "Data saved, welcome hfazai")
  }


  */
/**
   * Scenario : Fill to the form and entering unmatched passwords and then submit.
   * Expected : an error is displayed telling that passwords do not match.
   *//*

  @Test
  fun `submit not matched passwords shows error`() {
    val driver = driver!!
    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS)
    val shadow = Shadow(driver)

    val firstName = shadow.findElement(buildSelector(tag = "vaadin-text-field#firstname"))
    val lastName = shadow.findElement(buildSelector(tag = "vaadin-text-field#lastname"))
    val userHandle = shadow.findElement(buildSelector(tag = "vaadin-text-field#handle"))
    val password = shadow.findElement(buildSelector(tag = "vaadin-password-field#password"))
    val repeatPassword = shadow.findElement(buildSelector(tag = "vaadin-password-field#repeat-password"))
    val button = shadow.findElement(buildSelector(tag = "vaadin-button#submit"))

    firstName.sendKeys("hichem")
    lastName.sendKeys("fazai")
    userHandle.sendKeys("hfazai")
    password.sendKeys("111111111")
    repeatPassword.sendKeys("999999999")

    button.click()

    val passwordErrorSelector = buildSelector(tag = "vaadin-password-field>div",
            criteria = *arrayOf("part" to "error-message"))
    val passwordErrorMessage = shadow.findElement(passwordErrorSelector)
    val invalidEntryAttribute = password.getAttribute("invalid").toBoolean()

    assertEquals("Passwords do not match", passwordErrorMessage.text)
    assertTrue(invalidEntryAttribute)
  }
}*/
