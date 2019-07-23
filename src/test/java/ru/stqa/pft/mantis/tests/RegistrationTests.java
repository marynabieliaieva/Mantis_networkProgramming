package ru.stqa.pft.mantis.tests;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.lanwen.verbalregex.VerbalExpression;
import ru.stqa.pft.mantis.model.MailMessage;

import java.io.IOException;
import java.util.List;

import static org.testng.AssertJUnit.assertTrue;

public class RegistrationTests extends TestBase {

  //@BeforeMethod - because of external mail server
  public void startMailServer() {
    app.mail().start();
  }


  @Test
  public void testRegistration() throws IOException, MessagingException, javax.mail.MessagingException, InterruptedException {
    long now = System.currentTimeMillis();
    String email = String.format("user%s@localhost.localdoamin", now);
    String user = String.format("user%s", now);
    String password = "password";
    app.james().createUser(user, password);
    app.registration().start(user, email);
    //List<MailMessage> mailMessages = app.mail().waitForMAil(2, 10000); - because of external mail server
    List<MailMessage> mailMessages = app.james().waiForMAil(user, password, 60000);
    String confirmationLink = findConfirmationLink(mailMessages, email);
    app.registration().finish(confirmationLink, password);
    assertTrue(app.newSession().login(user, password));
  }

  private String findConfirmationLink(List<MailMessage> mailMessages, String email) {
    MailMessage mailMessage = mailMessages.stream().filter((m) -> m.to.equals(email)).findFirst().get();
    VerbalExpression regex = VerbalExpression.regex().find("http://").nonSpace().oneOrMore().build();
    return regex.getText(mailMessage.text);
  }

  //@AfterMethod(alwaysRun = true) - because of external mail server
  public void stopMailServer() {
    app.mail().stop();
  }
}
