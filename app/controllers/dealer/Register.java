package controllers.dealer;

import models.Dealer;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Result;

import static play.data.Form.form;

import views.html.dealer.register.index;
import views.html.dealer.register.created;

/**
 * Created by ruraj on 7/17/14.
 */
public class Register extends Controller {
    public static Result index() {
        return ok(index.render(form(RegistrationForm.class)));
    }

    public static Result save() {
        play.data.Form<RegistrationForm> dealerForm = form(RegistrationForm.class).bindFromRequest();

        if (dealerForm.hasErrors()) {
            return badRequest(index.render(dealerForm));
        }

        RegistrationForm register = dealerForm.get();

        Dealer dealer = new Dealer();

        dealer.name = register.name;
        dealer.owner = register.owner;
        dealer.pan = register.pan;

        dealer.save();

        return ok(created.render());
    }
    public static class RegistrationForm {

        @Constraints.Required
        public String name;

        @Constraints.Required
        public String owner;

        @Constraints.Required
        public String pan;

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {
            if (isBlank(name)) {
                return "Email is required";
            }

            if (isBlank(owner)) {
                return "Full name is required";
            }

            if (isBlank(pan)) {
                return "Password is required";
            }

            return null;
        }

        private boolean isBlank(String input) {
            return input == null || input.isEmpty() || input.trim().isEmpty();
        }
    }
}
